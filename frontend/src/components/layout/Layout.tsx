import React from 'react'
import { Sidebar } from './Sidebar'
import { ChatArea } from './ChatArea'

export const Layout = () => {
  return (
    <div className="flex w-full h-screen bg-white">
      <Sidebar />
      <ChatArea />
    </div>
  )
}