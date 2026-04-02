import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import './Auth.css';

function Login() {
  const navigate = useNavigate();
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [error, setError]       = useState('');
  const [loading, setLoading]   = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');

    if (!email || !password) {
      setError('Please fill all fields.');
      return;
    }

    try {
      setLoading(true);
      const res = await axios.post('http://localhost:8080/auth/login', {
        email,
        password,
      });

      // Save token and user info
      localStorage.setItem('token', res.data.token);
      localStorage.setItem('name',  res.data.name);
      localStorage.setItem('email', res.data.email);

      navigate('/dashboard');

    } catch (err) {
      setError(err.response?.data?.message || 'Login failed. Check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">

        <h2>Welcome back</h2>
        <p className="subtitle">Login to continue your interview practice</p>

        <form onSubmit={handleLogin}>
          <div className="field">
            <label>Email</label>
            <input
              type="email"
              placeholder="you@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div className="field">
            <label>Password</label>
            <input
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          {error && <div className="error">{error}</div>}

          <button type="submit" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        <p className="switch">
          Don't have an account? <Link to="/signup">Sign up</Link>
        </p>

      </div>
    </div>
  );
}

export default Login;