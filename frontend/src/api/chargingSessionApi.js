import api from "./axios";

export const startChargingSession = (payload) => {
  return api.post("/charging-sessions/start", payload);
};

export const markSessionAsCharging = (sessionId) => {
  return api.patch(`/charging-sessions/${sessionId}/charging`);
};