import axios, { AxiosError } from 'axios';
import { LoginCredentials, RegisterData, AuthResponse, User, TokenResponse } from '../types/user';
import { ApiError } from '../types/error';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080'
});

let isRefreshing = false;
let failedQueue: any[] = [];

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  response => response,
  async (error: AxiosError) => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest?._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then(token => {
            originalRequest.headers['Authorization'] = `Bearer ${token}`;
            return api(originalRequest);
          })
          .catch(err => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
          throw new Error('No refresh token available');
        }

        const response = await authApi.refreshToken(refreshToken);
        const { token, refreshToken: newRefreshToken } = response;

        localStorage.setItem('token', token);
        localStorage.setItem('refreshToken', newRefreshToken);
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`;

        processQueue(null, token);
        return api(originalRequest);
      } catch (err) {
        processQueue(err, null);
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        return Promise.reject(err);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export const authApi = {
  login: async (credentials: LoginCredentials): Promise<AuthResponse> => {
    try {
      const response = await api.post<AuthResponse>('/api/auth/login', credentials);
      const { token, refreshToken } = response.data;
      localStorage.setItem('token', token);
      localStorage.setItem('refreshToken', refreshToken);
      return response.data;
    } catch (error) {
      if (error instanceof ApiError) {
        throw error;
      }
      throw new ApiError(500, 'LOGIN_FAILED', 'Failed to login. Please try again.');
    }
  },

  register: async (userData: RegisterData): Promise<AuthResponse> => {
    try {
      const response = await api.post<AuthResponse>('/api/auth/register', userData);
      return response.data;
    } catch (error) {
      if (error instanceof ApiError) {
        throw error;
      }
      throw new ApiError(500, 'REGISTRATION_FAILED', 'Failed to register. Please try again.');
    }
  },

  verifyToken: async (): Promise<User> => {
    try {
      const response = await api.get<User>('/api/auth/verify');
      return response.data;
    } catch (error) {
      if (error instanceof ApiError) {
        throw error;
      }
      throw new ApiError(500, 'TOKEN_VERIFICATION_FAILED', 'Failed to verify token.');
    }
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    delete api.defaults.headers.common['Authorization'];
  },

  refreshToken: async (refreshToken: string): Promise<TokenResponse> => {
    try {
      const response = await api.post<TokenResponse>('/api/auth/refresh', { refreshToken });
      return response.data;
    } catch (error) {
      if (error instanceof ApiError) {
        throw error;
      }
      throw new ApiError(500, 'TOKEN_REFRESH_FAILED', 'Failed to refresh token.');
    }
  }
};

export const chatApi = {
  getRooms: (params: any) => api.get('/api/rooms', { params }),
  getMessages: (roomId: string, params: any) => api.get(`/api/rooms/${roomId}/messages`, { params }),
  sendMessage: (roomId: string, content: string) => api.post(`/api/rooms/${roomId}/messages`, { content }),
  updateRoomSettings: (roomId: string, settings: any) => api.put(`/api/rooms/${roomId}/settings`, settings),
}; 