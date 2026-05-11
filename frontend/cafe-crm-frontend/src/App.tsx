import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { LoginPage } from './components/pages/LoginPage';
import { OrdersPage } from './components/pages/OrdersPage';
import "./App.css";

export const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<OrdersPage />} />
        <Route path="/orders" element={<OrdersPage />} />
      </Routes>
    </BrowserRouter>
  );
};
