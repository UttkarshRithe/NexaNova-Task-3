import api from './axiosInstance';

const triggerBlobDownload = (blob: BlobPart, filename: string) => {
  const url = window.URL.createObjectURL(new Blob([blob]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', filename);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};

export const downloadBatchReport = async (
  batchId: number,
  fileBaseName: string,
  format: 'csv' | 'pdf' = 'pdf'
) => {
  const response = await api.get(`/api/reports/batch/${batchId}/export?format=${format}`, {
    responseType: 'blob',
  });
  triggerBlobDownload(response.data, `${fileBaseName.replace(/\s+/g, '_')}_Batch_Report.${format}`);
};

export const downloadParticipantReport = async (
  participantId: number,
  participantName: string,
  format: 'csv' | 'pdf' = 'pdf'
) => {
  const response = await api.get(`/api/reports/participant/${participantId}/export?format=${format}`, {
    responseType: 'blob',
  });
  triggerBlobDownload(response.data, `${participantName.replace(/\s+/g, '_')}_Individual_Report.${format}`);
};

export const downloadBatchAiAnalysisReport = async (
  batchId: number,
  batchName: string
) => {
  const response = await api.get(`/api/reports/batch/${batchId}/ai-analysis/export`, {
    responseType: 'blob',
  });
  triggerBlobDownload(response.data, `${batchName.replace(/\s+/g, '_')}_Holistic_AI_Analysis.pdf`);
};

export interface BatchAnalysisResponse {
  overallHealth: string;
  aiSummary: string;
  recommendation: string;
  technologySummaries: {
    name: string;
    status: string;
    avgScore: number;
    atRiskCount: number;
    trend: string;
  }[];
}

export const getBatchAiAnalysis = (batchId: number) =>
  api.get(`/api/reports/batch/${batchId}/ai-analysis`);

export const emailParticipantReport = (participantId: number) =>
  api.post(`/api/reports/participant/${participantId}/email`);
