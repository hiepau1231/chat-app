import { useState } from 'react';
import { ChatLayout } from './components/layout/ChatLayout';
import { ChatSidebar } from './components/chat/ChatSidebar';
import { ChatArea } from './components/chat/ChatArea';

// TODO: Replace with actual user authentication
const MOCK_USER_ID = '123';  // Keep this ID as we already created a room with this user

function App() {
  const [selectedRoomId, setSelectedRoomId] = useState<string | undefined>();

  return (
    <ChatLayout
      sidebar={
        <ChatSidebar
          userId={MOCK_USER_ID}
          selectedRoomId={selectedRoomId}
          onRoomSelect={setSelectedRoomId}
        />
      }
      content={
        selectedRoomId ? (
          <ChatArea
            roomId={selectedRoomId}
            userId={MOCK_USER_ID}
          />
        ) : (
          <div className="flex items-center justify-center h-full text-gray-500">
            Select a chat to start messaging
          </div>
        )
      }
    />
  );
}

export default App;