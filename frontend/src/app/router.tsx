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
import { ServiceAvailabilityGate } from "../components/ServiceAvailabilityGate/ServiceAvailabilityGate";
import { AdminAccessGate } from "../components/AdminAccessGate/AdminAccessGate";
import { CustomerAccessGate } from "../components/CustomerAccessGate/CustomerAccessGate";
import { AccountPage } from "../pages/AccountPage/AccountPage";
import { TelegramAccountLinkPage } from "../pages/TelegramAccountLinkPage/TelegramAccountLinkPage";
import { NotificationsPage } from "../pages/NotificationsPage/NotificationsPage";
import { AdminAnalyticsPage } from "../pages/AdminAnalyticsPage/AdminAnalyticsPage";

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
      { path: "/cleaning", element: <ServiceAvailabilityGate service="CLEANING"><CreateOrderPage /></ServiceAvailabilityGate> },
      {
        element: <CustomerAccessGate />,
        children: [
          { path: "/cleaning/orders", element: <OrdersPage /> },
          { path: "/cleaning/orders/:id", element: <OrderDetailsPage /> },
          { path: "/cleaning/orders/:id/created", element: <OrderCreatedPage /> },
          { path: "/rent/bookings", element: <RentalBookingsPage /> },
          { path: "/rent/bookings/:id", element: <RentalBookingDetailsPage /> },
          { path: "/account", element: <AccountPage /> },
          { path: "/account/link/telegram", element: <TelegramAccountLinkPage /> },
          { path: "/notifications", element: <NotificationsPage /> },
          { path: "/orders", element: <Navigate replace to="/cleaning/orders" /> },
          { path: "/orders/:id", element: <LegacyCleaningOrderRedirect /> },
          { path: "/orders/:id/created", element: <LegacyCleaningOrderRedirect created /> },
        ],
      },
      { path: "/rent", element: <ServiceAvailabilityGate service="RENTAL"><RentalCatalogPage /></ServiceAvailabilityGate> },
      { path: "/rent/properties", element: <ServiceAvailabilityGate service="RENTAL"><RentalCatalogPage /></ServiceAvailabilityGate> },
      { path: "/rent/properties/:slug", element: <ServiceAvailabilityGate service="RENTAL"><RentalPropertyPage /></ServiceAvailabilityGate> },
      {
        path: "/admin",
        element: <AdminAccessGate />,
        children: [
          { index: true, element: <AdminServiceCatalogPage /> },
          { path: "analytics", element: <AdminAnalyticsPage /> },
          { path: "cleaning", element: <AdminDashboardPage /> },
          { path: "cleaning/orders/:id", element: <AdminOrderPage /> },
          { path: "rent", element: <Navigate replace to="/admin/rent/properties" /> },
          { path: "rent/properties", element: <AdminRentalPropertiesPage /> },
          { path: "rent/properties/:id", element: <AdminRentalPropertyPage /> },
          { path: "rent/properties/:id/calendar", element: <AdminRentalCalendarPage /> },
          { path: "rent/bookings", element: <AdminRentalBookingsPage /> },
          { path: "rent/bookings/:id", element: <AdminRentalBookingPage /> },
          { path: "orders/:id", element: <LegacyAdminOrderRedirect /> },
        ],
      },
      { path: "*", element: <NotFoundPage /> },
    ],
  },
]);
