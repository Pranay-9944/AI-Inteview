import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ResumeUpload.css';

function ResumeUpload() {
  const navigate = useNavigate();
  const token    = localStorage.getItem('token');

  const [candidateName, setCandidateName] = useState('');
  const [email,         setEmail]         = useState('');
  const [file,          setFile]          = useState(null);
  const [loading,       setLoading]       = useState(false);
  const [error,         setError]         = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!candidateName || !email || !file) {
      setError('Please fill all fields and select a PDF file.');
      return;
    }

    const formData = new FormData();
    formData.append('candidateName', candidateName);
    formData.append('email', email);
    formData.append('file', file);

    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/api/resumes/upload', {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` },
        body: formData,
      });

      const data = await response.json();

      if (!response.ok) {
        setError(data.error || 'Upload failed.');
        return;
      }

      // Save resumeId for later use (question generation)
      localStorage.setItem('resumeId', data.id);

      // Go straight to profile page to show extracted data
      navigate('/profile');

    } catch (err) {
      setError('Could not connect to server. Make sure Spring Boot is running.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">

      <div className="header">
        <h1>AI Interview Platform</h1>
        <p>Upload your resume to begin the interview process</p>
      </div>

      <div className="card">
        <h2>Resume Upload</h2>

        <form onSubmit={handleSubmit}>

          <div className="field">
            <label>Full Name</label>
            <input
              type="text"
              placeholder="Enter your full name"
              value={candidateName}
              onChange={(e) => setCandidateName(e.target.value)}
            />
          </div>

          <div className="field">
            <label>Email Address</label>
            <input
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div className="field">
            <label>Resume (PDF only, max 5MB)</label>
            <div className="file-box">
              <input
                type="file"
                accept=".pdf"
                onChange={(e) => setFile(e.target.files[0])}
              />
              {file && <p className="file-name">Selected: {file.name}</p>}
            </div>
          </div>

          {error && <div className="error">{error}</div>}

          <button type="submit" disabled={loading}>
            {loading ? 'Uploading & Analyzing…' : 'Upload Resume'}
          </button>

        </form>
      </div>

    </div>
  );
}

export default ResumeUpload;