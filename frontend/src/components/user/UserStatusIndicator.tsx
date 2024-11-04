import React, { useEffect, useState } from 'react';
import { UserStatus } from '../../services/WebSocketService';
import { webSocketService } from '../../services/WebSocketService';
import './UserStatusIndicator.css';

interface UserStatusIndicatorProps {
  userId: string;
  showLastSeen?: boolean;
}

export const UserStatusIndicator: React.FC<UserStatusIndicatorProps> = ({
  userId,
  showLastSeen = false
}) => {
  const [status, setStatus] = useState<UserStatus | undefined>();

  useEffect(() => {
    const subscription = webSocketService.getUserStatusUpdates()
      .subscribe(statusMap => {
        setStatus(statusMap.get(userId));
      });

    return () => subscription.unsubscribe();
  }, [userId]);

  if (!status) return null;

  return (
    <div className="user-status">
      <span className={`status-dot ${status.online ? 'online' : 'offline'}`} />
      {showLastSeen && !status.online && (
        <span className="last-seen">
          Last seen {new Date(status.lastSeen).toLocaleString()}
        </span>
      )}
    </div>
  );
}; 