import api from './axiosInstance';

export interface Enrollment {
  id?: number;
  participantId: number;
  batchTechnologyId: number;
  participantName?: string;
  batchName?: string;
  technologyName?: string;
}

export const getEnrollments = (page: number = 0, size: number = 20) => 
  api.get(`/api/enrollments?page=${page}&size=${size}`);

export const createEnrollment = (enrollment: Enrollment) => 
  api.post('/api/enrollments', enrollment);

export const deleteEnrollment = (id: number) => 
  api.delete(`/api/enrollments/${id}`);

export const getEnrollmentsByBatchTechnology = (btId: number) => 
  api.get(`/api/enrollments/batch-technology/${btId}`);
