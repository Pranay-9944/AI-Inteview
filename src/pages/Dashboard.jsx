import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';
import Navbar from '../components/Navbar';

function Dashboard() {
  const navigate = useNavigate();

  const rounds = [
    {
      id:          'ai',
      title:       'AI Round',
      description: 'Technical questions from your resume.',
      icon:        '🤖',
      color:       '#4f46e5',
      bg:          '#eef2ff',
    },
    {
      id:          'hr',
      title:       'HR Round',
      description: 'Behavioural and situational questions.',
      icon:        '👔',
      color:       '#0891b2',
      bg:          '#ecfeff',
    },
    {
      id:          'dsa',
      title:       'DSA Round',
      description: 'Data structures and algorithms problems.',
      icon:        '💻',
      color:       '#16a34a',
      bg:          '#f0fdf4',
    },
  ];

  const name = localStorage.getItem('name');

  return (
    <div className="dashboard">

      <Navbar />

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
              onClick={() => navigate(`/interview/${round.id}`)}>
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
          <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
            <button
              className="upload-btn"
              onClick={() => navigate('/resume')}>
              Upload Resume
            </button>
            <button
              className="upload-btn"
              style={{ background: '#0891b2' }}
              onClick={() => navigate('/profile')}>
              View Profile
            </button>
          </div>
        </div>
      </div>

    </div>
  );
}

export default Dashboard;