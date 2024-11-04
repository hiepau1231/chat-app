import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { chatApi } from '../../services/api';
import '../../styles/ChatRoom.css';

export const ChatRoom = () => {
  const { roomId } = useParams();
  const { user } = useAuth();
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchMessages = async () => {
      try {
        setLoading(true);
        const response = await chatApi.getMessages(roomId!, {
          page: 0,
          size: 50
        });
        setMessages(response.data);
      } catch (err) {
        setError('Failed to load messages');
      } finally {
        setLoading(false);
      }
    };

    if (roomId) {
      fetchMessages();
    }
  }, [roomId]);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div className="chat-room">
      <div className="chat-header">
        <h2>Chat Room {roomId}</h2>
      </div>
      
      <div className="message-list">
        {messages.map((message: any) => (
          <div 
            key={message.id} 
            className={`message ${message.userId === user?.id ? 'sent' : 'received'}`}
          >
            <div className="message-content">{message.content}</div>
            <div className="message-time">
              {new Date(message.createdAt).toLocaleTimeString()}
            </div>
          </div>
        ))}
      </div>

      <div className="message-input">
        <input 
          type="text" 
          placeholder="Type a message..."
          // TODO: Implement message sending
        />
        <button>Send</button>
      </div>
    </div>
  );
}; 