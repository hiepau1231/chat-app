import React from 'react'

export const Sidebar = () => {
  return (
    <div className="w-sidebar h-screen border-r border-gray-100 bg-white shadow-sm">
      {/* Header */}
      <div className="h-header flex items-center px-4 border-b border-gray-100 bg-white shadow-sm">
        <div className="flex items-center space-x-2 w-full">
          <div className="w-10 h-10 rounded-full bg-telegram-blue flex items-center justify-center text-white font-medium">
            T
          </div>
          <div className="flex-1 relative">
            <input 
              type="text"
              placeholder="Search"
              className="w-full bg-gray-100 rounded-2xl px-4 py-2 text-sm focus:outline-none focus:bg-white focus:ring-1 focus:ring-telegram-blue/20 transition-all"
            />
          </div>
        </div>
      </div>

      {/* Chat List */}
      <div className="overflow-y-auto h-[calc(100vh-60px)]">
        {/* Placeholder Chat Items */}
        {Array.from({length: 10}).map((_, i) => (
          <div 
            key={i}
            className="flex items-center space-x-3 px-4 py-3 hover:bg-gray-50 cursor-pointer transition-colors"
          >
            <div className="w-12 h-12 rounded-full bg-gray-200 flex-shrink-0" />
            <div className="flex-1 min-w-0">
              <div className="flex justify-between items-center">
                <span className="font-medium text-sm text-gray-900 truncate">User {i + 1}</span>
                <span className="text-xs text-gray-400">12:00</span>
              </div>
              <div className="flex items-center space-x-1">
                <span className="text-sm text-gray-500 truncate">
                  Latest message preview...
                </span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}