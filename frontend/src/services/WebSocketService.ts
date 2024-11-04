import { Client } from '@stomp/stompjs';
import { BehaviorSubject } from 'rxjs';

export interface TypingStatus {
  userId: string;
  roomId: string;
  isTyping: boolean;
  timestamp: number;
}

export interface MessageStatus {
  messageId: string;
  userId: string;
  status: 'SENT' | 'DELIVERED' | 'READ';
  timestamp: number;
}

export interface UserStatus {
  userId: string;
  online: boolean;
  lastSeen: string;
  currentRoomId?: string;
  lastActivity: string;
}

class WebSocketService {
  private client: Client;
  private typingStatus = new BehaviorSubject<Map<string, Set<string>>>(new Map());
  private messageStatus = new BehaviorSubject<Map<string, MessageStatus>>(new Map());
  private userStatus = new BehaviorSubject<Map<string, UserStatus>>(new Map());

  constructor() {
    this.client = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = () => {
      console.log('Connected to WebSocket');
      this.subscribeToTyping();
      this.subscribeToMessageStatus();
      this.subscribeToUserStatus();
    };

    this.client.onStompError = (frame) => {
      console.error('STOMP error', frame);
    };

    this.client.activate();
  }

  private subscribeToTyping() {
    this.client.subscribe('/topic/room/*/typing', (message) => {
      const status: TypingStatus = JSON.parse(message.body);
      this.updateTypingStatus(status);
    });
  }

  private updateTypingStatus(status: TypingStatus) {
    const currentStatus = this.typingStatus.value;
    const roomTyping = currentStatus.get(status.roomId) || new Set();

    if (status.isTyping) {
      roomTyping.add(status.userId);
    } else {
      roomTyping.delete(status.userId);
    }

    currentStatus.set(status.roomId, roomTyping);
    this.typingStatus.next(new Map(currentStatus));
  }

  sendTypingStatus(roomId: string, userId: string, isTyping: boolean) {
    const status: TypingStatus = {
      userId,
      roomId,
      isTyping,
      timestamp: Date.now()
    };

    this.client.publish({
      destination: `/app/room/${roomId}/typing`,
      body: JSON.stringify(status)
    });
  }

  getTypingStatus(roomId: string) {
    return this.typingStatus.asObservable();
  }

  private subscribeToMessageStatus() {
    this.client.subscribe('/user/queue/message-status', (message) => {
      const status: MessageStatus = JSON.parse(message.body);
      this.updateMessageStatus(status);
    });
  }

  private updateMessageStatus(status: MessageStatus) {
    const currentStatus = this.messageStatus.value;
    currentStatus.set(status.messageId, status);
    this.messageStatus.next(new Map(currentStatus));
  }

  sendMessageStatus(messageId: string, userId: string, status: MessageStatus['status']) {
    const statusUpdate: MessageStatus = {
      messageId,
      userId,
      status,
      timestamp: Date.now()
    };

    this.client.publish({
      destination: '/app/message/status',
      body: JSON.stringify(statusUpdate)
    });
  }

  markMessageAsRead(messageId: string, userId: string) {
    this.client.publish({
      destination: `/app/message/read/${messageId}`,
      headers: { userId }
    });
  }

  markMessageAsDelivered(messageId: string, userId: string) {
    this.client.publish({
      destination: `/app/message/delivered/${messageId}`,
      headers: { userId }
    });
  }

  getMessageStatus(messageId: string) {
    return this.messageStatus.value.get(messageId);
  }

  getMessageStatusUpdates() {
    return this.messageStatus.asObservable();
  }

  private subscribeToUserStatus() {
    this.client.subscribe('/topic/user/*/status', (message) => {
      const status: UserStatus = JSON.parse(message.body);
      this.updateUserStatus(status);
    });
  }

  private updateUserStatus(status: UserStatus) {
    const currentStatus = this.userStatus.value;
    currentStatus.set(status.userId, status);
    this.userStatus.next(new Map(currentStatus));
  }

  getUserStatus(userId: string) {
    return this.userStatus.value.get(userId);
  }

  getUserStatusUpdates() {
    return this.userStatus.asObservable();
  }

  updateActivity(roomId: string, userId: string) {
    this.client.publish({
      destination: `/app/rooms/${roomId}/activity`,
      headers: { userId }
    });
  }
}

export const webSocketService = new WebSocketService(); 