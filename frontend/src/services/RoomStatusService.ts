import { webSocketService } from './WebSocketService';

export interface RoomStatus {
  roomId: string;
  onlineUsers: Set<string>;
  typingUsers: Set<string>;
  lastActivity: string;
  settings: {
    mutedUsers: Set<string>;
    blockedUsers: Set<string>;
    admins: Set<string>;
    isPublic: boolean;
    allowInvites: boolean;
  };
}

class RoomStatusService {
  private baseUrl = '/api/rooms';

  async getRoomStatus(roomId: string): Promise<RoomStatus> {
    try {
      const response = await fetch(`${this.baseUrl}/${roomId}/status`);
      if (!response.ok) throw new Error('Failed to fetch room status');
      return await response.json();
    } catch (error) {
      console.error('Error fetching room status:', error);
      throw error;
    }
  }

  async updateRoomSettings(roomId: string, settings: RoomStatus['settings']): Promise<void> {
    try {
      const response = await fetch(`${this.baseUrl}/${roomId}/settings`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(settings)
      });
      if (!response.ok) throw new Error('Failed to update room settings');
    } catch (error) {
      console.error('Error updating room settings:', error);
      throw error;
    }
  }

  subscribeToRoomStatus(roomId: string, callback: (status: RoomStatus) => void) {
    const subscription = webSocketService.client.subscribe(
      `/topic/room/${roomId}/status`,
      (message) => {
        const status: RoomStatus = JSON.parse(message.body);
        callback(status);
      }
    );
    return subscription;
  }

  updateUserActivity(roomId: string, userId: string) {
    webSocketService.updateActivity(roomId, userId);
  }
}

export const roomStatusService = new RoomStatusService(); 