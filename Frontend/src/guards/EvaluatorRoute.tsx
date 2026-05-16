import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';

const EvaluatorRoute = () => {
  const { user } = useAuth();
  const role = localStorage.getItem('role');

  if (!user && !role) {
    return <Navigate to="/login" replace />;
  }

  if (role !== 'EVALUATOR') {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
};

export default EvaluatorRoute;


