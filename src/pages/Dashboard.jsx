import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';

function Dashboard() {
  const navigate  = useNavigate();
  const name      = localStorage.getItem('name');
  const email     = localStorage.getItem('email');

  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
  };

  const rounds = [
    {
      id:          1,
      title:       'AI Round',
      description: 'Answer AI-generated technical questions and get instant feedback.',
      icon:        '🤖',
      color:       '#4f46e5',
      bg:          '#eef2ff',
      path:        '/interview/ai',
    },
    {
      id:          2,
      title:       'HR Round',
      description: 'Practice behavioral and situational questions for HR interviews.',
      icon:        '👔',
      color:       '#0891b2',
      bg:          '#ecfeff',
      path:        '/interview/hr',
    },
    {
      id:          3,
      title:       'DSA Round',
      description: 'Solve data structures and algorithms problems with AI evaluation.',
      icon:        '💻',
      color:       '#16a34a',
      bg:          '#f0fdf4',
      path:        '/interview/dsa',
    },
  ];

  return (
    <div className="dashboard">

      {/* Navbar */}
      <nav className="dash-nav">
        <h1 className="logo">AI Interview</h1>
        <div className="nav-right">
          <span className="user-email">{email}</span>
          <button onClick={handleLogout} className="logout-btn">Logout</button>
        </div>
      </nav>

      {/* Welcome */}
      <div className="welcome">
        <h2>Welcome back, {name}! 👋</h2>
        <p>Choose an interview round to begin your practice session.</p>
      </div>

      {/* Round Cards */}
      <div className="rounds-grid">
        {rounds.map((round) => (
          <div key={round.id} className="round-card"
            style={{ borderTop: `4px solid ${round.color}` }}>
            <div className="round-icon" style={{ background: round.bg }}>
              {round.icon}
            </div>
            <h3>{round.title}</h3>
            <p>{round.description}</p>
            <button
              className="start-btn"
              style={{ background: round.color }}
              onClick={() => navigate(round.path)}>
              Start {round.title}
            </button>
          </div>
        ))}
      </div>

      {/* Resume Upload Section */}
      <div className="resume-section">
        <div className="resume-card">
          <div>
            <h3>Upload Your Resume</h3>
            <p>Let AI analyze your resume and generate personalized questions.</p>
          </div>
          <button
            className="upload-btn"
            onClick={() => navigate('/resume')}>
            Upload Resume
          </button>
        </div>
      </div>

    </div>
  );
}

export default Dashboard;