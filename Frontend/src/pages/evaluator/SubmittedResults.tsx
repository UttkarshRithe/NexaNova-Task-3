import React, { useState, useEffect } from 'react';
import { Search, CheckCircle2, Star, Calendar, MessageSquare } from 'lucide-react';
import { getResultByAssignment } from '@/api/evaluationResultApi';
import { getMyAssignments } from '@/api/evaluationAssignmentApi';
import toast from 'react-hot-toast';
import { formatDate } from '@/utils/formatDate';

const SubmittedResults = () => {
  const [results, setResults] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchResults();
  }, []);

  const fetchResults = async () => {
    try {
      const assignmentRes = await getMyAssignments();

      const assignments = assignmentRes.data?.data || [];
      const completedAssignments = assignments.filter((a: any) => a.status === 'COMPLETED');

      const resultPairs = await Promise.all(
        completedAssignments.map(async (assignment: any) => {
          try {
            const res = await getResultByAssignment(assignment.id);
            return { assignment, result: res.data?.data };
          } catch {
            return null;
          }
        })
      );

      const merged = resultPairs
        .filter((pair): pair is { assignment: any; result: any } => Boolean(pair && pair.result))
        .map(({ assignment, result }) => {
          return {
            id: result.id,
            score: result.score,
            comments: result.aiFeedback || result.strengths || result.comments || 'No feedback provided.',
            evaluationDate: result.submittedAt,
            participantName: assignment.participantName || `Enrollment #${assignment.enrollmentId}`,
            batchName: assignment.batchName || '',
            technologyName: assignment.technologyName || '',
            roundNumber: assignment.roundNumber,
          };
        });

      setResults(merged);
    } catch (error: any) {
      toast.error('Failed to load submitted results');
    } finally {
      setLoading(false);
    }
  };

  const filtered = results.filter(r => 
    (r.participantName || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
    (r.batchName || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
    (r.technologyName || '').toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="space-y-8">
      <header>
        <h1 className="text-4xl font-serif text-chrome">Submitted Results</h1>
        <p className="text-chrome/60 mt-2">Historical record of evaluations you have completed.</p>
      </header>

      <div className="relative">
        <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-chrome/40" size={20} />
        <input 
          type="text" 
          placeholder="Search by participant or technology..." 
          className="input-field pl-12"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      <div className="space-y-6">
        {loading ? (
          <p className="text-center py-20 text-chrome/40">Loading history...</p>
        ) : filtered.length === 0 ? (
          <div className="card text-center py-20 border-dashed">
            <CheckCircle2 size={40} className="text-chrome/20 mx-auto mb-4" />
            <p className="text-chrome/60 font-serif text-xl">No completed evaluations found.</p>
          </div>
        ) : (
          filtered.map((result) => (
            <div key={result.id} className="card p-8 group">
              <div className="flex flex-col md:flex-row justify-between gap-8">
                <div className="flex-1 space-y-4">
                  <div className="flex items-center gap-4">
                    <div className="p-3 bg-status-success/10 text-status-success rounded-lg">
                      <CheckCircle2 size={24} />
                    </div>
                    <div>
                      <h3 className="text-2xl font-serif text-chrome">{result.participantName}</h3>
                      <div className="flex items-center gap-4 mt-1">
                        <p className="text-xs font-bold uppercase tracking-widest text-primary">
                          {result.batchName || 'Batch N/A'} • {result.technologyName || 'Technology N/A'} • Round {result.roundNumber}
                        </p>
                        <div className="flex items-center gap-1.5 text-xs text-chrome/40">
                          <Calendar size={14} />
                          {formatDate(result.evaluationDate)}
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="bg-canvas p-6 rounded-lg border border-surface-dim relative">
                    <div className="absolute top-4 right-4 text-chrome/10">
                      <MessageSquare size={32} />
                    </div>
                    <p className="text-chrome/70 italic text-sm leading-relaxed">
                      "{result.comments}"
                    </p>
                  </div>
                </div>

                <div className="md:w-48 flex flex-col items-center justify-center border-l border-surface-dim pl-8">
                  <p className="text-[10px] font-bold uppercase tracking-wider text-chrome/40 mb-2">Final Score</p>
                  <div className="relative">
                    <div className="text-5xl font-serif text-chrome">{result.score}</div>
                    <Star size={16} className="text-primary absolute -top-1 -right-4 fill-primary" />
                  </div>
                  <div className="mt-4 px-4 py-1 bg-surface-container rounded-full text-[10px] font-bold uppercase tracking-widest text-chrome/60">
                    Out of 10
                  </div>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default SubmittedResults;


