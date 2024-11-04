import React, { useEffect, useState } from 'react';
import { RoomStatus } from '../../services/RoomStatusService';
import { roomStatusService } from '../../services/RoomStatusService';
import { UserStatusIndicator } from '../user/UserStatusIndicator';
import './RoomHeader.css';
import { RoomSettingsMenu } from './RoomSettingsMenu';
import { roomSettingsService } from '../../services/RoomSettingsService';

interface RoomHeaderProps {
  roomId: string;
  roomName: string;
  isGroup: boolean;
  members: string[];
  currentUserId: string;
}

export const RoomHeader: React.FC<RoomHeaderProps> = ({
  roomId,
  roomName,
  isGroup,
  members,
  currentUserId
}) => {
  const [roomStatus, setRoomStatus] = useState<RoomStatus | null>(null);
  const [showSettings, setShowSettings] = useState(false);

  // Thêm defaultSettings
  const defaultSettings: RoomStatus['settings'] = {
    mutedUsers: new Set(),
    blockedUsers: new Set(),
    admins: new Set(),
    isPublic: false,
    allowInvites: true
  };

  useEffect(() => {
    // Fetch initial room status
    roomStatusService.getRoomStatus(roomId)
      .then(setRoomStatus)
      .catch(console.error);

    // Subscribe to room status updates
    const subscription = roomStatusService.subscribeToRoomStatus(
      roomId,
      setRoomStatus
    );

    // Subscribe to settings updates
    const settingsSubscription = roomSettingsService.subscribeToSettingsUpdates(
      roomId,
      (settings) => {
        if (roomStatus) {
          setRoomStatus({
            ...roomStatus,
            settings
          });
        }
      }
    );

    return () => {
      subscription.unsubscribe();
      settingsSubscription.unsubscribe();
    };
  }, [roomId]);

  const getOnlineStatus = () => {
    if (!roomStatus) return '';
    if (isGroup) {
      const onlineCount = roomStatus.onlineUsers.size;
      return onlineCount > 0 ? `${onlineCount} online` : '';
    } else {
      const otherUser = members.find(id => id !== currentUserId);
      if (otherUser && roomStatus.onlineUsers.has(otherUser)) {
        return 'online';
      }
      return 'offline';
    }
  };

  return (
    <div className="room-header">
      <div className="room-info">
        <h2>{roomName}</h2>
        <div className="room-status">
          {!isGroup && (
            <UserStatusIndicator
              userId={members.find(id => id !== currentUserId) || ''}
              showLastSeen
            />
          )}
          <span className="online-status">{getOnlineStatus()}</span>
        </div>
      </div>
      <div className="room-actions">
        <button 
          className="settings-button"
          onClick={() => setShowSettings(!showSettings)}
        >
          ⚙️
        </button>
        {showSettings && (
          <RoomSettingsMenu
            roomId={roomId}
            isGroup={isGroup}
            currentUserId={currentUserId}
            settings={roomStatus?.settings || defaultSettings}
            onUpdateSettings={(settings) => {
              roomStatusService.updateRoomSettings(roomId, settings);
            }}
            onClose={() => setShowSettings(false)}
          />
        )}
      </div>
    </div>
  );
}; 