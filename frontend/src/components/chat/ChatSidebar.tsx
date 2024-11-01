import { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import ApiService, { ChatRoom } from '../../lib/api';

interface ChatSidebarProps {
  userId: string;
  onRoomSelect: (roomId: string) => void;
  selectedRoomId?: string;
}

export function ChatSidebar({ userId, onRoomSelect, selectedRoomId }: ChatSidebarProps) {
  const [rooms, setRooms] = useState<ChatRoom[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const api = ApiService.getInstance();

  useEffect(() => {
    const loadRooms = async () => {
      setIsLoading(true);
      try {
        const fetchedRooms = await api.getRooms(userId);
        setRooms(fetchedRooms);
        setError(null);
      } catch (err) {
        setError(api.handleApiError(err));
      } finally {
        setIsLoading(false);
      }
    };

    loadRooms();
  }, [userId]);

  const filteredRooms = rooms.filter(room =>
    room.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="flex flex-col h-full bg-white">
      {/* Search Bar */}
      <div className="p-4 border-b">
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search chats..."
          className="w-full px-3 py-2 rounded-lg border focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      {/* Error Message */}
      <AnimatePresence>
        {error && (
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="px-4 py-2 bg-red-50 text-red-600 text-sm"
          >
            {error}
          </motion.div>
        )}
      </AnimatePresence>

      {/* Chat Rooms List */}
      <div className="flex-1 overflow-y-auto">
        {isLoading ? (
          <div className="flex justify-center items-center h-32">
            <motion.div
              animate={{ rotate: 360 }}
              transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
              className="w-6 h-6 border-2 border-blue-500 border-t-transparent rounded-full"
            />
          </div>
        ) : filteredRooms.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-32 text-gray-500">
            {searchQuery ? (
              <>
                <p>No chats found</p>
                <p className="text-sm">Try a different search term</p>
              </>
            ) : (
              <p>No chat rooms available</p>
            )}
          </div>
        ) : (
          <AnimatePresence>
            {filteredRooms.map((room) => (
              <motion.div
                key={room.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -20 }}
                onClick={() => room.id && onRoomSelect(room.id)}
                className={`p-4 hover:bg-gray-50 cursor-pointer transition-colors duration-200 ${
                  room.id === selectedRoomId ? 'bg-blue-50' : ''
                }`}
              >
                <div className="flex items-center space-x-3">
                  <div className="relative">
                    <div className="w-12 h-12 rounded-full bg-gray-300 flex items-center justify-center">
                      <span className="text-gray-600 font-medium">
                        {room.name.charAt(0).toUpperCase()}
                      </span>
                    </div>
                    {room.type === 'group' && (
                      <div className="absolute -bottom-1 -right-1 w-4 h-4 bg-blue-500 rounded-full flex items-center justify-center">
                        <svg className="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
                          <path d="M13 6a3 3 0 11-6 0 3 3 0 016 0zM18 8a2 2 0 11-4 0 2 2 0 014 0zM14 15a4 4 0 00-8 0v3h8v-3zM6 8a2 2 0 11-4 0 2 2 0 014 0zM16 18v-3a5.972 5.972 0 00-.75-2.906A3.005 3.005 0 0119 15v3h-3zM4.75 12.094A5.973 5.973 0 004 15v3H1v-3a3 3 0 013.75-2.906z" />
                        </svg>
                      </div>
                    )}
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center justify-between">
                      <h3 className="text-sm font-medium text-gray-900 truncate">
                        {room.name}
                      </h3>
                      {room.createdAt && (
                        <span className="text-xs text-gray-500">
                          {new Date(room.createdAt).toLocaleDateString()}
                        </span>
                      )}
                    </div>
                    <p className="text-sm text-gray-500 truncate">
                      {room.type === 'direct' ? 'Direct Message' : `${room.members.length} members`}
                    </p>
                  </div>
                </div>
              </motion.div>
            ))}
          </AnimatePresence>
        )}
      </div>
    </div>
  );
}
