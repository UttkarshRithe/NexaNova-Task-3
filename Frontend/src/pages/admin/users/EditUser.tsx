import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { useNavigate, useParams } from 'react-router-dom';
import { getUserById, updateUser, resetPassword } from '@/api/userApi';
import toast from 'react-hot-toast';
import { ChevronLeft, KeyRound } from 'lucide-react';

const userSchema = z.object({
  name: z.string().min(3, 'Name must be at least 3 characters'),
  email: z.string().email('Invalid email address'),
  role: z.enum(['ADMIN', 'EVALUATOR']),
});

type UserForm = z.infer<typeof userSchema>;

const EditUser = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [newPassword, setNewPassword] = useState('');
  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm<UserForm>({
    resolver: zodResolver(userSchema),
  });

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await getUserById(Number(id));
        const user = response.data.data;
        reset({
          name: user.name,
          email: user.email,
          role: user.role,
        });
      } catch (error: any) {
        toast.error('Failed to load user data');
        navigate('/admin/users');
      } finally {
        setLoading(false);
      }
    };
    fetchUser();
  }, [id, reset, navigate]);

  const onSubmit = async (data: UserForm) => {
    try {
      await updateUser(Number(id), data);
      toast.success('User updated successfully');
      navigate('/admin/users');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to update user');
    }
  };

  const handleResetPassword = async () => {
    if (!newPassword || newPassword.length < 6) {
      toast.error('Password must be at least 6 characters');
      return;
    }
    try {
      await resetPassword(Number(id), newPassword);
      toast.success('Password reset successfully');
      setNewPassword('');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to reset password');
    }
  };

  if (loading) return <div className="p-10 text-center">Loading user details...</div>;

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
        <h1 className="text-4xl font-serif">Edit User</h1>
        <p className="text-chrome/60 mt-2">Update account details or reset credentials.</p>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div className="card">
          <h2 className="text-xl font-serif mb-6">Profile Details</h2>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Full Name</label>
              <input
                {...register('name')}
                type="text"
                className="input-field"
              />
              {errors.name && <p className="text-status-error text-xs mt-1">{errors.name.message}</p>}
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Email Address</label>
              <input
                {...register('email')}
                type="email"
                className="input-field"
              />
              {errors.email && <p className="text-status-error text-xs mt-1">{errors.email.message}</p>}
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Role</label>
              <select {...register('role')} className="input-field">
                <option value="EVALUATOR">Evaluator</option>
                <option value="ADMIN">Administrator</option>
              </select>
              {errors.role && <p className="text-status-error text-xs mt-1">{errors.role.message}</p>}
            </div>

            <div className="flex justify-end gap-4 pt-4">
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

        <div className="card h-fit">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-2 bg-chrome/5 rounded text-chrome/60">
              <KeyRound size={20} />
            </div>
            <h2 className="text-xl font-serif">Security</h2>
          </div>
          <p className="text-sm text-chrome/60 mb-6">Enter a new password to reset it for this user.</p>
          <div className="space-y-4">
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider mb-2">New Password</label>
              <input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                className="input-field"
                placeholder="••••••••"
              />
            </div>
            <button 
              onClick={handleResetPassword}
              className="btn-secondary w-full"
              disabled={!newPassword || newPassword.length < 6}
            >
              Reset Password
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EditUser;


