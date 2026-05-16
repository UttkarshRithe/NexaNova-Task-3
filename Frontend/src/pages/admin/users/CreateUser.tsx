import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { useNavigate } from 'react-router-dom';
import { createUser } from '@/api/userApi';
import toast from 'react-hot-toast';
import { ChevronLeft } from 'lucide-react';

const userSchema = z.object({
  name: z.string().min(3, 'Name must be at least 3 characters'),
  email: z.string().email('Invalid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  role: z.enum(['ADMIN', 'EVALUATOR'], {
    errorMap: () => ({ message: 'Please select a role' }),
  }),
});

type UserForm = z.infer<typeof userSchema>;

const CreateUser = () => {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<UserForm>({
    resolver: zodResolver(userSchema),
    defaultValues: { role: 'EVALUATOR' }
  });

  const onSubmit = async (data: UserForm) => {
    try {
      await createUser(data);
      toast.success('User created successfully');
      navigate('/admin/users');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to create user');
    }
  };

  return (
    <div className="space-y-8">
      <header>
        <button 
          onClick={() => navigate('/admin/users')}
          className="flex items-center gap-2 text-chrome/60 hover:text-chrome transition-colors mb-4"
        >
          <ChevronLeft size={16} />
          Back to Users
        </button>
        <h1 className="text-4xl font-serif">Create New User</h1>
        <p className="text-chrome/60 mt-2">Add a new administrator or evaluator to the platform.</p>
      </header>

      <div className="card max-w-2xl">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Full Name</label>
              <input
                {...register('name')}
                type="text"
                className="input-field"
                placeholder="John Doe"
              />
              {errors.name && <p className="text-status-error text-xs mt-1">{errors.name.message}</p>}
            </div>
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Role</label>
              <select 
                {...register('role')}
                className="input-field"
              >
                <option value="EVALUATOR">Evaluator</option>
                <option value="ADMIN">Administrator</option>
              </select>
              {errors.role && <p className="text-status-error text-xs mt-1">{errors.role.message}</p>}
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Email Address</label>
            <input
              {...register('email')}
              type="email"
              className="input-field"
              placeholder="john@company.com"
            />
            {errors.email && <p className="text-status-error text-xs mt-1">{errors.email.message}</p>}
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Initial Password</label>
            <input
              {...register('password')}
              type="password"
              className="input-field"
              placeholder="••••••••"
            />
            {errors.password && <p className="text-status-error text-xs mt-1">{errors.password.message}</p>}
          </div>

          <div className="flex justify-end gap-4 pt-4">
            <button 
              type="button"
              onClick={() => navigate('/admin/users')}
              className="btn-secondary"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="btn-primary min-w-[120px]"
            >
              {isSubmitting ? 'Creating...' : 'Create User'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateUser;


