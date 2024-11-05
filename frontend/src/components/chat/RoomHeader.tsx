import React from 'react';

export const RoomHeader = () => {
  // Giả sử chúng ta có một state để lưu thông tin về room hiện tại
  const [currentRoom, setCurrentRoom] = React.useState<any>(null);

  if (!currentRoom) {
    return (
      <div className="border-b p-4">
        <h2 className="text-xl font-semibold">Select a chat to start messaging</h2>
      </div>
    );
  }

  return (
    <div className="border-b p-4">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-semibold">{currentRoom.name}</h2>
          <p className="text-sm text-gray-500">{currentRoom.participants} participants</p>
        </div>
        <div className="flex items-center space-x-4">
          <button className="text-gray-600 hover:text-gray-800">
            <span className="material-icons">call</span>
          </button>
          <button className="text-gray-600 hover:text-gray-800">
            <span className="material-icons">videocam</span>
          </button>
          <button className="text-gray-600 hover:text-gray-800">
            <span className="material-icons">more_vert</span>
          </button>
        </div>
      </div>
    </div>
  );
}; 