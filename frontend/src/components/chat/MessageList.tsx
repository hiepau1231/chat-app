import React, { useEffect, useState } from 'react';
import { useAuth } from '../../hooks/useAuth';
import { chatApi } from '../../services/api';
import { ApiError } from '../../types/error';
import { Message } from './Message';

interface MessageListProps {
  roomId?: string;
}

export const MessageList: React.FC<MessageListProps> = ({ roomId }) => {
  const [messages, setMessages] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuth();

  useEffect(() => {
    const fetchMessages = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = roomId 
          ? await chatApi.getMessages(roomId, { limit: 50 })
          : await chatApi.getAllMessages();
        setMessages(data);
      } catch (err) {
        console.error('Error fetching messages:', err);
        if (err instanceof ApiError) {
          setError(err.message);
        } else {
          setError('Failed to load messages. Please try again.');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchMessages();
  }, [roomId]);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-red-500 text-center">
          <p>{error}</p>
          <button 
            onClick={() => window.location.reload()}
            className="mt-2 px-4 py-2 bg-red-100 text-red-700 rounded hover:bg-red-200 transition-colors"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  if (!messages.length) {
    return (
      <div className="flex items-center justify-center h-full text-gray-500">
        No messages yet
      </div>
    );
  }

  return (
    <div className="flex flex-col space-y-4 p-4 overflow-y-auto h-full">
      {messages.map((message) => (
        <Message
          key={message.id}
          message={message}
          isOwnMessage={message.userId === user?.id}
        />
      ))}
    </div>
  );
};

export default MessageList;