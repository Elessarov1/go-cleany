import { lazy } from "react";
import { useTranslation } from "react-i18next";
import { useAuthentication } from "../../api/AuthApiProvider";
import { AuthenticationRequiredState } from "../../components/CustomerAccessGate/CustomerAccessGate";
import { BrandName } from "../../components/BrandName/BrandName";
import { Icon } from "../../components/Icon/Icon";

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

      <section className="transfer-panel" aria-labelledby="transfer-public-benefits">
        <div className="transfer-panel__heading">
          <span>01</span>
          <h2 id="transfer-public-benefits">{t("transfer.landing.title")}</h2>
        </div>
        <div className="rental-amenities">
          <span><Icon name="check" size={14} />{t("transfer.landing.directions")}</span>
          <span><Icon name="check" size={14} />{t("transfer.landing.price")}</span>
          <span><Icon name="check" size={14} />{t("transfer.landing.driver")}</span>
        </div>
      </section>

      <AuthenticationRequiredState compact />
    </div>
  );
}
