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
  logout: () => Promise<void>;
  refreshToken: () => Promise<string | null>;
  revokeToken: (token: string) => Promise<void>;
  revokeAllTokens: () => Promise<void>;
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
        
        // Validate token immediately
        validateAndRefreshToken();
        
        // Set up token refresh interval
        const refreshInterval = setInterval(validateAndRefreshToken, 14 * 60 * 1000); // 14 minutes

        return () => clearInterval(refreshInterval);
      } catch (error) {
        console.error('Error parsing user data:', error);
        handleAuthError();
      }
    }
  }, []);

  const validateAndRefreshToken = async () => {
    const currentToken = localStorage.getItem('token');
    if (!currentToken) return;

    try {
      // Validate current token
      const response = await fetch('http://localhost:8080/api/auth/validate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Origin': 'http://localhost:5173'
        },
        body: JSON.stringify({ token: currentToken })
      });

      const data = await response.json();
      
      if (!data.valid) {
        // If token is invalid, try to refresh
        const newToken = await refreshToken();
        if (!newToken) {
          handleAuthError();
        }
      }
    } catch (error) {
      console.error('Token validation error:', error);
      handleAuthError();
    }
  };

  const handleAuthError = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    setUser(null);
    setIsAuthenticated(false);
  };

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
      
      // Update tokens
      localStorage.setItem('token', data.accessToken);
      if (data.refreshToken) {
        localStorage.setItem('refreshToken', data.refreshToken);
      }
      
      return data.accessToken;
    } catch (error) {
      console.error('Error refreshing token:', error);
      handleAuthError();
      return null;
    }
  };

  const login = (token: string, userData: User) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);
    setIsAuthenticated(true);
  };

  const logout = async () => {
    try {
      const token = localStorage.getItem('token');
      if (token) {
        await fetch('http://localhost:8080/api/auth/logout', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'Origin': 'http://localhost:5173'
          },
          credentials: 'include'
        });
      }
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      setUser(null);
      setIsAuthenticated(false);
    }
  };

  const refreshToken = async (): Promise<string | null> => {
    return await refreshTokenRequest();
  };

  const revokeToken = async (token: string): Promise<void> => {
    try {
      const currentToken = localStorage.getItem('token');
      await fetch('http://localhost:8080/api/auth/revoke', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${currentToken}`,
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Origin': 'http://localhost:5173'
        },
        body: JSON.stringify({ token })
      });
    } catch (error) {
      console.error('Token revocation error:', error);
      throw error;
    }
  };

  const revokeAllTokens = async (): Promise<void> => {
    try {
      const currentToken = localStorage.getItem('token');
      await fetch('http://localhost:8080/api/auth/revoke-all', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${currentToken}`,
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Origin': 'http://localhost:5173'
        }
      });
      
      // Force logout after revoking all tokens
      await logout();
    } catch (error) {
      console.error('Token revocation error:', error);
      throw error;
    }
  };

  return (
    <AuthContext.Provider 
      value={{ 
        isAuthenticated, 
        user, 
        login, 
        logout,
        refreshToken,
        revokeToken,
        revokeAllTokens
      }}
    >
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
