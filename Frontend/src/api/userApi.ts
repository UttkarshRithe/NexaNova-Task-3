import api from './axiosInstance';

export interface User {
  id?: number;
  name: string;
  email: string;
  password?: string;
  role: 'ADMIN' | 'EVALUATOR';
}

export const getUsers = (page: number = 0, size: number = 20) => 
  api.get(`/api/users?page=${page}&size=${size}`);

export const getEvaluators = () => 
  api.get('/api/users/evaluators');

export const getUserById = (id: number) => 
  api.get(`/api/users/${id}`);

export const createUser = (user: User) => 
  api.post('/api/users', user);

export const updateUser = (id: number, user: Partial<User>) => 
  api.put(`/api/users/${id}`, user);

export const deleteUser = (id: number) => 
  api.delete(`/api/users/${id}`);

export const resetPassword = (id: number, password: string) => 
  api.put(`/api/users/${id}/password`, { newPassword: password });
