import React, { useState } from 'react';
import './NewChatModal.css';

interface NewChatModalProps {
  onClose: () => void;
  onCreateChat: (name: string, members: string[]) => void;
}

export const NewChatModal: React.FC<NewChatModalProps> = ({ onClose, onCreateChat }) => {
  const [chatName, setChatName] = useState('');
  const [isGroup, setIsGroup] = useState(false);
  const [selectedUsers, setSelectedUsers] = useState<string[]>([]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onCreateChat(chatName, selectedUsers);
    onClose();
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>{isGroup ? 'New Group Chat' : 'New Chat'}</h2>
        <form onSubmit={handleSubmit}>
          {isGroup && (
            <input
              type="text"
              placeholder="Group name"
              value={chatName}
              onChange={(e) => setChatName(e.target.value)}
            />
          )}
          <div className="user-list">
            {/* TODO: Add user selection list */}
          </div>
          <div className="modal-actions">
            <button type="button" onClick={() => setIsGroup(!isGroup)}>
              {isGroup ? 'Single Chat' : 'Group Chat'}
            </button>
            <button type="submit">Create</button>
            <button type="button" onClick={onClose}>Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
}; 