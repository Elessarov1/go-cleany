import { type FormEvent, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useTransferApi } from "../../api/TransferApiProvider";
import { BrandName } from "../../components/BrandName/BrandName";
import { ErrorState, LoadingState } from "../../components/PageState/PageState";
import type {
  AdminTransferAirport,
  AdminTransferDriver,
  AdminTransferPrice,
  AdminTransferVehicleType,
  TransferDirection,
  UpsertTransferDriverRequest,
} from "../../domain/transfer";

interface ConfigurationState {
  airports: AdminTransferAirport[];
  vehicles: AdminTransferVehicleType[];
  prices: AdminTransferPrice[];
  drivers: AdminTransferDriver[];
}

export function AdminTransferConfigurationPage() {
  const { t } = useTranslation();
  const api = useTransferApi();
  const [data, setData] = useState<ConfigurationState | null>(null);
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    let active = true;
    Promise.all([
      api.getAdminAirports(), api.getAdminVehicles(), api.getAdminPrices(), api.getAdminDrivers(),
    ])
      .then(([airports, vehicles, prices, drivers]) => {
        if (active) setData({ airports, vehicles, prices, drivers });
      })
      .catch(() => {
        if (active) setFailed(true);
      });
    return () => {
      active = false;
    };
  }, [api]);

  if (!data && failed) return <ErrorState message={t("adminTransfer.configuration.loadError")} />;
  if (!data) return <LoadingState />;

  return (
    <div className="page page--admin-transfer-config">
      <header className="page-header admin-rental-header">
        <div><span className="eyebrow"><BrandName service="transfer" /> / admin</span><h1>{t("adminTransfer.configuration.title")}</h1><p>{t("adminTransfer.configuration.subtitle")}</p></div>
        <Link className="button button--secondary" to="/admin/transfer/bookings">{t("adminTransfer.bookings.open")}</Link>
      </header>
      {failed ? <p className="form-error">{t("adminTransfer.configuration.saveError")}</p> : null}

      <ConfigSection title={t("adminTransfer.airports.title")} text={t("adminTransfer.airports.text")}>
        <div className="admin-transfer-editor-list">
          {data.airports.map((airport) => (
            <AirportEditor
              key={airport.id}
              airport={airport}
              onSave={async (request) => {
                try {
                  const updated = await api.updateAdminAirport(airport.id, request);
                  setData((current) => current && ({ ...current, airports: current.airports.map((item) => item.id === updated.id ? updated : item) }));
                } catch { setFailed(true); }
              }}
            />
          ))}
          <NewAirport onCreate={async (request) => {
            try {
              const created = await api.createAdminAirport(request);
              setData((current) => current && ({ ...current, airports: [...current.airports, created] }));
            } catch { setFailed(true); }
          }} />
        </div>
      </ConfigSection>

      <ConfigSection title={t("adminTransfer.vehicles.title")} text={t("adminTransfer.vehicles.text")}>
        <div className="admin-transfer-editor-list">
          {data.vehicles.map((vehicle) => (
            <VehicleEditor
              key={vehicle.id}
              vehicle={vehicle}
              onSave={async (request) => {
                try {
                  const updated = await api.updateAdminVehicle(vehicle.id, request);
                  setData((current) => current && ({ ...current, vehicles: current.vehicles.map((item) => item.id === updated.id ? updated : item) }));
                } catch { setFailed(true); }
              }}
            />
          ))}
          <NewVehicle onCreate={async (request) => {
            try {
              const created = await api.createAdminVehicle(request);
              setData((current) => current && ({ ...current, vehicles: [...current.vehicles, created] }));
            } catch { setFailed(true); }
          }} />
        </div>
      </ConfigSection>

      <ConfigSection title={t("adminTransfer.prices.title")} text={t("adminTransfer.prices.text")}>
        <div className="admin-transfer-price-grid">
          {data.airports.map((airport) => data.vehicles.map((vehicle) => (
            (["TO_AIRPORT", "FROM_AIRPORT"] as TransferDirection[]).map((direction) => (
              <PriceEditor
                key={`${airport.id}-${vehicle.id}-${direction}`}
                airport={airport}
                vehicle={vehicle}
                direction={direction}
                price={data.prices.find((item) => item.airportId === airport.id && item.vehicleTypeId === vehicle.id && item.direction === direction)}
                onSave={async (request) => {
                  try {
                    const updated = await api.upsertAdminPrice(request);
                    setData((current) => current && ({
                      ...current,
                      prices: current.prices.some((item) => item.id === updated.id)
                        ? current.prices.map((item) => item.id === updated.id ? updated : item)
                        : [...current.prices, updated],
                    }));
                  } catch { setFailed(true); }
                }}
              />
            ))
          )))}
        </div>
      </ConfigSection>

      <ConfigSection title={t("adminTransfer.drivers.title")} text={t("adminTransfer.drivers.text")}>
        <div className="admin-transfer-editor-list">
          {data.drivers.map((driver) => (
            <DriverEditor
              key={driver.id}
              driver={driver}
              onCreateLink={() => api.createAdminDriverTelegramLink(driver.id)}
              onSave={async (request) => {
                try {
                  const updated = await api.updateAdminDriver(driver.id, request);
                  setData((current) => current && ({ ...current, drivers: current.drivers.map((item) => item.id === updated.id ? updated : item) }));
                } catch { setFailed(true); }
              }}
            />
          ))}
          <NewDriver onCreate={async (request) => {
            try {
              const created = await api.createAdminDriver(request);
              setData((current) => current && ({ ...current, drivers: [...current.drivers, created] }));
            } catch { setFailed(true); }
          }} />
        </div>
      </ConfigSection>
    </div>
  );
}

