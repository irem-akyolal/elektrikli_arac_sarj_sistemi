import { useEffect, useState } from "react";

import {
    getAdminAuditLogs,
} from "../../../api/adminAuditLogsApi";

function AuditLogs() {

    const [logs, setLogs] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // Filters
    const [action, setAction] = useState("");
    const [entityType, setEntityType] = useState("");
    const [performedBy, setPerformedBy] = useState("");

    // Pagination
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    // Auto refresh
    const [autoRefresh, setAutoRefresh] = useState(true);


    const fetchLogs = async () => {

        try {

            setLoading(true);
            setError("");

            const response = await getAdminAuditLogs({

                action: action || undefined,

                entityType:
                    entityType.trim() || undefined,

                performedBy:
                    performedBy.trim() || undefined,

                page,

                size: 20,

                sort: "createdAt,desc",
            });


            setLogs(
                response.data.content || []
            );

            setTotalPages(
                response.data.totalPages || 0
            );

            setTotalElements(
                response.data.totalElements || 0
            );

        } catch (err) {

            console.error(
                "Audit logları alınamadı:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Log kayıtları yüklenirken bir hata oluştu."
            );

        } finally {

            setLoading(false);

        }
    };


    useEffect(() => {

        fetchLogs();

    }, [page]);


    useEffect(() => {

        if (!autoRefresh) {
            return;
        }

        const interval = setInterval(() => {

            fetchLogs();

        }, 30000);

        return () => clearInterval(interval);

    }, [
        autoRefresh,
        page,
        action,
        entityType,
        performedBy
    ]);


    const handleFilter = (e) => {

        e.preventDefault();

        setPage(0);

        if (page === 0) {
            fetchLogs();
        }
    };


    const handleClear = () => {

        setAction("");
        setEntityType("");
        setPerformedBy("");

        setPage(0);

        setTimeout(() => {
            fetchLogs();
        }, 0);
    };


    const getActionLabel = (action) => {

        switch (action) {

            case "CREATE":
                return "Oluşturuldu";

            case "UPDATE":
                return "Güncellendi";

            case "DELETE":
                return "Silindi";

            case "ACTIVATE":
                return "Aktifleştirildi";

            case "DEACTIVATE":
                return "Pasifleştirildi";

            case "LOGIN":
                return "Giriş";

            case "APPROVE":
                return "Onaylandı";

            case "REJECT":
                return "Reddedildi";

            default:
                return action || "-";
        }
    };


    const getActionClass = (action) => {

        switch (action) {

            case "CREATE":
            case "ACTIVATE":
            case "APPROVE":
                return "bg-green-100 text-green-700";

            case "UPDATE":
                return "bg-blue-100 text-blue-700";

            case "DELETE":
            case "DEACTIVATE":
            case "REJECT":
                return "bg-red-100 text-red-700";

            case "LOGIN":
                return "bg-purple-100 text-purple-700";

            default:
                return "bg-gray-100 text-gray-600";
        }
    };


    const formatDate = (date) => {

        if (!date) {
            return "-";
        }

        return new Date(date).toLocaleString(
            "tr-TR"
        );
    };


    const getEntityLabel = (entityType) => {

        switch (entityType) {

            case "LOCATION":
                return "İstasyon";

            case "EVSE":
                return "EVSE";

            case "CONNECTOR":
                return "Konnektör";

            case "CHARGING_SESSION":
                return "Şarj Oturumu";

            case "PAYMENT":
                return "Ödeme";

            case "PROVISION":
                return "Provizyon";

            case "ADMIN_USER":
                return "Admin Kullanıcı";

            case "EMAIL_QUEUE":
                return "Email";

            default:
                return entityType || "-";
        }
    };


    if (loading && logs.length === 0) {

        return (
            <div className="p-8">

                <p className="text-gray-500">
                    Sistem logları yükleniyor...
                </p>

            </div>
        );
    }


    if (error && logs.length === 0) {

        return (
            <div className="p-8">

                <p className="text-red-500">
                    {error}
                </p>

            </div>
        );
    }


    return (

        <div className="p-8 bg-gray-50 min-h-full">

            {/* HEADER */}

            <div className="flex items-center justify-between mb-6">

                <div>

                    <h1 className="text-3xl font-bold text-gray-900">
                        Sistem İzleme
                    </h1>

                    <p className="mt-1 text-gray-500">
                        Sistem üzerinde gerçekleştirilen işlemleri ve yönetici aktivitelerini izleyin.
                    </p>

                </div>


                <div className="flex items-center gap-4">

                    {/* AUTO REFRESH */}

                    <label className="flex items-center gap-2 text-sm text-gray-600">

                        <input
                            type="checkbox"
                            checked={autoRefresh}
                            onChange={(e) =>
                                setAutoRefresh(e.target.checked)
                            }
                            className="w-4 h-4"
                        />

                        Otomatik yenile

                    </label>


                    <button
                        onClick={fetchLogs}
                        className="
                            border
                            bg-white
                            px-4
                            py-2
                            rounded-lg
                            hover:bg-gray-50
                            text-sm
                        "
                    >
                        ↻ Yenile
                    </button>

                </div>

            </div>


            {/* SUMMARY */}

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">

                <div className="bg-white border rounded-xl p-5">

                    <p className="text-sm text-gray-500">
                        Toplam İşlem
                    </p>

                    <p className="text-2xl font-bold text-gray-900 mt-1">
                        {totalElements}
                    </p>

                </div>


                <div className="bg-white border rounded-xl p-5">

                    <p className="text-sm text-gray-500">
                        Görüntülenen Kayıt
                    </p>

                    <p className="text-2xl font-bold text-gray-900 mt-1">
                        {logs.length}
                    </p>

                </div>


                <div className="bg-white border rounded-xl p-5">

                    <p className="text-sm text-gray-500">
                        İzleme Durumu
                    </p>

                    <p className="text-2xl font-bold text-green-600 mt-1">

                        {autoRefresh
                            ? "Aktif"
                            : "Manuel"}

                    </p>

                </div>

            </div>


            {/* FILTERS */}

            <form
                onSubmit={handleFilter}
                className="bg-white border rounded-xl p-5 mb-6"
            >

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">


                    {/* ACTION */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            İşlem
                        </label>

                        <select
                            value={action}
                            onChange={(e) =>
                                setAction(e.target.value)
                            }
                            className="
                                w-full
                                border
                                rounded-lg
                                px-3
                                py-2
                                bg-white
                            "
                        >

                            <option value="">
                                Tüm İşlemler
                            </option>

                            <option value="CREATE">
                                Oluşturma
                            </option>

                            <option value="UPDATE">
                                Güncelleme
                            </option>

                            <option value="DELETE">
                                Silme
                            </option>

                            <option value="ACTIVATE">
                                Aktifleştirme
                            </option>

                            <option value="DEACTIVATE">
                                Pasifleştirme
                            </option>

                            <option value="LOGIN">
                                Giriş
                            </option>

                            <option value="APPROVE">
                                Onay
                            </option>

                            <option value="REJECT">
                                Red
                            </option>

                        </select>

                    </div>


                    {/* ENTITY TYPE */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Kaynak
                        </label>

                        <select
                            value={entityType}
                            onChange={(e) =>
                                setEntityType(e.target.value)
                            }
                            className="
                                w-full
                                border
                                rounded-lg
                                px-3
                                py-2
                                bg-white
                            "
                        >

                            <option value="">
                                Tüm Kaynaklar
                            </option>

                            <option value="LOCATION">
                                İstasyon
                            </option>

                            <option value="EVSE">
                                EVSE
                            </option>

                            <option value="CONNECTOR">
                                Konnektör
                            </option>

                            <option value="CHARGING_SESSION">
                                Şarj Oturumu
                            </option>

                            <option value="PAYMENT">
                                Ödeme
                            </option>

                            <option value="PROVISION">
                                Provizyon
                            </option>

                            <option value="ADMIN_USER">
                                Admin Kullanıcı
                            </option>

                            <option value="EMAIL_QUEUE">
                                Email
                            </option>

                        </select>

                    </div>


                    {/* PERFORMED BY */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            İşlemi Yapan
                        </label>

                        <input
                            type="text"
                            placeholder="Kullanıcı adı"
                            value={performedBy}
                            onChange={(e) =>
                                setPerformedBy(e.target.value)
                            }
                            className="
                                w-full
                                border
                                rounded-lg
                                px-3
                                py-2
                            "
                        />

                    </div>


                    {/* BUTTONS */}

                    <div className="flex items-end gap-2">

                        <button
                            type="submit"
                            className="
                                bg-gray-900
                                text-white
                                px-5
                                py-2
                                rounded-lg
                                hover:bg-gray-800
                            "
                        >
                            Filtrele
                        </button>


                        <button
                            type="button"
                            onClick={handleClear}
                            className="
                                border
                                px-5
                                py-2
                                rounded-lg
                                hover:bg-gray-50
                                bg-white
                            "
                        >
                            Temizle
                        </button>

                    </div>

                </div>

            </form>


            {/* LOG TABLE */}

            <div className="bg-white border rounded-xl overflow-hidden">

                <div className="overflow-x-auto">

                    <table className="w-full">

                        <thead className="bg-gray-50 border-b">

                            <tr>

                                <th className="text-left px-5 py-4">
                                    Zaman
                                </th>

                                <th className="text-left px-5 py-4">
                                    Kullanıcı
                                </th>

                                <th className="text-left px-5 py-4">
                                    İşlem
                                </th>

                                <th className="text-left px-5 py-4">
                                    Kaynak
                                </th>

                                <th className="text-left px-5 py-4">
                                    Kayıt ID
                                </th>

                            </tr>

                        </thead>


                        <tbody>

                            {logs.length === 0 ? (

                                <tr>

                                    <td
                                        colSpan={5}
                                        className="
                                            text-center
                                            py-10
                                            text-gray-500
                                        "
                                    >
                                        Filtrelere uygun log kaydı bulunamadı.
                                    </td>

                                </tr>

                            ) : (

                                logs.map((log) => (

                                    <tr
                                        key={log.id}
                                        className="
                                            border-b
                                            last:border-b-0
                                            hover:bg-gray-50
                                        "
                                    >

                                        {/* TIME */}

                                        <td className="px-5 py-4">

                                            <span className="text-sm text-gray-600">

                                                {formatDate(
                                                    log.createdAt
                                                )}

                                            </span>

                                        </td>


                                        {/* USER */}

                                        <td className="px-5 py-4">

                                            <span className="font-medium text-gray-900">

                                                {log.performedBy || "SYSTEM"}

                                            </span>

                                        </td>


                                        {/* ACTION */}

                                        <td className="px-5 py-4">

                                            <span
                                                className={`
                                                    inline-flex
                                                    px-3
                                                    py-1
                                                    rounded-full
                                                    text-xs
                                                    font-medium
                                                    ${getActionClass(
                                                        log.action
                                                    )}
                                                `}
                                            >

                                                {getActionLabel(
                                                    log.action
                                                )}

                                            </span>

                                        </td>


                                        {/* ENTITY */}

                                        <td className="px-5 py-4">

                                            <span className="text-sm text-gray-700">

                                                {getEntityLabel(
                                                    log.entityType
                                                )}

                                            </span>

                                        </td>


                                        {/* ENTITY ID */}

                                        <td className="px-5 py-4">

                                            <span
                                                className="
                                                    text-xs
                                                    text-gray-500
                                                    font-mono
                                                    max-w-xs
                                                    block
                                                    truncate
                                                "
                                                title={
                                                    log.entityId || ""
                                                }
                                            >

                                                {log.entityId || "-"}

                                            </span>

                                        </td>

                                    </tr>

                                ))

                            )}

                        </tbody>

                    </table>

                </div>

            </div>


            {/* PAGINATION */}

            {totalPages > 1 && (

                <div
                    className="
                        flex
                        justify-center
                        items-center
                        gap-3
                        mt-6
                    "
                >

                    <button
                        disabled={page === 0}
                        onClick={() =>
                            setPage((p) => p - 1)
                        }
                        className="
                            px-4
                            py-2
                            border
                            rounded-lg
                            bg-white
                            disabled:opacity-40
                        "
                    >
                        ← Önceki
                    </button>


                    <span className="text-sm text-gray-600">

                        Sayfa{" "}

                        <strong>
                            {page + 1}
                        </strong>

                        {" "} / {" "}

                        <strong>
                            {totalPages}
                        </strong>

                    </span>


                    <button
                        disabled={
                            page >= totalPages - 1
                        }
                        onClick={() =>
                            setPage((p) => p + 1)
                        }
                        className="
                            px-4
                            py-2
                            border
                            rounded-lg
                            bg-white
                            disabled:opacity-40
                        "
                    >
                        Sonraki →
                    </button>

                </div>

            )}

        </div>
    );
}

export default AuditLogs;