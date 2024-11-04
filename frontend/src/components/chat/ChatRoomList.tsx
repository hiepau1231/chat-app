import React, { useEffect, useState, useRef, useCallback } from 'react';
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

// Thêm mock data
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
  },
  {
    id: '2',
    name: 'Team Chat',
    avatar: null,
    lastMessage: {
      id: 'm2',
      content: 'Meeting at 2 PM',
      senderId: 'user3',
      timestamp: new Date().toISOString()
    },
    unreadCount: 5,
    members: ['user123', 'user2', 'user3'],
    isGroup: true,
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
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [typingStatuses, setTypingStatuses] = useState<Map<string, Set<string>>>(new Map());
  const [totalPages, setTotalPages] = useState(0);
  
  const observer = useRef<IntersectionObserver>();
  const lastRoomElementRef = useCallback((node: HTMLDivElement) => {
    if (loading) return;
    if (observer.current) observer.current.disconnect();
    observer.current = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && hasMore) {
        setPage(prevPage => prevPage + 1);
      }
    });
    if (node) observer.current.observe(node);
  }, [loading, hasMore]);

  useEffect(() => {
    fetchRooms();
    subscribeToUpdates();
  }, [page]);

  const fetchRooms = async () => {
    try {
      setLoading(true);
      // Tạm thời dùng mock data
      setRooms(MOCK_ROOMS);
      setHasMore(false);
    } catch (error) {
      console.error('Error fetching rooms:', error);
    } finally {
      setLoading(false);
    }
  };

  const subscribeToUpdates = () => {
    webSocketService.getTypingStatus('').subscribe(statusMap => {
      setTypingStatuses(statusMap);
    });
  };

  const filteredRooms = rooms.filter(room => 
    room.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  // Fetch rooms when search changes
  useEffect(() => {
    setPage(0); // Reset page when search changes
    fetchRooms();
  }, [searchQuery]);

  return (
    <div className="chat-room-list">
      <div className="search-container">
        <input
          type="text"
          placeholder="Search chats..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="search-input"
        />
      </div>

      <div className="rooms-container">
        {loading && page === 0 ? (
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
        ) : filteredRooms.length === 0 ? (
          <div className="no-rooms">
            <p>{searchQuery ? 'No results found' : 'No chat rooms available'}</p>
            <button onClick={handleNewChat} className="start-chat-button">
              Start a new chat
            </button>
          </div>
        ) : (
          filteredRooms.map((room, index) => (
            <div
              key={room.id}
              ref={index === filteredRooms.length - 1 ? lastRoomElementRef : null}
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

        {loading && page > 0 && (
          <div className="loading-more">
            <div className="spinner" />
          </div>
        )}
      </div>
    </div>
  );
}; 