function ConfigSection({ title, text, children }: React.PropsWithChildren<{ title: string; text: string }>) {
  return <section className="admin-transfer-section"><header><h2>{title}</h2><p>{text}</p></header>{children}</section>;
}

function AirportEditor({ airport, onSave }: { airport: AdminTransferAirport; onSave(request: Omit<AdminTransferAirport, "id" | "code" | "createdAt" | "updatedAt" | "version">): Promise<void> }) {
  const { t } = useTranslation();
  const [nameRu, setNameRu] = useState(airport.nameRu);
  const [nameEn, setNameEn] = useState(airport.nameEn);
  const [enabled, setEnabled] = useState(airport.enabled);
  const [sortOrder, setSortOrder] = useState(airport.sortOrder);

  return (
    <form className="admin-transfer-config-card" onSubmit={(event) => { event.preventDefault(); void onSave({ nameRu, nameEn, enabled, sortOrder }); }}>
      <header className="admin-transfer-config-card__header">
        <div><small>{t("adminTransfer.airports.code")}</small><strong>{airport.code}</strong></div>
        <Toggle enabled={enabled} setEnabled={setEnabled} />
      </header>
      <div className="admin-transfer-config-fields">
        <ConfigField label={t("adminTransfer.nameRu")}><input value={nameRu} onChange={(event) => setNameRu(event.target.value)} required /></ConfigField>
        <ConfigField label={t("adminTransfer.nameEn")}><input value={nameEn} onChange={(event) => setNameEn(event.target.value)} required /></ConfigField>
      </div>
      <AdvancedSettings>
        <ConfigField label={t("adminTransfer.sortOrder")} hint={t("adminTransfer.sortOrderHint")}>
          <input type="number" min={0} step={10} value={sortOrder} onChange={(event) => setSortOrder(Number(event.target.value))} />
        </ConfigField>
      </AdvancedSettings>
      <button className="button button--secondary" type="submit">{t("adminTransfer.save")}</button>
    </form>
  );
}

