import React, { useEffect, useRef } from 'react';
import { Message } from './Message';
import { TypingIndicator } from './TypingIndicator';
import { webSocketService } from '../../services/WebSocketService';
import '../../styles/components/MessageList.css';

interface MessageData {
  id: string;
  content: string;
  senderId: string;
  timestamp: string;
  status?: 'SENT' | 'DELIVERED' | 'READ';
}

interface MessageListProps {
  messages: MessageData[];
  currentUserId: string;
  roomId: string;
  users: Map<string, string>; // userId -> username mapping
}

export const MessageList: React.FC<MessageListProps> = ({
  messages,
  currentUserId,
  roomId,
  users
}) => {
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const [typingUsers, setTypingUsers] = React.useState<Set<string>>(new Set());
  const [messageStatuses, setMessageStatuses] = React.useState<Map<string, string>>(new Map());

  // Scroll to bottom when new messages arrive
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Subscribe to typing status updates
  useEffect(() => {
    const typingSubscription = webSocketService.getTypingStatus(roomId)
      .subscribe(statusMap => {
        const roomTyping = statusMap.get(roomId) || new Set();
        setTypingUsers(roomTyping);
      });

    // Subscribe to message status updates
    const statusSubscription = webSocketService.getMessageStatusUpdates()
      .subscribe(statusMap => {
        const newStatuses = new Map<string, string>();
        statusMap.forEach((status, messageId) => {
          newStatuses.set(messageId, status.status);
        });
        setMessageStatuses(newStatuses);
      });

    return () => {
      typingSubscription.unsubscribe();
      statusSubscription.unsubscribe();
    };
  }, [roomId]);

  // Group messages by date
  const groupMessagesByDate = (messages: MessageData[]) => {
    const groups = new Map<string, MessageData[]>();
    
    messages.forEach(message => {
      const date = new Date(message.timestamp).toLocaleDateString();
      const group = groups.get(date) || [];
      group.push(message);
      groups.set(date, group);
    });

    return groups;
  };

  const messageGroups = groupMessagesByDate(messages);

  return (
    <div className="message-list">
      {Array.from(messageGroups.entries()).map(([date, groupMessages]) => (
        <div key={date} className="message-group">
          <div className="date-separator">{date}</div>
          {groupMessages.map(message => (
            <Message
              key={message.id}
              id={message.id}
              content={message.content}
              senderId={message.senderId}
              currentUserId={currentUserId}
              timestamp={message.timestamp}
              status={messageStatuses.get(message.id) as 'SENT' | 'DELIVERED' | 'READ' | undefined}
            />
          ))}
        </div>
      ))}
      
      {typingUsers.size > 0 && (
        <TypingIndicator
          roomId={roomId}
          users={users}
        />
      )}
      <div ref={messagesEndRef} />
    </div>
  );
}; 