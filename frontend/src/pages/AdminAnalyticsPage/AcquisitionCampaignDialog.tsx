import { useEffect, useId, useState, type FormEvent } from "react";
import { useTranslation } from "react-i18next";
import type { CreateAcquisitionCampaignRequest } from "../../api/AnalyticsApi";
import { Icon } from "../../components/Icon/Icon";
import type {
  AcquisitionCampaign,
  AcquisitionChannel,
  AcquisitionMedium,
  AcquisitionTargetService,
} from "../../domain/analytics";

type CampaignChannel = Exclude<AcquisitionChannel, "ORGANIC">;

interface AcquisitionCampaignDialogProps {
  onCreate: (request: CreateAcquisitionCampaignRequest) => Promise<AcquisitionCampaign>;
  onClose: () => void;
}

const CHANNELS: CampaignChannel[] = [
  "QR",
  "PARTNER",
  "CUSTOMER_REFERRAL",
  "PROMO_CAMPAIGN",
  "DIRECT_CAMPAIGN",
  "OTHER",
];

const MEDIUMS: AcquisitionMedium[] = [
  "QR_STICKER",
  "QR_MAGNET",
  "QR_PRINT",
  "PARTNER_LINK",
  "REFERRAL_CODE",
  "PROMO_CODE",
  "DIRECT_LINK",
  "OTHER",
];

const TARGET_SERVICES: AcquisitionTargetService[] = ["PLATFORM", "CLEANING", "RENTAL"];

const DEFAULT_MEDIUM_BY_CHANNEL: Record<CampaignChannel, AcquisitionMedium> = {
  QR: "QR_MAGNET",
  PARTNER: "PARTNER_LINK",
  CUSTOMER_REFERRAL: "REFERRAL_CODE",
  PROMO_CAMPAIGN: "PROMO_CODE",
  DIRECT_CAMPAIGN: "DIRECT_LINK",
  OTHER: "OTHER",
};

