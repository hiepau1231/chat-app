import React from 'react';

interface MessageStatusProps {
  status: 'sent' | 'delivered' | 'read';
}

export const MessageStatus: React.FC<MessageStatusProps> = ({ status }) => {
  const getStatusIcon = () => {
    switch (status) {
      case 'sent':
        return '✓';
      case 'delivered':
        return '✓✓';
      case 'read':
        return '✓✓';
      default:
        return '';
    }
  };

  const getStatusColor = () => {
    switch (status) {
      case 'read':
        return 'text-blue-400';
      case 'delivered':
        return 'text-green-400';
      default:
        return 'text-gray-400';
    }
  };

  return (
    <span className={`inline-flex items-center ${getStatusColor()} ml-1 text-xs font-medium`}>
      {getStatusIcon()}
    </span>
  );
};

export default MessageStatus;