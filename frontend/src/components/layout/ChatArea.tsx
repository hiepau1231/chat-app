import React from 'react'

export const ChatArea = () => {
  return (
    <div className="flex-1 flex flex-col h-screen bg-gray-50">
      {/* Chat Header */}
      <div className="h-header flex items-center px-6 bg-white shadow-sm z-10">
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-full bg-gray-200" />
          <div>
            <div className="font-medium text-sm">Chat Name</div>
            <div className="text-xs text-gray-500">online</div>
          </div>
        </div>
      </div>

      {/* Messages Area */}
      <div className="flex-1 overflow-y-auto p-6">
        {/* Placeholder Messages */}
        {Array.from({length: 5}).map((_, i) => (
          <div 
            key={i}
            className={`flex ${i % 2 === 0 ? 'justify-end' : 'justify-start'} mb-4 group`}
          >
            <div 
              className={`max-w-[70%] rounded-2xl p-3 ${
                i % 2 === 0 
                  ? 'bg-telegram-blue text-white rounded-br-md hover:bg-telegram-blue/95' 
                  : 'bg-white rounded-bl-md hover:bg-gray-50'
              } shadow-sm transition-colors`}
            >
              <div className="text-sm">
                This is a sample message that might be a bit longer to show how it wraps.
              </div>
              <div className={`text-xs mt-1 ${i % 2 === 0 ? 'text-blue-100' : 'text-gray-400'}`}>
                12:00
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Message Input */}
      <div className="p-4 bg-white shadow-sm">
        <div className="flex items-center space-x-2">
          <button className="p-2 hover:bg-gray-100 rounded-full transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
            </svg>
          </button>
          <input
            type="text"
            placeholder="Write a message..."
            className="flex-1 bg-gray-50 rounded-full px-4 py-2 text-sm focus:outline-none focus:bg-white focus:ring-1 focus:ring-telegram-blue/20 transition-all"
          />
          <button className="p-2 hover:bg-gray-100 rounded-full transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-telegram-blue" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  )
}