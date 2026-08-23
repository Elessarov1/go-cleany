import { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCleaningApi } from "../../api/CleaningApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { OrderStatus } from "../../components/OrderStatus/OrderStatus";
import type { CleaningOrder, ReferralSummary } from "../../domain/order";
import { formatPrice } from "../../domain/pricing";
import { formatDate } from "../../utils/format";
import { BrandName } from "../../components/BrandName/BrandName";

export function OrdersPage() {
  const { t, i18n } = useTranslation();
  const api = useCleaningApi();
  const location = useLocation();
  const [orders, setOrders] = useState<CleaningOrder[] | null>(null);
  const [referral, setReferral] = useState<ReferralSummary | null>(null);
  const [codeCopied, setCodeCopied] = useState(false);
  const [error, setError] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const locale = i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setError(false);
    setOrders(null);
    Promise.all([api.getOrders(), api.getReferralSummary()])
      .then(([orderList, referralSummary]) => {
        if (active) {
          setOrders(orderList);
          setReferral(referralSummary);
        }
      })
      .catch(() => {
        if (active) setError(true);
      });
    return () => {
      active = false;
    };
  }, [api, reloadKey, location.search]);

  if (error) {
    return (
      <ErrorState
        message={t("orders.loadError")}
        onRetry={() => setReloadKey((value) => value + 1)}
      />
    );
  }

  if (!orders) {
    return <LoadingState />;
  }

  return (
    <div className="page">
      <header className="page-header">
        <span className="eyebrow">{t("orders.eyebrow")}</span>
        <h1>{t("orders.title")}</h1>
        <p>{t("orders.subtitle")}</p>
      </header>

      {referral ? (
        <section className="referral-card">
          <div>
            <span className="eyebrow"><BrandName service="cleaning" /> friends</span>
            <h2>{t("referrals.title")}</h2>
            <p>{t("referrals.text")}</p>
          </div>
          {referral.referralCode ? (
            <div className="referral-card__code">
              <span>{t("referrals.codeLabel")}</span>
              <strong>{referral.referralCode}</strong>
              <button
                className="button button--secondary"
                type="button"
                onClick={() => {
                  void navigator.clipboard.writeText(referral.referralCode!)
                    .then(() => setCodeCopied(true))
                    .catch(() => setCodeCopied(false));
                }}
              >
                {codeCopied ? t("referrals.copied") : t("referrals.copy")}
              </button>
              {referral.availableRewards > 0 ? (
                <small>{t("referrals.rewards", { count: referral.availableRewards })}</small>
              ) : null}
            </div>
          ) : (
            <p className="referral-card__locked">{t("referrals.locked")}</p>
          )}
        </section>
      ) : null}

      {orders.length === 0 ? (
        <section className="empty-state">
          <div className="empty-state__art" aria-hidden="true">
            <span className="broom-icon empty-state__broom-icon" />
          </div>
          <h2>{t("orders.emptyTitle")}</h2>
          <p>{t("orders.emptyText")}</p>
          <Link className="button button--primary" to="/cleaning">
            {t("orders.emptyAction")}
          </Link>
        </section>
      ) : (
        <div className="order-list">
          {orders.map((order) => (
            <Link className="order-card" key={order.id} to={`/cleaning/orders/${order.id}`}>
              <div className="order-card__top">
                <time dateTime={order.requestedDate}>
                  {formatDate(order.requestedDate, locale)}
                </time>
                <OrderStatus status={order.status} />
              </div>
              <div className="order-card__body">
                <div className="order-card__icon" aria-hidden="true">
                  <span className="broom-icon order-card__broom-icon" />
                </div>
                <div>
                  <h2>
                    {t(`apartments.${order.apartmentType}`)} · {t(`cleaning.${order.cleaningType}.title`)}
                  </h2>
                  <p>{t(`areas.${order.area}`)} · #{order.id}</p>
                </div>
                <strong>{formatPrice(order.price, order.currency, locale)}</strong>
              </div>
              <span className="order-card__link">{t("orders.open")} <Icon name="arrow-right" size={16} /></span>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
