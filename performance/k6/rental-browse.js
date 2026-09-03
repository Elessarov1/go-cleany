import http from 'k6/http';
import { sleep } from 'k6';

import { addDays, expectOk, loadManifest, localBaseUrl, shortOr } from './lib/common.js';

const BASE_URL = localBaseUrl('BASE_URL', 'http://frontend');
const manifest = loadManifest();

export const options = shortOr({
  vus: 8,
  duration: '45s',
});

export default function () {
  const property = manifest.rentalProperties[__ITER % manifest.rentalProperties.length];
  expectOk(
    http.get(`${BASE_URL}/api/v1/rental/properties`, { tags: { endpoint: 'rental-catalog' } }),
    'rental catalog',
  );
  expectOk(
    http.get(`${BASE_URL}/api/v1/rental/properties/${property.slug}`, {
      tags: { endpoint: 'rental-detail' },
    }),
    'rental detail',
  );
  const fromDate = addDays(manifest.anchorDate, 1);
  const toDate = addDays(manifest.anchorDate, 90);
  expectOk(
    http.get(
      `${BASE_URL}/api/v1/rental/properties/${property.id}/availability?fromDate=${fromDate}&toDate=${toDate}`,
      { tags: { endpoint: 'rental-availability' } },
    ),
    'rental availability',
  );
  sleep(0.3);
}
