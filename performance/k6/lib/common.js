import { check } from 'k6';

const ALLOWED_HOSTS = new Set([
  '127.0.0.1',
  'localhost',
  'host.docker.internal',
  'frontend',
  'backend',
]);

export function localBaseUrl(name, fallback) {
  const value = (__ENV[name] || fallback).replace(/\/$/, '');
  const match = /^https?:\/\/([^/:]+)(?::\d+)?(?:\/|$)/i.exec(value);
  if (!match || !ALLOWED_HOSTS.has(match[1].toLowerCase())) {
    throw new Error(`${name} must target localhost or the internal loco-perf Compose network`);
  }
  return value;
}

export function loadManifest() {
  const path = __ENV.PERF_MANIFEST || '/results/manifest.json';
  return JSON.parse(open(path));
}

export function shortOr(defaultOptions) {
  if (__ENV.PERF_VALIDATION === 'true') {
    return {
      vus: 1,
      iterations: 3,
      thresholds: defaultThresholds(),
    };
  }
  return {
    ...defaultOptions,
    thresholds: defaultOptions.thresholds || defaultThresholds(),
  };
}

export function defaultThresholds() {
  return {
    http_req_failed: ['rate<0.01'],
    checks: ['rate>0.99'],
  };
}

export function expectOk(response, label) {
  check(response, {
    [`${label}: status is 200`]: (result) => result.status === 200,
  });
}

export function addDays(isoDate, days) {
  const date = new Date(`${isoDate}T00:00:00Z`);
  date.setUTCDate(date.getUTCDate() + days);
  return date.toISOString().slice(0, 10);
}
