import http from 'k6/http';
import { check, sleep } from 'k6';

import { addDays, loadManifest, localBaseUrl, shortOr } from './lib/common.js';

const BASE_URL = localBaseUrl('BASE_URL', 'http://frontend');
const manifest = loadManifest();

export const options = shortOr({
  vus: 8,
  duration: '45s',
});

export default function () {
  const checkIn = addDays(manifest.anchorDate, 14 + (__ITER % 20));
  const monthly = __ITER % 3 === 0;
  const query = monthly
    ? `termType=MONTHLY&checkInDate=${checkIn}&months=1&guests=2`
    : `termType=DATE_RANGE&checkInDate=${checkIn}&checkOutDate=${addDays(checkIn, 7)}&guests=2`;
  const response = http.get(`${BASE_URL}/api/v1/rental/search?${query}`, {
    tags: { endpoint: 'rental-search', mode: monthly ? 'MONTHLY' : 'DATE_RANGE' },
  });
  check(response, {
    'rental search: status is 200': (result) => result.status === 200,
    'rental search: no-store': (result) => (result.headers['Cache-Control'] || '').includes('no-store'),
    'rental search: execution linked': (result) => {
      try {
        return Boolean(result.json('searchExecutionId'));
      } catch (_) {
        return false;
      }
    },
  });
  sleep(0.3);
}
