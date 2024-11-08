import { ChatRoom } from '../models/ChatRoom';
import { api } from './api';

class ChatRoomService {
  private baseUrl = '/api/chat-rooms';

  async getChatRooms(page: number = 0, size: number = 20): Promise<{
    content: ChatRoom[];
    totalPages: number;
  }> {
    try {
      const response = await api.get(`${this.baseUrl}?page=${page}&size=${size}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching chat rooms:', error);
      throw new Error('Failed to fetch chat rooms');
    }
  }

  async createChatRoom(name: string, members: string[]): Promise<ChatRoom> {
    try {
      const response = await api.post(this.baseUrl, { name, members });
      return response.data;
    } catch (error) {
      console.error('Error creating chat room:', error);
      throw new Error('Failed to create chat room');
    }
  }
}

export const chatRoomService = new ChatRoomService();