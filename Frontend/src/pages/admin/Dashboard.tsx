import React, { useEffect, useMemo, useState } from 'react';
import { 
  Users, 
  Package, 
  GraduationCap, 
  CheckCircle2 
} from 'lucide-react';
import toast from 'react-hot-toast';
import { getBatches } from '@/api/batchApi';
import { getEvaluators } from '@/api/userApi';
import { getParticipants } from '@/api/participantApi';
import { getAssignments } from '@/api/evaluationAssignmentApi';
import { getBatchTechnologies } from '@/api/batchTechnologyApi';
import { getEnrollments } from '@/api/enrollmentApi';

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

type BatchItem = {
  id: number;
  name: string;
  startDate?: string;
};

type EnrollmentItem = {
  batchTechnologyId: number;
};

type BatchTechnologyItem = {
  id: number;
  batchId: number;
};

type AssignmentItem = {
  evaluatorId?: number;
  status?: string;
};

const parseCollection = (payload: any): any[] => {
  const data = payload?.data;
  if (Array.isArray(data)) {
    return data;
  }
  if (Array.isArray(data?.content)) {
    return data.content;
  }
  if (Array.isArray(payload?.content)) {
    return payload.content;
  }
  if (Array.isArray(payload)) {
    return payload;
  }
  return [];
};

const parseTotal = (payload: any, fallback: number): number => {
  const data = payload?.data;
  if (typeof data?.totalElements === 'number') {
    return data.totalElements;
  }
  if (typeof payload?.totalElements === 'number') {
    return payload.totalElements;
  }
  return fallback;
};

const formatDaysAgo = (startDate?: string): string => {
  if (!startDate) return 'Start date unavailable';
  const start = new Date(startDate);
  if (Number.isNaN(start.getTime())) return 'Start date unavailable';
  const diffMs = Date.now() - start.getTime();
  const days = Math.floor(diffMs / (1000 * 60 * 60 * 24));
  if (days <= 0) return 'Started today';
  if (days === 1) return 'Started 1 day ago';
  return `Started ${days} days ago`;
};

