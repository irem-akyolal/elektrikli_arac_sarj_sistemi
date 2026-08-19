import api from "./axios";

const BASE_PATH = "/admin/audit-logs";

export const getAdminAuditLogs = ({
    action,
    entityType,
    performedBy,
    page = 0,
    size = 20,
    sort = "createdAt,desc",
} = {}) => {
    return api.get(BASE_PATH, {
        params: {
            ...(action && { action }),
            ...(entityType && { entityType }),
            ...(performedBy && { performedBy }),
            page,
            size,
            sort,
        },
    });
};