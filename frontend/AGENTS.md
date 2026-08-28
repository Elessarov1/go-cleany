# Frontend Codex Instructions

These instructions apply to the `frontend/` tree and extend the repository root `AGENTS.md`.

---

## Stack

```text
React 19
TypeScript
Vite
React Router
RU / EN i18n
```

---

## Product direction

The current UI contains a shared service catalog and two real verticals:

```text
Loco Cleaning
Loco Rent
```

Further platform direction:

```text
service catalog
├── cleaning
├── handyman
├── residence / relocation
└── future verticals
```

Keep the service catalog at `/` while both real verticals are available. Do not add placeholder cards for unimplemented services.

---

## Channel abstraction

Preserve the existing platform abstraction.

Channel-specific browser/Telegram behavior should not leak unnecessarily into ordinary UI/domain code.

Future channels may include:

```text
Telegram
WhatsApp-related web experiences
standalone Flutter app
```

Do not encode Telegram-specific assumptions into shared TypeScript domain models unless the API genuinely exposes Telegram-specific data.

---

## Business rules

Backend remains the source of truth for:

```text
prices
referral applicability
order state
financial calculations
booking availability
business validation
```

Frontend may provide early UX validation, but must not replace backend validation.

---

## Localization

All user-visible product text must support RU/EN through the existing i18n system.

Do not hardcode user-visible Russian or English strings directly in components unless the current code explicitly treats that content as non-localized technical/admin data.

When adding new statuses/reasons/messages, add both RU and EN translations.

---

## Responsive UI

Primary UI is mobile-oriented.

When changing layout, verify narrow widths, especially:

```text
320px
360px
390px
430px
480px
```

Avoid absolute positioning that can overlap translated text unless there is a strong reason.

Prefer robust grid/flex layouts.

---

## Cleaning routes and future platform routes

Current vertical routing is:

```text
/
/cleaning
/cleaning/orders
/cleaning/orders/:id
/rent
/rent/properties/:slug
/rent/bookings
```

The shared admin shell separates vertical routes:

```text
/admin/cleaning
/admin/rent
```

Legacy cleaning redirects may remain while controlled clients migrate. Do not implement fake Handyman/Residence pages before those verticals exist.

Route migration should preserve current user experience.

---

## API usage

Do not calculate canonical business prices locally when backend quote/configuration APIs are available.

Do not embed binary images as Base64 in ordinary DTOs.

Use authenticated binary/media endpoints or Blob/object URLs where appropriate.

---

## Refactoring

Avoid broad folder reorganizations unless required by the current architectural task.

When a second vertical appears, feature-oriented organization may evolve toward:

```text
features/
├── cleaning/
├── handyman/
└── residence/
```

Until then, prefer incremental moves.

---

## Validation

After frontend changes:

- run the existing frontend build;
- run tests/lint if configured;
- verify both RU and EN for user-facing changes;
- verify narrow mobile layout for responsive changes;
- verify Telegram Mini App behavior is not accidentally broken by browser-only assumptions.
