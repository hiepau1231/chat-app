import React from 'react';
import { UserStatusIndicator } from '../user/UserStatusIndicator';
import './RoomPreviewCard.css';

interface RoomPreviewCardProps {
  name: string;
  avatar?: string;
  lastMessage?: {
    content: string;
    timestamp: string;
  };
  unreadCount: number;
  isGroup: boolean;
  members: string[];
  currentUserId: string;
  isTyping?: boolean;
  onClick: () => void;
}

export const RoomPreviewCard: React.FC<RoomPreviewCardProps> = ({
  name,
  avatar,
  lastMessage,
  unreadCount,
  isGroup,
  members,
  currentUserId,
  isTyping,
  onClick
}) => {
  return (
    <div className="room-preview-card" onClick={onClick}>
      <div className="room-avatar">
        {avatar ? (
          <img src={avatar} alt={name} />
        ) : (
          <div className="avatar-placeholder">
            {name.charAt(0)}
          </div>
        )}
      </div>

      <div className="room-info">
        <div className="room-header">
          <h3 className="room-name">{name}</h3>
          {!isGroup && (
            <UserStatusIndicator 
              userId={members.find(id => id !== currentUserId) || ''} 
            />
          )}
          {lastMessage && (
            <span className="last-message-time">
              {new Date(lastMessage.timestamp).toLocaleTimeString()}
            </span>
          )}
        </div>

        <div className="room-footer">
          {isTyping ? (
            <span className="typing-text">typing...</span>
          ) : lastMessage ? (
            <p className="last-message">{lastMessage.content}</p>
          ) : (
            <p className="no-messages">No messages yet</p>
          )}

          {unreadCount > 0 && (
            <div className="unread-badge">
              {unreadCount}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}; 