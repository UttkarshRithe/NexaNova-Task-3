import React, { useState, useEffect } from 'react';
import { Plus, Search, Filter, Edit2, Trash2, Calendar } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { getBatches, deleteBatch, Batch } from '@/api/batchApi';
import toast from 'react-hot-toast';
import ConfirmModal from '@/components/ui/ConfirmModal';
import { formatDate } from '@/utils/formatDate';

const BatchList = () => {
  const navigate = useNavigate();
  const [batches, setBatches] = useState<Batch[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedBatchId, setSelectedBatchId] = useState<number | null>(null);

  useEffect(() => {
    fetchBatches();
  }, [page]);

  const fetchBatches = async () => {
    setLoading(true);
    try {
      const response = await getBatches(page, 10);
      const { content, totalElements } = response.data.data;
      setBatches(content);
      setTotalElements(totalElements);
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to fetch batches');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (selectedBatchId === null) return;
    try {
      await deleteBatch(selectedBatchId);
      toast.success('Batch deleted successfully');
      fetchBatches();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to delete batch');
    }
  };

  const openDeleteModal = (id: number) => {
    setSelectedBatchId(id);
    setIsDeleteModalOpen(true);
  };

  return (
    <div className="space-y-8">
      <header className="flex items-center justify-between">
        <div>
          <h1 className="text-4xl font-serif">Batches</h1>
          <nav className="text-xs font-semibold uppercase tracking-wider text-chrome/40 mt-2">
            Admin / Batches
          </nav>
        </div>
        <button 
          onClick={() => navigate('/admin/batches/create')}
          className="btn-primary"
        >
          <Plus size={20} />
          Create Batch
        </button>
      </header>

      <div className="flex gap-4">
        <div className="flex-1 relative">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-chrome/40" size={20} />
          <input 
            type="text" 
            placeholder="Search batches..." 
            className="input-field pl-12"
          />
        </div>
        <button className="btn-secondary">
          <Filter size={20} />
          Filters
        </button>
      </div>

      <div className="table-container">
        {loading ? (
          <div className="p-12 text-center text-chrome/40">
            <p>Loading batches...</p>
          </div>
        ) : batches.length === 0 ? (
          <div className="p-12 text-center text-chrome/40">
            <p>No batches found. Create one to get started.</p>
          </div>
        ) : (
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="table-header">
                <th className="px-6 py-4">Batch Name</th>
                <th className="px-6 py-4">Start Date</th>
                <th className="px-6 py-4">End Date</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {batches.map((batch) => (
                <tr key={batch.id} className="table-row">
                  <td className="table-cell font-medium">{batch.name}</td>
                  <td className="table-cell text-chrome/60">
                    <div className="flex items-center gap-2">
                      <Calendar size={14} />
                      {formatDate(batch.startDate)}
                    </div>
                  </td>
                  <td className="table-cell text-chrome/60">
                    <div className="flex items-center gap-2">
                      <Calendar size={14} />
                      {formatDate(batch.endDate)}
                    </div>
                  </td>
                  <td className="table-cell text-right">
                    <div className="flex justify-end gap-2">
                      <button 
                        onClick={() => navigate(`/admin/batches/${batch.id}/edit`)}
                        className="p-2 hover:bg-surface rounded transition-colors text-chrome/60"
                      >
                        <Edit2 size={16} />
                      </button>
                      <button 
                        onClick={() => openDeleteModal(batch.id!)}
                        className="p-2 hover:bg-status-error/10 rounded transition-colors text-status-error"
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div className="flex items-center justify-between">
        <p className="text-sm text-chrome/60">
          Showing {batches.length} of {totalElements} results
        </p>
        <div className="flex gap-2">
          <button 
            className="btn-secondary py-1 text-xs px-3" 
            disabled={page === 0}
            onClick={() => setPage(page - 1)}
          >
            Previous
          </button>
          <button 
            className="btn-secondary py-1 text-xs px-3"
            disabled={(page + 1) * 10 >= totalElements}
            onClick={() => setPage(page + 1)}
          >
            Next
          </button>
        </div>
      </div>

      <ConfirmModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onConfirm={handleDelete}
        title="Delete Batch?"
        message="This will also delete all related rounds and enrollments. This action cannot be undone."
      />
    </div>
  );
};

export default BatchList;


