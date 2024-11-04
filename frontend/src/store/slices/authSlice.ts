import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { User, LoginCredentials, RegisterData, AuthResponse } from '../../types/user';
import { authApi } from '../../services/api';

interface AuthState {
  user: User | null;
  token: string | null;
  refreshToken: string | null;
  loading: boolean;
  error: string | null;
}

const initialState: AuthState = {
  user: null,
  token: localStorage.getItem('token'),
  refreshToken: localStorage.getItem('refreshToken'),
  loading: false,
  error: null
};

export const login = createAsyncThunk(
  'auth/login',
  async (credentials: LoginCredentials) => {
    const response = await authApi.login(credentials);
    return response.data;
  }
);

export const register = createAsyncThunk(
  'auth/register',
  async (userData: RegisterData) => {
    const response = await authApi.register(userData);
    return response.data;
  }
);

export const verifyToken = createAsyncThunk(
  'auth/verify',
  async () => {
    const response = await authApi.verifyToken();
    return response.data;
  }
);

export const refreshToken = createAsyncThunk(
  'auth/refreshToken',
  async (refreshToken: string) => {
    const response = await authApi.refreshToken(refreshToken);
    return response;
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.refreshToken = null;
      state.error = null;
      authApi.logout();
    },
    clearError: (state) => {
      state.error = null;
    }
  },
  extraReducers: (builder) => {
    // Login
    builder.addCase(login.pending, (state) => {
      state.loading = true;
      state.error = null;
    });
    builder.addCase(login.fulfilled, (state, action: PayloadAction<AuthResponse>) => {
      state.loading = false;
      state.user = action.payload.user;
      state.token = action.payload.token;
      state.refreshToken = action.payload.refreshToken;
      localStorage.setItem('token', action.payload.token);
      localStorage.setItem('refreshToken', action.payload.refreshToken);
    });
    builder.addCase(login.rejected, (state, action) => {
      state.loading = false;
      state.error = action.error.message || 'Login failed';
    });

    // Register
    builder.addCase(register.pending, (state) => {
      state.loading = true;
      state.error = null;
    });
    builder.addCase(register.fulfilled, (state, action: PayloadAction<AuthResponse>) => {
      state.loading = false;
      state.user = action.payload.user;
      state.token = action.payload.token;
      state.refreshToken = action.payload.refreshToken;
      localStorage.setItem('token', action.payload.token);
      localStorage.setItem('refreshToken', action.payload.refreshToken);
    });
    builder.addCase(register.rejected, (state, action) => {
      state.loading = false;
      state.error = action.error.message || 'Registration failed';
    });

    // Verify token
    builder.addCase(verifyToken.fulfilled, (state, action: PayloadAction<User>) => {
      state.user = action.payload;
    });
    builder.addCase(verifyToken.rejected, (state) => {
      state.user = null;
      state.token = null;
      state.refreshToken = null;
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
    });

    // Refresh token
    builder.addCase(refreshToken.fulfilled, (state, action) => {
      state.token = action.payload.token;
      state.refreshToken = action.payload.refreshToken;
      localStorage.setItem('token', action.payload.token);
      localStorage.setItem('refreshToken', action.payload.refreshToken);
    });
    builder.addCase(refreshToken.rejected, (state) => {
      state.user = null;
      state.token = null;
      state.refreshToken = null;
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
    });
  }
});

export const { logout, clearError } = authSlice.actions;
export default authSlice.reducer; 