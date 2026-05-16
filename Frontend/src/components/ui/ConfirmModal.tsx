import React from 'react';
import { AlertCircle, X } from 'lucide-react';

interface ConfirmModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title: string;
  message: string;
  confirmLabel?: string;
  isDanger?: boolean;
}

const ConfirmModal: React.FC<ConfirmModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmLabel = 'Confirm',
  isDanger = true,
}) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-chrome/40 backdrop-blur-sm">
      <div className="bg-white max-w-md w-full rounded-lg shadow-xl overflow-hidden border border-surface-dim">
        <div className="p-6">
          <div className="flex items-center gap-4 mb-6">
            <div className={`p-3 rounded-full ${isDanger ? 'bg-status-error/10 text-status-error' : 'bg-primary/10 text-primary'}`}>
              <AlertCircle size={24} />
            </div>
            <div>
              <h3 className="text-xl font-serif">{title}</h3>
              <p className="text-sm text-chrome/60 mt-1">{message}</p>
            </div>
          </div>
          
          <div className="flex justify-end gap-3">
            <button 
              onClick={onClose}
              className="btn-secondary"
            >
              Cancel
            </button>
            <button 
              onClick={() => {
                onConfirm();
                onClose();
              }}
              className={isDanger ? 'btn-primary bg-status-error hover:bg-red-700' : 'btn-primary'}
            >
              {confirmLabel}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ConfirmModal;


