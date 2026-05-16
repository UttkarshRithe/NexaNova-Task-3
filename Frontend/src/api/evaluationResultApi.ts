import api from './axiosInstance';

export interface EvaluationResult {
  assignmentId: number;
  score: number;
  comments: string;
  technicalScore?: number;
  communicationScore?: number;
  problemSolvingScore?: number;
  strengths?: string;
  weaknesses?: string;
  aiFeedback?: string;
}

export const submitResult = (result: EvaluationResult) => 
  api.post('/api/evaluation-results', result);

export const getMyResults = () => 
  api.get('/api/evaluation-results/my');

export const getResultByAssignment = (assignmentId: number) => 
  api.get(`/api/evaluation-results/assignment/${assignmentId}`);
