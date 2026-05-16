import React, { useState, useEffect } from 'react';
import { FileSpreadsheet, Download, Search, History, Mail } from 'lucide-react';
import { getBatches, Batch } from '@/api/batchApi';
import { getParticipants, Participant } from '@/api/participantApi';
import { getEnrollments } from '@/api/enrollmentApi';
import { BatchAnalysisResponse, downloadBatchReport, downloadParticipantReport, getBatchAiAnalysis, downloadBatchAiAnalysisReport, emailParticipantReport } from '@/api/reportApi';
import toast from 'react-hot-toast';

const Reports = () => {
  const [batches, setBatches] = useState<Batch[]>([]);
  const [participants, setParticipants] = useState<Participant[]>([]);
  const [loading, setLoading] = useState(true);
  const [batchSearch, setBatchSearch] = useState('');
  const [participantSearch, setParticipantSearch] = useState('');
  const [downloading, setDownloading] = useState<string | null>(null);
  const [analysisModal, setAnalysisModal] = useState(false);
  const [analysisData, setAnalysisData] = useState<BatchAnalysisResponse | null>(null);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [selectedBatchName, setSelectedBatchName] = useState('');
  const [emailModal, setEmailModal] = useState(false);
  const [selectedParticipant, setSelectedParticipant] = useState<Participant | null>(null);
  const [isEmailing, setIsEmailing] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [bRes, pRes, eRes] = await Promise.all([
        getBatches(0, 100),
        getParticipants(0, 500),
        getEnrollments(0, 1000),
      ]);
      
      const enrollmentRows = Array.isArray(eRes.data?.data) ? eRes.data.data : (eRes.data?.data?.content || []);
      const enrolledParticipantIds = new Set<number>(enrollmentRows.map((en: any) => en.participantId));
      const participantRows = pRes.data.data.content || [];

      setBatches(bRes.data.data.content);
      setParticipants(participantRows.filter((p: Participant) => enrolledParticipantIds.has(p.id!)));
    } catch (error: any) {
      toast.error('Failed to load report targets');
    } finally {
      setLoading(false);
    }
  };

  const handleBatchDownload = async (batch: Batch) => {
    setDownloading(`batch-${batch.id}`);
    try {
      await downloadBatchReport(batch.id!, batch.name, 'pdf');
      toast.success('Batch report downloaded');
    } catch (error: any) {
      toast.error('Failed to download batch report');
    } finally {
      setDownloading(null);
    }
  };

  const handleParticipantDownload = async (p: Participant) => {
    setDownloading(`participant-${p.id}`);
    try {
      await downloadParticipantReport(p.id!, p.name, 'pdf');
      toast.success('Individual report downloaded');
    } catch (error: any) {
      toast.error('Failed to download individual report');
    } finally {
      setDownloading(null);
    }
  };

  const handleAnalyzeBatch = async (batchId: number, batchName: string) => {
    setIsAnalyzing(true);
    setAnalysisData(null);
    setSelectedBatchName(batchName);
    setAnalysisModal(true);
    try {
      const response = await getBatchAiAnalysis(batchId);
      setAnalysisData(response.data.data);
    } catch (error) {
      toast.error('Analysis failed. Please try again.');
      setAnalysisModal(false);
    } finally {
      setIsAnalyzing(false);
    }
  };

  const handleDownloadAnalysis = async (batchId: number, batchName: string) => {
    try {
      await downloadBatchAiAnalysisReport(batchId, batchName);
      toast.success('AI Analysis PDF downloaded');
    } catch (error) {
      toast.error('Failed to download AI Analysis PDF');
    }
  };

  const handleEmailReport = async () => {
    if (!selectedParticipant) return;
    setIsEmailing(true);
    try {
      await emailParticipantReport(selectedParticipant.id!);
      toast.success('Participant report email queued successfully');
      setEmailModal(false);
    } catch (error) {
      toast.error('Failed to queue report email');
    } finally {
      setIsEmailing(false);
    }
  };

  const filteredBatches = batches.filter(b => b.name.toLowerCase().includes(batchSearch.toLowerCase()));
  const filteredParticipants = participants.filter(p => p.name.toLowerCase().includes(participantSearch.toLowerCase()));

  return (
    <div className="space-y-10">
      <header>
        <h1 className="text-4xl font-serif">Reports & Analytics</h1>
        <p className="text-chrome/60 mt-2">Holistic AI-driven performance tracking across all programs.</p>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-10">
        {/* Batch Reports */}
        <div className="space-y-6">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-primary/10 text-primary rounded-lg">
              <FileSpreadsheet size={20} />
            </div>
            <h2 className="text-xl font-serif">Batch Reports</h2>
          </div>
          
          <div className="relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-chrome/40" size={18} />
            <input 
              type="text" 
              placeholder="Filter batches..." 
              className="input-field pl-12 py-2 text-sm"
              value={batchSearch}
              onChange={(e) => setBatchSearch(e.target.value)}
            />
          </div>

          <div className="table-container max-h-[500px] overflow-y-auto">
            <table className="w-full text-left border-collapse">
              <thead className="sticky top-0 z-10 bg-white">
                <tr className="table-header">
                  <th className="px-6 py-3">Batch Name</th>
                  <th className="px-6 py-3 text-right">Holistic AI</th>
                  <th className="px-6 py-3 text-right">PDF Report</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr><td colSpan={3} className="p-8 text-center text-chrome/40 text-sm">Loading...</td></tr>
                ) : filteredBatches.length === 0 ? (
                  <tr><td colSpan={3} className="p-8 text-center text-chrome/40 text-sm">No batches found</td></tr>
                ) : (
                  filteredBatches.map(batch => (
                    <tr key={batch.id} className="table-row">
                      <td className="table-cell py-3">
                        <span className="font-medium text-sm">{batch.name}</span>
                      </td>
                      <td className="table-cell py-3 text-right">
                        <button
                          onClick={() => handleAnalyzeBatch(batch.id!, batch.name)}
                          className="p-2 hover:bg-primary/5 text-primary rounded transition-colors"
                        >
                          🤖 Analyze
                        </button>
                      </td>
                      <td className="table-cell py-3 text-right">
                        <button 
                          onClick={() => handleBatchDownload(batch)}
                          disabled={!!downloading}
                          className="p-2 hover:bg-primary/5 text-primary rounded transition-colors"
                        >
                          {downloading === `batch-${batch.id}` ? (
                            <div className="w-4 h-4 border-2 border-primary border-t-transparent rounded-full animate-spin"></div>
                          ) : (
                            <Download size={18} />
                          )}
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Participant Reports */}
        <div className="space-y-6">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-chrome/10 text-chrome rounded-lg">
              <History size={20} />
            </div>
            <h2 className="text-xl font-serif">Participant Reports</h2>
          </div>

          <div className="relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-chrome/40" size={18} />
            <input 
              type="text" 
              placeholder="Filter participants..." 
              className="input-field pl-12 py-2 text-sm"
              value={participantSearch}
              onChange={(e) => setParticipantSearch(e.target.value)}
            />
          </div>

          <div className="table-container max-h-[500px] overflow-y-auto">
            <table className="w-full text-left border-collapse">
              <thead className="sticky top-0 z-10 bg-white">
                <tr className="table-header">
                  <th className="px-6 py-3">Participant Name</th>
                  <th className="px-6 py-3 text-right">Full Report</th>
                  <th className="px-6 py-3 text-right">Email Report</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr><td colSpan={3} className="p-8 text-center text-chrome/40 text-sm">Loading...</td></tr>
                ) : filteredParticipants.length === 0 ? (
                  <tr><td colSpan={3} className="p-8 text-center text-chrome/40 text-sm">No participants found</td></tr>
                ) : (
                  filteredParticipants.map(p => (
                    <tr key={p.id} className="table-row">
                      <td className="table-cell py-3 text-chrome">
                        <span className="font-medium text-sm">{p.name}</span>
                        <p className="text-[10px] text-chrome/40">{p.email}</p>
                      </td>
                      <td className="table-cell py-3 text-right">
                        <button 
                          onClick={() => handleParticipantDownload(p)}
                          disabled={!!downloading}
                          className="p-2 hover:bg-chrome/5 text-chrome rounded transition-colors"
                          title="Download PDF"
                        >
                          {downloading === `participant-${p.id}` ? (
                            <div className="w-4 h-4 border-2 border-chrome border-t-transparent rounded-full animate-spin"></div>
                          ) : (
                            <Download size={18} />
                          )}
                        </button>
                      </td>
                      <td className="table-cell py-3 text-right">
                        <button 
                          onClick={() => {
                            setSelectedParticipant(p);
                            setEmailModal(true);
                          }}
                          disabled={isEmailing}
                          className="p-2 hover:bg-primary/5 text-primary rounded transition-colors"
                          title="Send Report Email"
                        >
                          <Mail size={18} />
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {analysisModal && (
        <div className="fixed inset-0 z-50 bg-black/40 flex items-center justify-center p-4">
          <div className="bg-white w-full max-w-3xl rounded-xl shadow-xl p-6 max-h-[85vh] overflow-y-auto">
            <div className="flex justify-between items-start mb-4">
              <h2 className="text-2xl font-serif">🤖 Holistic AI Analysis — {selectedBatchName}</h2>
              <button 
                onClick={() => handleDownloadAnalysis(batches.find(b => b.name === selectedBatchName)?.id!, selectedBatchName)}
                className="flex items-center gap-2 px-4 py-2 bg-primary/10 text-primary rounded-lg hover:bg-primary/20 transition-colors text-sm font-medium"
              >
                <Download size={16} />
                Download PDF
              </button>
            </div>

            {isAnalyzing ? (
              <div className="text-chrome/60">Gathering cross-technology insights...</div>
            ) : analysisData && (
              <div className="space-y-4">
                <div className="inline-block px-3 py-1 rounded-full bg-primary/10 text-primary font-semibold">
                  Batch Health: {analysisData.overallHealth}
                </div>
                <div className="grid gap-2">
                  {analysisData.technologySummaries?.map((tech) => (
                    <div key={tech.name} className="border rounded-lg p-3 flex flex-wrap gap-3 text-sm">
                      <span className="font-semibold">{tech.name}</span>
                      <span>Avg: {tech.avgScore?.toFixed?.(2) ?? tech.avgScore}</span>
                      <span>{tech.status}</span>
                      <span>⚠️ {tech.atRiskCount} at-risk</span>
                      <span>📈 {tech.trend}</span>
                    </div>
                  ))}
                </div>
                <div>
                  <h4 className="font-semibold">📝 Holistic Analysis</h4>
                  <p className="text-sm leading-relaxed text-chrome/80">{analysisData.aiSummary}</p>
                </div>
                <div>
                  <h4 className="font-semibold">💡 Strategic Recommendation</h4>
                  <p className="text-sm leading-relaxed text-chrome/80">{analysisData.recommendation}</p>
                </div>
              </div>
            )}

            <div className="mt-6 text-right">
              <button className="btn-secondary" onClick={() => setAnalysisModal(false)}>Close</button>
            </div>
          </div>
        </div>
      )}

      {emailModal && (
        <div className="fixed inset-0 z-50 bg-black/40 flex items-center justify-center p-4">
          <div className="bg-white w-full max-w-md rounded-xl shadow-xl p-8">
            <h2 className="text-2xl font-serif mb-4">Send Participant Report?</h2>
            <p className="text-chrome/70 mb-8">
              Are you sure you want to email the evaluation report to <strong>{selectedParticipant?.name}</strong>?
            </p>
            <div className="flex justify-end gap-3">
              <button 
                className="btn-secondary" 
                onClick={() => setEmailModal(false)}
                disabled={isEmailing}
              >
                Cancel
              </button>
              <button 
                className="btn-primary flex items-center gap-2" 
                onClick={handleEmailReport}
                disabled={isEmailing}
              >
                {isEmailing ? (
                  <>
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                    Queuing...
                  </>
                ) : (
                  <>
                    <Mail size={18} />
                    Send Report
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Reports;
