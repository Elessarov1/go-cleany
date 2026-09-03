import http from 'k6/http';
import { sleep } from 'k6';

import { expectOk, loadManifest, localBaseUrl, shortOr } from './lib/common.js';

const BASE_URL = localBaseUrl('BASE_URL', 'http://frontend');
const manifest = loadManifest();

export const options = shortOr({
  vus: 12,
  duration: '60s',
});

export default function () {
  const property = manifest.rentalProperties[__ITER % manifest.rentalProperties.length];
  const requests = [
    ['GET', `${BASE_URL}/api/v1/catalog/services`, null, { tags: { endpoint: 'service-catalog' } }],
    ['GET', `${BASE_URL}/api/v1/account/home`, null, { tags: { endpoint: 'account-home' } }],
    ['GET', `${BASE_URL}/api/v1/account/activity`, null, { tags: { endpoint: 'account-activity' } }],
    ['GET', `${BASE_URL}/api/v1/account/notifications`, null, { tags: { endpoint: 'notifications' } }],
    ['GET', `${BASE_URL}/api/v1/rental/properties/${property.slug}`, null, {
      tags: { endpoint: 'rental-detail' },
    }],
  ];
  http.batch(requests).forEach((response, index) => expectOk(response, `mixed request ${index + 1}`));
  sleep(0.25);
}
