import { useEffect, useState, type FormEvent } from "react";
import { Link, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCleaningApi } from "../../api/CleaningApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import { OrderStatus } from "../../components/OrderStatus/OrderStatus";
import type { AdminIssuePhoto, AdminOrderDetails } from "../../domain/admin";
import { formatPrice } from "../../domain/pricing";
import { formatDate } from "../../utils/format";

export function AdminOrderPage() {
  const { id } = useParams();
  const orderId = Number(id);
  const { t, i18n } = useTranslation();
  const api = useCleaningApi();
  const [details, setDetails] = useState<AdminOrderDetails | null>(null);
  const [error, setError] = useState(false);
  const [resolutionComment, setResolutionComment] = useState("");
  const [isResolving, setIsResolving] = useState(false);
  const [resolutionError, setResolutionError] = useState(false);
  const locale = i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setDetails(null);
    setError(false);
    api.getAdminOrder(orderId)
      .then((value) => {
        if (active) setDetails(value);
      })
      .catch(() => {
        if (active) setError(true);
      });
    return () => {
      active = false;
    };
  }, [api, orderId]);

  if (error || !Number.isFinite(orderId)) {
    return <ErrorState message={t("admin.orderLoadError")} />;
  }

  if (!details) {
    return <LoadingState />;
  }

  const order = details.order;
  const financial = details.financial;

  const handleResolve = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const comment = resolutionComment.trim();
    if (!comment) {
      setResolutionError(true);
      return;
    }
    try {
      setIsResolving(true);
      setResolutionError(false);
      const updated = await api.resolveAdminIssue(orderId, comment);
      setDetails(updated);
      setResolutionComment("");
    } catch {
      setResolutionError(true);
    } finally {
      setIsResolving(false);
    }
  };

  return (
    <div className="page page--admin-order">
      <Link className="back-link" to="/admin/cleaning">
        <Icon name="arrow-left" size={17} /> {t("common.back")}
      </Link>
      <header className="page-header page-header--compact">
        <span className="eyebrow">{t("admin.orderEyebrow", { id: order.id })}</span>
        <h1>{t("admin.orderTitle")}</h1>
      </header>

      <section className="detail-status-card">
        <span className="detail-status-card__label">{t("details.timelineTitle")}</span>
        <OrderStatus status={order.status} withDescription />
      </section>

      <section className="details-card">
        <div className="details-card__price">
          <div>
            <span>{t("admin.customer")}</span>
            <h2>{order.customerName}</h2>
          </div>
          <strong>{formatPrice(order.price, order.currency, locale)}</strong>
        </div>
        <dl className="detail-list">
          <div><dt>{t("details.date")}</dt><dd>{formatDate(order.requestedDate, locale)}</dd></div>
          <div><dt>{t("details.area")}</dt><dd>{t(`areas.${order.area}`)}</dd></div>
          <div><dt>{t("details.address")}</dt><dd>{order.address}</dd></div>
          <div><dt>{t("details.phone")}</dt><dd>{order.phone}</dd></div>
          <div><dt>{t("admin.communicationIdentity")}</dt><dd>{order.communicationIdentityId}</dd></div>
          <div><dt>{t("admin.cleaner")}</dt><dd>{order.cleanerTelegramUserId ?? t("admin.notAssigned")}</dd></div>
          <div><dt>{t("details.photoReport")}</dt><dd>{details.photoCount}</dd></div>
          <div><dt>{t("admin.financial.basePrice")}</dt><dd>{formatPrice(financial.basePrice, order.currency, locale)}</dd></div>
          <div><dt>{t("admin.financial.baseCommission")}</dt><dd>{formatPrice(financial.baseCommission, order.currency, locale)}</dd></div>
          <div><dt>{t("admin.financial.customerDiscount")}</dt><dd>{formatPrice(financial.customerDiscount, order.currency, locale)}</dd></div>
          <div><dt>{t("admin.financial.partnerPayout")}</dt><dd>{formatPrice(financial.partnerPayout, order.currency, locale)}</dd></div>
          <div><dt>{t("admin.financial.platformNet")}</dt><dd>{formatPrice(financial.platformNet, order.currency, locale)}</dd></div>
          <div><dt>{t("admin.financial.acquisitionSource")}</dt><dd>{financial.acquisitionSource}</dd></div>
          <div><dt>{t("details.customerComment")}</dt><dd>{order.customerComment || t("common.notProvided")}</dd></div>
          {order.cleanerComment ? (
            <div><dt>{t("details.cleanerComment")}</dt><dd>{order.cleanerComment}</dd></div>
          ) : null}
        </dl>
      </section>

      {details.onsiteIssue ? (
        <section className="admin-issue-card">
          <div className="admin-section-heading">
            <div>
              <span className="eyebrow">{t("admin.onsiteIssue.eyebrow")}</span>
              <h2>{t("admin.onsiteIssue.title")}</h2>
            </div>
            <span className="admin-issue-card__icon" aria-hidden="true">⚠️</span>
          </div>

          <dl className="detail-list">
            <div>
              <dt>{t("admin.onsiteIssue.reason")}</dt>
              <dd>{t(`onsiteReasons.${details.onsiteIssue.reason}`)}</dd>
            </div>
            <div>
              <dt>{t("admin.onsiteIssue.cleaner")}</dt>
              <dd>{details.onsiteIssue.cleanerTelegramUserId}</dd>
            </div>
            <div>
              <dt>{t("admin.onsiteIssue.reportedAt")}</dt>
              <dd>{formatTimestamp(details.onsiteIssue.reportedAt, locale)}</dd>
            </div>
            <div>
              <dt>{t("admin.onsiteIssue.comment")}</dt>
              <dd>{details.onsiteIssue.comment}</dd>
            </div>
          </dl>

          <div className="admin-issue-evidence">
            <h3>{t("admin.onsiteIssue.evidence")}</h3>
            <div className="admin-issue-evidence__grid">
              {details.onsiteIssue.photos.map((photo) => (
                <IssueEvidencePhoto key={photo.id} orderId={orderId} photo={photo} />
              ))}
            </div>
          </div>

          {details.onsiteIssue.resolvedAt ? (
            <div className="admin-issue-resolution">
              <h3>{t("admin.onsiteIssue.resolution")}</h3>
              <p>{details.onsiteIssue.resolutionComment}</p>
              <small>
                {formatTimestamp(details.onsiteIssue.resolvedAt, locale)}
                {details.onsiteIssue.resolvedBy
                  ? ` / ${t("admin.onsiteIssue.resolvedBy")} ${details.onsiteIssue.resolvedBy}`
                  : ""}
              </small>
            </div>
          ) : (
            <form className="admin-issue-resolution" onSubmit={handleResolve}>
              <label htmlFor="resolution-comment">{t("admin.onsiteIssue.resolutionComment")}</label>
              <textarea
                id="resolution-comment"
                rows={3}
                maxLength={1000}
                value={resolutionComment}
                onChange={(event) => {
                  setResolutionComment(event.target.value);
                  setResolutionError(false);
                }}
                placeholder={t("admin.onsiteIssue.resolutionPlaceholder")}
              />
              {resolutionError ? (
                <p className="field-error">{t("admin.onsiteIssue.resolveError")}</p>
              ) : null}
              <button
                className="button button--primary button--full"
                type="submit"
                disabled={isResolving}
              >
                {isResolving
                  ? t("admin.onsiteIssue.resolving")
                  : t("admin.onsiteIssue.resolve")}
              </button>
            </form>
          )}
        </section>
      ) : null}

      <section className="admin-history">
        <div className="admin-section-heading">
          <div>
            <h2>{t("admin.history.title")}</h2>
            <p>{t("admin.history.subtitle")}</p>
          </div>
          <Icon name="admin" size={24} />
        </div>
        <ol className="admin-timeline">
          {details.events.map((event) => (
            <li key={event.id}>
              <span className="admin-timeline__marker"><Icon name="check" size={15} /></span>
              <div>
                <strong>{t(`admin.events.${event.eventType}`)}</strong>
                <p>
                  {new Intl.DateTimeFormat(locale, {
                    dateStyle: "medium",
                    timeStyle: "short",
                  }).format(new Date(event.occurredAt))}
                  {" / "}{t(`admin.actors.${event.actorType}`)}
                  {event.actorTelegramUserId ? ` / ${event.actorTelegramUserId}` : ""}
                </p>
                {event.details ? <p className="admin-timeline__details">{event.details}</p> : null}
              </div>
            </li>
          ))}
        </ol>
      </section>
    </div>
  );
}

