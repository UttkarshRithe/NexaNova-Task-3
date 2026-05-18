import React, { useState, useEffect } from 'react';
import { Plus, Search, Filter, Trash2, UserCheck, ClipboardList } from 'lucide-react';
import { getAssignments, createAssignment, deleteAssignment, EvaluationAssignment } from '@/api/evaluationAssignmentApi';
import { getEnrollments } from '@/api/enrollmentApi';
import { getEvaluators, User } from '@/api/userApi';
import { getBatchTechnologies, BatchTechnology } from '@/api/batchTechnologyApi';
import toast from 'react-hot-toast';
import ConfirmModal from '@/components/ui/ConfirmModal';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';

const assignmentSchema = z.object({
  enrollmentId: z.string().min(1, 'Please select an enrollment'),
  evaluatorId: z.string().min(1, 'Please select an evaluator'),
  roundNumber: z.coerce.number().min(1, 'Round must be at least 1'),
});

type AssignmentForm = z.infer<typeof assignmentSchema>;

const EvaluationAssignmentList = () => {
  const [assignments, setAssignments] = useState<EvaluationAssignment[]>([]);
  const [enrollments, setEnrollments] = useState<any[]>([]);
  const [evaluators, setEvaluators] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState<number | null>(null);

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm<AssignmentForm>({
    resolver: zodResolver(assignmentSchema),
    defaultValues: { roundNumber: 1 }
  });

  useEffect(() => {
    fetchAssignments();
    fetchSelectionData();
  }, [page]);

  const parseEnrollmentPayload = (payload: any) => {
    if (Array.isArray(payload)) {
      return payload;
    }
    return payload?.content || [];
  };

  const enrichEnrollmentsWithProgram = (items: any[], btList: BatchTechnology[]) => {
    const btMap = new Map(btList.map((bt) => [bt.id, bt]));
    return items.map((en) => {
      const bt = btMap.get(en.batchTechnologyId);
      return {
        ...en,
        batchName: en.batchName || bt?.batchName || '',
        technologyName: en.technologyName || bt?.technologyName || '',
      };
    });
  };

  const enrichAssignmentsFromEnrollments = (items: EvaluationAssignment[], enrollmentItems: any[]) => {
    const enrollmentMap = new Map(enrollmentItems.map((en) => [en.id, en]));
    return items
      // ✅ FIX: Filter out orphan assignments whose enrollment was deleted.
      // If enrollment_id has no matching entry in the live enrollment list, the
      // assignment is a ghost record from a cancelled enrollment.
      .filter((as) => enrollmentMap.has(as.enrollmentId))
      .map((as) => {
        const en = enrollmentMap.get(as.enrollmentId);
        return {
          ...as,
          participantName: as.participantName || en?.participantName || '',
          batchName: as.batchName || en?.batchName || '',
          technologyName: as.technologyName || en?.technologyName || '',
        };
      });
  };

  const fetchAssignments = async () => {
    setLoading(true);
    try {
      const [response, enRes, btRes] = await Promise.all([
        getAssignments(page, 10),
        getEnrollments(0, 500),
        getBatchTechnologies(),
      ]);
      const { content, totalElements } = response.data.data;
      const rawEnrollments = parseEnrollmentPayload(enRes.data?.data);
      const enrichedEnrollments = enrichEnrollmentsWithProgram(rawEnrollments, btRes.data?.data || []);
      setAssignments(enrichAssignmentsFromEnrollments(content, enrichedEnrollments));
      setTotalElements(totalElements);
    } catch (error: any) {
      toast.error('Failed to load assignments');
    } finally {
      setLoading(false);
    }
  };

  const fetchSelectionData = async () => {
    try {
      const [enRes, evRes, btRes] = await Promise.all([
        getEnrollments(0, 500),
        getEvaluators(),
        getBatchTechnologies(),
      ]);
      const rawEnrollments = parseEnrollmentPayload(enRes.data?.data);
      setEnrollments(enrichEnrollmentsWithProgram(rawEnrollments, btRes.data?.data || []));
      
      const rawEvaluators = evRes.data?.data?.content || evRes.data?.data || [];
      setEvaluators(Array.isArray(rawEvaluators) ? rawEvaluators : []);
    } catch (error: any) {
      console.error('Failed to load selection data', error);
      setEnrollments([]);
      setEvaluators([]);
    }
  };

  const onSubmit = async (data: AssignmentForm) => {
    try {
      await createAssignment({
        enrollmentId: Number(data.enrollmentId),
        evaluatorId: Number(data.evaluatorId),
        roundNumber: data.roundNumber
      });
      toast.success('Assignment created successfully');
      setIsModalOpen(false);
      reset();
      fetchAssignments();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Assignment failed');
    }
  };

  const handleDelete = async () => {
    if (selectedId === null) return;
    try {
      await deleteAssignment(selectedId);
      toast.success('Assignment deleted');
      fetchAssignments();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to delete assignment');
    }
  };

  return (
    <div className="space-y-8">
      <header className="flex items-center justify-between">
        <div>
          <h1 className="text-4xl font-serif">Evaluation Assignments</h1>
          <p className="text-chrome/60 mt-2">Assign evaluators to participants for specific rounds.</p>
        </div>
        <button 
          onClick={() => setIsModalOpen(true)}
          className="btn-primary"
        >
          <Plus size={20} />
          Create Assignment
        </button>
      </header>

      <div className="flex gap-4">
        <div className="flex-1 relative">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-chrome/40" size={20} />
          <input 
            type="text" 
            placeholder="Search assignments..." 
            className="input-field pl-12"
          />
        </div>
        <button className="btn-secondary">
          <Filter size={20} />
          Filters
        </button>
      </div>

      <div className="table-container">
        {loading ? (
          <div className="p-12 text-center text-chrome/40">Loading assignments...</div>
        ) : assignments.length === 0 ? (
          <div className="p-12 text-center text-chrome/40">No assignments found.</div>
        ) : (
          <>
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="table-header">
                  <th className="px-6 py-4">Participant</th>
                  <th className="px-6 py-4">Program & Round</th>
                  <th className="px-6 py-4">Evaluator</th>
                  <th className="px-6 py-4">Status</th>
                  <th className="px-6 py-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {assignments.map((as) => (
                  <tr key={as.id} className="table-row">
                    <td className="table-cell">
                      <p className="font-medium">{as.participantName}</p>
                    </td>
                    <td className="table-cell">
                      <div>
                        <p className="font-medium text-primary">{as.technologyName}</p>
                        <p className="text-[10px] font-bold uppercase tracking-widest text-chrome/40">
                          {as.batchName} • Round {as.roundNumber}
                        </p>
                      </div>
                    </td>
                    <td className="table-cell">
                      <div className="flex items-center gap-2">
                        <div className="w-6 h-6 rounded-full bg-surface flex items-center justify-center text-[10px] font-bold">
                          {as.evaluatorName?.[0]}
                        </div>
                        <span className="text-sm">{as.evaluatorName}</span>
                      </div>
                    </td>
                    <td className="table-cell">
                      <span className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider border
                        ${as.status === 'COMPLETED' 
                          ? 'bg-status-success/10 text-status-success border-status-success/20' 
                          : 'bg-status-warning/10 text-status-warning border-status-warning/20'}`}
                      >
                        {as.status}
                      </span>
                    </td>
                    <td className="table-cell text-right">
                      <button 
                        onClick={() => {
                          setSelectedId(as.id!);
                          setIsDeleteModalOpen(true);
                        }}
                        className="p-2 hover:bg-status-error/10 rounded transition-colors text-status-error"
                        title="Delete Assignment"
                      >
                        <Trash2 size={18} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {/* Pagination */}
            {totalElements > 10 && (
              <div className="px-6 py-4 border-t border-surface-dim flex items-center justify-between bg-white/50">
                <p className="text-xs text-chrome/60">
                  Showing {page * 10 + 1} to {Math.min((page + 1) * 10, totalElements)} of {totalElements} assignments
                </p>
                <div className="flex gap-2">
                  <button
                    onClick={() => setPage(p => Math.max(0, p - 1))}
                    disabled={page === 0}
                    className="btn-secondary py-1.5 px-3 text-xs disabled:opacity-50"
                  >
                    Previous
                  </button>
                  <button
                    onClick={() => setPage(p => p + 1)}
                    disabled={(page + 1) * 10 >= totalElements}
                    className="btn-secondary py-1.5 px-3 text-xs disabled:opacity-50"
                  >
                    Next
                  </button>
                </div>
              </div>
            )}
          </>
        )}
      </div>

      {/* Assignment Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-chrome/40 backdrop-blur-sm">
          <div className="bg-white max-w-md w-full rounded-lg shadow-xl border border-surface-dim overflow-hidden">
            <div className="p-6 border-b border-surface-dim flex justify-between items-center text-chrome">
              <h3 className="text-xl font-serif">Create Assignment</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-chrome/40 hover:text-chrome">
                <Plus size={20} className="rotate-45" />
              </button>
            </div>
            <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-5">
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Select Enrollment</label>
                <select {...register('enrollmentId')} className="input-field">
                  <option value="">Select participant enrollment</option>
                  {enrollments?.map(en => (
                    <option key={en.id} value={en.id}>
                      {en.participantName} ({en.batchName} - {en.technologyName})
                    </option>
                  ))}
                </select>
                {errors.enrollmentId && <p className="text-status-error text-xs mt-1">{errors.enrollmentId.message}</p>}
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Assign Evaluator</label>
                <select {...register('evaluatorId')} className="input-field">
                  <option value="">Select evaluator</option>
                  {evaluators?.map(ev => <option key={ev.id} value={ev.id}>{ev.name}</option>)}
                </select>
                {errors.evaluatorId && <p className="text-status-error text-xs mt-1">{errors.evaluatorId.message}</p>}
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Round Number</label>
                <input
                  type="number"
                  {...register('roundNumber')}
                  className="input-field"
                  min="1"
                />
                {errors.roundNumber && <p className="text-status-error text-xs mt-1">{errors.roundNumber.message}</p>}
              </div>

              <div className="bg-surface-container p-4 rounded border border-surface-dim flex items-start gap-3 text-chrome">
                <ClipboardList size={18} className="text-chrome/40 mt-0.5" />
                <p className="text-xs text-chrome/60 leading-relaxed">
                  Evaluators will see their assignments in their dashboard. They can only evaluate rounds that have been assigned to them.
                </p>
              </div>

              <div className="flex gap-3 pt-4">
                <button type="button" onClick={() => setIsModalOpen(false)} className="btn-secondary flex-1">Cancel</button>
                <button type="submit" disabled={isSubmitting} className="btn-primary flex-1">
                  {isSubmitting ? 'Assigning...' : 'Confirm Assignment'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <ConfirmModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onConfirm={handleDelete}
        title="Delete Assignment?"
        message="This will remove the assignment. If the evaluator has already started the evaluation, their progress will be lost."
      />
    </div>
  );
};

export default EvaluationAssignmentList;


