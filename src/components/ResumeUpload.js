import React, { useState } from 'react';
import './ResumeUpload.css';

function ResumeUpload() {
  const [candidateName, setCandidateName] = useState('');
  const [email, setEmail] = useState('');
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setResult(null);

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
        body: formData,
      });

      const data = await response.json();

      if (!response.ok) {
        setError(data.error || 'Something went wrong.');
        return;
      }

      setResult(data);
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
            {loading ? 'Uploading...' : 'Upload Resume'}
          </button>

        </form>
      </div>

      {result && (
        <div className="card result-card">
          <h2>Upload Successful!</h2>
          <div className="result-grid">
            <div className="result-item">
              <span className="label">Name</span>
              <span className="value">{result.candidateName}</span>
            </div>
            <div className="result-item">
              <span className="label">Email</span>
              <span className="value">{result.email}</span>
            </div>
            <div className="result-item">
              <span className="label">File</span>
              <span className="value">{result.originalFileName}</span>
            </div>
            <div className="result-item">
              <span className="label">Job Title</span>
              <span className="value">{result.jobTitle || 'Not detected'}</span>
            </div>
            <div className="result-item">
              <span className="label">Experience</span>
              <span className="value">{result.experienceYears || 'Not detected'}</span>
            </div>
            <div className="result-item">
              <span className="label">Status</span>
              <span className={`badge ${result.status === 'PARSED' ? 'green' : 'red'}`}>
                {result.status}
              </span>
            </div>
          </div>

          {result.skills && (
            <div className="skills-box">
              <span className="label">Skills Detected</span>
              <p>{result.skills}</p>
            </div>
          )}
        </div>
      )}

    </div>
  );
}

export default ResumeUpload;