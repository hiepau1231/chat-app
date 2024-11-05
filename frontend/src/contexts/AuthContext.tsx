import React, { createContext, useContext, useState, useEffect } from 'react';

interface User {
  id: string;
  username: string;
  email: string;
}

interface AuthContextType {
  isAuthenticated: boolean;
  user: User | null;
  login: (token: string, user: User) => void;
  logout: () => void;
  error: string | null;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<User | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');
    
    if (token) {
      if (storedUser) {
        try {
          const parsedUser = JSON.parse(storedUser);
          setIsAuthenticated(true);
          setUser(parsedUser);
          setError(null);
        } catch (error) {
          console.error('Error parsing user data:', error);
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          setIsAuthenticated(false);
          setUser(null);
          setError('Invalid user data');
        }
      } else {
        setIsAuthenticated(false);
        setUser(null);
        localStorage.removeItem('token');
      }
    }
  }, []);

  const login = (token: string, userData: User) => {
    try {
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(userData));
      setIsAuthenticated(true);
      setUser(userData);
      setError(null);
    } catch (error) {
      console.error('Error saving auth data:', error);
      setError('Failed to save authentication data');
    }
  };

  const logout = () => {
    try {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      setIsAuthenticated(false);
      setUser(null);
      setError(null);
    } catch (error) {
      console.error('Error during logout:', error);
      setError('Failed to logout');
    }
  };

  const value = {
    isAuthenticated,
    user,
    login,
    logout,
    error
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}; 