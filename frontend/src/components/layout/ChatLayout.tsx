import React from 'react';
import { ChatSidebar } from '../chat/ChatSidebar';
import { ChatArea } from './ChatArea';

export const ChatLayout = () => {
  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar chiếm 1/4 màn hình */}
      <div className="w-1/4 bg-white border-r">
        <ChatSidebar />
      </div>
      
      {/* Chat area chiếm 3/4 màn hình */}
      <div className="w-3/4">
        <ChatArea />
      </div>
    </div>
  );
};