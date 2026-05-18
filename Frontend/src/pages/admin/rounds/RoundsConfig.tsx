import React, { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, Settings, Layers } from 'lucide-react';
import { getBatchTechnologies, createBatchTechnology, updateBatchTechnology, deleteBatchTechnology, BatchTechnology } from '@/api/batchTechnologyApi';
import { getBatches, Batch } from '@/api/batchApi';
import { getTechnologies, Technology } from '@/api/technologyApi';
import toast from 'react-hot-toast';
import ConfirmModal from '@/components/ui/ConfirmModal';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';

const configSchema = z.object({
  batchId: z.string().min(1, 'Please select a batch'),
  technologyId: z.string().min(1, 'Please select a technology'),
  totalRounds: z.coerce.number().min(1, 'At least 1 round is required').max(10, 'Maximum 10 rounds'),
});

type ConfigForm = z.infer<typeof configSchema>;

const RoundsConfig = () => {
  const [configs, setConfigs] = useState<BatchTechnology[]>([]);
  const [batches, setBatches] = useState<Batch[]>([]);
  const [technologies, setTechnologies] = useState<Technology[]>([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingConfig, setEditingConfig] = useState<BatchTechnology | null>(null);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState<number | null>(null);

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm<ConfigForm>({
    resolver: zodResolver(configSchema),
    defaultValues: { totalRounds: 3 }
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [configsRes, batchesRes, techRes] = await Promise.all([
        getBatchTechnologies(),
        getBatches(0, 100),
        getTechnologies()
      ]);
      setConfigs(configsRes.data.data);
      setBatches(batchesRes.data.data.content);
      setTechnologies(techRes.data.data);
    } catch (error: any) {
      toast.error('Failed to load configuration data');
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data: ConfigForm) => {
    try {
      if (editingConfig) {
        await updateBatchTechnology(editingConfig.id!, { totalRounds: data.totalRounds });
        toast.success('Configuration updated');
      } else {
        await createBatchTechnology({
          batchId: Number(data.batchId),
          technologyId: Number(data.technologyId),
          totalRounds: data.totalRounds
        });
        toast.success('Configuration created');
      }
      setIsModalOpen(false);
      reset();
      setEditingConfig(null);
      fetchData();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Action failed');
    }
  };

  const handleDelete = async () => {
    if (selectedId === null) return;
    try {
      await deleteBatchTechnology(selectedId);
      toast.success('Configuration deleted');
      fetchData();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to delete');
    }
  };

  const openEditModal = (config: BatchTechnology) => {
    setEditingConfig(config);
    reset({
      batchId: String(config.batchId),
      technologyId: String(config.technologyId),
      totalRounds: config.totalRounds
    });
    setIsModalOpen(true);
  };

  return (
    <div className="space-y-8">
      <header className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl sm:text-4xl font-serif">Rounds Configuration</h1>
          <p className="text-chrome/60 mt-2">Define how many evaluation rounds each technology requires per batch.</p>
        </div>
        <button 
          onClick={() => {
            setEditingConfig(null);
            reset({ totalRounds: 3 });
            setIsModalOpen(true);
          }}
          className="btn-primary w-full sm:w-auto"
        >
          <Plus size={20} />
          New Configuration
        </button>
      </header>

      {loading ? (
        <div className="p-12 text-center text-chrome/40">Loading configurations...</div>
      ) : configs.length === 0 ? (
        <div className="card text-center p-20 border-dashed border-2">
          <div className="bg-surface p-4 rounded-full w-fit mx-auto mb-4">
            <Layers size={32} className="text-chrome/40" />
          </div>
          <h3 className="text-lg font-serif">No configurations yet</h3>
          <p className="text-chrome/60 mb-6">Start by linking a batch to a technology and setting round limits.</p>
          <button 
            onClick={() => setIsModalOpen(true)}
            className="btn-secondary mx-auto w-full sm:w-auto"
          >
            Create first config
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {configs.map((config) => (
            <div key={config.id} className="card hover:border-primary transition-colors group">
              <div className="flex justify-between items-start mb-6">
                <div className="p-3 bg-primary/10 text-primary rounded-lg">
                  <Settings size={20} />
                </div>
                <div className="flex gap-1 opacity-100 sm:opacity-0 group-hover:opacity-100 transition-opacity">
                  <button 
                    onClick={() => openEditModal(config)}
                    className="p-2 hover:bg-canvas rounded text-chrome/60"
                  >
                    <Edit2 size={16} />
                  </button>
                  <button 
                    onClick={() => {
                      setSelectedId(config.id!);
                      setIsDeleteModalOpen(true);
                    }}
                    className="p-2 hover:bg-status-error/10 rounded text-status-error"
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
              <h3 className="text-sm font-semibold uppercase tracking-wider text-chrome/40 mb-1">{config.batchName}</h3>
              <p className="text-xl font-serif mb-4">{config.technologyName}</p>
              <div className="flex items-center justify-between pt-4 border-t border-surface-dim">
                <span className="text-sm text-chrome/60">Total Rounds</span>
                <span className="px-3 py-1 bg-chrome text-white text-xs font-bold rounded-full">
                  {config.totalRounds}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Configuration Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-chrome/40 backdrop-blur-sm">
          <div className="bg-white w-full sm:max-w-md rounded-t-2xl sm:rounded-lg shadow-xl border border-surface-dim overflow-hidden max-h-[90vh] flex flex-col">
            <div className="p-6 border-b border-surface-dim flex justify-between items-center">
              <h3 className="text-xl font-serif">{editingConfig ? 'Edit Configuration' : 'New Configuration'}</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-chrome/40 hover:text-chrome">
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-5 overflow-y-auto">
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Batch</label>
                <select 
                  {...register('batchId')} 
                  disabled={!!editingConfig}
                  className="input-field appearance-none bg-[url('data:image/svg+xml;charset=utf-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20fill%3D%22none%22%20viewBox%3D%220%200%2020%2020%22%3E%3Cpath%20stroke%3D%22%236b7280%22%20stroke-linecap%3D%22round%22%20stroke-linejoin%3D%22round%22%20stroke-width%3D%221.5%22%20d%3D%22M6%208l4%204%204-4%22%2F%3E%3C%2Fsvg%3E')] bg-[position:right_0.5rem_center] bg-[length:1.5em_1.5em] bg-no-repeat pr-10"
                >
                  <option value="">Select a batch</option>
                  {batches.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                </select>
                {errors.batchId && <p className="text-status-error text-xs mt-1">{errors.batchId.message}</p>}
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Technology</label>
                <select 
                  {...register('technologyId')} 
                  disabled={!!editingConfig}
                  className="input-field appearance-none bg-[url('data:image/svg+xml;charset=utf-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20fill%3D%22none%22%20viewBox%3D%220%200%2020%2020%22%3E%3Cpath%20stroke%3D%22%236b7280%22%20stroke-linecap%3D%22round%22%20stroke-linejoin%3D%22round%22%20stroke-width%3D%221.5%22%20d%3D%22M6%208l4%204%204-4%22%2F%3E%3C%2Fsvg%3E')] bg-[position:right_0.5rem_center] bg-[length:1.5em_1.5em] bg-no-repeat pr-10"
                >
                  <option value="">Select a technology</option>
                  {technologies.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
                </select>
                {errors.technologyId && <p className="text-status-error text-xs mt-1">{errors.technologyId.message}</p>}
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Number of Rounds</label>
                <input
                  type="number"
                  {...register('totalRounds')}
                  className="input-field"
                  min="1"
                  max="10"
                />
                {errors.totalRounds && <p className="text-status-error text-xs mt-1">{errors.totalRounds.message}</p>}
              </div>

              <div className="flex flex-col sm:flex-row gap-3 pt-4">
                <button 
                  type="button" 
                  onClick={() => setIsModalOpen(false)} 
                  className="btn-secondary w-full sm:flex-1"
                >
                  Cancel
                </button>
                <button 
                  type="submit" 
                  disabled={isSubmitting}
                  className="btn-primary w-full sm:flex-1"
                >
                  {isSubmitting ? 'Saving...' : editingConfig ? 'Update Config' : 'Create Config'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <ConfirmModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onConfirm={handleDelete}
        title="Remove Configuration?"
        message="This will unlink the technology from the batch. Existing assignments and results will not be deleted but may become inaccessible."
      />
    </div>
  );
};

// Simple X icon for modal
const X = ({ size, className }: any) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
    <line x1="18" y1="6" x2="6" y2="18"></line>
    <line x1="6" y1="6" x2="18" y2="18"></line>
  </svg>
);

export default RoundsConfig;


