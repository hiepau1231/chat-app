import { useState, useEffect, useCallback, useRef } from 'react';
import WebSocketService from '../lib/websocket';
import ApiService, { Message } from '../lib/api';

interface TypingUser {
  userId: string;
  timestamp: number;
}

export function useChat(roomId: string, userId: string) {
  const [messages, setMessages] = useState<Message[]>([]);
  const [isConnected, setIsConnected] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [typingUsers, setTypingUsers] = useState<TypingUser[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const typingTimeoutRef = useRef<NodeJS.Timeout>();
  const ws = WebSocketService.getInstance();
  const api = ApiService.getInstance();

  useEffect(() => {
    setIsLoading(true);
    ws.connect();
    setIsConnected(true);

    ws.subscribe(`/topic/room/${roomId}`, (message: Message) => {
      setMessages(prev => {
        // Check if the message already exists in the state
        if (!prev.some(m => m.id === message.id)) {
          return [...prev, message];
        }
        return prev;
      });
    });

    ws.subscribeToTyping(roomId, ({ userId: typingUserId, isTyping }) => {
      setTypingUsers(prev => {
        if (isTyping) {
          return [...prev.filter(u => u.userId !== typingUserId), 
                  { userId: typingUserId, timestamp: Date.now() }];
        } else {
          return prev.filter(u => u.userId !== typingUserId);
        }
      });
    });

    api.getMessages(roomId)
      .then(messages => {
        setMessages(messages);
        setError(null);
      })
      .catch(err => {
        setError(api.handleApiError(err));
      })
      .finally(() => {
        setIsLoading(false);
      });

    const interval = setInterval(() => {
      setTypingUsers(prev => 
        prev.filter(user => Date.now() - user.timestamp < 3000)
      );
    }, 1000);

    return () => {
      ws.disconnect();
      setIsConnected(false);
      clearInterval(interval);
      if (typingTimeoutRef.current) {
        clearTimeout(typingTimeoutRef.current);
      }
    };
  }, [roomId]);

  const sendTypingStatus = useCallback((isTyping: boolean) => {
    if (!ws.isConnected()) {
      return;
    }
    ws.sendTypingStatus(roomId, userId, isTyping);
  }, [roomId, userId]);

  const handleTyping = useCallback(() => {
    sendTypingStatus(true);
    if (typingTimeoutRef.current) {
      clearTimeout(typingTimeoutRef.current);
    }
    typingTimeoutRef.current = setTimeout(() => {
      sendTypingStatus(false);
    }, 1500);
  }, [sendTypingStatus]);

  const sendMessage = useCallback(async (content: string) => {
    if (!ws.isConnected()) {
      throw new Error('Not connected to chat');
    }

    try {
      const message: Message = {
        content,
        senderId: userId,
        roomId,
        type: 'text',
      };

      await api.sendMessage(message);
      // Remove the following line to prevent adding the message twice
      // ws.send('/chat.send', message);
      sendTypingStatus(false);
      if (typingTimeoutRef.current) {
        clearTimeout(typingTimeoutRef.current);
      }
    } catch (err) {
      if (err instanceof Error) {
        setError(api.handleApiError(err));
      } else {
        setError('Failed to send message');
      }
      throw err;
    }
  }, [roomId, userId, sendTypingStatus]);

  const sendFile = useCallback(async (file: File) => {
    if (!ws.isConnected()) {
      throw new Error('Not connected to chat');
    }

    try {
      const message = await api.uploadFile(roomId, file);
      // Remove the following line to prevent adding the message twice
      // ws.send('/chat.send', message);
    } catch (err) {
      if (err instanceof Error) {
        setError(api.handleApiError(err));
      } else {
        setError('Failed to send file');
      }
      throw err;
    }
  }, [roomId]);

  return {
    messages,
    isConnected,
    error,
    isLoading,
    sendMessage,
    sendFile,
    handleTyping,
    typingUsers: typingUsers.map(u => u.userId),
  };
}
