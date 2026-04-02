import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Home.css';
function Home() {
  const navigate = useNavigate();
return (
    <div className="home">

      <nav className="navbar">
        <h1 className="logo">AI Interview</h1>
        <div className="nav-links">
          <button onClick={() => navigate('/login')}  className="btn-outline">Login</button>
          <button onClick={() => navigate('/signup')} className="btn-filled">Sign Up</button>
        </div>
      </nav>

      <div className="hero">
        <h2>Ace Your Next Interview</h2>
        <p>Practice with AI-powered mock interviews. Get real-time feedback on your answers across AI, HR, and DSA rounds.</p>
        <button onClick={() => navigate('/signup')} className="btn-hero">
          Get Started — It's Free
        </button>
      </div>

      <div className="features">
        <div className="feature-card">
          <div className="icon">🤖</div>
          <h3>AI Round</h3>
          <p>Answer AI-generated questions and get instant intelligent feedback.</p>
        </div>
        <div className="feature-card">
          <div className="icon">👔</div>
          <h3>HR Round</h3>
          <p>Practice behavioral questions and improve your communication skills.</p>
        </div>
        <div className="feature-card">
          <div className="icon">💻</div>
          <h3>DSA Round</h3>
          <p>Solve data structures and algorithms problems with AI evaluation.</p>
        </div>
      </div>

    </div>
  );
}

export default Home;