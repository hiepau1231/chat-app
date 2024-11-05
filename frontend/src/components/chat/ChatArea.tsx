import React from 'react';
import { MessageList } from './MessageList';
import { ChatInput } from './ChatInput';

export const ChatArea = () => {
  return (
    <div className="h-full flex flex-col">
      {/* Header */}
      <div className="p-4 border-b bg-white">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <h2 className="text-lg font-semibold">Chat Room Name</h2>
            <span className="text-sm text-gray-500">3 members</span>
          </div>
          <button className="p-2 hover:bg-gray-100 rounded-full">
            {/* Settings icon */}
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
          </button>
        </div>
      </div>

      {/* Messages Area */}
      <div className="flex-1 overflow-y-auto bg-gray-50 p-4">
        <MessageList />
      </div>

      {/* Input Area */}
      <div className="p-4 bg-white border-t">
        <ChatInput />
      </div>
    </div>
  );
};
