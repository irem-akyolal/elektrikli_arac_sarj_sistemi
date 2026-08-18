import api from "./axios";  // "axios" paketi değil, kendi instance'ımız

const BASE_PATH = "/public/locations";

export const getActiveLocations = () => {
  return api.get(`${BASE_PATH}/active`);
};

export const getLocationDetail = (id) => {
  return api.get(`${BASE_PATH}/${id}`);
};

export const getNearbyLocations = (latitude, longitude, radiusKm = 10) => {
  return api.get(`${BASE_PATH}/nearby`, {
    params: { latitude, longitude, radiusKm },
  });
};

export const searchLocations = ({
  name,
  city,
  connectorType,
  onlyAvailable,
  page = 0,
  size = 20,
  sort = "name,asc",
} = {}) => {
  return api.get(`${BASE_PATH}/search`, {
    params: {
      ...(name && { name }),
      ...(city && { city }),
      ...(connectorType && { connectorType }),
      ...(onlyAvailable !== undefined && { onlyAvailable }),
      page,
      size,
      sort,
    },
  });
};