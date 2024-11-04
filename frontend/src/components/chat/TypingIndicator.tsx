import React, { useEffect, useState } from 'react';
import { webSocketService } from '../../services/WebSocketService';
import '../../styles/components/TypingIndicator.css';

interface TypingIndicatorProps {
  roomId: string;
  users: Map<string, string>; // Map of userId to username
}

export const TypingIndicator: React.FC<TypingIndicatorProps> = ({ roomId, users }) => {
  const [typingUsers, setTypingUsers] = useState<Set<string>>(new Set());

  useEffect(() => {
    const subscription = webSocketService.getTypingStatus(roomId).subscribe((statusMap) => {
      const roomTyping = statusMap.get(roomId) || new Set();
      setTypingUsers(roomTyping);
    });

    return () => {
      subscription.unsubscribe();
    };
  }, [roomId]);

  if (typingUsers.size === 0) return null;

  const typingText = () => {
    const usernames = Array.from(typingUsers)
      .map(userId => users.get(userId) || 'Someone')
      .filter(username => username);

    if (usernames.length === 0) return '';
    if (usernames.length === 1) return `${usernames[0]} is typing...`;
    if (usernames.length === 2) return `${usernames[0]} and ${usernames[1]} are typing...`;
    return 'Several people are typing...';
  };

  return (
    <div className="typing-indicator">
      <div className="typing-animation">
        <span></span>
        <span></span>
        <span></span>
      </div>
      <span className="typing-text">{typingText()}</span>
    </div>
  );
}; 