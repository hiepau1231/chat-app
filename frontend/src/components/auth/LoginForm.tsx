import React, { useState } from 'react';
import { useAuth } from '../../hooks/useAuth';
import { Link, useNavigate } from 'react-router-dom';
import '../../styles/Auth.css';

export const LoginForm = () => {
  const navigate = useNavigate();
  const { login, loading, error } = useAuth();
  const [credentials, setCredentials] = useState({
    username: '',
    password: ''
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await login(credentials);
      navigate('/chat');
    } catch (err) {
      // Error đã được xử lý trong useAuth hook
    }
  };

  return (
    <div className="auth-container">
      <form onSubmit={handleSubmit} className="auth-form">
        <div className="auth-header">
          <h1>Welcome Back</h1>
          <p>Please sign in to continue</p>
        </div>

        <div className="form-group">
          <label htmlFor="username">Username</label>
          <input
            id="username"
            type="text"
            value={credentials.username}
            onChange={e => setCredentials({...credentials, username: e.target.value})}
            placeholder="Enter your username"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            value={credentials.password}
            onChange={e => setCredentials({...credentials, password: e.target.value})}
            placeholder="Enter your password"
            required
          />
        </div>

        {error && <div className="error-message">{error}</div>}

        <button 
          type="submit" 
          className="auth-button"
          disabled={loading}
        >
          {loading ? 'Signing in...' : 'Sign In'}
        </button>

        <div className="auth-links">
          <Link to="/forgot-password">Forgot password?</Link>
          <div className="auth-divider">
            <span>OR</span>
          </div>
          <Link to="/register">Create new account</Link>
        </div>
      </form>
    </div>
  );
}; 