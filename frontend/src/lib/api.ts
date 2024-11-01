const API_BASE_URL = 'http://localhost:8080/api';
const MAX_RETRIES = 3;
const RETRY_DELAY = 1000; // 1 second

export interface Message {
  id?: string;
  content: string;
  senderId: string;
  roomId: string;
  type: 'text' | 'image' | 'file';
  fileUrl?: string;
  fileName?: string;
  createdAt?: string;
}

export interface ChatRoom {
  id?: string;
  name: string;
  isPrivate: boolean;
  members: string[];
  type: 'direct' | 'group';
  createdAt?: string;
}

export interface ApiError extends Error {
  status?: number;
  retryAfter?: number;
}

class ApiService {
  private static instance: ApiService;
  private isLoading: boolean = false;
  private activeRequests: Set<string> = new Set();

  private constructor() {}

  public static getInstance(): ApiService {
    if (!ApiService.instance) {
      ApiService.instance = new ApiService();
    }
    return ApiService.instance;
  }

  public getLoadingState(): boolean {
    return this.isLoading;
  }

  private async delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  private getRequestKey(endpoint: string, options: RequestInit = {}): string {
    return `${options.method || 'GET'}-${endpoint}`;
  }

  private async retryRequest(
    endpoint: string,
    options: RequestInit = {},
    retryCount: number = 0
  ): Promise<any> {
    const requestKey = this.getRequestKey(endpoint, options);

    try {
      if (this.activeRequests.has(requestKey)) {
        throw new Error('Request already in progress');
      }

      this.activeRequests.add(requestKey);
      this.isLoading = true;

      const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...options,
        headers: {
          'Content-Type': 'application/json',
          ...options.headers,
        },
      });

      if (!response.ok) {
        const error: ApiError = new Error(`API error: ${response.statusText}`);
        error.status = response.status;
        
        if (response.status === 429) { // Rate limit
          error.retryAfter = parseInt(response.headers.get('Retry-After') || '5');
        }
        
        throw error;
      }

      return response.json();
    } catch (error) {
      if (retryCount < MAX_RETRIES) {
        const delay = error instanceof Error && (error as ApiError).retryAfter
          ? (error as ApiError).retryAfter! * 1000
          : RETRY_DELAY * Math.pow(2, retryCount);

        console.warn(`Retrying request (${retryCount + 1}/${MAX_RETRIES}) after ${delay}ms`);
        await this.delay(delay);
        return this.retryRequest(endpoint, options, retryCount + 1);
      }
      throw error;
    } finally {
      this.activeRequests.delete(requestKey);
      if (this.activeRequests.size === 0) {
        this.isLoading = false;
      }
    }
  }

  // Messages
  async getMessages(roomId: string): Promise<Message[]> {
    return this.retryRequest(`/messages/room/${roomId}`);
  }

  async sendMessage(message: Message): Promise<Message> {
    return this.retryRequest('/messages', {
      method: 'POST',
      body: JSON.stringify(message),
    });
  }

  // Chat Rooms
  async getRooms(userId: string): Promise<ChatRoom[]> {
    return this.retryRequest(`/rooms/user/${userId}`);
  }

  async createRoom(room: ChatRoom): Promise<ChatRoom> {
    return this.retryRequest('/rooms', {
      method: 'POST',
      body: JSON.stringify(room),
    });
  }

  async uploadFile(roomId: string, file: File): Promise<Message> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.retryRequest(`/messages/room/${roomId}/file`, {
      method: 'POST',
      headers: {
        // Don't set Content-Type to let browser set it with boundary
      },
      body: formData,
    });
  }

  // Error Handling Helper
  public handleApiError(error: any): string {
    if (error instanceof Error) {
      const apiError = error as ApiError;
      if (apiError.status) {
        switch (apiError.status) {
          case 401:
            return 'Unauthorized. Please log in again.';
          case 403:
            return 'You do not have permission to perform this action.';
          case 404:
            return 'The requested resource was not found.';
          case 429:
            return `Too many requests. Please try again in ${apiError.retryAfter} seconds.`;
          case 500:
            return 'Server error. Please try again later.';
          default:
            return apiError.message;
        }
      }
      return error.message;
    }
    return 'An unknown error occurred';
  }
}

export default ApiService;
