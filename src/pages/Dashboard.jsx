import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';
import Navbar from '../components/Navbar';

function Dashboard() {
  const navigate = useNavigate();
  const name     = localStorage.getItem('name');
  const email    = localStorage.getItem('email');
  const token    = localStorage.getItem('token');

  const [generating, setGenerating] = useState(null);  // round type being generated
  const [questions,  setQuestions]  = useState([]);
  const [activeRound, setActiveRound] = useState(null);
  const [answers,    setAnswers]    = useState({});
  const [results,    setResults]    = useState({});
  const [submitting, setSubmitting] = useState(null);
  const [error,      setError]      = useState('');

  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
  };

  const rounds = [
    { id: 'AI',  title: 'AI Round',  description: 'Technical questions from your resume.',   icon: '🤖', color: '#4f46e5', bg: '#eef2ff' },
    { id: 'HR',  title: 'HR Round',  description: 'Behavioural and situational questions.',   icon: '👔', color: '#0891b2', bg: '#ecfeff' },
    { id: 'DSA', title: 'DSA Round', description: 'Data structures and algorithms problems.', icon: '💻', color: '#16a34a', bg: '#f0fdf4' },
  ];

  // Load questions for a round via the backend → AI service
  const startRound = async (roundType) => {
    setError('');
    setGenerating(roundType);
    setQuestions([]);
    setAnswers({});
    setResults({});
    setActiveRound(null);

    // Get the latest resume for this user from localStorage (set after upload)
    const resumeId = localStorage.getItem('resumeId');
    if (!resumeId) {
      setError('Please upload your resume first before starting a round.');
      setGenerating(null);
      return;
    }

    try {
      const res = await fetch('http://localhost:8080/api/questions/generate', {
        method:  'POST',
        headers: { 'Content-Type': 'application/json',
                   'Authorization': `Bearer ${token}` },
        body: JSON.stringify({ resumeId, type: roundType, count: 5 }),
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.error || 'Failed to generate questions');
      setQuestions(data.questions || []);
      setActiveRound(roundType);
    } catch (e) {
      setError(e.message);
    } finally {
      setGenerating(null);
    }
  };

  const submitAnswer = async (question, idx) => {
    const ans = (answers[idx] || '').trim();
    if (!ans) return;
    setSubmitting(idx);

    try {
      const res = await fetch('http://localhost:8080/api/answers', {
        method:  'POST',
        headers: { 'Content-Type': 'application/json',
                   'Authorization': `Bearer ${token}` },
        body: JSON.stringify({
          answer:    ans,
          type:      activeRound,
          sessionId: 0,
          userId:    localStorage.getItem('userId') || 0,
        }),
      });
      const data = await res.json();
      setResults(r => ({ ...r, [idx]: { score: data.score, feedback: data.feedback } }));
    } catch (e) {
      setResults(r => ({ ...r, [idx]: { score: 0, feedback: 'Submission failed.' } }));
    } finally {
      setSubmitting(null);
    }
  };

  return (
    <div className="dashboard">

      <Navbar />

      {/* Welcome */}
      <div className="welcome">
        <h2>Welcome back, {name}! 👋</h2>
        <p>Choose an interview round to begin your practice session.</p>
      </div>

      {error && <div className="dash-error">{error}</div>}

      {/* Round Cards */}
      <div className="rounds-grid">
        {rounds.map((round) => (
          <div key={round.id} className="round-card"
               style={{ borderTop: `4px solid ${round.color}` }}>
            <div className="round-icon" style={{ background: round.bg }}>{round.icon}</div>
            <h3>{round.title}</h3>
            <p>{round.description}</p>
            <button
              className="start-btn"
              style={{ background: round.color }}
              disabled={!!generating}
              onClick={() => startRound(round.id)}>
              {generating === round.id ? 'Generating…' : `Start ${round.title}`}
            </button>
          </div>
        ))}
      </div>

      {/* Resume Upload CTA */}
      <div className="resume-section">
        <div className="resume-card">
          <div>
            <h3>Upload Your Resume</h3>
            <p>Let AI analyze your resume and generate personalized questions.</p>
          </div>
          <div style={{display:'flex', gap:'12px', flexWrap:'wrap'}}>
            <button className="upload-btn" onClick={() => navigate('/resume')}>
              Upload Resume
            </button>
            <button className="upload-btn" style={{background:'#0891b2'}}
              onClick={() => navigate('/profile')}>
              View Profile
            </button>
          </div>
        </div>
      </div>

      {/* Interview Questions */}
      {activeRound && questions.length > 0 && (
        <div className="questions-section">
          <h2>{activeRound} Round — Interview Questions</h2>
          {questions.map((q, idx) => (
            <div key={idx} className="question-card">
              <p className="q-text"><strong>Q{idx + 1}:</strong> {q}</p>
              <textarea
                rows={4}
                placeholder="Type your answer here…"
                value={answers[idx] || ''}
                onChange={e => setAnswers(a => ({ ...a, [idx]: e.target.value }))}
                disabled={!!results[idx]}
              />
              {!results[idx] ? (
                <button
                  className="submit-ans-btn"
                  disabled={submitting === idx || !answers[idx]?.trim()}
                  onClick={() => submitAnswer(q, idx)}>
                  {submitting === idx ? 'Evaluating…' : 'Submit Answer'}
                </button>
              ) : (
                <div className={`feedback-box ${results[idx].score >= 7 ? 'good' : results[idx].score >= 4 ? 'avg' : 'poor'}`}>
                  <span className="score-badge">Score: {results[idx].score}/10</span>
                  <p>{results[idx].feedback}</p>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

    </div>
  );
}

export default Dashboard;