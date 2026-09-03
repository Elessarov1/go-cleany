import http from 'k6/http';

import { expectOk, loadManifest, localBaseUrl, shortOr } from './lib/common.js';

const BASE_URL = localBaseUrl('BASE_URL', 'http://frontend');
const manifest = loadManifest();

export const options = shortOr({
  scenarios: {
    image_burst: {
      executor: 'constant-arrival-rate',
      rate: 20,
      timeUnit: '1s',
      duration: '45s',
      preAllocatedVUs: 12,
      maxVUs: 40,
    },
  },
});

export default function () {
  const property = manifest.rentalProperties[__ITER % manifest.rentalProperties.length];
  const imageUrls = property.imageBurstUrls || property.mediaUrls;
  const responses = http.batch(
    imageUrls.map((path) => ({
      method: 'GET',
      url: `${BASE_URL}${path}`,
      params: { tags: { endpoint: 'rental-image' } },
    })),
  );
  responses.forEach((response, index) => expectOk(response, `rental image ${index + 1}`));
}
