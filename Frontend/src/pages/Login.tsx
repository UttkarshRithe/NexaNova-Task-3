import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import api from '@/api/axiosInstance';
import toast from 'react-hot-toast';

const loginSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

type LoginForm = z.infer<typeof loginSchema>;

const Login = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginForm) => {
    try {
      const response = await api.post('/api/auth/login', data);
      const { token, role, name } = response.data.data;
      
      const user = { 
        name, 
        role, 
        email: data.email 
      };
      
      login(token, user);
      toast.success('Login successful');
      
      if (role === 'ADMIN') {
        navigate('/admin/dashboard');
      } else {
        navigate('/evaluator/dashboard');
      }
    } catch (error: any) {

      toast.error(error.response?.data?.message || 'Login failed');
    }
  };

  return (
    <div className="min-h-screen bg-canvas flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-white p-12 rounded-lg border border-surface-dim shadow-sm">
        <div className="text-center mb-10">
          <h1 className="text-4xl font-serif mb-2">EvalTrack</h1>
          <p className="text-chrome/60 font-sans">Mock Evaluation System</p>
        </div>
        
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Email Address</label>
            <input
              {...register('email')}
              type="email"
              className="input-field"
              placeholder="name@company.com"
            />
            {errors.email && <p className="text-status-error text-xs mt-1">{errors.email.message}</p>}
          </div>
          
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Password</label>
            <input
              {...register('password')}
              type="password"
              className="input-field"
              placeholder="••••••••"
            />
            {errors.password && <p className="text-status-error text-xs mt-1">{errors.password.message}</p>}
          </div>
          
          <button
            type="submit"
            disabled={isSubmitting}
            className="btn-primary w-full py-3"
          >
            {isSubmitting ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login;


