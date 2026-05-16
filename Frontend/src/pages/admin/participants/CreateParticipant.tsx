import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { useNavigate } from 'react-router-dom';
import { createParticipant } from '@/api/participantApi';
import toast from 'react-hot-toast';
import { ChevronLeft } from 'lucide-react';

const participantSchema = z.object({
  name: z.string().min(3, 'Name must be at least 3 characters'),
  email: z.string().email('Invalid email address'),
});

type ParticipantForm = z.infer<typeof participantSchema>;

const CreateParticipant = () => {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<ParticipantForm>({
    resolver: zodResolver(participantSchema),
  });

  const onSubmit = async (data: ParticipantForm) => {
    try {
      await createParticipant(data);
      toast.success('Participant added successfully');
      navigate('/admin/participants');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to add participant');
    }
  };

  return (
    <div className="space-y-8">
      <header>
        <button 
          onClick={() => navigate('/admin/participants')}
          className="flex items-center gap-2 text-chrome/60 hover:text-chrome transition-colors mb-4"
        >
          <ChevronLeft size={16} />
          Back to Participants
        </button>
        <h1 className="text-4xl font-serif">Add New Participant</h1>
        <p className="text-chrome/60 mt-2">Register a candidate for evaluation programs.</p>
      </header>

      <div className="card max-w-2xl">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Full Name</label>
            <input
              {...register('name')}
              type="text"
              className="input-field"
              placeholder="Alice Johnson"
            />
            {errors.name && <p className="text-status-error text-xs mt-1">{errors.name.message}</p>}
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Email Address</label>
            <input
              {...register('email')}
              type="email"
              className="input-field"
              placeholder="alice@example.com"
            />
            {errors.email && <p className="text-status-error text-xs mt-1">{errors.email.message}</p>}
          </div>

          <div className="flex justify-end gap-4 pt-4">
            <button 
              type="button"
              onClick={() => navigate('/admin/participants')}
              className="btn-secondary"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="btn-primary min-w-[120px]"
            >
              {isSubmitting ? 'Adding...' : 'Add Participant'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateParticipant;


