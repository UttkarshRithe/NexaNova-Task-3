import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';

const AdminRoute = () => {
  const { user } = useAuth();
  const role = localStorage.getItem('role');

  if (!user && !role) {
    return <Navigate to="/login" replace />;
  }

  if (role !== 'ADMIN') {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
};

export default AdminRoute;


