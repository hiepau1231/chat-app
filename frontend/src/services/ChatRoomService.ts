import { ChatRoom } from '../models/ChatRoom';

interface ChatRoomResponse {
  rooms: ChatRoom[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
}

class ChatRoomService {
  private baseUrl = '/api/rooms';

  async getRooms(
    userId: string,
    page = 0,
    size = 20,
    search?: string
  ): Promise<ChatRoomResponse> {
    try {
      let url = `${this.baseUrl}?userId=${userId}&page=${page}&size=${size}`;
      if (search) {
        url += `&search=${encodeURIComponent(search)}`;
      }

      const response = await fetch(url);
      if (!response.ok) throw new Error('Failed to fetch rooms');
      return await response.json();
    } catch (error) {
      console.error('Error fetching rooms:', error);
      return {
        rooms: [],
        currentPage: 0,
        totalItems: 0,
        totalPages: 0
      };
    }
  }

  async getRoomById(roomId: string): Promise<ChatRoom | null> {
    try {
      const response = await fetch(`${this.baseUrl}/${roomId}`);
      if (!response.ok) throw new Error('Failed to fetch room');
      return await response.json();
    } catch (error) {
      console.error('Error fetching room:', error);
      return null;
    }
  }

  async getUnreadCount(roomId: string): Promise<number> {
    try {
      const response = await fetch(`${this.baseUrl}/${roomId}/unread`);
      if (!response.ok) throw new Error('Failed to fetch unread count');
      const data = await response.json();
      return data.count;
    } catch (error) {
      console.error('Error fetching unread count:', error);
      return 0;
    }
  }

  async createRoom(roomData: {
    name: string;
    members: string[];
    isGroup: boolean;
  }): Promise<ChatRoom> {
    try {
      const response = await fetch(`${this.baseUrl}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(roomData)
      });
      
      if (!response.ok) throw new Error('Failed to create room');
      return await response.json();
    } catch (error) {
      console.error('Error creating room:', error);
      throw error;
    }
  }
}

export const chatRoomService = new ChatRoomService(); 