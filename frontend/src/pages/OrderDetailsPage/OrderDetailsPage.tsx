import { useEffect, useState } from "react";
import { Link, useLocation, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCleaningApi } from "../../api/CleaningApiProvider";
import { usePlatformCatalogApi } from "../../api/PlatformCatalogApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { OrderStatus } from "../../components/OrderStatus/OrderStatus";
import type { CleaningOrder } from "../../domain/order";
import { formatPrice } from "../../domain/pricing";
import { formatDate } from "../../utils/format";
import { BrandName } from "../../components/BrandName/BrandName";
import { TransactionCarePanel } from "../../components/TransactionCarePanel/TransactionCarePanel";

export function OrderDetailsPage() {
  const { id } = useParams();
  const orderId = Number(id);
  const { t, i18n } = useTranslation();
  const api = useCleaningApi();
  const catalogApi = usePlatformCatalogApi();
  const location = useLocation();
  const [order, setOrder] = useState<CleaningOrder | null>(null);
  const [error, setError] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const [isCancelling, setIsCancelling] = useState(false);
  const [cancelError, setCancelError] = useState(false);
  const [reportPhotos, setReportPhotos] = useState<Array<{ id: number; url: string }>>([]);
  const [activePhoto, setActivePhoto] = useState<string | null>(null);
  const [repeatAvailable, setRepeatAvailable] = useState(false);
  const locale = i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setOrder(null);
    setError(false);
    api.getOrder(orderId)
      .then((value) => {
        if (active) setOrder(value);
      })
      .catch(() => {
        if (active) setError(true);
      });
    return () => {
      active = false;
    };
  }, [api, orderId, reloadKey, location.search]);

  useEffect(() => {
    if (order?.report?.status !== "AVAILABLE") {
      setReportPhotos([]);
      return;
    }
    let active = true;
    const urls: string[] = [];
    Promise.all(order.report.photos.map(async (photo) => {
      const blob = await api.getReportPhoto(orderId, photo.id);
      const url = URL.createObjectURL(blob);
      urls.push(url);
      return { id: photo.id, url };
    })).then((photos) => {
      if (active) setReportPhotos(photos);
    }).catch(() => undefined);
    return () => {
      active = false;
      urls.forEach((url) => URL.revokeObjectURL(url));
    };
  }, [api, order, orderId]);

  useEffect(() => {
    let active = true;
    setRepeatAvailable(false);
    if (order?.status !== "COMPLETED") return () => { active = false; };
    catalogApi.getServices()
      .then((services) => {
        if (!active || !services.some((service) => service.service === "CLEANING")) return;
        setRepeatAvailable(true);
        void api.recordRepeatShown(order.id).catch(() => undefined);
      })
      .catch(() => undefined);
    return () => { active = false; };
  }, [api, catalogApi, order]);

  const cancelOrder = async () => {
    if (!window.confirm(t("details.cancelConfirm"))) return;
    try {
      setIsCancelling(true);
      setCancelError(false);
      setOrder(await api.cancelOrder(orderId));
    } catch {
      setCancelError(true);
    } finally {
      setIsCancelling(false);
    }
  };

  if (error || !Number.isFinite(orderId)) {
    return (
      <ErrorState
        message={t("details.loadError")}
        onRetry={() => setReloadKey((value) => value + 1)}
      />
    );
  }

  if (!order) {
    return <LoadingState />;
  }

  return (
    <div className="page page--details">
      <Link className="back-link" to="/cleaning/orders"><Icon name="arrow-left" size={17} /> {t("common.back")}</Link>
      <header className="page-header page-header--compact">
        <span className="eyebrow">{t("details.eyebrow", { id: order.id })}</span>
        <h1>{t("details.title")}</h1>
      </header>

      <section className="detail-status-card">
        <span className="detail-status-card__label">{t("details.timelineTitle")}</span>
        <OrderStatus status={order.status} withDescription />
      </section>

      <section className="details-card">
        <div className="details-card__price">
          <div>
            <span>{t("details.cleaning")}</span>
            <h2>{t(`cleaning.${order.cleaningType}.title`)}</h2>
          </div>
          <div className="details-card__price-value">
            {order.customerDiscount > 0 ? (
              <span>{formatPrice(order.basePrice, order.currency, locale)}</span>
            ) : null}
            <strong>{formatPrice(order.finalCustomerPrice, order.currency, locale)}</strong>
          </div>
        </div>
        <dl className="detail-list">
          <div><dt>{t("details.date")}</dt><dd>{formatDate(order.requestedDate, locale)}</dd></div>
          <div><dt>{t("details.area")}</dt><dd>{t(`areas.${order.area}`)}</dd></div>
          <div><dt>{t("details.address")}</dt><dd>{order.address}</dd></div>
          <div>
            <dt>{t("details.apartment")}</dt>
            <dd>
              {t(`apartments.${order.apartmentType}`)}
              {order.duplex ? ` · ${t("create.apartment.duplexTitle")}` : ""}
            </dd>
          </div>
          <div><dt>{t("details.phone")}</dt><dd>{order.phone}</dd></div>
          {order.customerDiscount > 0 ? (
            <div>
              <dt>{t("details.discount")}</dt>
              <dd>−{formatPrice(order.customerDiscount, order.currency, locale)}</dd>
            </div>
          ) : null}
          <div><dt>{t("details.customerComment")}</dt><dd>{order.customerComment || t("common.notProvided")}</dd></div>
          {order.cleanerComment ? (
            <div><dt>{t("details.cleanerComment")}</dt><dd>{order.cleanerComment}</dd></div>
          ) : null}
          {order.photoCount ? (
            <div><dt>{t("details.photoReport")}</dt><dd>{t("details.photos", { count: order.photoCount })}</dd></div>
          ) : null}
        </dl>
      </section>

      <section className="cleaning-report-card">
        <div className="cleaning-report-card__header">
          <div><span className="eyebrow"><BrandName /></span><h2>{t("details.reportTitle")}</h2></div>
          {order.report?.expiresAt && order.report.status === "AVAILABLE" ? (
            <time dateTime={order.report.expiresAt}>
              {t("details.reportAvailableUntil", {
                date: new Intl.DateTimeFormat(locale, { day: "numeric", month: "long" }).format(new Date(order.report.expiresAt)),
              })}
            </time>
          ) : null}
        </div>
        {order.report?.status === "AVAILABLE" ? (
          <>
            {order.report.cleanerComment ? <p>{order.report.cleanerComment}</p> : null}
            <div className="cleaning-report-gallery">
              {reportPhotos.map((photo, index) => (
                <button type="button" key={photo.id} onClick={() => setActivePhoto(photo.url)}>
                  <img src={photo.url} alt={t("details.reportPhotoAlt", { index: index + 1 })} />
                </button>
              ))}
            </div>
          </>
        ) : order.report?.status === "EXPIRED" ? (
          <div className="cleaning-report-card__expired">
            <strong>{t("details.reportExpired")}</strong>
            <p>{t("details.reportRetention", { count: order.report.retentionDays })}</p>
            {order.report.cleanerComment ? <p>{order.report.cleanerComment}</p> : null}
          </div>
        ) : (
          <p>{t("details.reportNotReady")}</p>
        )}
      </section>

      {activePhoto ? (
        <div className="cleaning-report-lightbox" role="dialog" aria-modal="true" onClick={() => setActivePhoto(null)}>
          <button type="button" aria-label={t("details.reportClose")} onClick={() => setActivePhoto(null)}>×</button>
          <img src={activePhoto} alt="" onClick={(event) => event.stopPropagation()} />
        </div>
      ) : null}

      <div className="payment-note payment-note--standalone">
        <span><Icon name="wallet" size={17} /></span>
        <div><strong>{t("details.price")}</strong><p>{t("details.payment")}</p></div>
      </div>

      <TransactionCarePanel service="CLEANING" sourceEntityId={order.id} />

      {cancelError ? <p className="form-alert" role="alert">{t("details.cancelError")}</p> : null}
      {repeatAvailable ? (
        <Link className="button button--primary button--full" to={`/cleaning?repeatFrom=${order.id}`}>
          {t("details.repeat")}
        </Link>
      ) : null}
      {order.status === "NEW" ? (
        <button
          className="button button--danger button--full"
          type="button"
          disabled={isCancelling}
          onClick={() => void cancelOrder()}
        >
          {isCancelling ? t("details.cancelling") : t("details.cancel")}
        </button>
      ) : null}
    </div>
  );
}
