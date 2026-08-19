import { useEffect, useState } from "react";

import {
    getAdminProvisions,
} from "../../../api/adminProvisionApi";

function Provisions() {

    const [provisions, setProvisions] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // Filters
    const [status, setStatus] = useState("");
    const [closedAfter, setClosedAfter] = useState("");
    const [closedBefore, setClosedBefore] = useState("");

    // Pagination
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);


    const fetchProvisions = async () => {

        try {

            setLoading(true);
            setError("");

            const response = await getAdminProvisions({

                status: status || undefined,

                closedAfter:
                    closedAfter
                        ? new Date(closedAfter).toISOString()
                        : undefined,

                closedBefore:
                    closedBefore
                        ? new Date(closedBefore).toISOString()
                        : undefined,

                page,

                size: 20,

                sort: "createdAt,desc",
            });


            setProvisions(
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
                "Provizyonlar alınamadı:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Provizyonlar yüklenirken bir hata oluştu."
            );

        } finally {

            setLoading(false);

        }
    };


    useEffect(() => {

        fetchProvisions();

    }, [page]);


    const handleFilter = (e) => {

        e.preventDefault();

        setPage(0);

        // Zaten ilk sayadaysak useEffect tetiklenmez.
        if (page === 0) {
            fetchProvisions();
        }
    };


    const handleClear = () => {

        setStatus("");
        setClosedAfter("");
        setClosedBefore("");

        setPage(0);

        setTimeout(() => {
            fetchProvisions();
        }, 0);
    };


    const getStatusLabel = (status) => {

        switch (status) {

            case "PENDING":
                return "Bekliyor";

            case "APPROVED":
                return "Onaylandı";

            case "CLOSED":
                return "Kapatıldı";

            case "FAILED":
                return "Başarısız";

            case "CANCELLED":
                return "İptal Edildi";

            default:
                return status || "-";
        }
    };


    const getStatusClass = (status) => {

        switch (status) {

            case "PENDING":
                return "bg-yellow-100 text-yellow-700";

            case "APPROVED":
                return "bg-green-100 text-green-700";

            case "CLOSED":
                return "bg-gray-100 text-gray-700";

            case "FAILED":
                return "bg-red-100 text-red-700";

            case "CANCELLED":
                return "bg-orange-100 text-orange-700";

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


    const formatAmount = (amount) => {

        if (
            amount === null ||
            amount === undefined
        ) {
            return "-";
        }

        return `${amount} ₺`;
    };


    if (loading) {

        return (
            <div className="p-8">

                <p className="text-gray-500">
                    Provizyonlar yükleniyor...
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
                        Provizyonlar
                    </h1>

                    <p className="mt-1 text-gray-500">
                        Sistemde oluşturulan ödeme provizyonlarını görüntüleyin ve filtreleyin.
                    </p>

                </div>


                <div className="text-sm text-gray-500">

                    Toplam{" "}

                    <span className="font-semibold text-gray-900">
                        {totalElements}
                    </span>{" "}

                    provizyon

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

                            <option value="PENDING">
                                Bekliyor
                            </option>

                            <option value="APPROVED">
                                Onaylandı
                            </option>

                            <option value="CLOSED">
                                Kapatıldı
                            </option>

                            <option value="FAILED">
                                Başarısız
                            </option>

                            <option value="CANCELLED">
                                İptal Edildi
                            </option>

                        </select>

                    </div>


                    {/* CLOSED AFTER */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Kapanış Başlangıcı
                        </label>

                        <input
                            type="datetime-local"
                            value={closedAfter}
                            onChange={(e) =>
                                setClosedAfter(e.target.value)
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


                    {/* CLOSED BEFORE */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Kapanış Bitişi
                        </label>

                        <input
                            type="datetime-local"
                            value={closedBefore}
                            onChange={(e) =>
                                setClosedBefore(e.target.value)
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
                                    Provizyon ID
                                </th>

                                <th className="text-left px-5 py-4">
                                    Şarj Oturumu
                                </th>

                                <th className="text-left px-5 py-4">
                                    Tutar
                                </th>

                                <th className="text-left px-5 py-4">
                                    Durum
                                </th>

                                <th className="text-left px-5 py-4">
                                    Provider Reference
                                </th>

                                <th className="text-left px-5 py-4">
                                    Kapanış
                                </th>

                            </tr>

                        </thead>


                        <tbody>

                            {provisions.length === 0 ? (

                                <tr>

                                    <td
                                        colSpan={6}
                                        className="
                                            text-center
                                            py-10
                                            text-gray-500
                                        "
                                    >
                                        Filtrelere uygun provizyon bulunamadı.
                                    </td>

                                </tr>

                            ) : (

                                provisions.map((provision) => (

                                    <tr
                                        key={provision.id}
                                        className="
                                            border-b
                                            last:border-b-0
                                            hover:bg-gray-50
                                        "
                                    >

                                        {/* PROVISION ID */}

                                        <td className="px-5 py-4">

                                            <span className="
                                                text-sm
                                                text-gray-600
                                                font-mono
                                            ">
                                                {provision.id}
                                            </span>

                                        </td>


                                        {/* CHARGING SESSION */}

                                        <td className="px-5 py-4">

                                            <span className="
                                                text-sm
                                                text-gray-600
                                                font-mono
                                            ">
                                                {provision.chargingSessionId}
                                            </span>

                                        </td>


                                        {/* AMOUNT */}

                                        <td className="px-5 py-4">

                                            <span className="font-medium">
                                                {formatAmount(
                                                    provision.requestedAmount
                                                )}
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
                                                        provision.status
                                                    )}
                                                `}
                                            >
                                                {getStatusLabel(
                                                    provision.status
                                                )}
                                            </span>

                                        </td>


                                        {/* PROVIDER REFERENCE */}

                                        <td className="px-5 py-4">

                                            <span className="
                                                text-sm
                                                text-gray-600
                                                font-mono
                                            ">
                                                {provision.providerReferenceId || "-"}
                                            </span>

                                        </td>


                                        {/* CLOSED */}

                                        <td className="px-5 py-4 text-sm text-gray-600">

                                            {formatDate(
                                                provision.closedAt
                                            )}

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

export default Provisions;