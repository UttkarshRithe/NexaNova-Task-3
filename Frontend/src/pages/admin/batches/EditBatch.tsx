import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { useNavigate, useParams } from 'react-router-dom';
import { getBatchById, updateBatch } from '@/api/batchApi';
import toast from 'react-hot-toast';
import { ChevronLeft } from 'lucide-react';

const batchSchema = z.object({
  name: z.string().min(3, 'Batch name must be at least 3 characters'),
  startDate: z.string().min(1, 'Start date is required'),
  endDate: z.string().min(1, 'End date is required'),
}).refine((data) => new Date(data.startDate) < new Date(data.endDate), {
  message: "End date must be after start date",
  path: ["endDate"],
});

type BatchForm = z.infer<typeof batchSchema>;

const EditBatch = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm<BatchForm>({
    resolver: zodResolver(batchSchema),
  });

  useEffect(() => {
    const fetchBatch = async () => {
      try {
        const response = await getBatchById(Number(id));
        const batch = response.data.data;
        // Format dates for input type="date" (YYYY-MM-DD)
        reset({
          name: batch.name,
          startDate: batch.startDate.split('T')[0],
          endDate: batch.endDate.split('T')[0],
        });
      } catch (error: any) {
        toast.error('Failed to load batch data');
        navigate('/admin/batches');
      } finally {
        setLoading(false);
      }
    };
    fetchBatch();
  }, [id, reset, navigate]);

  const onSubmit = async (data: BatchForm) => {
    try {
      await updateBatch(Number(id), data);
      toast.success('Batch updated successfully');
      navigate('/admin/batches');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to update batch');
    }
  };

  if (loading) return <div className="p-10 text-center">Loading batch details...</div>;

  return (
    <div className="space-y-8">
      <header>
        <button 
          onClick={() => navigate('/admin/batches')}
          className="flex items-center gap-2 text-chrome/60 hover:text-chrome transition-colors mb-4"
        >
          <ChevronLeft size={16} />
          Back to Batches
        </button>
        <h1 className="text-4xl font-serif">Edit Batch</h1>
        <p className="text-chrome/60 mt-2">Update the configuration for this evaluation batch.</p>
      </header>

      <div className="card max-w-2xl">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Batch Name</label>
            <input
              {...register('name')}
              type="text"
              className="input-field"
            />
            {errors.name && <p className="text-status-error text-xs mt-1">{errors.name.message}</p>}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Start Date</label>
              <input
                {...register('startDate')}
                type="date"
                className="input-field"
              />
              {errors.startDate && <p className="text-status-error text-xs mt-1">{errors.startDate.message}</p>}
            </div>
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider mb-2">End Date</label>
              <input
                {...register('endDate')}
                type="date"
                className="input-field"
              />
              {errors.endDate && <p className="text-status-error text-xs mt-1">{errors.endDate.message}</p>}
            </div>
          </div>

          <div className="flex justify-end gap-4 pt-4">
            <button 
              type="button"
              onClick={() => navigate('/admin/batches')}
              className="btn-secondary"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="btn-primary min-w-[120px]"
            >
              {isSubmitting ? 'Saving...' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditBatch;


