export interface Message {
  id: string;
  content: string;
  senderId: string;
  timestamp: string;
}

export interface ChatRoom {
  id: string;
  name: string;
  avatar: string | null;
  lastMessage?: Message;
  unreadCount: number;
  members: string[];
  isGroup: boolean;
  createdAt: string;
  updatedAt: string;
} 