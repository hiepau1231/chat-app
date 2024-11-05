import { ChatRoom } from '../models/ChatRoom';

class ChatRoomService {
  private baseUrl = 'http://localhost:8082/api/chat-rooms';

  async getChatRooms(page: number = 0, size: number = 20): Promise<{
    content: ChatRoom[];
    totalPages: number;
  }> {
    const token = localStorage.getItem('token');
    const response = await fetch(
      `${this.baseUrl}?page=${page}&size=${size}`,
      {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    );

    if (!response.ok) {
      throw new Error('Failed to fetch chat rooms');
    }

    return response.json();
  }

  async createChatRoom(name: string, members: string[]): Promise<ChatRoom> {
    const token = localStorage.getItem('token');
    const response = await fetch(this.baseUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ name, members })
    });

    if (!response.ok) {
      throw new Error('Failed to create chat room');
    }

    return response.json();
  }
}

export const chatRoomService = new ChatRoomService();