import { useEffect, useMemo, useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useCleaningApi } from "../../api/CleaningApiProvider";
import { Icon } from "../../components/Icon/Icon";
import { ServiceInfoDialog } from "../../components/ServiceInfoDialog/ServiceInfoDialog";
import { LoadingState, ErrorState } from "../../components/PageState/PageState";
import type { CleaningConfiguration } from "../../domain/configuration";
import type {
  ApartmentType,
  CleaningType,
  CreateCleaningOrderRequest,
  ServiceArea,
} from "../../domain/order";
import { calculateDisplayedPrice, formatPrice } from "../../domain/pricing";
import { addDaysToInputValue, todayAsInputValue } from "../../utils/format";

interface FormState {
  area?: ServiceArea;
  apartmentType?: ApartmentType;
  duplex: boolean;
  cleaningType?: CleaningType;
  requestedDate: string;
  address: string;
  phone: string;
  comment: string;
}

type FormField =
  | "area"
  | "apartmentType"
  | "cleaningType"
  | "requestedDate"
  | "address"
  | "phone";

const initialForm: FormState = {
  duplex: false,
  requestedDate: "",
  address: "",
  phone: "",
  comment: "",
};

export function CreateOrderPage() {
  const { t, i18n } = useTranslation();
  const api = useCleaningApi();
  const navigate = useNavigate();
  const [configuration, setConfiguration] = useState<CleaningConfiguration | null>(null);
  const [configurationError, setConfigurationError] = useState(false);
  const [reloadKey, setReloadKey] = useState(0);
  const [form, setForm] = useState<FormState>(initialForm);
  const [errors, setErrors] = useState<Partial<Record<FormField, string>>>({});
  const [serviceInfo, setServiceInfo] = useState<CleaningType | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState(false);
  const locale = i18n.resolvedLanguage === "ru" ? "ru-RU" : "en-GB";

  useEffect(() => {
    let active = true;
    setConfigurationError(false);
    api.getConfiguration()
      .then((value) => {
        if (active) setConfiguration(value);
      })
      .catch(() => {
        if (active) setConfigurationError(true);
      });
    return () => {
      active = false;
    };
  }, [api, reloadKey]);

  const price = useMemo(() => {
    if (!configuration || !form.apartmentType || !form.cleaningType) {
      return null;
    }
    return calculateDisplayedPrice(
      configuration,
      form.apartmentType,
      form.cleaningType,
      form.duplex,
    );
  }, [configuration, form.apartmentType, form.cleaningType, form.duplex]);

  const updateForm = <K extends keyof FormState>(key: K, value: FormState[K]) => {
    setForm((current) => ({ ...current, [key]: value }));
    if (key in errors) {
      setErrors((current) => ({ ...current, [key]: undefined }));
    }
  };

  const validate = (): Partial<Record<FormField, string>> => {
    const nextErrors: Partial<Record<FormField, string>> = {};
    if (!form.area) nextErrors.area = t("create.validation.area");
    if (!form.apartmentType) nextErrors.apartmentType = t("create.validation.apartmentType");
    if (!form.cleaningType) nextErrors.cleaningType = t("create.validation.cleaningType");
    if (!form.requestedDate) nextErrors.requestedDate = t("create.validation.requestedDate");
    if (!form.address.trim()) nextErrors.address = t("create.validation.address");
    if (!form.phone.trim()) nextErrors.phone = t("create.validation.phone");
    return nextErrors;
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const nextErrors = validate();
    setErrors(nextErrors);
    setSubmitError(false);

    if (Object.keys(nextErrors).length > 0) {
      document.querySelector(".field-error")?.parentElement?.scrollIntoView({
        behavior: "smooth",
        block: "center",
      });
      return;
    }

    if (!form.area || !form.apartmentType || !form.cleaningType) {
      return;
    }

    const request: CreateCleaningOrderRequest = {
      area: form.area,
      apartmentType: form.apartmentType,
      duplex: form.duplex,
      cleaningType: form.cleaningType,
      requestedDate: form.requestedDate,
      address: form.address.trim(),
      phone: form.phone.trim(),
      comment: form.comment.trim() || undefined,
    };

    try {
      setIsSubmitting(true);
      const order = await api.createOrder(request);
      navigate(`/orders/${order.id}/created`);
    } catch {
      setSubmitError(true);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!configuration && !configurationError) {
    return <LoadingState />;
  }

  if (configurationError || !configuration) {
    return (
      <ErrorState
        message={t("orders.loadError")}
        onRetry={() => setReloadKey((value) => value + 1)}
      />
    );
  }

  const today = todayAsInputValue();
  const lastBookingDate = addDaysToInputValue(today, configuration.bookingDaysAhead);

  return (
    <div className="page page--create">
      <section className="hero">
        <div className="hero__copy">
          <span className="eyebrow">{t("create.eyebrow")}</span>
          <h1>{t("create.title")}</h1>
          <p>{t("create.subtitle")}</p>
        </div>
        <div className="hero__art" aria-hidden="true">
          <span className="hero__broom-icon" />
        </div>
      </section>

      <form className="booking-form" noValidate onSubmit={handleSubmit}>
        <section className="form-section">
          <div className="section-heading">
            <span className="section-heading__number">{t("create.area.step")}</span>
            <div>
              <h2>{t("create.area.title")}</h2>
              <p>{t("create.area.hint")}</p>
            </div>
          </div>
          <div className="choice-grid choice-grid--areas">
            {configuration.areas.map((area) => (
              <button
                key={area}
                className={`choice-card${form.area === area ? " is-selected" : ""}`}
                type="button"
                aria-pressed={form.area === area}
                onClick={() => updateForm("area", area)}
              >
                <span className="choice-card__pin"><Icon name="location" size={20} /></span>
                <span>{t(`areas.${area}`)}</span>
              </button>
            ))}
          </div>
          {errors.area ? <p className="field-error">{errors.area}</p> : null}
        </section>

        <section className="form-section">
          <div className="section-heading">
            <span className="section-heading__number">{t("create.apartment.step")}</span>
            <div><h2>{t("create.apartment.title")}</h2></div>
          </div>
          <div className="choice-grid choice-grid--apartments">
            {configuration.apartmentTypes.map(({ type }) => (
              <button
                key={type}
                className={`choice-card choice-card--compact${form.apartmentType === type ? " is-selected" : ""}`}
                type="button"
                aria-pressed={form.apartmentType === type}
                onClick={() => updateForm("apartmentType", type)}
              >
                {t(`apartments.${type}`)}
              </button>
            ))}
          </div>
          {errors.apartmentType ? <p className="field-error">{errors.apartmentType}</p> : null}

          <div className="toggle-row">
            <div>
              <strong>{t("create.apartment.duplexTitle")}</strong>
              <p>{t("create.apartment.duplexHint")}</p>
            </div>
            <button
              className={`switch${form.duplex ? " is-on" : ""}`}
              type="button"
              role="switch"
              aria-checked={form.duplex}
              onClick={() => updateForm("duplex", !form.duplex)}
            >
              <span />
            </button>
          </div>
        </section>

        <section className="form-section">
          <div className="section-heading">
            <span className="section-heading__number">{t("create.cleaning.step")}</span>
            <div><h2>{t("create.cleaning.title")}</h2></div>
          </div>
          <div className="cleaning-options">
            {(["REGULAR", "DEEP"] as CleaningType[]).map((type) => (
              <article
                key={type}
                className={`cleaning-card${form.cleaningType === type ? " is-selected" : ""}`}
              >
                <button
                  className="cleaning-card__select"
                  type="button"
                  aria-pressed={form.cleaningType === type}
                  onClick={() => updateForm("cleaningType", type)}
                >
                  <span className="cleaning-card__radio" aria-hidden="true" />
                  <span>
                    <strong>{t(`cleaning.${type}.title`)}</strong>
                    <small>{t(`cleaning.${type}.description`)}</small>
                  </span>
                  {form.apartmentType ? (
                    <b>
                      {formatPrice(
                        calculateDisplayedPrice(configuration, form.apartmentType, type, form.duplex),
                        configuration.currency,
                        locale,
                      )}
                    </b>
                  ) : null}
                </button>
                <button
                  className="text-button"
                  type="button"
                  onClick={() => setServiceInfo(type)}
                >
                  {t("create.cleaning.included")} <Icon name="arrow-right" size={14} />
                </button>
              </article>
            ))}
          </div>
          {errors.cleaningType ? <p className="field-error">{errors.cleaningType}</p> : null}
        </section>

        <section className="form-section">
          <div className="section-heading">
            <span className="section-heading__number">{t("create.details.step")}</span>
            <div><h2>{t("create.details.title")}</h2></div>
          </div>

          <div className="field">
            <label htmlFor="requested-date">{t("create.details.date")}</label>
            <input
              id="requested-date"
              type="date"
              min={today}
              max={lastBookingDate}
              value={form.requestedDate}
              onChange={(event) => updateForm("requestedDate", event.target.value)}
            />
            <small>{t("create.details.dateHint")}</small>
            {errors.requestedDate ? <p className="field-error">{errors.requestedDate}</p> : null}
          </div>

          <div className="field">
            <label htmlFor="address">{t("create.details.address")}</label>
            <input
              id="address"
              autoComplete="street-address"
              placeholder={t("create.details.addressPlaceholder")}
              value={form.address}
              onChange={(event) => updateForm("address", event.target.value)}
            />
            {errors.address ? <p className="field-error">{errors.address}</p> : null}
          </div>

          <div className="field">
            <label htmlFor="phone">{t("create.details.phone")}</label>
            <input
              id="phone"
              type="tel"
              inputMode="tel"
              autoComplete="tel"
              placeholder={t("create.details.phonePlaceholder")}
              value={form.phone}
              onChange={(event) => updateForm("phone", event.target.value)}
            />
            {errors.phone ? <p className="field-error">{errors.phone}</p> : null}
          </div>

          <div className="field">
            <label htmlFor="comment">
              {t("create.details.comment")} <span>{t("common.optional")}</span>
            </label>
            <textarea
              id="comment"
              rows={3}
              placeholder={t("create.details.commentPlaceholder")}
              value={form.comment}
              onChange={(event) => updateForm("comment", event.target.value)}
            />
          </div>
        </section>

        <section className="price-summary">
          <div className="price-summary__top">
            <div>
              <span>{t("create.summary.title")}</span>
              {form.apartmentType && form.cleaningType ? (
                <p>
                  {t(`apartments.${form.apartmentType}`)} · {t(`cleaning.${form.cleaningType}.title`)}
                  {form.duplex ? ` · ${t("create.apartment.duplexTitle")}` : ""}
                </p>
              ) : <p>{t("create.summary.pending")}</p>}
            </div>
            {price !== null ? (
              <div className="price-summary__price">
                <strong>{formatPrice(price, configuration.currency, locale)}</strong>
                <small>{t("create.summary.fixed")}</small>
              </div>
            ) : null}
          </div>
          <div className="payment-note">
            <span><Icon name="wallet" size={17} /></span>
            <p>{t("create.summary.directPayment")}</p>
          </div>
          {submitError ? <p className="form-alert" role="alert">{t("create.error")}</p> : null}
          <button
            className="button button--primary button--full button--large"
            type="submit"
            disabled={isSubmitting || price === null}
          >
            {isSubmitting
              ? t("create.submitting")
              : price === null
                ? t("create.summary.pending")
                : t("create.submit", {
                    price: formatPrice(price, configuration.currency, locale),
                  })}
          </button>
        </section>
      </form>

      {serviceInfo ? (
        <ServiceInfoDialog cleaningType={serviceInfo} onClose={() => setServiceInfo(null)} />
      ) : null}
    </div>
  );
}
