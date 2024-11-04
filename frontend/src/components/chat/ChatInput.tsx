import React, { useState, useEffect, useRef } from 'react';
import { webSocketService } from '../../services/WebSocketService';
import '../../styles/components/ChatInput.css';

interface ChatInputProps {
  roomId: string;
  userId: string;
  onSendMessage: (message: string) => void;
}

export const ChatInput: React.FC<ChatInputProps> = ({ roomId, userId, onSendMessage }) => {
  const [message, setMessage] = useState('');
  const typingTimeoutRef = useRef<NodeJS.Timeout>();

  const handleTyping = () => {
    // Clear existing timeout
    if (typingTimeoutRef.current) {
      clearTimeout(typingTimeoutRef.current);
    }

    // Send typing status
    webSocketService.sendTypingStatus(roomId, userId, true);

    // Set timeout to clear typing status
    typingTimeoutRef.current = setTimeout(() => {
      webSocketService.sendTypingStatus(roomId, userId, false);
    }, 2000);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (message.trim()) {
      onSendMessage(message);
      setMessage('');
      // Clear typing status on send
      webSocketService.sendTypingStatus(roomId, userId, false);
    }
  };

  useEffect(() => {
    // Cleanup typing timeout on unmount
    return () => {
      if (typingTimeoutRef.current) {
        clearTimeout(typingTimeoutRef.current);
      }
    };
  }, []);

  return (
    <form onSubmit={handleSubmit} className="chat-input-container">
      <input
        type="text"
        value={message}
        onChange={(e) => {
          setMessage(e.target.value);
          handleTyping();
        }}
        placeholder="Type a message..."
        className="chat-input"
      />
      <button type="submit" className="send-button">
        Send
      </button>
    </form>
  );
}; 