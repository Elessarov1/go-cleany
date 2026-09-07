import { lazy } from "react";
import { useTranslation } from "react-i18next";
import { useAuthentication } from "../../api/AuthApiProvider";
import { AuthenticationRequiredState } from "../../components/CustomerAccessGate/CustomerAccessGate";
import { BrandName } from "../../components/BrandName/BrandName";

const AuthenticatedTransferPage = lazy(() => import("../TransferPage/TransferPage")
  .then((module) => ({ default: module.TransferPage })));

export function TransferEntryPage() {
  const { t } = useTranslation();
  const authentication = useAuthentication();

  if (authentication.status === "READY" && authentication.current.authenticated) {
    return <AuthenticatedTransferPage />;
  }

  return (
    <div className="page page--transfer">
      <header className="transfer-hero">
        <span className="eyebrow"><BrandName service="transfer" /> · Alanya</span>
        <h1>{t("transfer.title")}</h1>
        <p>{t("transfer.subtitle")}</p>
      </header>

      <AuthenticationRequiredState compact />
    </div>
  );
}
