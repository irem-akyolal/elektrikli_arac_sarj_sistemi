import api from "./axios";

const BASE_PATH = "/charging-sessions";

export const getAdminChargingSessions = ({
    status,
    email,
    plateNumber,
    startedAfter,
    startedBefore,
    page = 0,
    size = 20,
    sort = "startedAt,desc",
} = {}) => {

    return api.get(`${BASE_PATH}/search`, {
        params: {
            ...(status && { status }),
            ...(email && { email }),
            ...(plateNumber && { plateNumber }),
            ...(startedAfter && { startedAfter }),
            ...(startedBefore && { startedBefore }),
            page,
            size,
            sort,
        },
    });
};