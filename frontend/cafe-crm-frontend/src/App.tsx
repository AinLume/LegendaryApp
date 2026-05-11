import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { OrdersPage } from './components/pages/OrdersPage';
import "./App.css";

export const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<OrdersPage />} />
        <Route path="/orders" element={<OrdersPage />} />
      </Routes>
    </BrowserRouter>
  );
};
