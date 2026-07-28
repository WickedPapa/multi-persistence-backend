import http from "k6/http";
import { check, sleep } from "k6";

const baseUrl = __ENV.BASE_URL || "http://localhost:8080";

export const options = {
  vus: Number(__ENV.VUS || 5),
  duration: __ENV.DURATION || "30s",
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<800"],
  },
};

const endpoints = [
  "/actuator/health",
  "/users",
  "/products",
  "/orders",
  "/orders/stats/most-sold-products",
  "/orders/stats/total-spent-per-user",
];

export default function readApiBaseline() {
  for (const endpoint of endpoints) {
    const response = http.get(`${baseUrl}${endpoint}`);
    check(response, {
      "status is 200": (r) => r.status === 200,
    });
  }
  sleep(1);
}