function NewAirport({ onCreate }: { onCreate(request: { code: string; nameRu: string; nameEn: string; enabled: boolean; sortOrder: number }): Promise<void> }) {
  const { t } = useTranslation();
  const [code, setCode] = useState("");
  const [nameRu, setNameRu] = useState("");
  const [nameEn, setNameEn] = useState("");

  return (
    <form className="admin-transfer-config-card is-new" onSubmit={(event) => { event.preventDefault(); void onCreate({ code, nameRu, nameEn, enabled: true, sortOrder: 100 }).then(() => { setCode(""); setNameRu(""); setNameEn(""); }); }}>
      <header className="admin-transfer-config-card__header"><strong>{t("adminTransfer.airports.new")}</strong></header>
      <div className="admin-transfer-config-fields">
        <ConfigField label={t("adminTransfer.airports.code")} hint={t("adminTransfer.airports.codeHint")}><input placeholder="NQZ" value={code} maxLength={8} onChange={(event) => setCode(event.target.value.toUpperCase())} required /></ConfigField>
        <ConfigField label={t("adminTransfer.nameRu")}><input value={nameRu} onChange={(event) => setNameRu(event.target.value)} required /></ConfigField>
        <ConfigField label={t("adminTransfer.nameEn")}><input value={nameEn} onChange={(event) => setNameEn(event.target.value)} required /></ConfigField>
      </div>
      <button className="button button--primary" type="submit">{t("adminTransfer.airports.add")}</button>
    </form>
  );
}

function VehicleEditor({ vehicle, onSave }: { vehicle: AdminTransferVehicleType; onSave(request: Omit<AdminTransferVehicleType, "id" | "code" | "createdAt" | "updatedAt" | "version">): Promise<void> }) {
  const { t } = useTranslation();
  const [nameRu, setNameRu] = useState(vehicle.nameRu);
  const [nameEn, setNameEn] = useState(vehicle.nameEn);
  const [maxPassengers, setMaxPassengers] = useState(vehicle.maxPassengers);
  const [maxLuggage, setMaxLuggage] = useState(vehicle.maxLuggage);
  const [enabled, setEnabled] = useState(vehicle.enabled);
  const [sortOrder, setSortOrder] = useState(vehicle.sortOrder);

  return (
    <form className="admin-transfer-config-card" onSubmit={(event) => { event.preventDefault(); void onSave({ nameRu, nameEn, maxPassengers, maxLuggage, enabled, sortOrder }); }}>
      <header className="admin-transfer-config-card__header">
        <div><small>{t("adminTransfer.vehicles.code")}</small><strong>{vehicle.code}</strong></div>
        <Toggle enabled={enabled} setEnabled={setEnabled} />
      </header>
      <div className="admin-transfer-config-fields">
        <ConfigField label={t("adminTransfer.nameRu")}><input value={nameRu} onChange={(event) => setNameRu(event.target.value)} required /></ConfigField>
        <ConfigField label={t("adminTransfer.nameEn")}><input value={nameEn} onChange={(event) => setNameEn(event.target.value)} required /></ConfigField>
        <ConfigField label={t("adminTransfer.vehicles.maxPassengers")} hint={t("adminTransfer.vehicles.maxPassengersHint")}><input type="number" min={1} value={maxPassengers} onChange={(event) => setMaxPassengers(Number(event.target.value))} required /></ConfigField>
        <ConfigField label={t("adminTransfer.vehicles.maxLuggage")} hint={t("adminTransfer.vehicles.maxLuggageHint")}><input type="number" min={0} value={maxLuggage} onChange={(event) => setMaxLuggage(Number(event.target.value))} required /></ConfigField>
      </div>
      <AdvancedSettings>
        <ConfigField label={t("adminTransfer.sortOrder")} hint={t("adminTransfer.sortOrderHint")}><input type="number" min={0} step={10} value={sortOrder} onChange={(event) => setSortOrder(Number(event.target.value))} /></ConfigField>
      </AdvancedSettings>
      <button className="button button--secondary" type="submit">{t("adminTransfer.save")}</button>
    </form>
  );
}

