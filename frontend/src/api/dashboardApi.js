import api from "./axios";

const BASE_PATH = "/dashboard";

export const getDashboardSummary = () => {
    return api.get(`${BASE_PATH}/summary`);
};