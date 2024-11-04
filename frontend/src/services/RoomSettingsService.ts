import { webSocketService } from './WebSocketService';

export interface RoomSettings {
  mutedUsers: Set<string>;
  blockedUsers: Set<string>;
  admins: Set<string>;
  isPublic: boolean;
  allowInvites: boolean;
}

class RoomSettingsService {
  private baseUrl = '/api/rooms';

  async getRoomSettings(roomId: string): Promise<RoomSettings> {
    try {
      const response = await fetch(`${this.baseUrl}/${roomId}/settings`);
      if (!response.ok) throw new Error('Failed to fetch room settings');
      return await response.json();
    } catch (error) {
      console.error('Error fetching room settings:', error);
      throw error;
    }
  }

  async updateRoomSettings(roomId: string, settings: RoomSettings): Promise<void> {
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

  subscribeToSettingsUpdates(roomId: string, callback: (settings: RoomSettings) => void) {
    return webSocketService.client.subscribe(
      `/topic/room/${roomId}/settings`,
      (message) => {
        const settings: RoomSettings = JSON.parse(message.body);
        callback(settings);
      }
    );
  }

  async muteRoom(roomId: string, userId: string): Promise<void> {
    const settings = await this.getRoomSettings(roomId);
    const mutedUsers = new Set(settings.mutedUsers);
    mutedUsers.add(userId);
    await this.updateRoomSettings(roomId, { ...settings, mutedUsers });
  }

  async unmuteRoom(roomId: string, userId: string): Promise<void> {
    const settings = await this.getRoomSettings(roomId);
    const mutedUsers = new Set(settings.mutedUsers);
    mutedUsers.delete(userId);
    await this.updateRoomSettings(roomId, { ...settings, mutedUsers });
  }

  async blockUser(roomId: string, userId: string): Promise<void> {
    const settings = await this.getRoomSettings(roomId);
    const blockedUsers = new Set(settings.blockedUsers);
    blockedUsers.add(userId);
    await this.updateRoomSettings(roomId, { ...settings, blockedUsers });
  }

  async unblockUser(roomId: string, userId: string): Promise<void> {
    const settings = await this.getRoomSettings(roomId);
    const blockedUsers = new Set(settings.blockedUsers);
    blockedUsers.delete(userId);
    await this.updateRoomSettings(roomId, { ...settings, blockedUsers });
  }
}

export const roomSettingsService = new RoomSettingsService(); 