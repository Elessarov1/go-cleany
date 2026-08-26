import { Outlet } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useAuthentication } from "../../api/AuthApiProvider";
import { ErrorState, LoadingState } from "../PageState/PageState";
import { AdminLoginPage } from "../../pages/AdminLoginPage/AdminLoginPage";
import { NotFoundPage } from "../../pages/NotFoundPage/NotFoundPage";

export function AdminAccessGate() {
  const { t } = useTranslation();
  const authentication = useAuthentication();

  if (authentication.status === "LOADING") return <LoadingState />;
  if (authentication.status === "ERROR") {
    return <ErrorState message={t("auth.loadError")} onRetry={authentication.reload} />;
  }
  if (!authentication.current.authenticated) return <AdminLoginPage />;
  if (!authentication.isAdmin) return <NotFoundPage />;
  return <Outlet />;
}
