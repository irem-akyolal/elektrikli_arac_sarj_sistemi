import { useEffect, useState } from "react";
import { useAuth } from "../../../context/AuthContext";

import {
    getAdminEvses,
    updateEvseStatus,
} from "../../../api/adminEvseApi";

function Evses() {
    const { user } = useAuth();

    const [evses, setEvses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [status, setStatus] = useState("");
    const [locationId, setLocationId] = useState("");

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    // SUPER_ADMIN ve OPERATOR yönetebilir.
    // VIEWER sadece görüntüleyebilir.
    const canManage =
        user?.role === "SUPER_ADMIN" ||
        user?.role === "OPERATOR";

    const fetchEvses = async () => {
        try {
            setLoading(true);
            setError("");

            const response = await getAdminEvses({
                status: status || undefined,
                locationId: locationId || undefined,
                page,
                size: 20,
            });

            setEvses(response.data.content);
            setTotalPages(response.data.totalPages);
        } catch (err) {
            console.error("EVSE'ler alınamadı:", err);
            setError("EVSE'ler yüklenirken bir hata oluştu.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchEvses();
    }, [page]);

    const handleFilter = () => {
        setPage(0);

        // page zaten 0 ise useEffect çalışmayacağı için
        // filtreyi burada doğrudan uyguluyoruz.
        if (page === 0) {
            fetchEvses();
        }
    };

    const handleStatusChange = async (id, newStatus) => {
        try {
            await updateEvseStatus(id, newStatus);

            await fetchEvses();
        } catch (err) {
            console.error("EVSE durumu değiştirilemedi:", err);
            alert("EVSE durumu değiştirilemedi.");
        }
    };

    if (loading) {
        return (
            <div className="p-8">
                <p className="text-gray-500">
                    EVSE'ler yükleniyor...
                </p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="p-8">
                <p className="text-red-500">
                    {error}
                </p>
            </div>
        );
    }

    return (
        <div className="p-8">

            {/* HEADER */}
            <div className="flex items-center justify-between mb-6">

                <div>
                    <h1 className="text-3xl font-bold text-gray-900">
                        EVSE Yönetimi
                    </h1>

                    <p className="mt-1 text-gray-500">
                        Şarj istasyonlarındaki EVSE'leri yönetin.
                    </p>
                </div>

                {canManage && (
                    <button
                        className="
                            bg-blue-600
                            text-white
                            px-4
                            py-2
                            rounded-lg
                            hover:bg-blue-700
                        "
                    >
                        + EVSE Ekle
                    </button>
                )}

            </div>

            {/* FILTER */}
            <div className="bg-white border rounded-xl p-5 mb-6">

                <div className="flex flex-col md:flex-row gap-3">

                    <select
                        value={status}
                        onChange={(e) => setStatus(e.target.value)}
                        className="
                            border
                            rounded-lg
                            px-3
                            py-2
                            bg-white
                        "
                    >
                        <option value="">
                            Tüm Durumlar
                        </option>

                        <option value="AVAILABLE">
                            AVAILABLE
                        </option>

                        <option value="BLOCKED">
                            BLOCKED
                        </option>

                        <option value="CHARGING">
                            CHARGING
                        </option>

                        <option value="INOPERATIVE">
                            INOPERATIVE
                        </option>

                        <option value="OUT_OF_ORDER">
                            OUT_OF_ORDER
                        </option>

                        <option value="PENDING_REMOVAL">
                            PENDING_REMOVAL
                        </option>

                        <option value="PLANNED">
                            PLANNED
                        </option>

                        <option value="REMOVED">
                            REMOVED
                        </option>

                        <option value="RESERVED">
                            RESERVED
                        </option>

                        <option value="UNKNOWN">
                            UNKNOWN
                        </option>
                    </select>

                    <input
                        type="text"
                        placeholder="Location ID"
                        value={locationId}
                        onChange={(e) =>
                            setLocationId(e.target.value)
                        }
                        className="
                            border
                            rounded-lg
                            px-3
                            py-2
                            flex-1
                        "
                    />

                    <button
                        onClick={handleFilter}
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

                </div>

            </div>

            {/* TABLE */}
            <div className="bg-white border rounded-xl overflow-hidden">

                <table className="w-full">

                    <thead className="bg-gray-50 border-b">

                        <tr>

                            <th className="text-left px-5 py-4">
                                EVSE ID
                            </th>

                            <th className="text-left px-5 py-4">
                                Location ID
                            </th>

                            <th className="text-left px-5 py-4">
                                Durum
                            </th>

                            {canManage && (
                                <th className="text-left px-5 py-4">
                                    İşlem
                                </th>
                            )}

                        </tr>

                    </thead>

                    <tbody>

                        {evses.length === 0 ? (

                            <tr>
                                <td
                                    colSpan={canManage ? 4 : 3}
                                    className="
                                        text-center
                                        py-8
                                        text-gray-500
                                    "
                                >
                                    EVSE bulunamadı.
                                </td>
                            </tr>

                        ) : (

                            evses.map((evse) => (

                                <tr
                                    key={evse.id}
                                    className="border-b last:border-b-0"
                                >

                                    {/* EVSE ID */}
                                    <td className="px-5 py-4 font-medium">
                                        {evse.evseId || evse.id}
                                    </td>

                                    {/* LOCATION ID */}
                                    <td className="px-5 py-4 text-gray-600">
                                        {evse.locationId}
                                    </td>

                                    {/* STATUS */}
                                    <td className="px-5 py-4">

                                        <span
                                            className={`
                                                px-3
                                                py-1
                                                rounded-full
                                                text-xs
                                                font-medium

                                                ${
                                                    evse.status === "AVAILABLE"
                                                        ? "bg-green-100 text-green-700"
                                                        : evse.status === "CHARGING"
                                                        ? "bg-blue-100 text-blue-700"
                                                        : evse.status === "BLOCKED"
                                                        ? "bg-yellow-100 text-yellow-700"
                                                        : evse.status === "RESERVED"
                                                        ? "bg-purple-100 text-purple-700"
                                                        : evse.status === "INOPERATIVE" ||
                                                          evse.status === "OUT_OF_ORDER"
                                                        ? "bg-red-100 text-red-700"
                                                        : "bg-gray-100 text-gray-700"
                                                }
                                            `}
                                        >
                                            {evse.status}
                                        </span>

                                    </td>

                                    {/* ACTION */}
                                    {canManage && (

                                        <td className="px-5 py-4">

                                            <select
                                                value={evse.status}
                                                onChange={(e) =>
                                                    handleStatusChange(
                                                        evse.id,
                                                        e.target.value
                                                    )
                                                }
                                                className="
                                                    border
                                                    rounded-lg
                                                    px-3
                                                    py-2
                                                    text-sm
                                                    bg-white
                                                "
                                            >

                                                <option value="AVAILABLE">
                                                    AVAILABLE
                                                </option>

                                                <option value="BLOCKED">
                                                    BLOCKED
                                                </option>

                                                <option value="CHARGING">
                                                    CHARGING
                                                </option>

                                                <option value="INOPERATIVE">
                                                    INOPERATIVE
                                                </option>

                                                <option value="OUT_OF_ORDER">
                                                    OUT_OF_ORDER
                                                </option>

                                                <option value="PENDING_REMOVAL">
                                                    PENDING_REMOVAL
                                                </option>

                                                <option value="PLANNED">
                                                    PLANNED
                                                </option>

                                                <option value="REMOVED">
                                                    REMOVED
                                                </option>

                                                <option value="RESERVED">
                                                    RESERVED
                                                </option>

                                                <option value="UNKNOWN">
                                                    UNKNOWN
                                                </option>

                                            </select>

                                        </td>

                                    )}

                                </tr>

                            ))

                        )}

                    </tbody>

                </table>

            </div>

            {/* PAGINATION */}
            {totalPages > 1 && (

                <div className="flex justify-center items-center gap-3 mt-6">

                    <button
                        disabled={page === 0}
                        onClick={() => setPage((p) => p - 1)}
                        className="
                            px-4
                            py-2
                            border
                            rounded-lg
                            disabled:opacity-40
                        "
                    >
                        Önceki
                    </button>

                    <span className="text-sm text-gray-600">
                        Sayfa {page + 1} / {totalPages}
                    </span>

                    <button
                        disabled={page >= totalPages - 1}
                        onClick={() => setPage((p) => p + 1)}
                        className="
                            px-4
                            py-2
                            border
                            rounded-lg
                            disabled:opacity-40
                        "
                    >
                        Sonraki
                    </button>

                </div>

            )}

        </div>
    );
}

export default Evses;