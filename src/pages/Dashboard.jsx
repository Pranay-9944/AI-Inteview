import React from 'react';
import { useNavigate } from 'react-router-dom';

function Dashboard() {
  const navigate = useNavigate();
  const name = localStorage.getItem('name');

  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
  };

  return (
    <div style={{ padding: '40px', textAlign: 'center' }}>
      <h2>Welcome, {name}! 👋</h2>
      <p style={{ marginTop: '12px', color: '#666' }}>Dashboard coming soon...</p>
      <button onClick={handleLogout}
        style={{ marginTop: '24px', padding: '10px 24px',
          background: '#ef4444', color: 'white',
          border: 'none', borderRadius: '8px', cursor: 'pointer' }}>
        Logout
      </button>
    </div>
  );
}

export default Dashboard;
