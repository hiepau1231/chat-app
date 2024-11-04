import React from 'react';
import '../../styles/components/MessageStatus.css';

interface MessageStatusProps {
  status: 'SENT' | 'DELIVERED' | 'READ';
}

export const MessageStatus: React.FC<MessageStatusProps> = ({ status }) => {
  const getStatusIcon = () => {
    switch (status) {
      case 'SENT':
        return '✓';
      case 'DELIVERED':
        return '✓✓';
      case 'READ':
        return '✓✓';
      default:
        return '';
    }
  };

  return (
    <span className={`message-status ${status.toLowerCase()}`}>
      {getStatusIcon()}
    </span>
  );
}; 