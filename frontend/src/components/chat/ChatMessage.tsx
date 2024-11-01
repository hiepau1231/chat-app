import { motion } from 'framer-motion';
import { Message } from '../../lib/api';

interface ChatMessageProps {
  message: Message;
  isOwnMessage: boolean;
}

export function ChatMessage({ message, isOwnMessage }: ChatMessageProps) {
  return (
    <motion.div
      initial={{ 
        opacity: 0, 
        x: isOwnMessage ? 20 : -20,
        scale: 0.95
      }}
      animate={{ 
        opacity: 1, 
        x: 0,
        scale: 1
      }}
      transition={{
        duration: 0.2,
        ease: "easeOut"
      }}
      className={`flex ${isOwnMessage ? 'justify-end' : 'justify-start'}`}
    >
      <motion.div
        whileHover={{ scale: 1.02 }}
        className={`max-w-[70%] rounded-lg p-3 ${
          isOwnMessage ? 'bg-blue-500 text-white' : 'bg-gray-100'
        }`}
      >
        {message.type === 'text' ? (
          <div className="text-sm">{message.content}</div>
        ) : message.type === 'file' ? (
          <div className="flex items-center space-x-2">
            <motion.svg 
              whileHover={{ scale: 1.1 }}
              className="w-5 h-5" 
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path 
                strokeLinecap="round" 
                strokeLinejoin="round" 
                strokeWidth={2} 
                d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" 
              />
            </motion.svg>
            <motion.a
              whileHover={{ scale: 1.05 }}
              href={message.fileUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="text-sm underline"
            >
              {message.fileName}
            </motion.a>
          </div>
        ) : (
          <motion.img
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.3 }}
            src={message.fileUrl}
            alt={message.fileName}
            className="max-w-full rounded"
          />
        )}
        {message.createdAt && (
          <div className="text-xs mt-1 opacity-70">
            {new Date(message.createdAt).toLocaleTimeString()}
          </div>
        )}
      </motion.div>
    </motion.div>
  );
} 