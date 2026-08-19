import api from "./axios";

const BASE_PATH = "/payments";

export const getAdminPayments = ({
    status,
    transactionId,
    page = 0,
    size = 20,
    sort = "createdAt,desc",
} = {}) => {

    return api.get(`${BASE_PATH}/search`, {
        params: {
            ...(status && { status }),
            ...(transactionId && { transactionId }),
            page,
            size,
            sort,
        },
    });
};

export const getPaymentById = (id) => {
    return api.get(`${BASE_PATH}/${id}`);
};