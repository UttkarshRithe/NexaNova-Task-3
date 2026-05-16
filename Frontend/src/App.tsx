import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import AdminDashboard from './pages/admin/Dashboard';
import BatchList from './pages/admin/batches/BatchList';
import CreateBatch from './pages/admin/batches/CreateBatch';
import EditBatch from './pages/admin/batches/EditBatch';
import TechnologyList from './pages/admin/technologies/TechnologyList';
import RoundsConfig from './pages/admin/rounds/RoundsConfig';
import UserList from './pages/admin/users/UserList';
import CreateUser from './pages/admin/users/CreateUser';
import EditUser from './pages/admin/users/EditUser';
import ParticipantList from './pages/admin/participants/ParticipantList';
import CreateParticipant from './pages/admin/participants/CreateParticipant';
import ParticipantDetail from './pages/admin/participants/ParticipantDetail';
import EnrollmentList from './pages/admin/enrollments/EnrollmentList';
import EvaluationAssignmentList from './pages/admin/assignments/EvaluationAssignmentList';
import Reports from './pages/admin/reports/Reports';

import EvaluatorDashboard from './pages/evaluator/Dashboard';
import MyAssignments from './pages/evaluator/MyAssignments';
import EvaluationForm from './pages/evaluator/EvaluationForm';
import SubmittedResults from './pages/evaluator/SubmittedResults';

import AdminRoute from './guards/AdminRoute';
import EvaluatorRoute from './guards/EvaluatorRoute';
import AdminLayout from './components/layout/AdminLayout';
import EvaluatorLayout from './components/layout/EvaluatorLayout';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        
        {/* Admin Routes */}
        <Route path="/admin" element={<AdminRoute />}>
          <Route element={<AdminLayout />}>
            <Route path="dashboard" element={<AdminDashboard />} />
            <Route path="batches" element={<BatchList />} />
            <Route path="batches/create" element={<CreateBatch />} />
            <Route path="batches/:id/edit" element={<EditBatch />} />
            <Route path="technologies" element={<TechnologyList />} />
            <Route path="rounds-config" element={<RoundsConfig />} />
            <Route path="users" element={<UserList />} />
            <Route path="users/create" element={<CreateUser />} />
            <Route path="users/:id/edit" element={<EditUser />} />
            <Route path="participants" element={<ParticipantList />} />
            <Route path="participants/create" element={<CreateParticipant />} />
            <Route path="participants/:id" element={<ParticipantDetail />} />
            <Route path="enrollments" element={<EnrollmentList />} />
            <Route path="evaluation-assignments" element={<EvaluationAssignmentList />} />
            <Route path="reports" element={<Reports />} />
          </Route>
        </Route>

        {/* Evaluator Routes */}
        <Route path="/evaluator" element={<EvaluatorRoute />}>
          <Route element={<EvaluatorLayout />}>
            <Route path="dashboard" element={<EvaluatorDashboard />} />
            <Route path="assignments" element={<MyAssignments />} />
            <Route path="evaluate/:assignmentId" element={<EvaluationForm />} />
            <Route path="results" element={<SubmittedResults />} />
          </Route>
        </Route>

        {/* Redirects */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
}

export default App;



