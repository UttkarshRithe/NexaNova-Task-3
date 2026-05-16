import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getParticipantById, getParticipantEnrollments, Participant } from '@/api/participantApi';
import { getBatchTechnologies, BatchTechnology } from '@/api/batchTechnologyApi';
import toast from 'react-hot-toast';
import { ChevronLeft, Mail, User, BookOpen, Layers } from 'lucide-react';

const ParticipantDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [participant, setParticipant] = useState<Participant | null>(null);
  const [enrollments, setEnrollments] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  const enrichEnrollmentsWithProgram = (items: any[], btList: BatchTechnology[]) => {
    const btMap = new Map(btList.map((bt) => [bt.id, bt]));
    return items.map((en) => {
      const bt = btMap.get(en.batchTechnologyId);
      return {
        ...en,
        batchName: en.batchName || bt?.batchName || '',
        technologyName: en.technologyName || bt?.technologyName || '',
        totalRounds: en.totalRounds ?? bt?.totalRounds ?? 0,
      };
    });
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [pRes, eRes, btRes] = await Promise.all([
          getParticipantById(Number(id)),
          getParticipantEnrollments(Number(id)),
          getBatchTechnologies(),
        ]);
        setParticipant(pRes.data.data);
        setEnrollments(enrichEnrollmentsWithProgram(eRes.data.data || [], btRes.data?.data || []));
      } catch (error: any) {
        toast.error('Failed to load participant details');
        navigate('/admin/participants');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id, navigate]);

  if (loading) return <div className="p-10 text-center">Loading details...</div>;
  if (!participant) return null;

  return (
    <div className="space-y-8">
      <header>
        <button 
          onClick={() => navigate('/admin/participants')}
          className="flex items-center gap-2 text-chrome/60 hover:text-chrome transition-colors mb-4"
        >
          <ChevronLeft size={16} />
          Back to Participants
        </button>
        <div className="flex items-center gap-6">
          <div className="w-20 h-20 rounded-full bg-primary text-white flex items-center justify-center text-3xl font-serif">
            {participant.name[0]}
          </div>
          <div>
            <h1 className="text-4xl font-serif">{participant.name}</h1>
            <div className="flex items-center gap-2 text-chrome/60 mt-1">
              <Mail size={16} />
              {participant.email}
            </div>
          </div>
        </div>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left: General Info */}
        <div className="lg:col-span-1 space-y-6">
          <div className="card">
            <h2 className="text-xl font-serif mb-6 flex items-center gap-2">
              <User size={20} className="text-primary" />
              Information
            </h2>
            <div className="space-y-4">
              <div>
                <label className="block text-[10px] font-bold uppercase tracking-wider text-chrome/40 mb-1">Full Name</label>
                <p className="font-medium">{participant.name}</p>
              </div>
              <div>
                <label className="block text-[10px] font-bold uppercase tracking-wider text-chrome/40 mb-1">Email</label>
                <p className="font-medium">{participant.email}</p>
              </div>
              <div>
                <label className="block text-[10px] font-bold uppercase tracking-wider text-chrome/40 mb-1">Joined Date</label>
                <p className="font-medium text-chrome/60">Oct 12, 2023</p>
              </div>
            </div>
          </div>
        </div>

        {/* Right: Enrollments */}
        <div className="lg:col-span-2">
          <div className="card">
            <h2 className="text-xl font-serif mb-6 flex items-center gap-2">
              <BookOpen size={20} className="text-primary" />
              Active Enrollments
            </h2>
            
            {enrollments.length === 0 ? (
              <div className="text-center py-12 bg-canvas rounded-lg border border-dashed border-surface-dim">
                <Layers size={32} className="text-chrome/20 mx-auto mb-2" />
                <p className="text-sm text-chrome/40">No active enrollments found for this participant.</p>
              </div>
            ) : (
              <div className="space-y-4">
                {enrollments.map((en) => (
                  <div key={en.id} className="p-5 rounded-lg border border-surface-dim hover:border-primary transition-colors flex items-center justify-between group">
                    <div className="flex items-center gap-4">
                      <div className="p-3 bg-surface-container rounded-lg">
                        <Layers size={20} className="text-chrome/60" />
                      </div>
                      <div>
                        <p className="font-medium text-lg">{en.technologyName}</p>
                        <p className="text-xs text-chrome/40 uppercase tracking-widest font-semibold">{en.batchName}</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-6">
                      <div className="text-right">
                        <p className="text-xs font-bold uppercase tracking-wider text-chrome/40 mb-1">Rounds</p>
                        <p className="text-sm font-medium">1 / {en.totalRounds}</p>
                      </div>
                      <span className="px-3 py-1 bg-status-success/10 text-status-success text-[10px] font-bold uppercase tracking-wider rounded-full">
                        In Progress
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ParticipantDetail;


