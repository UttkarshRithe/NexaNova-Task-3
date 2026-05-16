import api from './axiosInstance';

export interface EvaluationAssignment {
  id?: number;
  enrollmentId: number;
  evaluatorId: number;
  roundNumber: number;
  status?: string;
  participantName?: string;
  evaluatorName?: string;
  batchName?: string;
  technologyName?: string;
}

export const getAssignments = (page: number = 0, size: number = 20) => 
  api.get(`/api/evaluation-assignments?page=${page}&size=${size}`);

export const getMyAssignments = () => 
  api.get('/api/evaluation-assignments/my');

export const createAssignment = (assignment: EvaluationAssignment) => 
  api.post('/api/evaluation-assignments', assignment);

export const updateAssignmentStatus = (id: number, status: string) => 
  api.patch(`/api/evaluation-assignments/${id}/status?status=${status}`);

export const deleteAssignment = (id: number) => 
  api.delete(`/api/evaluation-assignments/${id}`);

export const getAssignmentById = (id: number) => 
  api.get(`/api/evaluation-assignments/${id}`);
