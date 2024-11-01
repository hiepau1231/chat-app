import { useState, useRef, useEffect } from 'react';
import { useChat } from '../../hooks/useChat';
import { Message } from '../../lib/api';
import { ChatMessage } from './ChatMessage';
import { motion, AnimatePresence } from 'framer-motion';

interface ChatAreaProps {
  roomId: string;
  userId: string;
}

export function ChatArea({ roomId, userId }: ChatAreaProps) {
  const [inputValue, setInputValue] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const { 
    messages, 
    sendMessage, 
    sendFile, 
    isConnected, 
    error, 
    handleTyping, 
    typingUsers,
    isLoading 
  } = useChat(roomId, userId);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputValue.trim() || isSubmitting) return;

    try {
      setIsSubmitting(true);
      await sendMessage(inputValue);
      setInputValue('');
    } catch (err) {
      console.error('Failed to send message:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
    handleTyping();
  };

  const handleFileSelect = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files || files.length === 0) return;

    const file = files[0];
    try {
      setIsSubmitting(true);
      await sendFile(file);
    } catch (err) {
      console.error('Failed to upload file:', err);
    } finally {
      setIsSubmitting(false);
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    }
  };

  return (
    <div className="flex flex-col h-full relative">
      {/* Connection Status */}
      <AnimatePresence>
        {!isConnected && (
          <motion.div 
            initial={{ opacity: 0, y: -50 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -50 }}
            className="absolute top-0 left-0 right-0 bg-yellow-100 text-yellow-800 px-4 py-2 text-center"
          >
            Reconnecting to chat...
          </motion.div>
        )}
      </AnimatePresence>

      {/* Header */}
      <div className="p-4 border-b bg-white shadow-sm">
        <h2 className="text-lg font-medium">Chat Room</h2>
        {error && (
          <motion.div 
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            className="text-sm text-red-500 mt-1"
          >
            {error}
          </motion.div>
        )}
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {isLoading ? (
          <div className="flex justify-center items-center h-full">
            <motion.div
              animate={{ rotate: 360 }}
              transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
              className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full"
            />
          </div>
        ) : messages.length === 0 ? (
          <div className="flex justify-center items-center h-full text-gray-500">
            No messages yet
          </div>
        ) : (
          <AnimatePresence>
            {messages.map((message) => (
              <ChatMessage
                key={message.id}
                message={message}
                isOwnMessage={message.senderId === userId}
              />
            ))}
          </AnimatePresence>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Typing Indicator */}
      <AnimatePresence>
        {typingUsers.length > 0 && (
          <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 20 }}
            className="px-4 py-2 text-sm text-gray-500 bg-gray-50"
          >
            <motion.span
              animate={{ opacity: [0.4, 1, 0.4] }}
              transition={{ duration: 1.5, repeat: Infinity }}
            >
              {typingUsers.length === 1
                ? 'Someone is typing...'
                : `${typingUsers.length} people are typing...`}
            </motion.span>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Input */}
      <form onSubmit={handleSubmit} className="p-4 border-t bg-white">
        <div className="flex items-center space-x-2">
          <motion.button
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.95 }}
            type="button"
            onClick={() => fileInputRef.current?.click()}
            className="p-2 text-gray-500 hover:text-blue-500 focus:outline-none disabled:opacity-50"
            disabled={isSubmitting || !isConnected}
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                    d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
            </svg>
          </motion.button>
          <input
            type="file"
            ref={fileInputRef}
            onChange={handleFileSelect}
            className="hidden"
          />
          <input
            type="text"
            value={inputValue}
            onChange={handleInputChange}
            placeholder={isConnected ? "Type a message..." : "Reconnecting..."}
            disabled={!isConnected}
            className="flex-1 px-4 py-2 border rounded-full focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:bg-gray-100"
          />
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            type="submit"
            disabled={!inputValue.trim() || isSubmitting || !isConnected}
            className="px-6 py-2 bg-blue-500 text-white rounded-full hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:bg-blue-300"
          >
            {isSubmitting ? (
              <motion.div
                animate={{ rotate: 360 }}
                transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                className="w-5 h-5 border-2 border-white border-t-transparent rounded-full"
              />
            ) : (
              'Send'
            )}
          </motion.button>
        </div>
      </form>
    </div>
  );
}
