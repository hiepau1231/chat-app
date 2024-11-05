import React from 'react';
import { ChatRoomList } from './ChatRoomList';
import { useAuth } from '../../contexts/AuthContext';
import { UserStatusIndicator } from '../user/UserStatusIndicator';

export const ChatSidebar = () => {
  const { user } = useAuth();

  return (
    <div className="h-full flex flex-col">
      {/* User Profile Section */}
      <div className="p-4 border-b">
        <div className="flex items-center space-x-3">
          <div className="relative">
            <img
              src={user?.avatar || 'https://via.placeholder.com/40'} 
              alt="Profile"
              className="w-10 h-10 rounded-full"
            />
            <UserStatusIndicator status="online" />
          </div>
          <div>
            <h3 className="font-medium">{user?.username}</h3>
            <p className="text-sm text-gray-500">{user?.email}</p>
          </div>
        </div>
      </div>

      {/* Search Bar */}
      <div className="p-4 border-b">
        <input
          type="text"
          placeholder="Search chats..."
          className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>

      {/* Chat Rooms List */}
      <div className="flex-1 overflow-y-auto">
        <ChatRoomList />
      </div>

      {/* New Chat Button */}
      <div className="p-4 border-t">
        <button
          className="w-full py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          New Chat
        </button>
      </div>
    </div>
  );
};
