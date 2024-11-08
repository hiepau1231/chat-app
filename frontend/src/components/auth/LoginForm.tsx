import React, { useState, useEffect } from 'react';
import { useNavigate, Link, Navigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { validateEmail, validateLoginPassword } from '../../types/auth';

const MAX_LOGIN_ATTEMPTS = 5;
const LOCKOUT_DURATION = 15 * 60 * 1000; // 15 minutes

export const LoginForm = () => {
  const navigate = useNavigate();
  const { login, isAuthenticated } = useAuth();
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [loginStatus, setLoginStatus] = useState<'idle' | 'loading' | 'success' | 'error'>('idle');
  const [loginAttempts, setLoginAttempts] = useState(0);
  const [lockoutUntil, setLockoutUntil] = useState<number | null>(null);

  useEffect(() => {
    // Check for existing lockout
    const storedLockout = localStorage.getItem('loginLockout');
    if (storedLockout) {
      const lockoutTime = parseInt(storedLockout);
      if (lockoutTime > Date.now()) {
        setLockoutUntil(lockoutTime);
      } else {
        localStorage.removeItem('loginLockout');
        localStorage.removeItem('loginAttempts');
      }
    }

    // Restore login attempts
    const storedAttempts = localStorage.getItem('loginAttempts');
    if (storedAttempts) {
      setLoginAttempts(parseInt(storedAttempts));
    }
  }, []);

  useEffect(() => {
    let timer: NodeJS.Timeout;
    if (lockoutUntil && lockoutUntil > Date.now()) {
      timer = setInterval(() => {
        if (lockoutUntil <= Date.now()) {
          setLockoutUntil(null);
          setLoginAttempts(0);
          localStorage.removeItem('loginLockout');
          localStorage.removeItem('loginAttempts');
        }
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [lockoutUntil]);

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  const handleLoginAttempt = () => {
    const newAttempts = loginAttempts + 1;
    setLoginAttempts(newAttempts);
    localStorage.setItem('loginAttempts', newAttempts.toString());

    if (newAttempts >= MAX_LOGIN_ATTEMPTS) {
      const lockoutTime = Date.now() + LOCKOUT_DURATION;
      setLockoutUntil(lockoutTime);
      localStorage.setItem('loginLockout', lockoutTime.toString());
      setError(`Too many failed attempts. Please try again in 15 minutes.`);
      return false;
    }
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (lockoutUntil && lockoutUntil > Date.now()) {
      const minutesLeft = Math.ceil((lockoutUntil - Date.now()) / 60000);
      setError(`Account is locked. Please try again in ${minutesLeft} minutes.`);
      return;
    }

    if (!validateEmail(formData.email.trim())) {
      setError('Please enter a valid email address');
      return;
    }

    if (!validateLoginPassword(formData.password)) {
      setError('Password is required');
      return;
    }

    setLoading(true);
    setError('');
    setLoginStatus('loading');

    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Origin': 'http://localhost:5173'
        },
        credentials: 'include',
        body: JSON.stringify(formData)
      });

      const data = await response.json();

      if (!response.ok) {
        if (!handleLoginAttempt()) {
          return;
        }
        throw new Error(data.message || 'Invalid email or password');
      }

      // Reset login attempts on successful login
      setLoginAttempts(0);
      localStorage.removeItem('loginAttempts');
      localStorage.removeItem('loginLockout');
      
      setLoginStatus('success');
      
      const userData = {
        id: data.user?.id,
        email: data.user?.email,
        username: data.user?.username,
      };

      // Store both tokens
      await login(data.accessToken, userData);
      if (data.refreshToken) {
        localStorage.setItem('refreshToken', data.refreshToken);
      }
      
      navigate('/', { replace: true });

    } catch (err) {
      setLoginStatus('error');
      setError(err instanceof Error ? err.message : 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const getRemainingLockoutTime = () => {
    if (!lockoutUntil || lockoutUntil <= Date.now()) return null;
    const minutes = Math.ceil((lockoutUntil - Date.now()) / 60000);
    return `${minutes} minute${minutes === 1 ? '' : 's'}`;
  };

  const remainingAttempts = MAX_LOGIN_ATTEMPTS - loginAttempts;
  const lockoutTime = getRemainingLockoutTime();

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full space-y-8 p-8 bg-white rounded-lg shadow">
        <div>
          <h2 className="text-center text-3xl font-extrabold text-gray-900">
            Sign in to your account
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Please enter your email and password
          </p>
        </div>
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          {error && (
            <div className="text-red-500 text-center bg-red-50 p-2 rounded">{error}</div>
          )}
          {loginStatus === 'success' && (
            <div className="text-green-500 text-center bg-green-50 p-2 rounded">
              Login successful!
            </div>
          )}
          {remainingAttempts < MAX_LOGIN_ATTEMPTS && !lockoutTime && (
            <div className="text-yellow-600 text-center bg-yellow-50 p-2 rounded">
              {remainingAttempts} login attempt{remainingAttempts === 1 ? '' : 's'} remaining
            </div>
          )}
          {lockoutTime && (
            <div className="text-red-500 text-center bg-red-50 p-2 rounded">
              Account is locked. Please try again in {lockoutTime}.
            </div>
          )}
          <div className="rounded-md shadow-sm space-y-4">
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                Email address
              </label>
              <input
                id="email"
                name="email"
                type="email"
                required
                className="appearance-none rounded-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                placeholder="Enter your email"
                value={formData.email}
                onChange={(e) => setFormData({...formData, email: e.target.value})}
                disabled={loading || !!lockoutTime}
              />
            </div>
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
                Password
              </label>
              <input
                id="password"
                name="password"
                type="password"
                required
                className="appearance-none rounded-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                placeholder="Enter your password"
                value={formData.password}
                onChange={(e) => setFormData({...formData, password: e.target.value})}
                disabled={loading || !!lockoutTime}
              />
            </div>
          </div>

          <div className="flex items-center justify-between">
            <div className="text-sm">
              <Link to="/register" className="text-indigo-600 hover:text-indigo-500">
                Don't have an account? Register
              </Link>
            </div>
            <div className="text-sm">
              <Link to="/forgot-password" className="text-indigo-600 hover:text-indigo-500">
                Forgot password?
              </Link>
            </div>
          </div>

          <div>
            <button
              type="submit"
              disabled={loading || !!lockoutTime}
              className={`group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white ${
                loading || !!lockoutTime ? 'bg-indigo-400 cursor-not-allowed' : 'bg-indigo-600 hover:bg-indigo-700'
              } focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500`}
            >
              {loading ? (
                <>
                  <span className="absolute left-0 inset-y-0 flex items-center pl-3">
                    {/* Add loading spinner icon here */}
                  </span>
                  Signing in...
                </>
              ) : (
                'Sign in'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
