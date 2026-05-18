import React, { useState, useEffect } from 'react';
import { Search, Filter, ClipboardList, CheckCircle2, ChevronRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { getMyAssignments, EvaluationAssignment } from '@/api/evaluationAssignmentApi';
import toast from 'react-hot-toast';

const MyAssignments = () => {
  const navigate = useNavigate();
  const [assignments, setAssignments] = useState<EvaluationAssignment[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    fetchAssignments();
  }, []);

  const fetchAssignments = async () => {
    try {
      const response = await getMyAssignments();
      setAssignments(response.data?.data || []);
    } catch (error: any) {
      toast.error('Failed to load assignments');
    } finally {
      setLoading(false);
    }
  };

  const filtered = assignments.filter(a => {
    // ✅ FIX: Exclude orphan assignments (enrollment was deleted) — they have no participant name.
    if (!a.participantName || a.participantName.trim() === '') return false;
    const query = searchTerm.toLowerCase();
    const matchesSearch =
      !query ||
      (a.participantName || '').toLowerCase().includes(query) ||
      (a.technologyName || '').toLowerCase().includes(query);
    const matchesStatus = statusFilter === 'ALL' || a.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-8">
      <header>
        <h1 className="text-4xl font-serif text-chrome">My Assignments</h1>
        <p className="text-chrome/60 mt-2">View and manage all your assigned participant evaluations.</p>
      </header>

      <div className="flex flex-col sm:flex-row gap-4">
        <div className="flex-1 relative">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-chrome/40" size={20} />
          <input 
            type="text" 
            placeholder="Search by participant or technology..." 
            className="input-field pl-12"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <select 
          className="btn-secondary w-full sm:w-auto"
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
        >
          <option value="ALL">All Status</option>
          <option value="PENDING">Pending</option>
          <option value="COMPLETED">Completed</option>
        </select>
      </div>

      <div className="space-y-4">
        {loading ? (
          <p className="text-center py-20 text-chrome/40">Loading your assignments...</p>
        ) : filtered.length === 0 ? (
          <div className="card text-center py-20 border-dashed">
            <ClipboardList size={40} className="text-chrome/20 mx-auto mb-4" />
            <p className="text-chrome/60 font-serif text-xl">No assignments found matching your criteria.</p>
          </div>
        ) : (
          filtered.map((as) => (
            <div key={as.id} className="card p-5 sm:p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-6 hover:border-primary transition-colors group">
              <div className="flex items-center gap-4 sm:gap-5 min-w-0">
                <div className={`p-3.5 sm:p-4 rounded-full flex-shrink-0 ${as.status === 'COMPLETED' ? 'bg-status-success/10 text-status-success' : 'bg-status-warning/10 text-status-warning'}`}>
                  {as.status === 'COMPLETED' ? <CheckCircle2 size={24} /> : <ClipboardList size={24} />}
                </div>
                <div className="min-w-0">
                  <h3 className="text-lg sm:text-xl font-serif text-chrome truncate">{as.participantName}</h3>
                  <p className="text-xs sm:text-sm font-semibold uppercase tracking-widest text-chrome/40 truncate">
                    {as.batchName} • {as.technologyName} • Round {as.roundNumber}
                  </p>
                </div>
              </div>
              
              <div className="flex items-center justify-between sm:justify-end gap-6 w-full sm:w-auto border-t sm:border-none pt-4 sm:pt-0">
                <div className="text-left sm:text-right">
                  <p className="text-[10px] font-bold uppercase tracking-wider text-chrome/40 mb-1">Status</p>
                  <span className={`text-xs font-bold uppercase tracking-wider ${as.status === 'COMPLETED' ? 'text-status-success' : 'text-status-warning'}`}>
                    {as.status}
                  </span>
                </div>
                <div className="flex items-center gap-3">
                  {as.status === 'PENDING' ? (
                    <button 
                      onClick={() => navigate(`/evaluator/evaluate/${as.id}`)}
                      className="btn-primary py-1.5 px-3 sm:px-4 text-xs sm:text-sm"
                    >
                      Start Evaluation
                    </button>
                  ) : (
                    <button 
                      onClick={() => navigate(`/evaluator/results`)}
                      className="btn-secondary py-1.5 px-3 sm:px-4 text-xs sm:text-sm"
                    >
                      View Result
                    </button>
                  )}
                  <ChevronRight size={20} className="text-chrome/20 group-hover:text-primary transition-colors hidden sm:block" />
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default MyAssignments;


