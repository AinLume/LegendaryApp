import { BrowserRouter, Routes, Route, Outlet } from 'react-router-dom';
import { LoginPage } from './components/pages/LoginPage';
import { OrdersPage } from './components/pages/OrdersPage';
import { TablesPage } from './components/pages/TablesPage';
import { AnalyticsPage } from './components/pages/AnalyticsPage';
import { MenuPage } from './components/pages/MenuPage';
import { StaffRegisterPage } from './components/pages/StaffRegisterPage';
import { Header, RouteWithAuth } from './components/ui';
import { StaffRole } from './types';
import './App.css';

export const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Публичный роут - логин */}
        <Route path="/login" element={<LoginPage />} />

        {/* Защищённые роуты - требуют авторизации */}
        <Route element={<RouteWithAuth />}>
          <Route element={<><Header /><Outlet /></>}>
            {/* Доступно всем авторизованным */}
            <Route path="/" element={<OrdersPage />} />
            <Route path="/orders" element={<OrdersPage />} />
            <Route path="/tables" element={<TablesPage />} />

            {/* Только для администраторов */}
            <Route
              element={<RouteWithAuth allowedRoles={[StaffRole.ADMIN]} />}
            >
              <Route path="/analytics" element={<AnalyticsPage />} />
              <Route path="/staff/register" element={<StaffRegisterPage />} />
            </Route>

            {/* Для администраторов, поваров и барменов */}
            <Route
              element={
                <RouteWithAuth
                  allowedRoles={[StaffRole.ADMIN, StaffRole.COOK, StaffRole.BARTENDER]}
                />
              }
            >
              <Route path="/menu" element={<MenuPage />} />
            </Route>
          </Route>
        </Route>
      </Routes>
    </BrowserRouter>
  );
};
