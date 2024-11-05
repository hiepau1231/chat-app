import React, { createContext, useState, useEffect, useContext } from 'react';

interface User {
  id: string;
  email: string;
  username?: string;
}

interface AuthContextType {
  isAuthenticated: boolean;
  user: User | null;
  login: (token: string, userData: User) => void;
  logout: () => void;
  refreshToken: () => Promise<string | null>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    // Check token and user data when component mounts
    const token = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');
    const refreshToken = localStorage.getItem('refreshToken');

    if (token && storedUser && refreshToken) {
      try {
        const userData = JSON.parse(storedUser);
        setUser(userData);
        setIsAuthenticated(true);
        
        // Set up token refresh interval
        const refreshInterval = setInterval(async () => {
          const newToken = await refreshTokenRequest();
          if (newToken) {
            localStorage.setItem('token', newToken);
          } else {
            // If refresh fails, log out the user
            logout();
          }
        }, 14 * 60 * 1000); // Refresh token every 14 minutes

        return () => clearInterval(refreshInterval);
      } catch (error) {
        console.error('Error parsing user data:', error);
        // Clear invalid data
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
      }
    }
  }, []);

  const refreshTokenRequest = async (): Promise<string | null> => {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) return null;

    try {
      const response = await fetch('http://localhost:8080/api/auth/refresh', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Origin': 'http://localhost:5173'
        },
        credentials: 'include',
        body: JSON.stringify({ refreshToken })
      });

      if (!response.ok) {
        throw new Error('Token refresh failed');
      }

      const data = await response.json();
      return data.accessToken;
    } catch (error) {
      console.error('Error refreshing token:', error);
      return null;
    }
  };

  const login = (token: string, userData: User) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
    setIsAuthenticated(true);
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    setUser(null);
    setIsAuthenticated(false);
  };

  const refreshToken = async (): Promise<string | null> => {
    const newToken = await refreshTokenRequest();
    if (newToken) {
      localStorage.setItem('token', newToken);
      return newToken;
    }
    return null;
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, login, logout, refreshToken }}>
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