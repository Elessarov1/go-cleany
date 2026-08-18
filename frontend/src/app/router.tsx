import { createBrowserRouter } from "react-router-dom";
import { AppShell } from "../components/AppShell/AppShell";
import { CreateOrderPage } from "../pages/CreateOrderPage/CreateOrderPage";
import { OrderCreatedPage } from "../pages/OrderCreatedPage/OrderCreatedPage";
import { OrderDetailsPage } from "../pages/OrderDetailsPage/OrderDetailsPage";
import { OrdersPage } from "../pages/OrdersPage/OrdersPage";
import { NotFoundPage } from "../pages/NotFoundPage/NotFoundPage";
import { AdminDashboardPage } from "../pages/AdminDashboardPage/AdminDashboardPage";
import { AdminOrderPage } from "../pages/AdminOrderPage/AdminOrderPage";

export const router = createBrowserRouter([
  {
    element: <AppShell />,
    children: [
      { path: "/", element: <CreateOrderPage /> },
      { path: "/orders", element: <OrdersPage /> },
      { path: "/orders/:id", element: <OrderDetailsPage /> },
      { path: "/orders/:id/created", element: <OrderCreatedPage /> },
      { path: "/admin", element: <AdminDashboardPage /> },
      { path: "/admin/orders/:id", element: <AdminOrderPage /> },
      { path: "*", element: <NotFoundPage /> },
    ],
  },
]);
