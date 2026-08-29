import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import {
  appliedTheme,
  selectTheme,
  subscribeToSystemTheme,
  type ColorTheme,
} from "../../theme/theme";
import { Icon } from "../Icon/Icon";

export function ThemeSwitcher() {
  const { t } = useTranslation();
  const [theme, setTheme] = useState<ColorTheme>(() => appliedTheme());

  useEffect(() => subscribeToSystemTheme(setTheme), []);

  const chooseTheme = (nextTheme: ColorTheme) => {
    selectTheme(nextTheme);
    setTheme(nextTheme);
  };

  return (
    <div className="theme-switcher" role="group" aria-label={t("theme.label")}>
      <button
        className={theme === "light" ? "is-active" : ""}
        type="button"
        title={t("theme.light")}
        aria-label={t("theme.light")}
        aria-pressed={theme === "light"}
        onClick={() => chooseTheme("light")}
      >
        <Icon name="sun" size={16} strokeWidth={2} />
      </button>
      <button
        className={theme === "dark" ? "is-active" : ""}
        type="button"
        title={t("theme.dark")}
        aria-label={t("theme.dark")}
        aria-pressed={theme === "dark"}
        onClick={() => chooseTheme("dark")}
      >
        <Icon name="moon" size={16} strokeWidth={2} />
      </button>
    </div>
  );
}
