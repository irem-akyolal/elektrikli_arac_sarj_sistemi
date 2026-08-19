import api from "./axios";

const BASE_PATH = "/provisions";

export const getAdminProvisions = ({
    status,
    closedAfter,
    closedBefore,
    page = 0,
    size = 20,
    sort = "createdAt,desc",
} = {}) => {

    return api.get(`${BASE_PATH}/search`, {
        params: {
            ...(status && { status }),

            ...(closedAfter && {
                closedAfter: new Date(closedAfter).toISOString(),
            }),

            ...(closedBefore && {
                closedBefore: new Date(closedBefore).toISOString(),
            }),

            page,
            size,
            sort,
        },
    });
};

export const getProvisionById = (id) => {
    return api.get(`${BASE_PATH}/${id}`);
};