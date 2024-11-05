import React from 'react';
import { format } from 'date-fns';
import { useAuth } from '../../contexts/AuthContext';
import { MessageStatus } from './MessageStatus';

interface MessageProps {
  message: {
    id: string;
    content: string;
    userId: string;
    timestamp: string;
    status: 'sent' | 'delivered' | 'read';
  };
  isOwnMessage: boolean;
}

export const Message: React.FC<MessageProps> = ({ message, isOwnMessage }) => {
  return (
    <div className={`flex ${isOwnMessage ? 'justify-end' : 'justify-start'} mb-4`}>
      <div
        className={`max-w-[70%] rounded-lg px-4 py-2 ${
          isOwnMessage ? 'bg-indigo-500 text-white' : 'bg-gray-200 text-gray-900'
        }`}
      >
        <div className="text-sm break-words">{message.content}</div>
        <div className="flex items-center justify-end space-x-1 mt-1">
          <span className="text-xs opacity-75">
            {format(new Date(message.timestamp), 'HH:mm')}
          </span>
          {isOwnMessage && <MessageStatus status={message.status} />}
        </div>
      </div>
    </div>
  );
};

export default Message;