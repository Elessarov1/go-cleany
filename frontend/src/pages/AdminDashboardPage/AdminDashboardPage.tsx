import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCleaningApi } from "../../api/CleaningApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { OrderStatus } from "../../components/OrderStatus/OrderStatus";
import type { AdminDashboard, AdminReferralOverview } from "../../domain/admin";
import type { CleaningOrderStatus } from "../../domain/order";
import { formatPrice } from "../../domain/pricing";
import { formatDate } from "../../utils/format";

type StatusFilter = CleaningOrderStatus | "ALL";

export function AdminDashboardPage() {
  const { t, i18n } = useTranslation();
  const api = useCleaningApi();
  const [dashboard, setDashboard] = useState<AdminDashboard | null>(null);
  const [referrals, setReferrals] = useState<AdminReferralOverview | null>(null);
  const [partnerName, setPartnerName] = useState("");
  const [referralActionPending, setReferralActionPending] = useState(false);
  const [error, setError] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const [statusFilter, setStatusFilter] = useState<StatusFilter>("ALL");
  const locale = i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setDashboard(null);
    setError(false);
    Promise.all([api.getAdminDashboard(), api.getAdminReferralOverview()])
      .then(([dashboardValue, referralValue]) => {
        if (active) {
          setDashboard(dashboardValue);
          setReferrals(referralValue);
        }
      })
      .catch(() => {
        if (active) setError(true);
      });
    return () => {
      active = false;
    };
  }, [api, reloadKey]);

  const createPartner = async () => {
    const name = partnerName.trim();
    if (!name) return;
    try {
      setReferralActionPending(true);
      await api.createReferralPartner(name);
      setPartnerName("");
      setReferrals(await api.getAdminReferralOverview());
    } catch {
      setError(true);
    } finally {
      setReferralActionPending(false);
    }
  };

  const markPayoutPaid = async (payoutId: number) => {
    try {
      setReferralActionPending(true);
      await api.markPartnerPayoutPaid(payoutId);
      setReferrals(await api.getAdminReferralOverview());
    } catch {
      setError(true);
    } finally {
      setReferralActionPending(false);
    }
  };

  const orders = useMemo(() => {
    if (!dashboard) return [];
    return statusFilter === "ALL"
      ? dashboard.recentOrders
      : dashboard.recentOrders.filter((order) => order.status === statusFilter);
  }, [dashboard, statusFilter]);

  if (error) {
    return (
      <ErrorState
        message={t("admin.loadError")}
        onRetry={() => setReloadKey((value) => value + 1)}
      />
    );
  }

  if (!dashboard) {
    return <LoadingState />;
  }

  const stats = dashboard.stats;
  const statItems = [
    ["total", stats.totalOrders],
    ["today", stats.ordersToday],
    ["new", stats.newOrders],
    ["active", stats.activeOrders],
    ["completed", stats.completedOrders],
    ["cancelled", stats.cancelledOrders],
  ] as const;
  const filters: StatusFilter[] = [
    "ALL",
    "NEW",
    "ACCEPTED",
    "AWAITING_REPORT",
    "COMPLETED",
    "REJECTED",
    "CANCELLED",
  ];

  return (
    <div className="page page--admin">
      <header className="page-header admin-header">
        <span className="eyebrow">{t("admin.eyebrow")}</span>
        <h1>{t("admin.title")}</h1>
        <p>{t("admin.subtitle")}</p>
      </header>

      <section className="admin-stats" aria-label={t("admin.statsLabel")}>
        {statItems.map(([key, value]) => (
          <article className={`admin-stat admin-stat--${key}`} key={key}>
            <span>{t(`admin.stats.${key}`)}</span>
            <strong>{value}</strong>
          </article>
        ))}
        <article className="admin-stat admin-stat--amount">
          <span>{t("admin.stats.amount")}</span>
          <strong>{formatPrice(stats.completedAmount, stats.currency, locale)}</strong>
        </article>
      </section>

      {referrals ? (
        <section className="admin-orders admin-referrals">
          <div className="admin-section-heading">
            <div>
              <h2>{t("admin.referrals.title")}</h2>
              <p>{t("admin.referrals.subtitle")}</p>
            </div>
            <Icon name="user" size={24} />
          </div>

          <div className="admin-referrals__create">
            <input
              maxLength={255}
              placeholder={t("admin.referrals.partnerName")}
              value={partnerName}
              onChange={(event) => setPartnerName(event.target.value)}
            />
            <button
              className="button button--secondary"
              type="button"
              disabled={referralActionPending || !partnerName.trim()}
              onClick={() => void createPartner()}
            >
              {t("admin.referrals.create")}
            </button>
          </div>

          {referrals.partners.length === 0 ? (
            <p className="admin-orders__empty">{t("admin.referrals.noPartners")}</p>
          ) : (
            <div className="admin-referrals__list">
              {referrals.partners.map((partner) => (
                <div className="admin-referral-row" key={partner.id}>
                  <strong>{partner.name}</strong>
                  <span>{t("admin.referrals.code")}</span>
                  <code>{partner.referralCode}</code>
                </div>
              ))}
            </div>
          )}

          <h3>{t("admin.referrals.payouts")}</h3>
          {referrals.payouts.length === 0 ? (
            <p className="admin-orders__empty">{t("admin.referrals.noPayouts")}</p>
          ) : (
            <div className="admin-referrals__list">
              {referrals.payouts.map((payout) => (
                <div className="admin-referral-row admin-referral-row--payout" key={payout.id}>
                  <div>
                    <strong>{payout.partnerName}</strong>
                    <small>#{payout.sourceOrderId}</small>
                  </div>
                  <b>{formatPrice(payout.amount, payout.currency, locale)}</b>
                  <span>
                    {t(`admin.referrals.${payout.status === "PAID" ? "paid" : "payable"}`)}
                  </span>
                  {payout.status === "PAYABLE" ? (
                    <button
                      className="button button--secondary"
                      type="button"
                      disabled={referralActionPending}
                      onClick={() => void markPayoutPaid(payout.id)}
                    >
                      {t("admin.referrals.markPaid")}
                    </button>
                  ) : null}
                </div>
              ))}
            </div>
          )}
        </section>
      ) : null}

      <section className="admin-orders">
        <div className="admin-section-heading">
          <div>
            <h2>{t("admin.orders.title")}</h2>
            <p>{t("admin.orders.subtitle")}</p>
          </div>
          <Icon name="clipboard" size={24} />
        </div>

        <div className="admin-filters" aria-label={t("admin.orders.filterLabel")}>
          {filters.map((filter) => (
            <button
              className={statusFilter === filter ? "is-active" : ""}
              key={filter}
              type="button"
              onClick={() => setStatusFilter(filter)}
            >
              {filter === "ALL" ? t("admin.orders.all") : t(`status.${filter}`)}
            </button>
          ))}
        </div>

        {orders.length === 0 ? (
          <p className="admin-orders__empty">{t("admin.orders.empty")}</p>
        ) : (
          <div className="admin-order-list">
            {orders.map((order) => (
              <Link className="admin-order-row" key={order.id} to={`/admin/orders/${order.id}`}>
                <div className="admin-order-row__main">
                  <span>#{order.id}</span>
                  <strong>{order.customerName}</strong>
                  <small>
                    {t(`areas.${order.area}`)} / {formatDate(order.requestedDate, locale)}
                  </small>
                </div>
                <div className="admin-order-row__meta">
                  <OrderStatus status={order.status} />
                  <b>{formatPrice(order.price, order.currency, locale)}</b>
                </div>
                <Icon name="arrow-right" size={18} />
              </Link>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
