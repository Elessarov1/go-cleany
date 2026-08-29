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

  const nextTheme: ColorTheme = theme === "light" ? "dark" : "light";
  const switchLabel = nextTheme === "dark" ? t("theme.dark") : t("theme.light");

  const toggleTheme = () => {
    selectTheme(nextTheme);
    setTheme(nextTheme);
  };

  return (
    <button
      className="theme-switcher"
      type="button"
      title={switchLabel}
      aria-label={switchLabel}
      onClick={toggleTheme}
    >
      <Icon name={theme === "light" ? "sun" : "moon"} size={18} strokeWidth={2} />
    </button>
  );
}
