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

// Used only during registration
export const validateRegistrationPassword = (password: string): boolean => {
  const re = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{6,}$/;
  return re.test(password);
};

// Used during login - only checks if password is not empty
export const validateLoginPassword = (password: string): boolean => {
  return password.length > 0;
};