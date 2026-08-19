import api from "./axios";

const BASE_PATH = "/admin/email-queue";

export const getAdminEmailQueue = ({
    status,
    recipient,
    invoiceNumber,
    createdAfter,
    createdBefore,
    page = 0,
    size = 20,
    sort = "createdAt,desc",
} = {}) => {

    return api.get(BASE_PATH, {
        params: {
            ...(status && { status }),

            ...(recipient?.trim() && {
                recipient: recipient.trim(),
            }),

            ...(invoiceNumber?.trim() && {
                invoiceNumber: invoiceNumber.trim(),
            }),

            ...(createdAfter && {
                createdAfter: new Date(createdAfter).toISOString(),
            }),

            ...(createdBefore && {
                createdBefore: new Date(createdBefore).toISOString(),
            }),

            page,
            size,
            sort,
        },
    });
};