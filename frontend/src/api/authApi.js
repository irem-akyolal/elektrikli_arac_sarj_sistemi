import api from "./axios";

export const login = (username, password) => {
  return api.post("/auth/login", {
    username,
    password,
  });
};