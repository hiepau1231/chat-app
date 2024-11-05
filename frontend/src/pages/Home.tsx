import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ChatLayout } from '../components/layout/ChatLayout';

export const Home = () => {
  const navigate = useNavigate();

  React.useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login', { replace: true });
    }
    // TODO: Thêm logic kiểm tra token hết hạn
  }, [navigate]);

  return <ChatLayout />;
}; 