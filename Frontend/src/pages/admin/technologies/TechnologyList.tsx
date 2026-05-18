import React, { useState, useEffect } from 'react';
import { Plus, Search, Edit2, Trash2, Check, X } from 'lucide-react';
import { getTechnologies, createTechnology, updateTechnology, deleteTechnology, Technology } from '@/api/technologyApi';
import toast from 'react-hot-toast';
import ConfirmModal from '@/components/ui/ConfirmModal';

const TechnologyList = () => {
  const [technologies, setTechnologies] = useState<Technology[]>([]);
  const [loading, setLoading] = useState(true);
  const [newTechName, setNewTechName] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editingName, setEditingName] = useState('');
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedTechId, setSelectedTechId] = useState<number | null>(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchTechnologies();
  }, []);

  const fetchTechnologies = async () => {
    setLoading(true);
    try {
      const response = await getTechnologies();
      setTechnologies(response.data.data);
    } catch (error: any) {
      toast.error('Failed to load technologies');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTechName.trim()) return;
    try {
      await createTechnology({ name: newTechName });
      toast.success('Technology added');
      setNewTechName('');
      fetchTechnologies();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to add technology');
    }
  };

  const handleUpdate = async (id: number) => {
    if (!editingName.trim()) return;
    try {
      await updateTechnology(id, { name: editingName });
      toast.success('Technology updated');
      setEditingId(null);
      fetchTechnologies();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to update technology');
    }
  };

  const handleDelete = async () => {
    if (selectedTechId === null) return;
    try {
      await deleteTechnology(selectedTechId);
      toast.success('Technology deleted');
      fetchTechnologies();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to delete technology');
    }
  };

  const startEditing = (tech: Technology) => {
    setEditingId(tech.id!);
    setEditingName(tech.name);
  };

  const filteredTechnologies = technologies.filter(t => 
    t.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="space-y-8">
      <header>
        <h1 className="text-4xl font-serif">Technologies</h1>
        <p className="text-chrome/60 mt-2">Manage the list of technologies available for evaluation.</p>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left: Add New Form */}
        <div className="lg:col-span-1">
          <div className="card sticky top-8">
            <h2 className="text-xl font-serif mb-6">Add Technology</h2>
            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider mb-2">Technology Name</label>
                <input
                  type="text"
                  value={newTechName}
                  onChange={(e) => setNewTechName(e.target.value)}
                  className="input-field"
                  placeholder="e.g. React, Java, AWS"
                />
              </div>
              <button 
                type="submit" 
                className="btn-primary w-full"
                disabled={!newTechName.trim()}
              >
                <Plus size={18} />
                Add Technology
              </button>
            </form>
          </div>
        </div>

        {/* Right: List & Management */}
        <div className="lg:col-span-2 space-y-6">
          <div className="relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-chrome/40" size={20} />
            <input 
              type="text" 
              placeholder="Search technologies..." 
              className="input-field pl-12"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <div className="table-container">
            {loading ? (
              <div className="p-12 text-center text-chrome/40">Loading...</div>
            ) : filteredTechnologies.length === 0 ? (
              <div className="p-12 text-center text-chrome/40">No technologies found.</div>
            ) : (
              <table className="w-full min-w-[400px] text-left border-collapse">
                <thead>
                  <tr className="table-header">
                    <th className="px-6 py-4 w-full">Name</th>
                    <th className="px-6 py-4 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredTechnologies.map((tech) => (
                    <tr key={tech.id} className="table-row">
                      <td className="table-cell">
                        {editingId === tech.id ? (
                          <input
                            type="text"
                            value={editingName}
                            onChange={(e) => setEditingName(e.target.value)}
                            className="input-field py-1"
                            autoFocus
                            onKeyDown={(e) => {
                              if (e.key === 'Enter') handleUpdate(tech.id!);
                              if (e.key === 'Escape') setEditingId(null);
                            }}
                          />
                        ) : (
                          <span className="font-medium">{tech.name}</span>
                        )}
                      </td>
                      <td className="table-cell text-right">
                        <div className="flex justify-end gap-2">
                          {editingId === tech.id ? (
                            <>
                              <button 
                                onClick={() => handleUpdate(tech.id!)}
                                className="p-2 hover:bg-status-success/10 rounded transition-colors text-status-success"
                              >
                                <Check size={18} />
                              </button>
                              <button 
                                onClick={() => setEditingId(null)}
                                className="p-2 hover:bg-chrome/5 rounded transition-colors text-chrome/40"
                              >
                                <X size={18} />
                              </button>
                            </>
                          ) : (
                            <>
                              <button 
                                onClick={() => startEditing(tech)}
                                className="p-2 hover:bg-surface rounded transition-colors text-chrome/60"
                              >
                                <Edit2 size={16} />
                              </button>
                              <button 
                                onClick={() => {
                                  setSelectedTechId(tech.id!);
                                  setIsDeleteModalOpen(true);
                                }}
                                className="p-2 hover:bg-status-error/10 rounded transition-colors text-status-error"
                              >
                                <Trash2 size={16} />
                              </button>
                            </>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>

      <ConfirmModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onConfirm={handleDelete}
        title="Delete Technology?"
        message="This will remove the technology from the system. It may affect existing rounds configuration."
      />
    </div>
  );
};

export default TechnologyList;


