export interface RegisterRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  email: string;
}

export const validateEmail = (email: string): boolean => {
  const re = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  return re.test(email);
};

// Used only during registration - simplified validation
export const validateRegistrationPassword = (password: string): boolean => {
  return password.length >= 4; // Chỉ kiểm tra độ dài tối thiểu 4 ký tự
};

// Used during login - only checks if password is not empty
export const validateLoginPassword = (password: string): boolean => {
  return password.length > 0;
};