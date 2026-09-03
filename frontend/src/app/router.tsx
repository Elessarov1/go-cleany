import { lazy } from "react";
import { createBrowserRouter, Navigate, useParams } from "react-router-dom";
import { AppShell } from "../components/AppShell/AppShell";
import { ServiceAvailabilityGate } from "../components/ServiceAvailabilityGate/ServiceAvailabilityGate";
import { AdminAccessGate } from "../components/AdminAccessGate/AdminAccessGate";
import { CustomerAccessGate } from "../components/CustomerAccessGate/CustomerAccessGate";
import { CustomerHubLayout } from "../components/CustomerHubLayout/CustomerHubLayout";

const CreateOrderPage = lazy(() => import("../pages/CreateOrderPage/CreateOrderPage").then((module) => ({ default: module.CreateOrderPage })));
const OrderCreatedPage = lazy(() => import("../pages/OrderCreatedPage/OrderCreatedPage").then((module) => ({ default: module.OrderCreatedPage })));
const OrderDetailsPage = lazy(() => import("../pages/OrderDetailsPage/OrderDetailsPage").then((module) => ({ default: module.OrderDetailsPage })));
const OrdersPage = lazy(() => import("../pages/OrdersPage/OrdersPage").then((module) => ({ default: module.OrdersPage })));
const NotFoundPage = lazy(() => import("../pages/NotFoundPage/NotFoundPage").then((module) => ({ default: module.NotFoundPage })));
const AdminDashboardPage = lazy(() => import("../pages/AdminDashboardPage/AdminDashboardPage").then((module) => ({ default: module.AdminDashboardPage })));
const AdminOrderPage = lazy(() => import("../pages/AdminOrderPage/AdminOrderPage").then((module) => ({ default: module.AdminOrderPage })));
const RentalBookingDetailsPage = lazy(() => import("../pages/RentalBookingDetailsPage/RentalBookingDetailsPage").then((module) => ({ default: module.RentalBookingDetailsPage })));
const RentalBookingsPage = lazy(() => import("../pages/RentalBookingsPage/RentalBookingsPage").then((module) => ({ default: module.RentalBookingsPage })));
const RentalCatalogPage = lazy(() => import("../pages/RentalCatalogPage/RentalCatalogPage").then((module) => ({ default: module.RentalCatalogPage })));
const RentalPropertyPage = lazy(() => import("../pages/RentalPropertyPage/RentalPropertyPage").then((module) => ({ default: module.RentalPropertyPage })));
const ServiceCatalogPage = lazy(() => import("../pages/ServiceCatalogPage/ServiceCatalogPage").then((module) => ({ default: module.ServiceCatalogPage })));
const AdminServiceCatalogPage = lazy(() => import("../pages/AdminServiceCatalogPage/AdminServiceCatalogPage").then((module) => ({ default: module.AdminServiceCatalogPage })));
const AdminRentalPropertiesPage = lazy(() => import("../pages/AdminRentalPropertiesPage/AdminRentalPropertiesPage").then((module) => ({ default: module.AdminRentalPropertiesPage })));
const AdminRentalPropertyPage = lazy(() => import("../pages/AdminRentalPropertyPage/AdminRentalPropertyPage").then((module) => ({ default: module.AdminRentalPropertyPage })));
const AdminRentalCalendarPage = lazy(() => import("../pages/AdminRentalCalendarPage/AdminRentalCalendarPage").then((module) => ({ default: module.AdminRentalCalendarPage })));
const AdminRentalBookingsPage = lazy(() => import("../pages/AdminRentalBookingsPage/AdminRentalBookingsPage").then((module) => ({ default: module.AdminRentalBookingsPage })));
const AdminRentalBookingPage = lazy(() => import("../pages/AdminRentalBookingPage/AdminRentalBookingPage").then((module) => ({ default: module.AdminRentalBookingPage })));
const AccountPage = lazy(() => import("../pages/AccountPage/AccountPage").then((module) => ({ default: module.AccountPage })));
const TelegramAccountLinkPage = lazy(() => import("../pages/TelegramAccountLinkPage/TelegramAccountLinkPage").then((module) => ({ default: module.TelegramAccountLinkPage })));
const NotificationsPage = lazy(() => import("../pages/NotificationsPage/NotificationsPage").then((module) => ({ default: module.NotificationsPage })));
const AdminAnalyticsPage = lazy(() => import("../pages/AdminAnalyticsPage/AdminAnalyticsPage").then((module) => ({ default: module.AdminAnalyticsPage })));
const LegalPage = lazy(() => import("../pages/LegalPage/LegalPage").then((module) => ({ default: module.LegalPage })));
const TransferPage = lazy(() => import("../pages/TransferPage/TransferPage").then((module) => ({ default: module.TransferPage })));
const TransferBookingsPage = lazy(() => import("../pages/TransferBookingsPage/TransferBookingsPage").then((module) => ({ default: module.TransferBookingsPage })));
const TransferBookingDetailsPage = lazy(() => import("../pages/TransferBookingDetailsPage/TransferBookingDetailsPage").then((module) => ({ default: module.TransferBookingDetailsPage })));
const AdminTransferBookingsPage = lazy(() => import("../pages/AdminTransferBookingsPage/AdminTransferBookingsPage").then((module) => ({ default: module.AdminTransferBookingsPage })));
const AdminTransferBookingPage = lazy(() => import("../pages/AdminTransferBookingPage/AdminTransferBookingPage").then((module) => ({ default: module.AdminTransferBookingPage })));
const AdminTransferConfigurationPage = lazy(() => import("../pages/AdminTransferConfigurationPage/AdminTransferConfigurationPage").then((module) => ({ default: module.AdminTransferConfigurationPage })));
const CustomerActivityPage = lazy(() => import("../pages/CustomerActivityPage/CustomerActivityPage").then((module) => ({ default: module.CustomerActivityPage })));
const AdminSupportCasesPage = lazy(() => import("../pages/AdminSupportCasesPage/AdminSupportCasesPage").then((module) => ({ default: module.AdminSupportCasesPage })));
const AdminSupportCasePage = lazy(() => import("../pages/AdminSupportCasePage/AdminSupportCasePage").then((module) => ({ default: module.AdminSupportCasePage })));

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
      { path: "/privacy", element: <LegalPage kind="privacy" /> },
      { path: "/terms", element: <LegalPage kind="terms" /> },
      { path: "/cleaning", element: <ServiceAvailabilityGate service="CLEANING"><CreateOrderPage /></ServiceAvailabilityGate> },
      {
        element: <CustomerAccessGate />,
        children: [
          { path: "/cleaning/orders", element: <OrdersPage /> },
          { path: "/cleaning/orders/:id", element: <OrderDetailsPage /> },
          { path: "/cleaning/orders/:id/created", element: <OrderCreatedPage /> },
          { path: "/rent/bookings", element: <RentalBookingsPage /> },
          { path: "/rent/bookings/:id", element: <RentalBookingDetailsPage /> },
          { path: "/transfer", element: <ServiceAvailabilityGate service="TRANSFER"><TransferPage /></ServiceAvailabilityGate> },
          { path: "/transfer/bookings", element: <TransferBookingsPage /> },
          { path: "/transfer/bookings/:id", element: <TransferBookingDetailsPage /> },
          { path: "/account", element: <AccountPage /> },
          {
            element: <CustomerHubLayout />,
            children: [
              { path: "/account/activity", element: <CustomerActivityPage /> },
              { path: "/notifications", element: <NotificationsPage /> },
            ],
          },
          { path: "/account/link/telegram", element: <TelegramAccountLinkPage /> },
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
          { path: "support", element: <AdminSupportCasesPage /> },
          { path: "support/cases/:id", element: <AdminSupportCasePage /> },
          { path: "cleaning", element: <AdminDashboardPage /> },
          { path: "cleaning/orders/:id", element: <AdminOrderPage /> },
          { path: "rent", element: <Navigate replace to="/admin/rent/properties" /> },
          { path: "rent/properties", element: <AdminRentalPropertiesPage /> },
          { path: "rent/properties/:id", element: <AdminRentalPropertyPage /> },
          { path: "rent/properties/:id/calendar", element: <AdminRentalCalendarPage /> },
          { path: "rent/bookings", element: <AdminRentalBookingsPage /> },
          { path: "rent/bookings/:id", element: <AdminRentalBookingPage /> },
          { path: "transfer", element: <Navigate replace to="/admin/transfer/bookings" /> },
          { path: "transfer/bookings", element: <AdminTransferBookingsPage /> },
          { path: "transfer/bookings/:id", element: <AdminTransferBookingPage /> },
          { path: "transfer/configuration", element: <AdminTransferConfigurationPage /> },
          { path: "orders/:id", element: <LegacyAdminOrderRedirect /> },
        ],
      },
      { path: "*", element: <NotFoundPage /> },
    ],
  },
]);
