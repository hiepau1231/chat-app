import React from 'react';
import { RoomHeader } from '../chat/RoomHeader';

export const ChatArea = () => {
  const [messages, setMessages] = React.useState<any[]>([]);
  const [newMessage, setNewMessage] = React.useState('');

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newMessage.trim()) return;

    // TODO: Implement sending message
    console.log('Sending message:', newMessage);
    setNewMessage('');
  };

  return (
    <div className="flex flex-col h-full">
      <RoomHeader />
      
      {/* Messages area */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages.map((message, index) => (
          <div key={index} className="flex flex-col">
            <div className="flex items-center space-x-2">
              <span className="font-semibold">{message.sender}</span>
              <span className="text-sm text-gray-500">{message.time}</span>
            </div>
            <div className="mt-1 text-gray-700">{message.content}</div>
          </div>
        ))}
      </div>

      {/* Message input */}
      <form onSubmit={handleSend} className="border-t p-4">
        <div className="flex space-x-4">
          <input
            type="text"
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            placeholder="Type a message..."
            className="flex-1 rounded-lg border p-2 focus:outline-none focus:border-blue-500"
          />
          <button
            type="submit"
            className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 focus:outline-none"
          >
            Send
          </button>
        </div>
      </form>
    </div>
  );
};