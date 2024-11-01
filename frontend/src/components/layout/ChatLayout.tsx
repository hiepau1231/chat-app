import { ReactNode, useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';

interface ChatLayoutProps {
  sidebar: ReactNode;
  content: ReactNode;
}

export function ChatLayout({ sidebar, content }: ChatLayoutProps) {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [isLargeScreen, setIsLargeScreen] = useState(false);

  useEffect(() => {
    const checkScreenSize = () => {
      setIsLargeScreen(window.innerWidth >= 1024);
    };

    // Initial check
    checkScreenSize();

    // Add event listener
    window.addEventListener('resize', checkScreenSize);

    // Cleanup
    return () => window.removeEventListener('resize', checkScreenSize);
  }, []);

  return (
    <div className="flex h-screen bg-gray-100 overflow-hidden">
      {/* Mobile Sidebar Toggle Button */}
      <button
        onClick={() => setIsSidebarOpen(!isSidebarOpen)}
        className="lg:hidden fixed top-4 left-4 z-50 p-2 bg-white rounded-full shadow-lg"
      >
        <svg
          className="w-6 h-6 text-gray-600"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          {isSidebarOpen ? (
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M6 18L18 6M6 6l12 12"
            />
          ) : (
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M4 6h16M4 12h16M4 18h16"
            />
          )}
        </svg>
      </button>

      {/* Sidebar */}
      <AnimatePresence mode="wait">
        {(isSidebarOpen || isLargeScreen) && (
          <>
            {/* Mobile Overlay */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 0.5 }}
              exit={{ opacity: 0 }}
              className="lg:hidden fixed inset-0 bg-black z-30"
              onClick={() => setIsSidebarOpen(false)}
            />

            {/* Sidebar Content */}
            <motion.div
              initial={{ x: -320 }}
              animate={{ x: 0 }}
              exit={{ x: -320 }}
              transition={{ type: "spring", bounce: 0.2 }}
              className={`
                fixed lg:static
                w-80 h-full
                bg-white border-r border-gray-200
                z-40 lg:z-auto
                shadow-lg lg:shadow-none
              `}
            >
              {sidebar}
            </motion.div>
          </>
        )}
      </AnimatePresence>

      {/* Main Content */}
      <motion.div
        layout
        className="flex-1 relative"
        animate={{
          marginLeft: (isSidebarOpen && isLargeScreen) ? "0" : "0",
        }}
      >
        {content}
      </motion.div>
    </div>
  );
}