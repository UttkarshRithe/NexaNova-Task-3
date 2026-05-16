import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { useNavigate } from 'react-router-dom';
import { createBatch } from '@/api/batchApi';
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

const CreateBatch = () => {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<BatchForm>({
    resolver: zodResolver(batchSchema),
  });

  const onSubmit = async (data: BatchForm) => {
    try {
      await createBatch(data);
      toast.success('Batch created successfully');
      navigate('/admin/batches');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to create batch');
    }
  };

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
        <h1 className="text-4xl font-serif">Create New Batch</h1>
        <p className="text-chrome/60 mt-2">Initialize a new evaluation program for candidates.</p>
      </header>

      <div className="card max-w-2xl">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Batch Name</label>
            <input
              {...register('name')}
              type="text"
              className="input-field"
              placeholder="e.g. 2024 Graduate Program - Summer Intake"
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
              {isSubmitting ? 'Creating...' : 'Create Batch'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateBatch;


