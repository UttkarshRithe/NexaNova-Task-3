import api from './axiosInstance';

export interface Batch {
  id?: number;
  name: string;
  startDate: string;
  endDate: string;
}

export const getBatches = (page: number = 0, size: number = 20) => 
  api.get(`/api/batches?page=${page}&size=${size}`);

export const getBatchById = (id: number) => 
  api.get(`/api/batches/${id}`);

export const createBatch = (batch: Batch) => 
  api.post('/api/batches', batch);

export const updateBatch = (id: number, batch: Batch) => 
  api.put(`/api/batches/${id}`, batch);

export const deleteBatch = (id: number) => 
  api.delete(`/api/batches/${id}`);
