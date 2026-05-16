import api from './axiosInstance';

export interface Technology {
  id?: number;
  name: string;
}

export const getTechnologies = () => 
  api.get('/api/technologies');

export const createTechnology = (technology: Technology) => 
  api.post('/api/technologies', technology);

export const updateTechnology = (id: number, technology: Technology) => 
  api.put(`/api/technologies/${id}`, technology);

export const deleteTechnology = (id: number) => 
  api.delete(`/api/technologies/${id}`);