const AdminDashboard = () => {
  const [loading, setLoading] = useState(true);
  const [totalBatches, setTotalBatches] = useState(0);
  const [activeEvaluators, setActiveEvaluators] = useState(0);
  const [participants, setParticipants] = useState(0);
  const [evaluationsDone, setEvaluationsDone] = useState(0);
  const [recentBatches, setRecentBatches] = useState<Array<BatchItem & { participantCount: number }>>([]);
  const [completionRate, setCompletionRate] = useState(0);
  const [activityRate, setActivityRate] = useState(0);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const [
          batchesRes,
          evaluatorsRes,
          participantsRes,
          assignmentsRes,
          enrollmentsRes,
          batchTechRes,
        ] = await Promise.all([
          getBatches(0, 200),
          getEvaluators(),
          getParticipants(0, 2000),
          getAssignments(0, 5000),
          getEnrollments(),
          getBatchTechnologies(),
        ]);

        const batchList: BatchItem[] = parseCollection(batchesRes.data);
        const evaluatorList = parseCollection(evaluatorsRes.data);
        const participantList = parseCollection(participantsRes.data);
        const assignmentList: AssignmentItem[] = parseCollection(assignmentsRes.data);
        const enrollmentList: EnrollmentItem[] = parseCollection(enrollmentsRes.data);
        const batchTechList: BatchTechnologyItem[] = parseCollection(batchTechRes.data);

        setTotalBatches(parseTotal(batchesRes.data, batchList.length));
        setActiveEvaluators(evaluatorList.length);
        setParticipants(parseTotal(participantsRes.data, participantList.length));
        setEvaluationsDone(assignmentList.filter(a => a.status === 'COMPLETED').length);

        const btToBatch = new Map<number, number>();
        batchTechList.forEach(bt => btToBatch.set(bt.id, bt.batchId));

        const participantsByBatch = new Map<number, number>();
        enrollmentList.forEach(enrollment => {
          const batchId = btToBatch.get(enrollment.batchTechnologyId);
          if (!batchId) return;
          participantsByBatch.set(batchId, (participantsByBatch.get(batchId) || 0) + 1);
        });

        const sortedRecent = [...batchList]
          .sort((a, b) => {
            const aTime = a.startDate ? new Date(a.startDate).getTime() : 0;
            const bTime = b.startDate ? new Date(b.startDate).getTime() : 0;
            return bTime - aTime;
          })
          .slice(0, 3)
          .map(batch => ({
            ...batch,
            participantCount: participantsByBatch.get(batch.id) || 0,
          }));

        setRecentBatches(sortedRecent);

        const totalAssignments = assignmentList.length;
        const completedAssignments = assignmentList.filter(a => a.status === 'COMPLETED').length;
        const uniqueActiveEvaluators = new Set(
          assignmentList
            .filter(a => a.evaluatorId != null)
            .map(a => a.evaluatorId)
        ).size;

        const completion = totalAssignments > 0
          ? Math.round((completedAssignments / totalAssignments) * 100)
          : 0;
        const activity = evaluatorList.length > 0
          ? Math.round((uniqueActiveEvaluators / evaluatorList.length) * 100)
          : 0;

        setCompletionRate(completion);
        setActivityRate(activity);
      } catch (error) {
        toast.error('Failed to load dashboard data');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  const displayedCompletionRate = useMemo(
    () => `${Math.min(100, Math.max(0, completionRate))}%`,
    [completionRate]
  );

  const displayedActivityRate = useMemo(
    () => `${Math.min(100, Math.max(0, activityRate))}%`,
    [activityRate]
  );

  return (
    <div className="space-y-10">
      <header>
        <h1 className="text-4xl font-serif">Dashboard</h1>
        <p className="text-chrome/60 mt-2">Welcome back, Admin. Here's what's happening today.</p>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard 
          icon={Package} 
          label="Total Batches" 
          value={loading ? '...' : totalBatches} 
          color="bg-primary/10 text-primary" 
        />
        <StatCard 
          icon={Users} 
          label="Active Evaluators" 
          value={loading ? '...' : activeEvaluators} 
          color="bg-status-success/10 text-status-success" 
        />
        <StatCard 
          icon={GraduationCap} 
          label="Participants" 
          value={loading ? '...' : participants} 
          color="bg-status-warning/10 text-status-warning" 
        />
        <StatCard 
          icon={CheckCircle2} 
          label="Evaluations Done" 
          value={loading ? '...' : evaluationsDone.toLocaleString()} 
          color="bg-chrome/10 text-chrome" 
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div className="card">
          <h2 className="text-xl font-serif mb-6">Recent Batches</h2>
          <div className="space-y-4">
            {loading ? (
              <p className="text-center py-6 text-chrome/40">Loading recent batches...</p>
            ) : recentBatches.length === 0 ? (
              <p className="text-center py-6 text-chrome/40">No batches found.</p>
            ) : (
              recentBatches.map((batch) => (
                <div key={batch.id} className="flex items-center justify-between p-4 rounded border border-surface-dim hover:bg-canvas transition-colors">
                  <div>
                    <p className="font-medium">{batch.name}</p>
                    <p className="text-xs text-chrome/60">
                      {formatDaysAgo(batch.startDate)} • {batch.participantCount} participants
                    </p>
                  </div>
                  <span className="text-xs font-semibold uppercase tracking-wider text-primary">Active</span>
                </div>
              ))
            )}
          </div>
        </div>

        <div className="card">
          <h2 className="text-xl font-serif mb-6">Evaluator Activity</h2>
          <div className="space-y-6">
            <div>
              <div className="flex justify-between text-sm mb-2">
                <span>Evaluation Completion</span>
                <span>{displayedCompletionRate}</span>
              </div>
              <div className="w-full h-2 bg-surface-container rounded-full overflow-hidden">
                <div className="h-full bg-primary" style={{ width: displayedCompletionRate }}></div>
              </div>
            </div>
            <div>
              <div className="flex justify-between text-sm mb-2">
                <span>Evaluator Activity</span>
                <span>{displayedActivityRate}</span>
              </div>
              <div className="w-full h-2 bg-surface-container rounded-full overflow-hidden">
                <div className="h-full bg-status-success" style={{ width: displayedActivityRate }}></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;


