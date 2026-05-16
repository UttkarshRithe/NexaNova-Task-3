import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getAssignmentById, EvaluationAssignment } from '@/api/evaluationAssignmentApi';
import { submitResult } from '@/api/evaluationResultApi';
import api from '@/api/axiosInstance';
import toast from 'react-hot-toast';
import { ChevronLeft, Info, Send, User } from 'lucide-react';

const EvaluationForm = () => {
  const { assignmentId } = useParams();
  const navigate = useNavigate();
  const [assignment, setAssignment] = useState<EvaluationAssignment | null>(null);
  const [loading, setLoading] = useState(true);
  const [score, setScore] = useState(5);
  const [comments, setComments] = useState('');
  const [technicalScore, setTechnicalScore] = useState(70);
  const [communicationScore, setCommunicationScore] = useState(70);
  const [problemSolvingScore, setProblemSolvingScore] = useState(70);
  const [strengths, setStrengths] = useState('');
  const [weaknesses, setWeaknesses] = useState('');
  const [aiFeedback, setAiFeedback] = useState('');
  const [isGeneratingFeedback, setIsGeneratingFeedback] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const fetchAssignment = async () => {
      try {
        const response = await getAssignmentById(Number(assignmentId));
        const data = response.data.data;

        // ✅ Guard 1: Evaluation already submitted
        if (data.status === 'COMPLETED') {
          toast.error('This evaluation has already been completed.');
          navigate('/evaluator/assignments');
          return;
        }

        // ✅ Guard 2: Enrollment was deleted — participant name will be null/empty.
        // This happens when admin deletes an enrollment after the assignment was created.
        // The backend still returns the assignment row but with no participant data.
        if (!data.participantName || data.participantName.trim() === '') {
          toast.error(
            'This evaluation is no longer valid. The participant enrollment may have been cancelled.',
            { duration: 5000 }
          );
          navigate('/evaluator/assignments');
          return;
        }

        setAssignment(data);
      } catch (error: any) {
        toast.error('Failed to load assignment details');
        navigate('/evaluator/assignments');
      } finally {
        setLoading(false);
      }
    };
    fetchAssignment();
  }, [assignmentId, navigate]);

  const getScoreLabel = (val: number) => {
    if (val <= 2) return { text: 'Poor', color: 'text-status-error' };
    if (val <= 4) return { text: 'Average', color: 'text-status-warning' };
    if (val <= 6) return { text: 'Good', color: 'text-primary' };
    if (val <= 8) return { text: 'Very Good', color: 'text-status-success' };
    return { text: 'Excellent', color: 'text-status-success font-bold' };
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!comments.trim()) {
      toast.error('Please provide evaluation comments');
      return;
    }
    setSubmitting(true);
    try {
      await submitResult({
        assignmentId: Number(assignmentId),
        score: score * 10,
        comments,
        technicalScore,
        communicationScore,
        problemSolvingScore,
        strengths,
        weaknesses,
        aiFeedback: aiFeedback || undefined,
      });
      toast.success('Evaluation submitted successfully');
      navigate('/evaluator/assignments');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to submit evaluation');
    } finally {
      setSubmitting(false);
    }
  };

  const generateAIFeedback = async () => {
    if (!comments.trim()) {
      toast.error('Please add evaluator comments before generating AI feedback');
      return;
    }
    setIsGeneratingFeedback(true);
    try {
      const response = await api.post('/api/ai/generate-feedback', {
        technology: assignment?.technologyName,
        roundNumber: assignment?.roundNumber,
        overallScore: score * 10,
        technicalScore,
        communicationScore,
        problemSolvingScore,
        strengths,
        weaknesses,
        evaluatorComment: comments,
        difficultyLevel: 'MEDIUM',
      });
      setAiFeedback(response?.data?.data?.aiFeedback || '');
      toast.success('AI feedback generated!');
    } catch (error) {
      toast.error('Failed to generate AI feedback');
    } finally {
      setIsGeneratingFeedback(false);
    }
  };

  if (loading) return <div className="p-10 text-center text-chrome">Loading assignment...</div>;
  if (!assignment) return null;

  const scoreLabel = getScoreLabel(score);

  return (
    <div className="max-w-3xl mx-auto space-y-10">
      <header>
        <button 
          onClick={() => navigate('/evaluator/assignments')}
          className="flex items-center gap-2 text-chrome/60 hover:text-chrome transition-colors mb-6"
        >
          <ChevronLeft size={16} />
          Back to Assignments
        </button>
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-4xl font-serif text-chrome">Evaluation Form</h1>
            <p className="text-chrome/60 mt-2">Submit your assessment for the participant.</p>
          </div>
          <div className="bg-primary/5 px-4 py-2 rounded border border-primary/20 text-primary text-xs font-bold uppercase tracking-widest">
            Round {assignment.roundNumber}
          </div>
        </div>
      </header>

      <div className="card bg-chrome text-white border-none shadow-xl relative overflow-hidden">
        <div className="absolute top-0 right-0 p-8 opacity-10">
          <User size={120} />
        </div>
        <div className="relative z-10 flex items-center gap-6">
          <div className="w-16 h-16 rounded-full bg-white/10 flex items-center justify-center text-2xl font-serif font-bold">
            {assignment.participantName?.[0]}
          </div>
          <div>
            <h2 className="text-2xl font-serif">{assignment.participantName}</h2>
            <p className="text-white/60 text-sm font-semibold uppercase tracking-widest">
              {assignment.batchName} • {assignment.technologyName}
            </p>
          </div>
        </div>
      </div>

      <div className="card">
        <form onSubmit={handleSubmit} className="space-y-10">
          {/* Scoring Section */}
          <div className="space-y-6">
            <div className="flex items-center justify-between">
              <h3 className="text-xl font-serif flex items-center gap-2">
                Assessment Score
                <span className={`text-sm font-sans font-bold ml-2 ${scoreLabel.color} px-3 py-1 bg-surface-container rounded`}>
                  {scoreLabel.text}
                </span>
              </h3>
              <span className="text-4xl font-serif text-primary">{score} / 10</span>
            </div>
            
            <input 
              type="range" 
              min="0" 
              max="10" 
              value={score} 
              onChange={(e) => setScore(Number(e.target.value))}
              className="w-full h-2 bg-surface-container rounded-lg appearance-none cursor-pointer accent-primary"
            />
            
            <div className="flex justify-between text-[10px] font-bold uppercase tracking-widest text-chrome/40 px-1">
              <span>0 (Poor)</span>
              <span>5 (Good)</span>
              <span>10 (Excellent)</span>
            </div>
          </div>

          <hr className="border-surface-dim" />

          <div className="space-y-4">
            <h3 className="text-xl font-serif">Evaluation Criteria</h3>
            <label className="block text-sm">Technical Knowledge: {technicalScore}</label>
            <input
              type="range"
              min="0"
              max="100"
              value={technicalScore}
              onChange={(e) => setTechnicalScore(Number(e.target.value))}
              className="w-full h-2 bg-surface-container rounded-lg appearance-none cursor-pointer accent-primary"
            />

            <label className="block text-sm">Communication: {communicationScore}</label>
            <input
              type="range"
              min="0"
              max="100"
              value={communicationScore}
              onChange={(e) => setCommunicationScore(Number(e.target.value))}
              className="w-full h-2 bg-surface-container rounded-lg appearance-none cursor-pointer accent-primary"
            />

            <label className="block text-sm">Problem Solving: {problemSolvingScore}</label>
            <input
              type="range"
              min="0"
              max="100"
              value={problemSolvingScore}
              onChange={(e) => setProblemSolvingScore(Number(e.target.value))}
              className="w-full h-2 bg-surface-container rounded-lg appearance-none cursor-pointer accent-primary"
            />

            <textarea
              className="input-field min-h-[90px]"
              placeholder="What did participant do well? (Strengths)"
              value={strengths}
              onChange={(e) => setStrengths(e.target.value)}
              maxLength={1000}
            />
            <textarea
              className="input-field min-h-[90px]"
              placeholder="What needs improvement? (Areas to improve)"
              value={weaknesses}
              onChange={(e) => setWeaknesses(e.target.value)}
              maxLength={1000}
            />
            <button
              type="button"
              onClick={generateAIFeedback}
              disabled={isGeneratingFeedback}
              className="btn-secondary"
            >
              {isGeneratingFeedback ? '✨ Generating...' : '✨ Generate AI Feedback'}
            </button>
            {aiFeedback && (
              <div className="bg-primary/5 border border-primary/20 rounded-lg p-4">
                <h4 className="font-semibold mb-2">🤖 AI Generated Feedback</h4>
                <p className="text-sm whitespace-pre-wrap">{aiFeedback}</p>
                <small className="text-chrome/50">This will be saved with the evaluation.</small>
              </div>
            )}
          </div>

          <hr className="border-surface-dim" />

          {/* Evaluator Notes Section */}
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-xl font-serif">Evaluator Notes</h3>
              <div className="flex items-center gap-1.5 text-chrome/40">
                <Info size={14} />
                <span className="text-[10px] font-bold uppercase tracking-wider">Used for AI Feedback</span>
              </div>
            </div>
            <textarea
              className="input-field min-h-[200px] py-4 leading-relaxed"
              placeholder="Enter raw observations and specific examples here. The AI will use these notes to generate the final professional feedback report..."
              value={comments}
              onChange={(e) => setComments(e.target.value)}
              required
            />
          </div>

          <div className="pt-6 flex justify-end gap-4">
            <button 
              type="button" 
              onClick={() => navigate('/evaluator/assignments')}
              className="btn-secondary px-8"
            >
              Cancel
            </button>
            <button 
              type="submit" 
              disabled={submitting}
              className="btn-primary px-10 gap-2 shadow-lg shadow-primary/20"
            >
              <Send size={18} />
              {submitting ? 'Submitting...' : 'Complete Evaluation'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EvaluationForm;


