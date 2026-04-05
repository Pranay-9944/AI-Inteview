import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Profile.css';

function Profile() {
  const navigate  = useNavigate();
  const resumeId  = localStorage.getItem('resumeId');
  const token     = localStorage.getItem('token');

  const [profile,  setProfile]  = useState(null);
  const [loading,  setLoading]  = useState(true);
  const [error,    setError]    = useState('');

  useEffect(() => {
    if (!resumeId) {
      setError('No resume uploaded yet.');
      setLoading(false);
      return;
    }
    fetch(`http://localhost:8080/api/resumes/${resumeId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
      .then(r => r.json())
      .then(data => { setProfile(data); setLoading(false); })
      .catch(() => { setError('Failed to load profile.'); setLoading(false); });
  }, [resumeId, token]);

  if (loading) return <div className="prof-center"><div className="spinner" />Loading profile…</div>;

  if (error) return (
    <div className="prof-center">
      <p className="prof-error">{error}</p>
      <button className="prof-btn" onClick={() => navigate('/resume')}>Upload Resume</button>
    </div>
  );

  const skills = profile.skills
    ? profile.skills.split(',').map(s => s.trim()).filter(Boolean)
    : [];

  return (
    <div className="prof-page">

      {/* Top bar */}
      <div className="prof-topbar">
        <button className="back-btn" onClick={() => navigate('/dashboard')}>← Dashboard</button>
        <button className="prof-btn outline" onClick={() => navigate('/resume')}>Re-upload Resume</button>
      </div>

      {/* Hero card */}
      <div className="prof-hero">
        <div className="prof-avatar">{initials(profile.candidateName)}</div>
        <div className="prof-hero-info">
          <h1>{profile.candidateName}</h1>
          <p className="prof-email">📧 {profile.email}</p>
          <p className="prof-job">💼 {profile.jobTitle || 'Job title not detected'}</p>
          <p className="prof-exp">⏱ {profile.experienceYears || 'Experience not specified'}</p>
        </div>
        <span className={`prof-badge ${profile.status === 'PARSED' ? 'green' : 'red'}`}>
          {profile.status}
        </span>
      </div>

      {/* Skills */}
      <div className="prof-section">
        <h2>🛠 Skills</h2>
        {skills.length > 0 ? (
          <div className="skills-grid">
            {skills.slice(0, 20).map((s, i) => (
              <span key={i} className="skill-tag">{s}</span>
            ))}
          </div>
        ) : (
          <p className="prof-empty">No skills detected from resume.</p>
        )}
      </div>

      {/* File info */}
      <div className="prof-section">
        <h2>📄 Resume File</h2>
        <div className="prof-info-row">
          <span className="prof-info-label">File name</span>
          <span className="prof-info-value">{profile.originalFileName}</span>
        </div>
        <div className="prof-info-row">
          <span className="prof-info-label">Uploaded at</span>
          <span className="prof-info-value">
            {profile.uploadedAt ? new Date(profile.uploadedAt).toLocaleString() : '—'}
          </span>
        </div>
        <div className="prof-info-row">
          <span className="prof-info-label">Parsed at</span>
          <span className="prof-info-value">
            {profile.parsedAt ? new Date(profile.parsedAt).toLocaleString() : '—'}
          </span>
        </div>
      </div>

      {/* CTA */}
      <div className="prof-cta">
        <p>Ready to practice?</p>
        <button className="prof-btn" onClick={() => navigate('/dashboard')}>
          Start Interview Round
        </button>
      </div>

    </div>
  );
}

function initials(name = '') {
  return name.split(' ').slice(0, 2).map(w => w[0]?.toUpperCase() || '').join('') || '?';
}

export default Profile;