import { Observable, Subject } from 'rxjs';

class WebSocketService {
  private socket: WebSocket | null = null;
  private typingStatus = new Subject<Map<string, Set<string>>>();
  private userStatus = new Subject<Map<string, string>>();

  connect(url: string): void {
    this.socket = new WebSocket(url);

    this.socket.onmessage = (event) => {
      const data = JSON.parse(event.data);
      if (data.type === 'TYPING_STATUS') {
        this.typingStatus.next(new Map(Object.entries(data.status)));
      } else if (data.type === 'USER_STATUS') {
        this.userStatus.next(new Map(Object.entries(data.status)));
      }
    };
  }

  disconnect(): void {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  }

  sendMessage(message: any): void {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify(message));
    }
  }

  getTypingStatus(roomId: string): Observable<Map<string, Set<string>>> {
    return this.typingStatus.asObservable();
  }

  getUserStatusUpdates(): Observable<Map<string, string>> {
    return this.userStatus.asObservable();
  }
}

export const webSocketService = new WebSocketService(); 