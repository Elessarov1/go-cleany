import http from 'k6/http';
import { sleep } from 'k6';

import { expectOk, localBaseUrl, shortOr } from './lib/common.js';

const BASE_URL = localBaseUrl('BASE_URL', 'http://frontend');
const BACKEND_BASE_URL = localBaseUrl('BACKEND_BASE_URL', 'http://backend:8080');

export const options = shortOr({
  vus: 2,
  duration: '20s',
});

export default function () {
  expectOk(http.get(`${BASE_URL}/health`, { tags: { endpoint: 'frontend-health' } }), 'frontend health');
  expectOk(
    http.get(`${BACKEND_BASE_URL}/actuator/health`, { tags: { endpoint: 'backend-health' } }),
    'backend health',
  );
  expectOk(
    http.get(`${BASE_URL}/api/v1/catalog/services`, { tags: { endpoint: 'service-catalog' } }),
    'service catalog',
  );
  expectOk(
    http.get(`${BASE_URL}/api/v1/rental/properties`, { tags: { endpoint: 'rental-catalog' } }),
    'rental catalog',
  );
  sleep(0.2);
}
