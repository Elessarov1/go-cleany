import http from 'k6/http';
import { check, sleep } from 'k6';

import { addDays, loadManifest, localBaseUrl, shortOr } from './lib/common.js';

const BASE_URL = localBaseUrl('BASE_URL', 'http://frontend');
const manifest = loadManifest();

export const options = shortOr({
  vus: 8,
  duration: '45s',
});

export function setup() {
  const checkIn = addDays(manifest.anchorDate, 14);
  const query = `termType=DATE_RANGE&checkInDate=${checkIn}&checkOutDate=${addDays(checkIn, 7)}&guests=2&size=20`;
  const response = http.get(`${BASE_URL}/api/v1/rental/search?${query}`, {
    tags: { endpoint: 'rental-search-cursor-setup' },
  });
  const cursor = response.json('nextCursor');
  const searchExecutionId = response.json('searchExecutionId');
  if (response.status !== 200 || !cursor || !searchExecutionId) {
    throw new Error('Cursor scenario requires a non-empty second Rental search page');
  }
  return { query, cursor, searchExecutionId };
}

export default function (data) {
  const response = http.get(`${BASE_URL}/api/v1/rental/search?${data.query}&cursor=${encodeURIComponent(data.cursor)}`, {
    tags: { endpoint: 'rental-search-cursor' },
  });
  check(response, {
    'rental cursor: status is 200': (result) => result.status === 200,
    'rental cursor: no-store': (result) => (result.headers['Cache-Control'] || '').includes('no-store'),
    'rental cursor: execution preserved': (result) => result.json('searchExecutionId') === data.searchExecutionId,
    'rental cursor: page is bounded': (result) => result.json('properties').length <= 20,
  });
  sleep(0.3);
}
