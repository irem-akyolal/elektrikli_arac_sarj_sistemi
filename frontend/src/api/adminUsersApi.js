import api from "./axios";

const BASE_PATH = "/admin-users";

export const getAdminUsers = () => {
    return api.get(BASE_PATH);
};

export const getAdminUserById = (id) => {
    return api.get(`${BASE_PATH}/${id}`);
};

export const createAdminUser = (data) => {
    return api.post(BASE_PATH, data);
};

export const deactivateAdminUser = (id) => {
    return api.delete(`${BASE_PATH}/${id}`);
};