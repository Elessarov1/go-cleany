const DEFAULT_TELEGRAM_BOT_USERNAME = "go_cleany_bot";

export function telegramBotUsername(): string {
  const configured = import.meta.env.VITE_TELEGRAM_BOT_USERNAME?.trim().replace(/^@/, "");
  return configured && /^[A-Za-z0-9_]{5,32}$/.test(configured)
    ? configured
    : DEFAULT_TELEGRAM_BOT_USERNAME;
}

export function telegramMainMiniAppUrl(): string {
  return `https://t.me/${telegramBotUsername()}?startapp`;
}
