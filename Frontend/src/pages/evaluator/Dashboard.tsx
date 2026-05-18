import React, { useState, useEffect } from 'react';
import { 
  ClipboardList, 
  CheckCircle2, 
  Clock, 
  ArrowRight 
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { getMyAssignments, EvaluationAssignment } from '@/api/evaluationAssignmentApi';
import { useAuth } from '@/context/AuthContext';
import toast from 'react-hot-toast';

const StatCard = ({ icon: Icon, label, value, color }: any) => (
  <div className="card flex items-start justify-between">
    <div>
      <p className="text-xs font-semibold uppercase tracking-wider text-chrome/60 mb-1">{label}</p>
      <p className="text-3xl font-serif">{value}</p>
    </div>
    <div className={`p-3 rounded-lg ${color}`}>
      <Icon size={24} />
    </div>
  </div>
);

const EvaluatorDashboard = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [assignments, setAssignments] = useState<EvaluationAssignment[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAssignments();
  }, []);

  const fetchAssignments = async () => {
    try {
      const response = await getMyAssignments();
      const allAssignments = response.data?.data || [];
      // ✅ FIX: Filter out orphan assignments (where enrollment was deleted and name is null/empty)
      const validAssignments = allAssignments.filter(
        (a: EvaluationAssignment) => a.participantName && a.participantName.trim() !== ''
      );
      setAssignments(validAssignments);
    } catch (error: any) {
      toast.error('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const pending = assignments.filter(a => a.status === 'PENDING');
  const completed = assignments.filter(a => a.status === 'COMPLETED');

  return (
    <div className="space-y-10">
      <header>
        <h1 className="text-4xl font-serif text-chrome">Evaluator Dashboard</h1>
        <p className="text-chrome/60 mt-2 text-chrome">Welcome, {user?.name}. You have {pending.length} pending evaluations.</p>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <StatCard 
          icon={ClipboardList} 
          label="Total Assigned" 
          value={assignments.length} 
          color="bg-primary/10 text-primary" 
        />
        <StatCard 
          icon={Clock} 
          label="Pending" 
          value={pending.length} 
          color="bg-status-warning/10 text-status-warning" 
        />
        <StatCard 
          icon={CheckCircle2} 
          label="Completed" 
          value={completed.length} 
          color="bg-status-success/10 text-status-success" 
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-1 gap-8">
        <div className="card">
          <div className="flex items-center justify-between mb-8">
            <h2 className="text-2xl font-serif text-chrome">Recent Assignments</h2>
            <button 
              onClick={() => navigate('/evaluator/assignments')}
              className="text-primary text-sm font-semibold uppercase tracking-wider flex items-center gap-2 hover:gap-3 transition-all"
            >
              View All <ArrowRight size={16} />
            </button>
          </div>

          {loading ? (
            <p className="text-center py-10 text-chrome/40">Loading assignments...</p>
          ) : pending.length === 0 ? (
            <div className="text-center py-20 bg-canvas rounded-lg border border-dashed border-surface-dim">
              <CheckCircle2 size={40} className="text-status-success mx-auto mb-4" />
              <h3 className="text-xl font-serif text-chrome">All caught up!</h3>
              <p className="text-sm text-chrome/60">No pending evaluations at the moment.</p>
            </div>
          ) : (
            <div className="space-y-4">
              {pending.slice(0, 5).map((as) => (
                <div key={as.id} className="flex flex-col sm:flex-row sm:items-center justify-between p-5 rounded-lg border border-surface-dim hover:border-primary transition-all bg-white group gap-4">
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 rounded bg-surface-container flex items-center justify-center font-serif text-xl font-bold text-chrome/40 flex-shrink-0">
                      {as.participantName?.[0]}
                    </div>
                    <div className="min-w-0">
                      <p className="font-medium text-lg text-chrome truncate">{as.participantName}</p>
                      <p className="text-xs text-chrome/40 uppercase tracking-widest font-semibold truncate">
                        {as.batchName} • {as.technologyName} • Round {as.roundNumber}
                      </p>
                    </div>
                  </div>
                  <button 
                    onClick={() => navigate(`/evaluator/evaluate/${as.id}`)}
                    className="btn-primary w-full sm:w-auto opacity-100 lg:opacity-0 lg:group-hover:opacity-100 transition-opacity"
                  >
                    Start Evaluation
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default EvaluatorDashboard;


