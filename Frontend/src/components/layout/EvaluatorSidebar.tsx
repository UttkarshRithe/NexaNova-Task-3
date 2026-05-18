import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  ClipboardList, 
  CheckCircle2,
  LogOut,
  X
} from 'lucide-react';
import { useAuth } from '@/context/AuthContext';

interface EvaluatorSidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

const EvaluatorSidebar: React.FC<EvaluatorSidebarProps> = ({ isOpen, onClose }) => {
  const { logout, user } = useAuth();

  const navItems = [
    { icon: LayoutDashboard, label: 'My Dashboard', path: '/evaluator/dashboard' },
    { icon: ClipboardList, label: 'My Assignments', path: '/evaluator/assignments' },
    { icon: CheckCircle2, label: 'Submitted Results', path: '/evaluator/results' },
  ];

  return (
    <div className={`
      w-72 bg-chrome text-white h-screen flex flex-col fixed left-0 top-0 z-50
      transition-transform duration-300 ease-in-out lg:translate-x-0
      ${isOpen ? 'translate-x-0' : '-translate-x-full'}
    `}>
      <div className="p-8 flex items-center justify-between border-b border-white/10 lg:border-none">
        <h1 className="text-2xl font-serif">EvalTrack</h1>
        <button 
          onClick={onClose}
          className="lg:hidden p-1 text-white/70 hover:text-white transition-colors"
          aria-label="Close sidebar"
        >
          <X size={20} />
        </button>
      </div>

      <nav className="flex-1 px-4 space-y-1 overflow-y-auto">
        {navItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            onClick={onClose}
            className={({ isActive }) => `
              flex items-center gap-3 px-4 py-3 rounded transition-all
              ${isActive 
                ? 'bg-primary text-white font-medium border-l-4 border-white' 
                : 'text-white/70 hover:text-white hover:bg-white/5'}
            `}
          >
            <item.icon size={20} />
            <span className="text-sm">{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="p-4 border-t border-white/10">
        <div className="flex items-center gap-3 px-4 py-3 mb-2">
          <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center text-xs font-bold">
            {user?.name?.[0] || 'E'}
          </div>
          <div className="overflow-hidden">
            <p className="text-sm font-medium truncate">{user?.name || 'Evaluator'}</p>
          </div>
        </div>
        <button
          onClick={logout}
          className="flex items-center gap-3 px-4 py-3 w-full text-white/70 hover:text-white hover:bg-white/5 rounded transition-all"
        >
          <LogOut size={20} />
          <span className="text-sm">Logout</span>
        </button>
      </div>
    </div>
  );
};

export default EvaluatorSidebar;


