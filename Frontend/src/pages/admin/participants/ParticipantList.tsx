import React, { useState, useEffect } from 'react';
import { Plus, Search, Filter, Edit2, Trash2, Mail, ExternalLink } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { getParticipants, deleteParticipant, Participant } from '@/api/participantApi';
import toast from 'react-hot-toast';
import ConfirmModal from '@/components/ui/ConfirmModal';

const ParticipantList = () => {
  const navigate = useNavigate();
  const [participants, setParticipants] = useState<Participant[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState<number | null>(null);

  useEffect(() => {
    fetchParticipants();
  }, [page]);

  const fetchParticipants = async () => {
    setLoading(true);
    try {
      const response = await getParticipants(page, 10);
      const { content, totalElements } = response.data.data;
      setParticipants(content);
      setTotalElements(totalElements);
    } catch (error: any) {
      toast.error('Failed to load participants');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (selectedId === null) return;
    try {
      await deleteParticipant(selectedId);
      toast.success('Participant removed');
      fetchParticipants();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to remove participant');
    }
  };

  return (
    <div className="space-y-8">
      <header className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl sm:text-4xl font-serif">Participants</h1>
          <nav className="text-xs font-semibold uppercase tracking-wider text-chrome/40 mt-2">
            Admin / Participants
          </nav>
        </div>
        <button 
          onClick={() => navigate('/admin/participants/create')}
          className="btn-primary w-full sm:w-auto"
        >
          <Plus size={20} />
          Add Participant
        </button>
      </header>

      <div className="flex flex-col sm:flex-row gap-4">
        <div className="flex-1 relative">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-chrome/40" size={20} />
          <input 
            type="text" 
            placeholder="Search candidates..." 
            className="input-field pl-12"
          />
        </div>
        <button className="btn-secondary w-full sm:w-auto">
          <Filter size={20} />
          Filters
        </button>
      </div>

      <div className="table-container">
        {loading ? (
          <div className="p-12 text-center text-chrome/40">Loading participants...</div>
        ) : (
          <table className="w-full min-w-[600px] text-left border-collapse">
            <thead>
              <tr className="table-header">
                <th className="px-6 py-4">Participant Details</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {participants.map((p) => (
                <tr key={p.id} className="table-row">
                  <td className="table-cell">
                    <div className="flex items-center gap-4">
                      <div className="w-10 h-10 rounded-full bg-surface-container flex items-center justify-center font-bold text-chrome/40 border border-surface-dim flex-shrink-0">
                        {p.name[0]}
                      </div>
                      <div className="min-w-0">
                        <p className="font-medium truncate">{p.name}</p>
                        <div className="flex items-center gap-1 text-xs text-chrome/40 truncate">
                          <Mail size={12} className="flex-shrink-0" />
                          {p.email}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td className="table-cell text-right">
                    <div className="flex justify-end gap-2">
                      <button 
                        onClick={() => navigate(`/admin/participants/${p.id}`)}
                        className="p-2 hover:bg-surface rounded transition-colors text-chrome/60"
                        title="View Details"
                      >
                        <ExternalLink size={16} />
                      </button>
                      <button 
                        onClick={() => {
                          setSelectedId(p.id!);
                          setIsDeleteModalOpen(true);
                        }}
                        className="p-2 hover:bg-status-error/10 rounded transition-colors text-status-error"
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div className="flex items-center justify-between">
        <p className="text-sm text-chrome/60">
          Showing {participants.length} of {totalElements} results
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
            disabled={(page + 1) * 10 >= totalElements}
            onClick={() => setPage(page + 1)}
          >
            Next
          </button>
        </div>
      </div>

      <ConfirmModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onConfirm={handleDelete}
        title="Remove Participant?"
        message="This will permanently delete the participant record and all their enrollment history."
      />
    </div>
  );
};

export default ParticipantList;


