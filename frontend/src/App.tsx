import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { RegisterForm } from './components/auth/RegisterForm';
import { LoginForm } from './components/auth/LoginForm';
import { Home } from './pages/Home';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);

  useEffect(() => {
    const checkAuth = () => {
      const token = localStorage.getItem('token');
      setIsAuthenticated(!!token);
    };

    checkAuth();
    window.addEventListener('storage', checkAuth);

    return () => {
      window.removeEventListener('storage', checkAuth);
    };
  }, []);

  if (isAuthenticated === null) {
    return <div>Loading...</div>;
  }

  return (
    <Router>
      <Routes>
        <Route 
          path="/" 
          element={isAuthenticated ? <Home /> : <Navigate to="/login" replace />} 
        />
        <Route 
          path="/register" 
          element={isAuthenticated ? <Navigate to="/" replace /> : <RegisterForm />} 
        />
        <Route 
          path="/login" 
          element={isAuthenticated ? <Navigate to="/" replace /> : <LoginForm />} 
        />
      </Routes>
    </Router>
  );
}

export default App;