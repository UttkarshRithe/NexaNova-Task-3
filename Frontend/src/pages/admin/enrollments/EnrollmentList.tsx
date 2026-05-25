import React, { useState, useEffect } from 'react';
import { Plus, Search, Filter, Trash2, UserPlus, BookOpen } from 'lucide-react';
import { getEnrollments, createEnrollment, deleteEnrollment, Enrollment } from '@/api/enrollmentApi';
import { getAllParticipants, Participant } from '@/api/participantApi';
import { getBatchTechnologies, BatchTechnology } from '@/api/batchTechnologyApi';
import toast from 'react-hot-toast';
import ConfirmModal from '@/components/ui/ConfirmModal';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';

const enrollmentSchema = z.object({
  participantId: z.string().min(1, 'Please select a participant'),
  batchTechnologyId: z.string().min(1, 'Please select a program'),
});

type EnrollmentForm = z.infer<typeof enrollmentSchema>;

const EnrollmentList = () => {
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [participants, setParticipants] = useState<Participant[]>([]);
  const [programs, setPrograms] = useState<BatchTechnology[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState<number | null>(null);

  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm<EnrollmentForm>({
    resolver: zodResolver(enrollmentSchema),
  });

  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    fetchEnrollments();
    fetchSelectionData();
  }, []); // Only fetch once on mount since pagination is client-side

  useEffect(() => {
    setPage(0); // Reset page to 0 on search query change
  }, [searchQuery]);

  const parseEnrollmentPayload = (payload: any) => {
    if (Array.isArray(payload)) {
      return {
        content: payload,
        totalElements: payload.length,
      };
    }
    return {
      content: payload?.content || [],
      totalElements: payload?.totalElements ?? 0,
    };
  };

  const enrichEnrollmentsWithProgram = (items: Enrollment[], btList: BatchTechnology[]) => {
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

  const fetchEnrollments = async () => {
    setLoading(true);
    try {
      const [response, btRes] = await Promise.all([
        getEnrollments(0, 1000), // Fetch all since backend doesn't support pagination
        getBatchTechnologies(),
      ]);
      const { content, totalElements } = parseEnrollmentPayload(response.data?.data);
      const enriched = enrichEnrollmentsWithProgram(content, btRes.data?.data || []);
      setEnrollments(enriched);
      setTotalElements(totalElements);
    } catch (error: any) {
      toast.error('Failed to load enrollments');
    } finally {
      setLoading(false);
    }
  };

  const fetchSelectionData = async () => {
    try {
      const [pRes, progRes] = await Promise.all([
        getAllParticipants(),
        getBatchTechnologies()
      ]);
      setParticipants(pRes.data?.data || []);
      setPrograms(progRes.data?.data || []);
    } catch (error: any) {
      console.error('Failed to load selection data', error);
      setParticipants([]);
      setPrograms([]);
    }
  };

  const onSubmit = async (data: EnrollmentForm) => {
    try {
      await createEnrollment({
        participantId: Number(data.participantId),
        batchTechnologyId: Number(data.batchTechnologyId),
      });
      toast.success('Participant enrolled successfully');
      setIsModalOpen(false);
      reset();
      fetchEnrollments();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Enrollment failed');
    }
  };

  const handleDelete = async () => {
    if (selectedId === null) return;
    try {
      await deleteEnrollment(selectedId);
      toast.success('Enrollment cancelled');
      fetchEnrollments();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to cancel enrollment');
    }
  };

  const ITEMS_PER_PAGE = 10;

  const filteredEnrollments = enrollments?.filter(en => 
    en.participantName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
    en.technologyName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
    en.batchName?.toLowerCase().includes(searchQuery.toLowerCase())
  ) || [];

  const paginatedEnrollments = filteredEnrollments.slice(
    page * ITEMS_PER_PAGE,
    (page + 1) * ITEMS_PER_PAGE
  );

  return (
    <div className="space-y-8">
      <header className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl sm:text-4xl font-serif">Enrollments</h1>
          <p className="text-chrome/60 mt-2">Manage participant enrollments across different batches and technologies.</p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="btn-primary w-full sm:w-auto"
        >
          <UserPlus size={20} />
          Enroll Participant
        </button>
      </header>

      <div className="flex flex-col sm:flex-row gap-4">
        <div className="flex-1 relative">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-chrome/40" size={20} />
          <input
            type="text"
            placeholder="Search enrollments..."
            className="input-field pl-12"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      <div className="table-container">
        {loading ? (
          <div className="p-12 text-center text-chrome/40">
            Loading enrollments...
          </div>
        ) : filteredEnrollments.length === 0 ? (
          <div className="p-12 text-center text-chrome/40">
            No enrollments found.
          </div>
        ) : (
          <table className="w-full min-w-[600px] text-left border-collapse">
            <thead>
              <tr className="table-header">
                <th className="px-6 py-4">Participant</th>
                <th className="px-6 py-4">Program</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>

             <tbody>
              {(() => {
                const grouped: { [key: string]: typeof paginatedEnrollments } = {};
                paginatedEnrollments.forEach(en => {
                  const key = en.participantName || '';
                  if (!grouped[key]) {
                    grouped[key] = [];
                  }
                  grouped[key].push(en);
                });

                return Object.entries(grouped).map(([participantName, items]) => (
                  <React.Fragment key={participantName}>
                    {items.map((en, index) => (
                      <tr key={en.id} className="table-row">
                        {index === 0 && (
                          <td className="table-cell align-top" rowSpan={items.length}>
                            <p className="font-medium">{participantName}</p>
                            <p className="text-[10px] font-bold uppercase tracking-widest text-chrome/40">
                              {en.batchName}
                            </p>
                          </td>
                        )}

                        <td className="table-cell">
                          <div>
                            <p className="font-medium text-primary">
                              {en.technologyName}
                            </p>
                          </div>
                        </td>

                        <td className="table-cell text-right">
                          <button
                            onClick={() => {
                              setSelectedId(en.id!);
                              setIsDeleteModalOpen(true);
                            }}
                            className="p-2 hover:bg-status-error/10 rounded transition-colors text-status-error"
                            title="Cancel Enrollment"
                          >
                            <Trash2 size={18} />
                          </button>
                        </td>
                      </tr>
                    ))}
                  </React.Fragment>
                ));
              })()}
            </tbody>
          </table>
        )}
      </div>

      <div className="flex items-center justify-between">
        <p className="text-sm text-chrome/60">
          Showing {paginatedEnrollments.length} of {filteredEnrollments.length} results
        </p>
        <div className="flex gap-2">
          <button 
            className="btn-secondary py-1 text-xs px-3" 
            disabled={page === 0}
            onClick={() => setPage(page - 1)}
          >
            Previous
          </button>
          <button 
            className="btn-secondary py-1 text-xs px-3"
            disabled={(page + 1) * ITEMS_PER_PAGE >= filteredEnrollments.length}
            onClick={() => setPage(page + 1)}
          >
            Next
          </button>
        </div>
      </div>
      {/* Enrollment Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-chrome/40 backdrop-blur-sm">
          <div className="bg-white w-full sm:max-w-md rounded-t-2xl sm:rounded-lg shadow-xl border border-surface-dim overflow-hidden max-h-[90vh] flex flex-col">
            <div className="p-6 border-b border-surface-dim flex justify-between items-center text-chrome">
              <h3 className="text-xl font-serif">Enroll Participant</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-chrome/40 hover:text-chrome">
                <Plus size={20} className="rotate-45" />
              </button>
            </div>
            <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-5 overflow-y-auto">
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Select Participant</label>
                <select {...register('participantId')} className="input-field">
                  <option value="">Select candidate</option>
                  {participants?.map(p => <option key={p.id} value={p.id}>{p.name} ({p.email})</option>)}
                </select>
                {errors.participantId && <p className="text-status-error text-xs mt-1">{errors.participantId.message}</p>}
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Select Program</label>
                <select {...register('batchTechnologyId')} className="input-field">
                  <option value="">Select batch & technology</option>
                  {programs?.map(prog => (
                    <option key={prog.id} value={prog.id}>
                      {prog.batchName} - {prog.technologyName}
                    </option>
                  ))}
                </select>
                {errors.batchTechnologyId && <p className="text-status-error text-xs mt-1">{errors.batchTechnologyId.message}</p>}
              </div>

              <div className="bg-surface-container p-4 rounded border border-surface-dim flex items-start gap-3">
                <BookOpen size={18} className="text-chrome/40 mt-0.5" />
                <p className="text-xs text-chrome/60 leading-relaxed">
                  Enrolling a participant will allow you to assign evaluators and start the round-based evaluation process for them.
                </p>
              </div>

              <div className="flex flex-col sm:flex-row gap-3 pt-4">
                <button type="button" onClick={() => setIsModalOpen(false)} className="btn-secondary w-full sm:flex-1">Cancel</button>
                <button type="submit" disabled={isSubmitting} className="btn-primary w-full sm:flex-1">
                  {isSubmitting ? 'Enrolling...' : 'Confirm Enrollment'}
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
        title="Cancel Enrollment?"
        message="This will remove the participant from this program. All evaluation progress for this enrollment will be lost."
      />
    </div>
  );
};

export default EnrollmentList;


