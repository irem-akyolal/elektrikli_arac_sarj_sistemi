import api from "./axios";

const BASE_PATH = "/admin/locations";

export const getAdminLocations = ({
  name,
  city,
  active,
  page = 0,
  size = 20,
  sort = "createdAt,desc",
} = {}) => {
  return api.get(BASE_PATH, {
    params: {
      ...(name && { name }),
      ...(city && { city }),
      ...(active !== undefined && { active }),
      page,
      size,
      sort,
    },
  });
};

export const getAdminLocationById = (id) => {
  return api.get(`${BASE_PATH}/${id}`);
};

export const updateLocation = (id, data) => {
  return api.put(`${BASE_PATH}/${id}`, data);
};

export const activateLocation = (id) => {
  return api.patch(`${BASE_PATH}/${id}/activate`);
};

export const getAdminLocationDetail = (id) => {
  return api.get(`${BASE_PATH}/${id}/detail`);
};

export const deactivateLocation = (id) => {
  return api.patch(`${BASE_PATH}/${id}/deactivate`);
};
