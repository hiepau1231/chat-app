export interface AuthError {
  code: string;
  message: string;
  details?: Record<string, string>;
}

export class ApiError extends Error {
  constructor(
    public statusCode: number,
    public code: string,
    message: string,
    public details?: Record<string, string>
  ) {
    super(message);
    this.name = 'ApiError';
  }
} 