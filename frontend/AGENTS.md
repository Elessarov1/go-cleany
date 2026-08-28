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

The current UI contains a shared Loco Place service catalog and two real verticals:

```text
Loco Cleaning
Loco Rent
```

Further platform direction may include:

```text
service catalog
├── cleaning
├── rent
├── handyman
├── residence / relocation
└── future verticals
```

Keep the service catalog at `/` while both real verticals are available. Do not add placeholder cards for unimplemented services.

---

## Channel abstraction

Preserve the existing platform abstraction.

Channel-specific browser/Telegram behavior should not leak unnecessarily into ordinary UI/domain code.

Current customer entry points are:

```text
standalone WEB
Telegram Mini App
```

Future mobile or other channels may be added only when there is a concrete product requirement.

WhatsApp integration is not planned. Do not add WhatsApp-specific frontend abstractions, screens, copy or provider assumptions.

Do not encode Telegram-specific assumptions into shared TypeScript domain models unless the API genuinely exposes Telegram-specific data.

---

## Authentication and account identity

Standalone WEB uses Google login. Telegram uses verified Mini App authentication.

Google ↔ Telegram account linking is already implemented and is explicit/verified.

Telegram is optional; Google-only customers must retain full WEB functionality.

Shared WEB navigation should expose a clear authentication action/status consistently rather than hiding login behind a service-specific flow.

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

Primary UI is mobile-oriented, while standalone WEB must also remain intentional on desktop.

When changing layout, verify narrow widths, especially:

```text
320px
360px
390px
430px
480px
```

and at least one normal desktop viewport.

Avoid absolute positioning that can overlap translated text unless there is a strong reason.

Prefer robust grid/flex layouts.

---

## Current routes

Current customer routing includes:

```text
/
/cleaning
/cleaning/orders
/cleaning/orders/:id
/rent
/rent/properties/:slug
/rent/bookings
/account
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

As vertical-specific UI grows, feature-oriented organization may evolve incrementally.

Do not reorganize the whole frontend merely to prepare hypothetical future services.

---

## Validation

After frontend changes:

- run the existing frontend build;
- run tests/lint if configured;
- verify both RU and EN for user-facing changes;
- verify narrow mobile layout for responsive changes;
- verify Telegram Mini App behavior is not accidentally broken by browser-only assumptions;
- verify standalone WEB auth/navigation behavior for browser-specific changes.
