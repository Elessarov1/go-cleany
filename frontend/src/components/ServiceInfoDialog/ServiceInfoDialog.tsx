import { useEffect } from "react";
import { useTranslation } from "react-i18next";
import type { CleaningType } from "../../domain/order";
import { Icon } from "../Icon/Icon";
import { BrandName } from "../BrandName/BrandName";

interface ServiceInfoDialogProps {
  cleaningType: CleaningType;
  onClose: () => void;
}

export function ServiceInfoDialog({
  cleaningType,
  onClose,
}: ServiceInfoDialogProps) {
  const { t } = useTranslation();
  const included = t(`cleaning.${cleaningType}.included`, {
    returnObjects: true,
  }) as string[];
  const excluded = t(`cleaning.${cleaningType}.excluded`, {
    returnObjects: true,
  }) as string[];

  useEffect(() => {
    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        onClose();
      }
    };
    document.body.classList.add("modal-open");
    window.addEventListener("keydown", handleEscape);
    return () => {
      document.body.classList.remove("modal-open");
      window.removeEventListener("keydown", handleEscape);
    };
  }, [onClose]);

  return (
    <div className="modal-backdrop" role="presentation" onMouseDown={onClose}>
      <section
        className="service-dialog"
        role="dialog"
        aria-modal="true"
        aria-labelledby="service-dialog-title"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="service-dialog__handle" aria-hidden="true" />
        <div className="service-dialog__header">
          <div>
            <span className="eyebrow"><BrandName service="cleaning" /></span>
            <h2 id="service-dialog-title">
              {t("create.cleaning.includedTitle", {
                type: t(`cleaning.${cleaningType}.title`),
              })}
            </h2>
          </div>
          <button
            className="icon-button"
            type="button"
            aria-label={t("common.close")}
            onClick={onClose}
          >
            <Icon name="close" size={20} />
          </button>
        </div>

        <ul className="check-list">
          {included.map((item) => (
            <li key={item}><span><Icon name="check" size={15} /></span>{item}</li>
          ))}
        </ul>

        <h3 className="service-dialog__subtitle">{t("create.cleaning.exclusions")}</h3>
        <ul className="excluded-list">
          {excluded.map((item) => <li key={item}>{item}</li>)}
        </ul>

        <button className="button button--primary button--full" type="button" onClick={onClose}>
          {t("common.close")}
        </button>
      </section>
    </div>
  );
}
