import React, { useEffect, useState } from 'react';
import { webSocketService } from '../../services/WebSocketService';
import './UserStatusIndicator.css';

interface UserStatusIndicatorProps {
  userId: string;
}

export const UserStatusIndicator: React.FC<UserStatusIndicatorProps> = ({ userId }) => {
  const [status, setStatus] = useState<string>('offline');

  useEffect(() => {
    const subscription = webSocketService.getUserStatusUpdates().subscribe(statusMap => {
      const userStatus = statusMap.get(userId);
      if (userStatus) {
        setStatus(userStatus);
      }
    });

    return () => {
      subscription.unsubscribe();
    };
  }, [userId]);

  return (
    <div className={`status-indicator ${status}`}>
      <span className="status-dot"></span>
      <span className="status-text">{status}</span>
    </div>
  );
}; 