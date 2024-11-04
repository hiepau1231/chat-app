import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../store';
import { login as loginAction, register as registerAction, logout as logoutAction } from '../store/slices/authSlice';
import { LoginCredentials, RegisterData } from '../types/user';
import { ApiError } from '../types/error';

export const useAuth = () => {
  const dispatch = useDispatch();
  const auth = useSelector((state: RootState) => state.auth);

  const login = async (credentials: LoginCredentials) => {
    try {
      await dispatch(loginAction(credentials)).unwrap();
    } catch (error) {
      if (error instanceof ApiError) {
        throw error;
      }
      throw new ApiError(500, 'LOGIN_FAILED', 'Failed to login. Please try again.');
    }
  };

  const register = async (userData: RegisterData) => {
    try {
      await dispatch(registerAction(userData)).unwrap();
    } catch (error) {
      if (error instanceof ApiError) {
        throw error;
      }
      throw new ApiError(500, 'REGISTRATION_FAILED', 'Failed to register. Please try again.');
    }
  };

  const logout = () => {
    dispatch(logoutAction());
  };

  return {
    user: auth.user,
    token: auth.token,
    loading: auth.loading,
    error: auth.error,
    login,
    register,
    logout
  };
}; 