function IssueEvidencePhoto({
  orderId,
  photo,
}: {
  orderId: number;
  photo: AdminIssuePhoto;
}) {
  const { t } = useTranslation();
  const api = useCleaningApi();
  const [url, setUrl] = useState<string | null>(null);
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    let active = true;
    let objectUrl: string | null = null;
    api.getAdminIssuePhoto(orderId, photo.id)
      .then((blob) => {
        if (!active) return;
        objectUrl = URL.createObjectURL(blob);
        setUrl(objectUrl);
      })
      .catch(() => {
        if (active) setFailed(true);
      });
    return () => {
      active = false;
      if (objectUrl) URL.revokeObjectURL(objectUrl);
    };
  }, [api, orderId, photo.id]);

  if (failed) {
    return <div className="admin-issue-photo is-error">{t("admin.onsiteIssue.photoError")}</div>;
  }
  if (!url) {
    return <div className="admin-issue-photo is-loading">{t("common.loading")}</div>;
  }
  return (
    <a className="admin-issue-photo" href={url} target="_blank" rel="noreferrer">
      <img src={url} alt={t("admin.onsiteIssue.photoAlt", { id: photo.id })} />
      <small>{formatFileSize(photo.sizeBytes)}</small>
    </a>
  );
}

function formatTimestamp(value: string, locale: string): string {
  return new Intl.DateTimeFormat(locale, {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));
}

function formatFileSize(sizeBytes: number): string {
  return `${(sizeBytes / 1024 / 1024).toFixed(1)} MB`;
}
