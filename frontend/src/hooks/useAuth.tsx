import { useState } from 'react';

interface RegisterData {
  email: string;
  password: string;
}

export const useAuth = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const register = async (data: RegisterData) => {
    setLoading(true);
    setError(null);
    try {
      console.log('Sending registration request:', data);

      const response = await fetch('http://localhost:8081/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: data.email,
          password: data.password
        }),
        credentials: 'include'
      });

      const result = await response.json();
      console.log('Registration response:', result);
      
      if (!response.ok) {
        throw new Error(result.message || result.error || 'Registration failed');
      }

      localStorage.setItem('token', result.token);
      localStorage.setItem('refreshToken', result.refreshToken);
      
      return result;
    } catch (err) {
      console.error('Registration error:', err);
      const message = err instanceof Error ? err.message : 'Registration failed';
      setError(message);
      throw new Error(message);
    } finally {
      setLoading(false);
    }
  };

  return { register, loading, error };
}; 