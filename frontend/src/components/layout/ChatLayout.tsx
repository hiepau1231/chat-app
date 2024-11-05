import React from 'react';
import { ChatSidebar } from '../chat/ChatSidebar';
import { ChatArea } from '../chat/ChatArea';
import { useAuth } from '../../contexts/AuthContext';

export const ChatLayout = () => {
  const { logout } = useAuth();

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar chứa danh sách chat và profile */}
      <div className="w-1/4 bg-white border-r">
        <div className="p-4 border-b flex justify-between items-center">
          <h2 className="text-xl font-semibold">Chats</h2>
          <button
            onClick={logout}
            className="px-3 py-1 text-sm text-red-600 hover:bg-red-50 rounded"
          >
            Logout
          </button>
        </div>
        <ChatSidebar />
      </div>

      {/* Main chat area */}
      <div className="flex-1">
        <ChatArea />
      </div>
    </div>
  );
};