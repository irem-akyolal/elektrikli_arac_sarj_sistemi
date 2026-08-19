import { useEffect, useState } from "react";
import { useAuth } from "../../../context/AuthContext";

import {
    getAdminChargingSessions,
} from "../../../api/adminChargingSessionApi";

function Sessions() {

    const { user } = useAuth();

    const [sessions, setSessions] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // Filters
    const [status, setStatus] = useState("");
    const [email, setEmail] = useState("");
    const [plateNumber, setPlateNumber] = useState("");
    const [startedAfter, setStartedAfter] = useState("");
    const [startedBefore, setStartedBefore] = useState("");

    // Pagination
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);


    const fetchSessions = async () => {

        try {

            setLoading(true);
            setError("");

            const response = await getAdminChargingSessions({

                status: status || undefined,

                email: email.trim() || undefined,

                plateNumber:
                    plateNumber.trim() || undefined,

                startedAfter:
                    startedAfter
                        ? new Date(startedAfter).toISOString()
                        : undefined,

                startedBefore:
                    startedBefore
                        ? new Date(startedBefore).toISOString()
                        : undefined,

                page,

                size: 20,

                sort: "startedAt,desc",
            });


            setSessions(
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
                "Şarj oturumları alınamadı:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Şarj oturumları yüklenirken bir hata oluştu."
            );

        } finally {

            setLoading(false);

        }
    };


    useEffect(() => {

        fetchSessions();

    }, [page]);


    const handleFilter = (e) => {

        e.preventDefault();

        setPage(0);

        // Zaten ilk sayadaysak useEffect tetiklenmez.
        if (page === 0) {
            fetchSessions();
        }
    };


    const handleClear = () => {

        setStatus("");
        setEmail("");
        setPlateNumber("");
        setStartedAfter("");
        setStartedBefore("");

        setPage(0);

        setTimeout(() => {
            fetchSessions();
        }, 0);
    };


    const getStatusLabel = (status) => {

        switch (status) {

            case "STARTED":
                return "Başlatıldı";

            case "CHARGING":
                return "Şarj Oluyor";

            case "COMPLETED":
                return "Tamamlandı";

            case "CONNECTOR_PENDING_REMOVAL":
                return "Konnektör Bekleniyor";

            case "CLOSED":
                return "Kapatıldı";

            case "FAILED":
                return "Başarısız";

            default:
                return status || "-";
        }
    };


    const getStatusClass = (status) => {

        switch (status) {

            case "STARTED":
                return "bg-blue-100 text-blue-700";

            case "CHARGING":
                return "bg-yellow-100 text-yellow-700";

            case "COMPLETED":
                return "bg-green-100 text-green-700";

            case "CONNECTOR_PENDING_REMOVAL":
                return "bg-orange-100 text-orange-700";

            case "CLOSED":
                return "bg-gray-100 text-gray-700";

            case "FAILED":
                return "bg-red-100 text-red-700";

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


    const formatEnergy = (energy) => {

        if (
            energy === null ||
            energy === undefined
        ) {
            return "-";
        }

        return `${energy} kWh`;
    };


    if (loading) {

        return (
            <div className="p-8">

                <p className="text-gray-500">
                    Şarj oturumları yükleniyor...
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
                        Şarj Oturumları
                    </h1>

                    <p className="mt-1 text-gray-500">
                        Sistemde gerçekleşen şarj oturumlarını görüntüleyin ve filtreleyin.
                    </p>

                </div>


                <div className="text-sm text-gray-500">

                    Toplam{" "}

                    <span className="font-semibold text-gray-900">
                        {totalElements}
                    </span>{" "}

                    oturum

                </div>

            </div>


            {/* FILTERS */}

            <form
                onSubmit={handleFilter}
                className="bg-white border rounded-xl p-5 mb-6"
            >

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">


                    {/* STATUS */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Durum
                        </label>

                        <select
                            value={status}
                            onChange={(e) =>
                                setStatus(e.target.value)
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
                                Tüm Durumlar
                            </option>

                            <option value="STARTED">
                                Başlatıldı
                            </option>

                            <option value="CHARGING">
                                Şarj Oluyor
                            </option>

                            <option value="COMPLETED">
                                Tamamlandı
                            </option>

                            <option value="CONNECTOR_PENDING_REMOVAL">
                                Konnektör Bekleniyor
                            </option>

                            <option value="CLOSED">
                                Kapatıldı
                            </option>

                            <option value="FAILED">
                                Başarısız
                            </option>

                        </select>

                    </div>


                    {/* EMAIL */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            E-posta
                        </label>

                        <input
                            type="text"
                            placeholder="Kullanıcı e-postası"
                            value={email}
                            onChange={(e) =>
                                setEmail(e.target.value)
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


                    {/* PLATE */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Plaka
                        </label>

                        <input
                            type="text"
                            placeholder="Plaka numarası"
                            value={plateNumber}
                            onChange={(e) =>
                                setPlateNumber(e.target.value)
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


                    {/* STARTED AFTER */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Başlangıç
                        </label>

                        <input
                            type="datetime-local"
                            value={startedAfter}
                            onChange={(e) =>
                                setStartedAfter(e.target.value)
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


                    {/* STARTED BEFORE */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Bitiş
                        </label>

                        <input
                            type="datetime-local"
                            value={startedBefore}
                            onChange={(e) =>
                                setStartedBefore(e.target.value)
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
                            "
                        >
                            Temizle
                        </button>

                    </div>

                </div>

            </form>


            {/* TABLE */}

            <div className="bg-white border rounded-xl overflow-hidden">

                <div className="overflow-x-auto">

                    <table className="w-full">

                        <thead className="bg-gray-50 border-b">

                            <tr>

                                <th className="text-left px-5 py-4">
                                    Kullanıcı
                                </th>

                                <th className="text-left px-5 py-4">
                                    Plaka
                                </th>

                                <th className="text-left px-5 py-4">
                                    Connector ID
                                </th>

                                <th className="text-left px-5 py-4">
                                    Durum
                                </th>

                                <th className="text-left px-5 py-4">
                                    Başlangıç
                                </th>

                                <th className="text-left px-5 py-4">
                                    Tamamlanma
                                </th>

                                <th className="text-left px-5 py-4">
                                    Enerji
                                </th>

                                <th className="text-left px-5 py-4">
                                    Konnektör Çıkarma
                                </th>

                                {/* EKLENEN ALAN */}

                                <th className="text-left px-5 py-4">
                                    Bekleme Süresi
                                </th>

                            </tr>

                        </thead>


                        <tbody>

                            {sessions.length === 0 ? (

                                <tr>

                                    <td
                                        colSpan={9}
                                        className="
                                            text-center
                                            py-10
                                            text-gray-500
                                        "
                                    >
                                        Filtrelere uygun şarj oturumu bulunamadı.
                                    </td>

                                </tr>

                            ) : (

                                sessions.map((session) => (

                                    <tr
                                        key={session.id}
                                        className="
                                            border-b
                                            last:border-b-0
                                            hover:bg-gray-50
                                        "
                                    >

                                        {/* USER */}

                                        <td className="px-5 py-4">

                                            <div className="flex flex-col">

                                                <span className="font-medium text-gray-900">
                                                    {session.email}
                                                </span>

                                                <span className="text-xs text-gray-400 mt-1">
                                                    {session.id}
                                                </span>

                                            </div>

                                        </td>


                                        {/* PLATE */}

                                        <td className="px-5 py-4">

                                            <span className="font-medium">
                                                {session.plateNumber}
                                            </span>

                                        </td>


                                        {/* CONNECTOR */}

                                        <td className="px-5 py-4">

                                            <span className="
                                                text-sm
                                                text-gray-600
                                                font-mono
                                            ">
                                                {session.connectorId}
                                            </span>

                                        </td>


                                        {/* STATUS */}

                                        <td className="px-5 py-4">

                                            <span
                                                className={`
                                                    inline-flex
                                                    px-3
                                                    py-1
                                                    rounded-full
                                                    text-xs
                                                    font-medium
                                                    ${getStatusClass(
                                                        session.status
                                                    )}
                                                `}
                                            >
                                                {getStatusLabel(
                                                    session.status
                                                )}
                                            </span>

                                        </td>


                                        {/* START */}

                                        <td className="px-5 py-4 text-sm text-gray-600">

                                            {formatDate(
                                                session.startedAt
                                            )}

                                        </td>


                                        {/* COMPLETED */}

                                        <td className="px-5 py-4 text-sm text-gray-600">

                                            {formatDate(
                                                session.completedAt
                                            )}

                                        </td>


                                        {/* ENERGY */}

                                        <td className="px-5 py-4">

                                            {formatEnergy(
                                                session.energyConsumedKwh
                                            )}

                                        </td>


                                        {/* CONNECTOR REMOVED */}

                                        <td className="px-5 py-4 text-sm text-gray-600">

                                            {formatDate(
                                                session.connectorRemovedAt
                                            )}

                                        </td>


                                        {/* PENDING REMOVAL DURATION */}

                                        <td className="px-5 py-4 text-sm text-gray-600">

                                            {session.pendingRemovalDurationSeconds !== null &&
                                            session.pendingRemovalDurationSeconds !== undefined
                                                ? `${session.pendingRemovalDurationSeconds} sn`
                                                : "-"
                                            }

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

export default Sessions;