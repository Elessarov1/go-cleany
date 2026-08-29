export type IconName =
  | "admin"
  | "arrow-left"
  | "arrow-right"
  | "bed"
  | "bell"
  | "building"
  | "calendar-plus"
  | "camera"
  | "chart"
  | "check"
  | "clipboard"
  | "close"
  | "home"
  | "info"
  | "location"
  | "logout"
  | "moon"
  | "services"
  | "sparkles"
  | "user"
  | "wallet";

interface IconProps {
  name: IconName;
  size?: number;
  strokeWidth?: number;
}

export function Icon({ name, size = 22, strokeWidth = 1.8 }: IconProps) {
  const common = {
    fill: "none",
    stroke: "currentColor",
    strokeLinecap: "round" as const,
    strokeLinejoin: "round" as const,
    strokeWidth,
  };

  return (
    <svg
      aria-hidden="true"
      className="icon"
      width={size}
      height={size}
      viewBox="0 0 24 24"
      {...common}
    >
      {name === "calendar-plus" ? (
        <>
          <path d="M7 3v3M17 3v3M4 9h16M5 5h14a1 1 0 0 1 1 1v13a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1Z" />
          <path d="M12 12v5M9.5 14.5h5" />
        </>
      ) : null}
      {name === "clipboard" ? (
        <>
          <path d="M9 5H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-3" />
          <rect x="9" y="3" width="6" height="4" rx="1.5" />
          <path d="M8 12h8M8 16h6" />
        </>
      ) : null}
      {name === "admin" ? (
        <>
          <path d="M4 20V10M10 20V4M16 20v-7M22 20H2" />
          <path d="m16 7 2-2 2 2M18 5v6" />
        </>
      ) : null}
      {name === "location" ? (
        <>
          <path d="M20 10c0 5-8 11-8 11S4 15 4 10a8 8 0 1 1 16 0Z" />
          <circle cx="12" cy="10" r="2.5" />
        </>
      ) : null}
      {name === "logout" ? (
        <>
          <path d="M10 4H5a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h5" />
          <path d="m15 16 4-4-4-4M19 12H9" />
        </>
      ) : null}
      {name === "home" ? (
        <>
          <path d="m3 11 9-8 9 8" />
          <path d="M5 10v10h14V10M9 20v-6h6v6" />
        </>
      ) : null}
      {name === "services" ? (
        <>
          <rect x="3" y="3" width="7" height="7" rx="1.5" />
          <rect x="14" y="3" width="7" height="7" rx="1.5" />
          <rect x="3" y="14" width="7" height="7" rx="1.5" />
          <rect x="14" y="14" width="7" height="7" rx="1.5" />
        </>
      ) : null}
      {name === "building" ? (
        <>
          <path d="M4 21V5a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v16M17 9h3v12M2 21h20" />
          <path d="M8 7h2M13 7h1M8 11h2M13 11h1M8 15h2M13 15h1M8 21v-3h5v3" />
        </>
      ) : null}
      {name === "bed" ? (
        <>
          <path d="M3 19v-8M21 19v-6a2 2 0 0 0-2-2H8a2 2 0 0 0-2 2v3M3 16h18" />
          <path d="M6 11V8a2 2 0 0 1 2-2h3a2 2 0 0 1 2 2v3" />
        </>
      ) : null}
      {name === "bell" ? (
        <>
          <path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9Z" />
          <path d="M10 21h4" />
        </>
      ) : null}
      {name === "moon" ? <path d="M20 15.5A8.5 8.5 0 0 1 8.5 4 8.5 8.5 0 1 0 20 15.5Z" /> : null}
      {name === "wallet" ? (
        <>
          <path d="M4 6h14a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h12" />
          <path d="M16 11h6v5h-6a2.5 2.5 0 0 1 0-5Z" />
          <circle cx="17" cy="13.5" r=".5" fill="currentColor" stroke="none" />
        </>
      ) : null}
      {name === "camera" ? (
        <>
          <path d="M4 7h3l1.5-2h7L17 7h3a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2Z" />
          <circle cx="12" cy="13" r="4" />
        </>
      ) : null}
      {name === "chart" ? (
        <>
          <path d="M4 20V10M10 20V4M16 20v-7M22 20H2" />
          <path d="m4 7 6-4 6 6 5-5" />
        </>
      ) : null}
      {name === "user" ? (
        <>
          <circle cx="12" cy="8" r="4" />
          <path d="M4 21a8 8 0 0 1 16 0" />
        </>
      ) : null}
      {name === "sparkles" ? (
        <>
          <path d="M12 2c.5 4.2 2.8 6.5 7 7-4.2.5-6.5 2.8-7 7-.5-4.2-2.8-6.5-7-7 4.2-.5 6.5-2.8 7-7Z" />
          <path d="M19 15c.2 1.8 1.2 2.8 3 3-1.8.2-2.8 1.2-3 3-.2-1.8-1.2-2.8-3-3 1.8-.2 2.8-1.2 3-3Z" />
        </>
      ) : null}
      {name === "check" ? <path d="m5 12 4 4L19 6" /> : null}
      {name === "close" ? <path d="m6 6 12 12M18 6 6 18" /> : null}
      {name === "arrow-left" ? <path d="m15 18-6-6 6-6" /> : null}
      {name === "arrow-right" ? <path d="m9 18 6-6-6-6" /> : null}
      {name === "info" ? (
        <>
          <circle cx="12" cy="12" r="9" />
          <path d="M12 11v5M12 8h.01" />
        </>
      ) : null}
    </svg>
  );
}
