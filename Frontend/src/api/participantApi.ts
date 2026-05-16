import api from './axiosInstance';

export interface Participant {
  id?: number;
  name: string;
  email: string;
}

export const getParticipants = (page: number = 0, size: number = 20) => 
  api.get(`/api/participants?page=${page}&size=${size}`);

export const getAllParticipants = () => 
  api.get('/api/participants/all');

export const getParticipantById = (id: number) => 
  api.get(`/api/participants/${id}`);

export const createParticipant = (participant: Participant) => 
  api.post('/api/participants', participant);

export const updateParticipant = (id: number, participant: Participant) => 
  api.put(`/api/participants/${id}`, participant);

export const deleteParticipant = (id: number) => 
  api.delete(`/api/participants/${id}`);

export const getParticipantEnrollments = (id: number) => 
  api.get(`/api/enrollments/participant/${id}`);