export function AcquisitionCampaignDialog({ onCreate, onClose }: AcquisitionCampaignDialogProps) {
  const { t } = useTranslation();
  const titleId = useId();
  const [name, setName] = useState("");
  const [publicCode, setPublicCode] = useState("");
  const [channel, setChannel] = useState<CampaignChannel>("QR");
  const [medium, setMedium] = useState<AcquisitionMedium>("QR_MAGNET");
  const [targetService, setTargetService] = useState<AcquisitionTargetService>("CLEANING");
  const [partnerId, setPartnerId] = useState("");
  const [created, setCreated] = useState<AcquisitionCampaign | null>(null);
  const [pending, setPending] = useState(false);
  const [failed, setFailed] = useState(false);
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape" && !pending) onClose();
    };
    document.body.classList.add("modal-open");
    window.addEventListener("keydown", handleEscape);
    return () => {
      document.body.classList.remove("modal-open");
      window.removeEventListener("keydown", handleEscape);
    };
  }, [onClose, pending]);

  const trackingUrl = created ? `${window.location.origin}${created.trackingPath}` : "";

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setPending(true);
    setFailed(false);
    try {
      const campaign = await onCreate({
        publicCode: publicCode.trim(),
        name: name.trim(),
        channel,
        medium,
        targetService,
        partnerId: partnerId ? Number(partnerId) : null,
      });
      setCreated(campaign);
    } catch {
      setFailed(true);
    } finally {
      setPending(false);
    }
  }

  async function copyTrackingUrl() {
    try {
      await navigator.clipboard.writeText(trackingUrl);
      setCopied(true);
    } catch {
      setCopied(false);
    }
  }

  return (
    <div className="modal-backdrop" role="presentation" onMouseDown={() => !pending && onClose()}>
      <section
        className="campaign-dialog"
        role="dialog"
        aria-modal="true"
        aria-labelledby={titleId}
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="campaign-dialog__header">
          <div>
            <span className="eyebrow">{t("analytics.campaigns.eyebrow")}</span>
            <h2 id={titleId}>{t(created ? "analytics.campaigns.createdTitle" : "analytics.campaigns.createTitle")}</h2>
          </div>
          <button className="icon-button" type="button" disabled={pending} aria-label={t("common.close")} onClick={onClose}>
            <Icon name="close" size={20} />
          </button>
        </div>

        {created ? (
          <div className="campaign-dialog__success">
            <span className="campaign-dialog__success-icon"><Icon name="check" size={24} /></span>
            <p>{t("analytics.campaigns.createdText", { name: created.name })}</p>
            <label className="field">
              <span>{t("analytics.campaigns.trackingUrl")}</span>
              <input value={trackingUrl} readOnly onFocus={(event) => event.currentTarget.select()} />
            </label>
            <p className="campaign-dialog__hint">{t("analytics.campaigns.qrHint")}</p>
            <div className="campaign-dialog__actions">
              <button className="button button--secondary" type="button" onClick={() => void copyTrackingUrl()}>
                {t(copied ? "analytics.campaigns.copied" : "analytics.campaigns.copy")}
              </button>
              <button className="button button--primary" type="button" onClick={onClose}>{t("common.close")}</button>
            </div>
          </div>
        ) : (
          <form className="campaign-dialog__form" onSubmit={(event) => void submit(event)}>
            <p className="campaign-dialog__intro">{t("analytics.campaigns.createText")}</p>
            <div className="campaign-dialog__grid">
              <label className="field campaign-dialog__wide">
                <span>{t("analytics.campaigns.name")}</span>
                <input value={name} maxLength={255} required onChange={(event) => setName(event.target.value)} />
              </label>
              <label className="field campaign-dialog__wide">
                <span>{t("analytics.campaigns.publicCode")}</span>
                <input
                  value={publicCode}
                  maxLength={60}
                  pattern="[a-z0-9]+(?:-[a-z0-9]+)*"
                  placeholder="mahmutlar-magnet-sep-2026-a"
                  autoCapitalize="none"
                  spellCheck={false}
                  required
                  onChange={(event) => setPublicCode(event.target.value.toLowerCase().replaceAll("_", "-"))}
                />
                <small>{t("analytics.campaigns.publicCodeHint")}</small>
              </label>
              <label className="field">
                <span>{t("analytics.campaigns.channel")}</span>
                <select value={channel} onChange={(event) => {
                  const value = event.target.value as CampaignChannel;
                  setChannel(value);
                  setMedium(DEFAULT_MEDIUM_BY_CHANNEL[value]);
                }}>
                  {CHANNELS.map((item) => <option key={item} value={item}>{t(`analytics.channels.${item}`)}</option>)}
                </select>
              </label>
              <label className="field">
                <span>{t("analytics.campaigns.medium")}</span>
                <select value={medium} onChange={(event) => setMedium(event.target.value as AcquisitionMedium)}>
                  {MEDIUMS.map((item) => <option key={item} value={item}>{t(`analytics.mediums.${item}`)}</option>)}
                </select>
              </label>
              <label className="field">
                <span>{t("analytics.campaigns.targetService")}</span>
                <select value={targetService} onChange={(event) => setTargetService(event.target.value as AcquisitionTargetService)}>
                  {TARGET_SERVICES.map((item) => <option key={item} value={item}>{t(`analytics.campaigns.targetServices.${item}`)}</option>)}
                </select>
              </label>
              <label className="field">
                <span>{t("analytics.campaigns.partnerId")}</span>
                <input type="number" min="1" inputMode="numeric" value={partnerId} onChange={(event) => setPartnerId(event.target.value)} />
                <small>{t("analytics.campaigns.partnerIdHint")}</small>
              </label>
            </div>
            {failed ? <p className="field-error" role="alert">{t("analytics.campaigns.createError")}</p> : null}
            <div className="campaign-dialog__actions">
              <button className="button button--secondary" type="button" disabled={pending} onClick={onClose}>{t("common.cancel")}</button>
              <button className="button button--primary" type="submit" disabled={pending}>
                {t(pending ? "analytics.campaigns.creating" : "analytics.campaigns.create")}
              </button>
            </div>
          </form>
        )}
      </section>
    </div>
  );
}
