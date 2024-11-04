import React, { useEffect } from 'react';
import { MessageStatus } from './MessageStatus';
import { webSocketService } from '../../services/WebSocketService';
import '../../styles/components/Message.css';

interface MessageProps {
  id: string;
  content: string;
  senderId: string;
  currentUserId: string;
  timestamp: string;
  status?: 'SENT' | 'DELIVERED' | 'READ';
}

export const Message: React.FC<MessageProps> = ({
  id,
  content,
  senderId,
  currentUserId,
  timestamp,
  status = 'SENT'
}) => {
  const isSentByMe = senderId === currentUserId;

  useEffect(() => {
    if (!isSentByMe) {
      // Mark message as delivered when received
      webSocketService.markMessageAsDelivered(id, currentUserId);
      
      // Mark as read when message is visible
      const observer = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting) {
            webSocketService.markMessageAsRead(id, currentUserId);
            observer.disconnect();
          }
        },
        { threshold: 1.0 }
      );

      const messageElement = document.getElementById(`message-${id}`);
      if (messageElement) {
        observer.observe(messageElement);
      }

      return () => observer.disconnect();
    }
  }, [id, currentUserId, isSentByMe]);

  return (
    <div 
      id={`message-${id}`}
      className={`message ${isSentByMe ? 'sent' : 'received'}`}
    >
      <div className="message-content">
        {content}
        {isSentByMe && <MessageStatus status={status} />}
      </div>
      <div className="message-timestamp">
        {new Date(timestamp).toLocaleTimeString()}
      </div>
    </div>
  );
}; 