import { createBrowserRouter, Navigate, useParams } from "react-router-dom";
import { AppShell } from "../components/AppShell/AppShell";
import { CreateOrderPage } from "../pages/CreateOrderPage/CreateOrderPage";
import { OrderCreatedPage } from "../pages/OrderCreatedPage/OrderCreatedPage";
import { OrderDetailsPage } from "../pages/OrderDetailsPage/OrderDetailsPage";
import { OrdersPage } from "../pages/OrdersPage/OrdersPage";
import { NotFoundPage } from "../pages/NotFoundPage/NotFoundPage";
import { AdminDashboardPage } from "../pages/AdminDashboardPage/AdminDashboardPage";
import { AdminOrderPage } from "../pages/AdminOrderPage/AdminOrderPage";
import { RentalBookingDetailsPage } from "../pages/RentalBookingDetailsPage/RentalBookingDetailsPage";
import { RentalBookingsPage } from "../pages/RentalBookingsPage/RentalBookingsPage";
import { RentalCatalogPage } from "../pages/RentalCatalogPage/RentalCatalogPage";
import { RentalPropertyPage } from "../pages/RentalPropertyPage/RentalPropertyPage";
import { ServiceCatalogPage } from "../pages/ServiceCatalogPage/ServiceCatalogPage";
import { AdminServiceCatalogPage } from "../pages/AdminServiceCatalogPage/AdminServiceCatalogPage";
import { AdminRentalPropertiesPage } from "../pages/AdminRentalPropertiesPage/AdminRentalPropertiesPage";
import { AdminRentalPropertyPage } from "../pages/AdminRentalPropertyPage/AdminRentalPropertyPage";
import { AdminRentalCalendarPage } from "../pages/AdminRentalCalendarPage/AdminRentalCalendarPage";
import { AdminRentalBookingsPage } from "../pages/AdminRentalBookingsPage/AdminRentalBookingsPage";
import { AdminRentalBookingPage } from "../pages/AdminRentalBookingPage/AdminRentalBookingPage";

function LegacyCleaningOrderRedirect({ created = false }: { created?: boolean }) {
  const { id } = useParams();
  return <Navigate replace to={`/cleaning/orders/${id}${created ? "/created" : ""}`} />;
}

function LegacyAdminOrderRedirect() {
  const { id } = useParams();
  return <Navigate replace to={`/admin/cleaning/orders/${id}`} />;
}

export const router = createBrowserRouter([
  {
    element: <AppShell />,
    children: [
      { path: "/", element: <ServiceCatalogPage /> },
      { path: "/cleaning", element: <CreateOrderPage /> },
      { path: "/cleaning/orders", element: <OrdersPage /> },
      { path: "/cleaning/orders/:id", element: <OrderDetailsPage /> },
      { path: "/cleaning/orders/:id/created", element: <OrderCreatedPage /> },
      { path: "/rent", element: <RentalCatalogPage /> },
      { path: "/rent/properties", element: <RentalCatalogPage /> },
      { path: "/rent/properties/:slug", element: <RentalPropertyPage /> },
      { path: "/rent/bookings", element: <RentalBookingsPage /> },
      { path: "/rent/bookings/:id", element: <RentalBookingDetailsPage /> },
      { path: "/orders", element: <Navigate replace to="/cleaning/orders" /> },
      { path: "/orders/:id", element: <LegacyCleaningOrderRedirect /> },
      { path: "/orders/:id/created", element: <LegacyCleaningOrderRedirect created /> },
      { path: "/admin", element: <AdminServiceCatalogPage /> },
      { path: "/admin/cleaning", element: <AdminDashboardPage /> },
      { path: "/admin/cleaning/orders/:id", element: <AdminOrderPage /> },
      { path: "/admin/rent", element: <Navigate replace to="/admin/rent/properties" /> },
      { path: "/admin/rent/properties", element: <AdminRentalPropertiesPage /> },
      { path: "/admin/rent/properties/:id", element: <AdminRentalPropertyPage /> },
      { path: "/admin/rent/properties/:id/calendar", element: <AdminRentalCalendarPage /> },
      { path: "/admin/rent/bookings", element: <AdminRentalBookingsPage /> },
      { path: "/admin/rent/bookings/:id", element: <AdminRentalBookingPage /> },
      { path: "/admin/orders/:id", element: <LegacyAdminOrderRedirect /> },
      { path: "*", element: <NotFoundPage /> },
    ],
  },
]);
