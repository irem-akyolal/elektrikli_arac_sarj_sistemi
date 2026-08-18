import api from "./axios";

const BASE_PATH = "/evses";

export const getAdminEvses = ({
    status,
    locationId,
    page = 0,
    size = 20,
    sort = "createdAt,desc",
} = {}) => {
    return api.get(`${BASE_PATH}/search`, {
        params: {
            ...(status && { status }),
            ...(locationId && { locationId }),
            page,
            size,
            sort,
        },
    });
};

export const getEvseById = (id) => {
    return api.get(`${BASE_PATH}/${id}`);
};

export const getEvsesByLocation = (locationId) => {
    return api.get(BASE_PATH, {
        params: { locationId },
    });
};

export const createEvse = (data) => {
    return api.post(BASE_PATH, data);
};

export const updateEvseStatus = (id, status) => {
    return api.patch(`${BASE_PATH}/${id}/status`, null, {
        params: { status },
    });
};