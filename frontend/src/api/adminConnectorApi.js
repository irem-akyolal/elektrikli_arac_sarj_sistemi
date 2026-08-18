import api from "./axios";

const BASE_PATH = "/connectors";

export const getAdminConnectors = ({
    standard,
    powerType,
    evseId,
    page = 0,
    size = 20,
    sort = "createdAt,desc",
} = {}) => {
    return api.get(`${BASE_PATH}/search`, {
        params: {
            ...(standard && { standard }),
            ...(powerType && { powerType }),
            ...(evseId && { evseId }),
            page,
            size,
            sort,
        },
    });
};

export const getConnectorById = (id) => {
    return api.get(`${BASE_PATH}/${id}`);
};

export const getConnectorsByEvse = (evseId) => {
    return api.get(BASE_PATH, {
        params: { evseId },
    });
};

export const createConnector = (data) => {
    return api.post(BASE_PATH, data);
};

export const updateConnector = (id, data) => {
    return api.patch(`${BASE_PATH}/${id}`, data);
};

export const updateConnectorUnitPrice = (id, price) => {
    return api.patch(`${BASE_PATH}/${id}/unit-price`, null, {
        params: { price },
    });
};