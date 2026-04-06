import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import './Navbar.css';

function Navbar() {
  const navigate  = useNavigate();
  const [open, setOpen] = useState(false);
  const [profile, setProfile] = useState(null);
  const dropdownRef = useRef();

  // Load user info from localStorage + resume profile
  useEffect(() => {
    const name     = localStorage.getItem('name')     || '';
    const email    = localStorage.getItem('email')    || '';
    const resumeId = localStorage.getItem('resumeId');
    const token    = localStorage.getItem('token');

    let base = { name, email, jobTitle: null, skills: null, experienceYears: null };

    if (resumeId && token) {
      fetch(`http://localhost:8080/api/resumes/${resumeId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
        .then(r => r.json())
        .then(data => setProfile({ ...base, ...data, name, email }))
        .catch(() => setProfile(base));
    } else {
      setProfile(base);
    }
  }, []);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handler = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
  };

  const initials = (name = '') =>
    name.split(' ').slice(0, 2).map(w => w[0]?.toUpperCase() || '').join('') || '?';

  const skills = profile?.skills
    ? profile.skills.split(',').map(s => s.trim()).filter(Boolean).slice(0, 5)
    : [];

  return (
    <nav className="navbar">

      {/* Logo */}
      <div className="nav-logo" onClick={() => navigate('/dashboard')}>
        🤖 AI Interview
      </div>

      {/* Right side */}
      <div className="nav-right" ref={dropdownRef}>

        {/* Avatar button */}
        <button className="avatar-btn" onClick={() => setOpen(o => !o)}>
          <div className="avatar-circle">
            {initials(profile?.name)}
          </div>
          <span className="nav-name">{profile?.name}</span>
          <span className="chevron">{open ? '▲' : '▼'}</span>
        </button>

        {/* Dropdown */}
        {open && profile && (
          <div className="profile-dropdown">

            {/* Header */}
            <div className="dropdown-header">
              <div className="dropdown-avatar">{initials(profile.name)}</div>
              <div>
                <p className="dropdown-name">{profile.name}</p>
                <p className="dropdown-email">{profile.email}</p>
              </div>
            </div>

            <div className="dropdown-divider" />

            {/* Resume details */}
            <div className="dropdown-section">
              <p className="dropdown-label">💼 Job Title</p>
              <p className="dropdown-value">
                {profile.jobTitle || 'Not detected — upload resume'}
              </p>
            </div>

            <div className="dropdown-section">
              <p className="dropdown-label">⏱ Experience</p>
              <p className="dropdown-value">
                {profile.experienceYears || 'Not specified'}
              </p>
            </div>

            {skills.length > 0 && (
              <div className="dropdown-section">
                <p className="dropdown-label">🛠 Top Skills</p>
                <div className="dropdown-skills">
                  {skills.map((s, i) => (
                    <span key={i} className="dropdown-skill-tag">{s}</span>
                  ))}
                </div>
              </div>
            )}

            <div className="dropdown-divider" />

            {/* Actions */}
            <button className="dropdown-action"
              onClick={() => { setOpen(false); navigate('/profile'); }}>
              👤 View Full Profile
            </button>
            <button className="dropdown-action"
              onClick={() => { setOpen(false); navigate('/resume'); }}>
              📄 Upload Resume
            </button>
            <button className="dropdown-action logout"
              onClick={handleLogout}>
              🚪 Logout
            </button>

          </div>
        )}
      </div>
    </nav>
  );
}

export default Navbar;