function NewVehicle({ onCreate }: { onCreate(request: { code: string; nameRu: string; nameEn: string; maxPassengers: number; maxLuggage: number; enabled: boolean; sortOrder: number }): Promise<void> }) {
  const { t } = useTranslation();
  const [code, setCode] = useState("");
  const [nameRu, setNameRu] = useState("");
  const [nameEn, setNameEn] = useState("");
  const [maxPassengers, setMaxPassengers] = useState(4);
  const [maxLuggage, setMaxLuggage] = useState(4);

  return (
    <form className="admin-transfer-config-card is-new" onSubmit={(event) => { event.preventDefault(); void onCreate({ code, nameRu, nameEn, maxPassengers, maxLuggage, enabled: true, sortOrder: 100 }).then(() => { setCode(""); setNameRu(""); setNameEn(""); }); }}>
      <header className="admin-transfer-config-card__header"><strong>{t("adminTransfer.vehicles.new")}</strong></header>
      <div className="admin-transfer-config-fields">
        <ConfigField label={t("adminTransfer.vehicles.code")} hint={t("adminTransfer.vehicles.codeHint")}><input placeholder="SUV" value={code} maxLength={16} onChange={(event) => setCode(event.target.value.toUpperCase())} required /></ConfigField>
        <ConfigField label={t("adminTransfer.nameRu")}><input value={nameRu} onChange={(event) => setNameRu(event.target.value)} required /></ConfigField>
        <ConfigField label={t("adminTransfer.nameEn")}><input value={nameEn} onChange={(event) => setNameEn(event.target.value)} required /></ConfigField>
        <ConfigField label={t("adminTransfer.vehicles.maxPassengers")}><input type="number" min={1} value={maxPassengers} onChange={(event) => setMaxPassengers(Number(event.target.value))} required /></ConfigField>
        <ConfigField label={t("adminTransfer.vehicles.maxLuggage")}><input type="number" min={0} value={maxLuggage} onChange={(event) => setMaxLuggage(Number(event.target.value))} required /></ConfigField>
      </div>
      <button className="button button--primary" type="submit">{t("adminTransfer.vehicles.add")}</button>
    </form>
  );
}

function PriceEditor({ airport, vehicle, direction, price, onSave }: { airport: AdminTransferAirport; vehicle: AdminTransferVehicleType; direction: TransferDirection; price?: AdminTransferPrice; onSave(request: { airportId: number; vehicleTypeId: number; direction: TransferDirection; amount: number; currency: string; enabled: boolean }): Promise<void> }) {
  const { t } = useTranslation();
  const [amount, setAmount] = useState(price?.amount ?? 0);
  const [currency, setCurrency] = useState(price?.currency ?? "TRY");
  const [enabled, setEnabled] = useState(price?.enabled ?? false);

  return (
    <form className="admin-transfer-price" onSubmit={(event) => { event.preventDefault(); void onSave({ airportId: airport.id, vehicleTypeId: vehicle.id, direction, amount, currency, enabled }); }}>
      <header className="admin-transfer-config-card__header">
        <div><small>{t(`transfer.direction.${direction}`)}</small><strong>{airport.nameRu} · {vehicle.nameRu}</strong></div>
        <Toggle enabled={enabled} setEnabled={setEnabled} />
      </header>
      <div className="admin-transfer-config-fields">
        <ConfigField label={t("adminTransfer.prices.amount")} hint={t("adminTransfer.prices.amountHint")}><input type="number" min="0.01" step="0.01" value={amount || ""} onChange={(event) => setAmount(Number(event.target.value))} required /></ConfigField>
        <ConfigField label={t("adminTransfer.prices.currency")}>
          <select value={currency} onChange={(event) => setCurrency(event.target.value)}>
            <option value="TRY">TRY · ₺</option><option value="EUR">EUR · €</option><option value="USD">USD · $</option>
          </select>
        </ConfigField>
      </div>
      <button className="button button--secondary" type="submit">{t("adminTransfer.save")}</button>
    </form>
  );
}

