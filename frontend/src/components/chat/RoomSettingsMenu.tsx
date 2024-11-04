import React from 'react';
import { RoomStatus } from '../../services/RoomStatusService';
import './RoomSettingsMenu.css';

interface RoomSettingsMenuProps {
  roomId: string;
  isGroup: boolean;
  currentUserId: string;
  settings: RoomStatus['settings'];
  onUpdateSettings: (settings: RoomStatus['settings']) => void;
  onClose: () => void;
}

export const RoomSettingsMenu: React.FC<RoomSettingsMenuProps> = ({
  roomId,
  isGroup,
  currentUserId,
  settings,
  onUpdateSettings,
  onClose
}) => {
  const handleMuteToggle = () => {
    const mutedUsers = new Set(settings.mutedUsers);
    if (mutedUsers.has(currentUserId)) {
      mutedUsers.delete(currentUserId);
    } else {
      mutedUsers.add(currentUserId);
    }
    onUpdateSettings({ ...settings, mutedUsers });
  };

  const handleBlockUser = (userId: string) => {
    const blockedUsers = new Set(settings.blockedUsers);
    if (blockedUsers.has(userId)) {
      blockedUsers.delete(userId);
    } else {
      blockedUsers.add(userId);
    }
    onUpdateSettings({ ...settings, blockedUsers });
  };

  return (
    <div className="room-settings-menu">
      <div className="settings-header">
        <h3>Room Settings</h3>
        <button className="close-button" onClick={onClose}>×</button>
      </div>

      <div className="settings-content">
        <div className="settings-section">
          <h4>Notifications</h4>
          <label className="setting-item">
            <span>Mute notifications</span>
            <input
              type="checkbox"
              checked={settings.mutedUsers.has(currentUserId)}
              onChange={handleMuteToggle}
            />
          </label>
        </div>

        {isGroup && (
          <div className="settings-section">
            <h4>Group Settings</h4>
            <label className="setting-item">
              <span>Allow invites</span>
              <input
                type="checkbox"
                checked={settings.allowInvites}
                onChange={(e) => onUpdateSettings({
                  ...settings,
                  allowInvites: e.target.checked
                })}
              />
            </label>
          </div>
        )}

        {!isGroup && (
          <div className="settings-section">
            <h4>Privacy</h4>
            <button
              className="block-button"
              onClick={() => handleBlockUser(currentUserId)}
            >
              {settings.blockedUsers.has(currentUserId) ? 'Unblock User' : 'Block User'}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}; 