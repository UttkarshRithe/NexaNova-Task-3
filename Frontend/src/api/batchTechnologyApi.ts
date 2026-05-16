import api from './axiosInstance';

export interface BatchTechnology {
  id?: number;
  batchId: number;
  technologyId: number;
  totalRounds: number;
  batchName?: string;
  technologyName?: string;
}

export const getBatchTechnologies = () => 
  api.get('/api/batch-technology');

export const getBatchTechnologiesByBatch = (batchId: number) => 
  api.get(`/api/batch-technology/batch/${batchId}`);

export const createBatchTechnology = (bt: BatchTechnology) => 
  api.post('/api/batch-technology', bt);

export const updateBatchTechnology = (id: number, bt: Partial<BatchTechnology>) => 
  api.put(`/api/batch-technology/${id}`, bt);

export const deleteBatchTechnology = (id: number) => 
  api.delete(`/api/batch-technology/${id}`);
