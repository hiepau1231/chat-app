import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
  private client: Client;
  private static instance: WebSocketService;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;

  private constructor() {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      debug: function (str) {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = () => {
      console.log('Connected to WebSocket');
      this.reconnectAttempts = 0;
    };

    this.client.onDisconnect = () => {
      console.log('Disconnected from WebSocket');
      this.handleDisconnect();
    };

    this.client.onStompError = (frame) => {
      console.error('STOMP error', frame);
      this.handleError(frame);
    };
  }

  private handleDisconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
      setTimeout(() => {
        this.connect();
      }, 5000 * this.reconnectAttempts); // Exponential backoff
    } else {
      console.error('Max reconnection attempts reached');
    }
  }

  private handleError(frame: any) {
    if (frame.headers && frame.headers['message']) {
      console.error('STOMP Error Message:', frame.headers['message']);
    }
    this.handleDisconnect();
  }

  public static getInstance(): WebSocketService {
    if (!WebSocketService.instance) {
      WebSocketService.instance = new WebSocketService();
    }
    return WebSocketService.instance;
  }

  public connect(): void {
    try {
      this.client.activate();
    } catch (error) {
      console.error('Failed to connect to WebSocket:', error);
      this.handleDisconnect();
    }
  }

  public disconnect(): void {
    try {
      this.client.deactivate();
    } catch (error) {
      console.error('Error disconnecting from WebSocket:', error);
    }
  }

  public subscribe(destination: string, callback: (message: any) => void): void {
    try {
      if (!this.client.connected) {
        console.warn('WebSocket not connected. Attempting to reconnect...');
        this.connect();
        setTimeout(() => this.subscribe(destination, callback), 1000);
        return;
      }

      this.client.subscribe(destination, (message) => {
        try {
          callback(JSON.parse(message.body));
        } catch (error) {
          console.error('Error parsing message:', error);
        }
      });
    } catch (error) {
      console.error('Error subscribing to destination:', error);
    }
  }

  public send(destination: string, body: any): void {
    try {
      if (!this.client.connected) {
        throw new Error('WebSocket not connected');
      }

      this.client.publish({
        destination,
        body: JSON.stringify(body),
      });
    } catch (error) {
      console.error('Error sending message:', error);
      this.handleDisconnect();
    }
  }

  public sendTypingStatus(roomId: string, userId: string, isTyping: boolean): void {
    this.send('/chat.typing', {
      roomId,
      userId,
      isTyping
    });
  }

  public subscribeToTyping(roomId: string, callback: (data: {userId: string, isTyping: boolean}) => void): void {
    this.subscribe(`/topic/room/${roomId}/typing`, callback);
  }

  public isConnected(): boolean {
    return this.client.connected;
  }
}

export default WebSocketService;