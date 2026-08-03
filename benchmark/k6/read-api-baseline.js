import http from "k6/http";
import { check, fail } from "k6";

const baseUrl = __ENV.BASE_URL || "http://localhost:8080";

const weightedEndpoints = [
  { path: "/users", tag: "users", weight: 14 },
  { path: "/products", tag: "products", weight: 14 },
  { path: "/orders", tag: "orders", weight: 14 },
  {
    path: "/orders/stats/most-sold-products",
    tag: "orders-stats-most-sold-products",
    weight: 29,
  },
  {
    path: "/orders/stats/total-spent-per-user",
    tag: "orders-stats-total-spent-per-user",
    weight: 29,
  },
];

const totalWeight = weightedEndpoints.reduce((sum, endpoint) => sum + endpoint.weight, 0);

function pickWeightedEndpoint() {
  const random = Math.random() * totalWeight;
  let cumulativeWeight = 0;

  for (const endpoint of weightedEndpoints) {
    cumulativeWeight += endpoint.weight;
    if (random < cumulativeWeight) {
      return endpoint;
    }
  }

  return weightedEndpoints[weightedEndpoints.length - 1];
}

export function setup() {
  const health = http.get(`${baseUrl}/actuator/health`);
  if (!check(health, { "health status is 200": (response) => response.status === 200 })) {
    fail("Backend is not healthy before starting the benchmark.");
  }
}

export const options = {
  scenarios: {
    read_stress: {
      executor: "constant-arrival-rate",
      rate: Number(__ENV.RATE || 120),
      timeUnit: "1s",
      duration: __ENV.DURATION || "5m",
      preAllocatedVUs: Number(__ENV.PREALLOCATED_VUS || 80),
      maxVUs: Number(__ENV.MAX_VUS || 300),
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.02"],
    http_req_duration: ["p(95)<1500"],
    "http_req_duration{endpoint:users}": ["p(95)<1200"],
    "http_req_duration{endpoint:products}": ["p(95)<1200"],
    "http_req_duration{endpoint:orders}": ["p(95)<1400"],
    "http_req_duration{endpoint:orders-stats-most-sold-products}": ["p(95)<2200"],
    "http_req_duration{endpoint:orders-stats-total-spent-per-user}": ["p(95)<2200"],
  },
};

export default function readApiBaseline() {
  const endpoint = pickWeightedEndpoint();
  const response = http.get(`${baseUrl}${endpoint.path}`, {
    tags: { endpoint: endpoint.tag },
  });

  check(response, {
    "status is 200": (r) => r.status === 200,
  });
}
