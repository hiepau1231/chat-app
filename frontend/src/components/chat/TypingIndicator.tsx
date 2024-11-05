import React from 'react';

interface TypingIndicatorProps {
  roomId: string;
  users: Map<string, string>; // Map of userId to username
}

export const TypingIndicator: React.FC<TypingIndicatorProps> = ({ roomId, users }) => {
  return (
    <div className="typing-indicator">
      <div className="dots">
        <span className="dot"></span>
        <span className="dot"></span>
        <span className="dot"></span>
      </div>
      <span className="text">typing...</span>
    </div>
  );
}; 