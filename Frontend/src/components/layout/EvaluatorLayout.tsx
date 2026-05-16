import React from 'react';
import { Outlet } from 'react-router-dom';
import EvaluatorSidebar from './EvaluatorSidebar';

const EvaluatorLayout = () => {
  return (
    <div className="flex min-h-screen bg-canvas">
      <EvaluatorSidebar />
      <main className="flex-1 ml-72 p-10">
        <div className="max-w-6xl mx-auto">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default EvaluatorLayout;