function DriverEditor({ driver, onSave, onCreateLink }: { driver: AdminTransferDriver; onSave(request: UpsertTransferDriverRequest): Promise<void>; onCreateLink(): Promise<{ url: string; expiresAt: string }> }) {
  const { t } = useTranslation();
  const [name, setName] = useState(driver.name);
  const [phone, setPhone] = useState(driver.phone);
  const [telegramId, setTelegramId] = useState(driver.configuredTelegramUserId?.toString() ?? "");
  const [enabled, setEnabled] = useState(driver.enabled);
  const [link, setLink] = useState<string | null>(null);
  const [linkFailed, setLinkFailed] = useState(false);

  return (
    <form className="admin-transfer-config-card admin-transfer-driver" onSubmit={(event) => { event.preventDefault(); void onSave({ name, phone, enabled, telegramUserId: telegramId ? Number(telegramId) : null }); }}>
      <header className="admin-transfer-config-card__header">
        <div><strong>{driver.name}</strong><small>{t(`adminTransfer.drivers.telegram.${driver.telegramStatus}`)}</small></div>
        <Toggle enabled={enabled} setEnabled={setEnabled} />
      </header>
      <div className="admin-transfer-config-fields">
        <ConfigField label={t("adminTransfer.drivers.name")}><input value={name} onChange={(event) => setName(event.target.value)} required /></ConfigField>
        <ConfigField label={t("adminTransfer.drivers.phone")}><input type="tel" value={phone} onChange={(event) => setPhone(event.target.value)} required /></ConfigField>
        <ConfigField wide label={t("adminTransfer.drivers.telegramId")} hint={t("adminTransfer.drivers.telegramHint")}><input inputMode="numeric" pattern="[0-9]*" value={telegramId} onChange={(event) => setTelegramId(event.target.value)} /></ConfigField>
      </div>
      <div className="admin-transfer-config-actions">
        <button className="button button--secondary" type="submit">{t("adminTransfer.save")}</button>
        {driver.configuredTelegramUserId ? <button className="button button--ghost" type="button" onClick={() => { setLinkFailed(false); void onCreateLink().then((value) => { setLink(value.url); void navigator.clipboard?.writeText(value.url).catch(() => undefined); }).catch(() => setLinkFailed(true)); }}>{t("adminTransfer.drivers.createLink")}</button> : <small>{t("adminTransfer.drivers.linkHint")}</small>}
      </div>
      {link ? <a className="admin-transfer-driver-link" href={link} target="_blank" rel="noreferrer">{t("adminTransfer.drivers.linkReady")}</a> : null}
      {linkFailed ? <small className="form-error admin-transfer-driver-link">{t("adminTransfer.drivers.linkError")}</small> : null}
    </form>
  );
}

function NewDriver({ onCreate }: { onCreate(request: UpsertTransferDriverRequest): Promise<void> }) {
  const { t } = useTranslation();
  const [name, setName] = useState("");
  const [phone, setPhone] = useState("");
  const [telegramId, setTelegramId] = useState("");

  return (
    <form className="admin-transfer-config-card admin-transfer-driver is-new" onSubmit={(event: FormEvent) => { event.preventDefault(); void onCreate({ name, phone, enabled: true, telegramUserId: telegramId ? Number(telegramId) : null }).then(() => { setName(""); setPhone(""); setTelegramId(""); }); }}>
      <header className="admin-transfer-config-card__header"><strong>{t("adminTransfer.drivers.new")}</strong></header>
      <div className="admin-transfer-config-fields">
        <ConfigField label={t("adminTransfer.drivers.name")}><input value={name} onChange={(event) => setName(event.target.value)} required /></ConfigField>
        <ConfigField label={t("adminTransfer.drivers.phone")}><input type="tel" value={phone} onChange={(event) => setPhone(event.target.value)} required /></ConfigField>
        <ConfigField wide label={t("adminTransfer.drivers.telegramId")} hint={t("adminTransfer.drivers.telegramHint")}><input inputMode="numeric" pattern="[0-9]*" value={telegramId} onChange={(event) => setTelegramId(event.target.value)} /></ConfigField>
      </div>
      <button className="button button--primary" type="submit">{t("adminTransfer.drivers.add")}</button>
    </form>
  );
}

function Toggle({ enabled, setEnabled }: { enabled: boolean; setEnabled(value: boolean): void }) {
  const { t } = useTranslation();
  return <label className="admin-transfer-toggle"><input type="checkbox" checked={enabled} onChange={(event) => setEnabled(event.target.checked)} /><span>{t(enabled ? "adminTransfer.enabled" : "adminTransfer.disabled")}</span></label>;
}

function ConfigField({ label, hint, wide = false, children }: React.PropsWithChildren<{ label: string; hint?: string; wide?: boolean }>) {
  return <label className={`admin-transfer-field${wide ? " is-wide" : ""}`}><span>{label}{hint ? <small>{hint}</small> : null}</span>{children}</label>;
}

function AdvancedSettings({ children }: React.PropsWithChildren) {
  const { t } = useTranslation();
  return <details className="admin-transfer-advanced"><summary>{t("adminTransfer.advanced")}</summary>{children}</details>;
}
