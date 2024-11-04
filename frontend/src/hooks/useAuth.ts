import { useState } from 'react';
import axios from 'axios';
import { RegisterRequest, AuthResponse } from '../types/auth';
export const useAuth = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const register = async (data: RegisterRequest) => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.post<AuthResponse>(
        'http://localhost:8080/api/auth/register',
        data,
        {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
          },
          withCredentials: true
        }
      );
      console.log('Registration response:', response);
      return response.data;
    } catch (err: any) {
      console.error('Registration error:', err);
      if (err.response) {
        console.error('Error response:', err.response.data);
        console.error('Error status:', err.response.status);
        console.error('Error headers:', err.response.headers);
      } else if (err.request) {
        console.error('Error request:', err.request);
      } else {
        console.error('Error message:', err.message);
      }
      setError(
        err.response?.data?.message || 
        err.message ||
        'Registration failed. Please try again.'
      );
      throw err;
    } finally {
      setLoading(false);
    }
  };
  return { register, loading, error };
};
