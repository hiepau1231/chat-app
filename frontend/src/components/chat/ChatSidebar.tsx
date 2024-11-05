import React from 'react';
import { ChatRoomList } from './ChatRoomList';
import { useNavigate } from 'react-router-dom';

export const ChatSidebar = () => {
  const navigate = useNavigate();
  const username = "User"; // TODO: Lấy từ user profile

  const handleLogout = () => {
    localStorage.clear(); // Xóa tất cả localStorage thay vì chỉ xóa token
    navigate('/login', { replace: true }); // Thêm replace: true để tránh quay lại trang trước đó
  };

  return (
    <div className="flex flex-col h-full">
      {/* Header */}
      <div className="p-4 border-b">
        <div className="flex items-center justify-between">
          <h2 className="text-xl font-semibold">Chat</h2>
          <div className="flex items-center space-x-2">
            <span>{username}</span>
            <button 
              onClick={handleLogout}
              className="text-sm text-red-500 hover:text-red-700"
            >
              Logout
            </button>
          </div>
        </div>
      </div>

      {/* Chat room list */}
      <div className="flex-1 overflow-y-auto">
        <ChatRoomList currentUserId="user123" onRoomSelect={() => {}} isLoading={false} />
      </div>
    </div>
  );
};
