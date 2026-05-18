import React, { useState, useEffect } from 'react';
import { Plus, Search, Filter, Edit2, Trash2, Mail, ShieldCheck, ShieldAlert } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { getUsers, deleteUser, User } from '@/api/userApi';
import toast from 'react-hot-toast';
import ConfirmModal from '@/components/ui/ConfirmModal';

const UserList = () => {
  const navigate = useNavigate();
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);

  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    fetchUsers();
  }, [page]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await getUsers(page, 10);
      const { content, totalElements } = response.data.data;
      setUsers(content);
      setTotalElements(totalElements);
    } catch (error: any) {
      toast.error('Failed to load users');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (selectedUserId === null) return;
    try {
      await deleteUser(selectedUserId);
      toast.success('User deactivated successfully');
      fetchUsers();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to deactivate user');
    }
  };

  const filteredUsers = users.filter(user => 
    user.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    user.email.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="space-y-8">
      <header className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl sm:text-4xl font-serif">Users</h1>
          <nav className="text-xs font-semibold uppercase tracking-wider text-chrome/40 mt-2">
            Admin / Users
          </nav>
        </div>
        <button 
          onClick={() => navigate('/admin/users/create')}
          className="btn-primary w-full sm:w-auto"
        >
          <Plus size={20} />
          Create User
        </button>
      </header>

      <div className="flex flex-col sm:flex-row gap-4">
        <div className="flex-1 relative">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-chrome/40" size={20} />
          <input 
            type="text" 
            placeholder="Search users by name or email..." 
            className="input-field pl-12"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      <div className="table-container">
        {loading ? (
          <div className="p-12 text-center text-chrome/40">Loading users...</div>
        ) : filteredUsers.length === 0 ? (
          <div className="p-12 text-center text-chrome/40">No users found.</div>
        ) : (
          <table className="w-full min-w-[600px] text-left border-collapse">
            <thead>
              <tr className="table-header">
                <th className="px-6 py-4">User</th>
                <th className="px-6 py-4">Role</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.map((user) => (
                <tr key={user.id} className="table-row">
                  <td className="table-cell">
                    <div className="flex items-center gap-4">
                      <div className="w-10 h-10 rounded-full bg-surface flex items-center justify-center font-bold text-chrome/40 flex-shrink-0">
                        {user.name[0]}
                      </div>
                      <div className="min-w-0">
                        <p className="font-medium truncate">{user.name}</p>
                        <div className="flex items-center gap-1 text-xs text-chrome/40 truncate">
                          <Mail size={12} className="flex-shrink-0" />
                          {user.email}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td className="table-cell">
                    <div className="flex items-center gap-2">
                      {user.role === 'ADMIN' ? (
                        <span className="flex items-center gap-1.5 px-3 py-1 bg-chrome text-white text-[10px] font-bold uppercase tracking-wider rounded-full">
                          <ShieldCheck size={12} />
                          Admin
                        </span>
                      ) : (
                        <span className="flex items-center gap-1.5 px-3 py-1 bg-surface-container text-chrome text-[10px] font-bold uppercase tracking-wider rounded-full border border-surface-dim">
                          <ShieldAlert size={12} />
                          Evaluator
                        </span>
                      )}
                    </div>
                  </td>
                  <td className="table-cell text-right">
                    <div className="flex justify-end gap-2">
                      <button 
                        onClick={() => navigate(`/admin/users/${user.id}/edit`)}
                        className="p-2 hover:bg-surface rounded transition-colors text-chrome/60"
                      >
                        <Edit2 size={16} />
                      </button>
                      <button 
                        onClick={() => {
                          setSelectedUserId(user.id!);
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
          Showing {users.length} of {totalElements} results
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
        title="Deactivate User?"
        message="This user will no longer be able to log in. Their existing assignments will remain in the system."
      />
    </div>
  );
};

export default UserList;


