export type ColorTheme = "light" | "dark";

const THEME_STORAGE_KEY = "loco.theme";
const DARK_THEME_QUERY = "(prefers-color-scheme: dark)";

function storedTheme(): ColorTheme | null {
  try {
    const value = localStorage.getItem(THEME_STORAGE_KEY);
    return value === "light" || value === "dark" ? value : null;
  } catch {
    return null;
  }
}

function systemTheme(): ColorTheme {
  return window.matchMedia(DARK_THEME_QUERY).matches ? "dark" : "light";
}

export function resolvedTheme(): ColorTheme {
  return storedTheme() ?? systemTheme();
}

export function appliedTheme(): ColorTheme {
  return document.documentElement.dataset.theme === "dark" ? "dark" : "light";
}

export function applyTheme(theme: ColorTheme): void {
  document.documentElement.dataset.theme = theme;
  document.documentElement.style.colorScheme = theme;
}

export function initializeTheme(): void {
  applyTheme(resolvedTheme());
}

export function selectTheme(theme: ColorTheme): void {
  try {
    localStorage.setItem(THEME_STORAGE_KEY, theme);
  } catch {
    // Theme selection still applies for the current page when storage is unavailable.
  }
  applyTheme(theme);
}

export function subscribeToSystemTheme(listener: (theme: ColorTheme) => void): () => void {
  const media = window.matchMedia(DARK_THEME_QUERY);
  const handleChange = () => {
    if (storedTheme() !== null) return;
    const theme = media.matches ? "dark" : "light";
    applyTheme(theme);
    listener(theme);
  };

  media.addEventListener("change", handleChange);
  return () => media.removeEventListener("change", handleChange);
}
