import http from 'k6/http';

import { expectOk, loadManifest, localBaseUrl } from './lib/common.js';

const API_BASE_URL = localBaseUrl('API_BASE_URL', 'http://frontend');
const manifest = loadManifest();

export const options = __ENV.PERF_VALIDATION === 'true'
  ? { vus: 1, iterations: 3 }
  : {
      stages: [
        { duration: '30s', target: 10 },
        { duration: '45s', target: 30 },
        { duration: '45s', target: 60 },
        { duration: '45s', target: 100 },
        { duration: '30s', target: 0 },
      ],
      thresholds: {
        http_req_failed: ['rate<0.10'],
      },
      summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
    };

export default function () {
  const property = manifest.rentalProperties[__ITER % manifest.rentalProperties.length];
  const response = http.get(`${API_BASE_URL}/api/v1/rental/properties/${property.slug}`, {
    tags: { endpoint: 'rental-detail-stress' },
  });
  expectOk(response, 'stress rental detail');
}
