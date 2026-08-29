import { useTranslation } from "react-i18next";

export type LegalPageKind = "privacy" | "terms";

interface LegalPageProps {
  kind: LegalPageKind;
}

export function LegalPage({ kind }: LegalPageProps) {
  const { t } = useTranslation();
  const prefix = `legal.${kind}`;

  return (
    <div className="page page--legal">
      <header className="legal-page__header">
        <span className="eyebrow">Loco Place</span>
        <h1>{t(`${prefix}.title`)}</h1>
        <p>{t(`${prefix}.intro`)}</p>
      </header>

      <div className="legal-page__content">
        {["first", "second", "third", "fourth"].map((section) => (
          <section key={section}>
            <h2>{t(`${prefix}.sections.${section}.title`)}</h2>
            <p>{t(`${prefix}.sections.${section}.text`)}</p>
          </section>
        ))}
      </div>

      <p className="legal-page__pilot-note">{t("legal.pilotNote")}</p>
    </div>
  );
}
