import { useTranslation } from "react-i18next";
import { Icon } from "../Icon/Icon";

interface LoadingStateProps {
  label?: string;
}

export function LoadingState({ label }: LoadingStateProps) {
  const { t } = useTranslation();
  return (
    <div className="page-state" role="status">
      <span className="loader" aria-hidden="true" />
      <p>{label ?? t("common.loading")}</p>
    </div>
  );
}

interface ErrorStateProps {
  message: string;
  onRetry?: () => void;
}

export function ErrorState({ message, onRetry }: ErrorStateProps) {
  const { t } = useTranslation();
  return (
    <div className="page-state page-state--error" role="alert">
      <span className="page-state__symbol"><Icon name="info" size={25} /></span>
      <h2>{t("common.errorTitle")}</h2>
      <p>{message}</p>
      {onRetry ? (
        <button className="button button--secondary" type="button" onClick={onRetry}>
          {t("common.retry")}
        </button>
      ) : null}
    </div>
  );
}
