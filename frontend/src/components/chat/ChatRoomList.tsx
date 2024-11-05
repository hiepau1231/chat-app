import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { ChatRoom } from '../../models/ChatRoom';
import { chatRoomService } from '../../services/ChatRoomService';
import { TypingIndicator } from './TypingIndicator';
import { webSocketService } from '../../services/WebSocketService';
import '../../styles/components/ChatRoomList.css';
import { UserStatusIndicator } from '../user/UserStatusIndicator';

interface ChatRoomListProps {
  currentUserId: string;
  onRoomSelect: (roomId: string) => void;
  isLoading: boolean;
}

// Mock data được định nghĩa bên ngoài component để tránh re-render
const MOCK_ROOMS: ChatRoom[] = [
  {
    id: '1',
    name: 'John Doe',
    avatar: null,
    lastMessage: {
      id: 'm1',
      content: 'Hey, how are you?',
      senderId: 'user2',
      timestamp: new Date().toISOString()
    },
    unreadCount: 2,
    members: ['user123', 'user2'],
    isGroup: false,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  }
];

export const ChatRoomList: React.FC<ChatRoomListProps> = ({
  currentUserId,
  onRoomSelect,
  isLoading: initialLoading
}) => {
  const [rooms, setRooms] = useState<ChatRoom[]>([]);
  const [loading, setLoading] = useState(initialLoading);
  const [searchQuery, setSearchQuery] = useState('');
  const [typingStatuses, setTypingStatuses] = useState<Map<string, Set<string>>>(new Map());
  const [showNewChatModal, setShowNewChatModal] = useState(false);
  const [initialized, setInitialized] = useState(false);

  // Fetch rooms chỉ khi component mount
  useEffect(() => {
    if (!initialized) {
      setRooms(MOCK_ROOMS);
      setInitialized(true);
    }
  }, [initialized]);

  // Xử lý search riêng biệt
  const filteredRooms = useMemo(() => 
    rooms.filter(room => 
      room.name.toLowerCase().includes(searchQuery.toLowerCase())
    ),
    [rooms, searchQuery]
  );

  // WebSocket subscription
  useEffect(() => {
    const subscription = webSocketService.getTypingStatus('').subscribe(statusMap => {
      setTypingStatuses(statusMap);
    });

    return () => {
      subscription.unsubscribe();
    };
  }, []);

  const handleSearch = useCallback((query: string) => {
    setSearchQuery(query);
  }, []);

  const handleNewChat = useCallback(() => {
    setShowNewChatModal(true);
  }, []);

  return (
    <div className="chat-room-list">
      <div className="search-container">
        <input
          type="text"
          placeholder="Search chats..."
          value={searchQuery}
          onChange={(e) => handleSearch(e.target.value)}
          className="search-input"
        />
      </div>

      <div className="rooms-container">
        {loading ? (
          <div className="loading-skeleton">
            {[1, 2, 3].map(n => (
              <div key={n} className="room-item-skeleton">
                <div className="avatar-skeleton" />
                <div className="content-skeleton">
                  <div className="title-skeleton" />
                  <div className="message-skeleton" />
                </div>
              </div>
            ))}
          </div>
        ) : rooms.length === 0 ? (
          <div className="no-rooms">
            <p>{searchQuery ? 'No results found' : 'No chat rooms available'}</p>
            <button onClick={handleNewChat} className="start-chat-button">
              Start a new chat
            </button>
          </div>
        ) : (
          rooms.map((room) => (
            <div
              key={room.id}
              className="room-item"
              onClick={() => onRoomSelect(room.id)}
            >
              <div className="room-avatar">
                {room.avatar ? (
                  <img src={room.avatar} alt={room.name} />
                ) : (
                  <div className="avatar-placeholder">
                    {room.name.charAt(0)}
                  </div>
                )}
              </div>

              <div className="room-content">
                <div className="room-header">
                  <h3 className="room-name">{room.name}</h3>
                  {!room.isGroup && (
                    <UserStatusIndicator 
                      userId={room.members.find(id => id !== currentUserId) || ''} 
                    />
                  )}
                  {room.lastMessage && (
                    <span className="last-message-time">
                      {new Date(room.lastMessage.timestamp).toLocaleTimeString()}
                    </span>
                  )}
                </div>

                <div className="room-footer">
                  {typingStatuses.get(room.id)?.size > 0 ? (
                    <TypingIndicator
                      roomId={room.id}
                      users={new Map()} // Pass actual user map here
                    />
                  ) : room.lastMessage ? (
                    <p className="last-message">{room.lastMessage.content}</p>
                  ) : (
                    <p className="no-messages">No messages yet</p>
                  )}

                  {room.unreadCount > 0 && (
                    <div className="unread-count">
                      {room.unreadCount}
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {showNewChatModal && (
        <div className="modal">
          <div className="modal-content">
            <h3>Start New Chat</h3>
            <button onClick={() => setShowNewChatModal(false)}>
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
}; 