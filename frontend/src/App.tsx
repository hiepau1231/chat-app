import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { LoginForm } from './components/auth/LoginForm';
import { RegisterForm } from './components/auth/RegisterForm';
import { ChatRoom } from './components/chat/ChatRoom';
import { PrivateRoute } from './components/common/PrivateRoute';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route 
          path="/" 
          element={
            <PrivateRoute>
              <Navigate to="/chat" replace />
            </PrivateRoute>
          } 
        />

        <Route path="/login" element={<LoginForm />} />
        <Route path="/register" element={<RegisterForm />} />

        <Route 
          path="/chat" 
          element={
            <PrivateRoute>
              <ChatRoom />
            </PrivateRoute>
          } 
        />
        <Route 
          path="/chat/:roomId" 
          element={
            <PrivateRoute>
              <ChatRoom />
            </PrivateRoute>
          } 
        />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;