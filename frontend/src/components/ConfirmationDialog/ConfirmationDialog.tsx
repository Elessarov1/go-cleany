import { useEffect, useId } from "react";
import { useTranslation } from "react-i18next";
import { Icon } from "../Icon/Icon";

interface ConfirmationDialogProps {
  title: string;
  description: string;
  confirmLabel: string;
  pending?: boolean;
  destructive?: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}

export function ConfirmationDialog({
  title,
  description,
  confirmLabel,
  pending = false,
  destructive = false,
  onConfirm,
  onCancel,
}: ConfirmationDialogProps) {
  const { t } = useTranslation();
  const titleId = useId();
  const descriptionId = useId();

  useEffect(() => {
    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape" && !pending) onCancel();
    };
    document.body.classList.add("modal-open");
    window.addEventListener("keydown", handleEscape);
    return () => {
      document.body.classList.remove("modal-open");
      window.removeEventListener("keydown", handleEscape);
    };
  }, [onCancel, pending]);

  return (
    <div className="modal-backdrop" role="presentation" onMouseDown={() => !pending && onCancel()}>
      <section
        className="confirmation-dialog"
        role="dialog"
        aria-modal="true"
        aria-labelledby={titleId}
        aria-describedby={descriptionId}
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="confirmation-dialog__header">
          <h2 id={titleId}>{title}</h2>
          <button className="icon-button" type="button" disabled={pending} aria-label={t("common.close")} onClick={onCancel}>
            <Icon name="close" size={20} />
          </button>
        </div>
        <p id={descriptionId}>{description}</p>
        <div className="confirmation-dialog__actions">
          <button className="button button--secondary" type="button" disabled={pending} onClick={onCancel}>
            {t("common.cancel")}
          </button>
          <button className={`button ${destructive ? "button--danger" : "button--primary"}`} type="button" disabled={pending} onClick={onConfirm}>
            {confirmLabel}
          </button>
        </div>
      </section>
    </div>
  );
}
