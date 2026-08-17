import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/public/locations";

/**
 * Tüm aktif şarj istasyonlarını getirir.
 */
export const getActiveLocations = () => {
  return axios.get(`${API_BASE_URL}/active`);
};

/**
 * ID'ye göre istasyon detayını getirir.
 */
export const getLocationDetail = (id) => {
  return axios.get(`${API_BASE_URL}/${id}`);
};

/**
 * Belirli koordinatlara göre yakındaki istasyonları getirir.
 *
 * @param {number} latitude
 * @param {number} longitude
 * @param {number} radiusKm
 */
export const getNearbyLocations = (
  latitude,
  longitude,
  radiusKm = 10
) => {
  return axios.get(`${API_BASE_URL}/nearby`, {
    params: {
      latitude,
      longitude,
      radiusKm,
    },
  });
};

/**
 * İstasyonlarda filtreli arama yapar.
 *
 * @param {object} params
 * @param {string} params.name
 * @param {string} params.city
 * @param {string} params.connectorType
 * @param {boolean} params.onlyAvailable
 * @param {number} params.page
 * @param {number} params.size
 * @param {string} params.sort
 */
export const searchLocations = ({
  name,
  city,
  connectorType,
  onlyAvailable,
  page = 0,
  size = 20,
  sort = "name,asc",
} = {}) => {
  return axios.get(`${API_BASE_URL}/search`, {
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