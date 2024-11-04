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
export const validatePassword = (password: string): boolean => {
  const re = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{6,}$/;
  return re.test(password);
};
