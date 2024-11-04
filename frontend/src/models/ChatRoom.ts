export interface ChatRoom {
  id: string;
  name: string;
  avatar?: string;
  lastMessage?: {
    id: string;
    content: string;
    senderId: string;
    timestamp: string;
  };
  unreadCount: number;
  members: string[];
  isGroup: boolean;
  createdAt: string;
  updatedAt: string;
} 