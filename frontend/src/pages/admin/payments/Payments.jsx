import { useEffect, useState } from "react";
import { useAuth } from "../../../context/AuthContext";

import {
    getAdminPayments,
} from "../../../api/adminPaymentApi";

function Payments() {

    const { user } = useAuth();

    const [payments, setPayments] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // Filters
    const [status, setStatus] = useState("");
    const [transactionId, setTransactionId] = useState("");

    // Pagination
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);


    const fetchPayments = async () => {

        try {

            setLoading(true);
            setError("");

            const response = await getAdminPayments({

                status:
                    status || undefined,

                transactionId:
                    transactionId.trim() || undefined,

                page,

                size: 20,

                sort: "createdAt,desc",
            });


            setPayments(
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
                "Ödemeler alınamadı:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Ödemeler yüklenirken bir hata oluştu."
            );

        } finally {

            setLoading(false);

        }
    };


    useEffect(() => {

        fetchPayments();

    }, [page]);


    const handleFilter = (e) => {

        e.preventDefault();

        setPage(0);

        // Zaten ilk sayadaysak useEffect tetiklenmez.
        if (page === 0) {
            fetchPayments();
        }
    };


    const handleClear = () => {

        setStatus("");
        setTransactionId("");

        setPage(0);

        setTimeout(() => {
            fetchPayments();
        }, 0);
    };


    const getStatusLabel = (status) => {

        switch (status) {

            case "AUTHORIZED":
                return "Provizyon Alındı";

            case "CAPTURED":
                return "Tahsil Edildi";

            case "PARTIALLY_REFUNDED":
                return "Kısmi İade";

            case "REFUNDED":
                return "İade Edildi";

            case "FAILED":
                return "Başarısız";

            case "REFUND_FAILED":
                return "İade Başarısız";

            default:
                return status || "-";
        }
    };


    const getStatusClass = (status) => {

        switch (status) {

            case "AUTHORIZED":
                return "bg-blue-100 text-blue-700";

            case "CAPTURED":
                return "bg-green-100 text-green-700";

            case "PARTIALLY_REFUNDED":
                return "bg-yellow-100 text-yellow-700";

            case "REFUNDED":
                return "bg-gray-100 text-gray-700";

            case "FAILED":
                return "bg-red-100 text-red-700";

            case "REFUND_FAILED":
                return "bg-orange-100 text-orange-700";

            default:
                return "bg-gray-100 text-gray-600";
        }
    };


    const formatAmount = (amount) => {

        if (
            amount === null ||
            amount === undefined
        ) {
            return "-";
        }

        return `${Number(amount).toLocaleString(
            "tr-TR",
            {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
            }
        )} TL`;
    };


    const formatProvider = (providerType) => {

        switch (providerType) {

            case "IYZICO":
                return "iyzico";

            case "PAYTR":
                return "PayTR";

            default:
                return providerType || "-";
        }
    };


    if (loading) {

        return (
            <div className="p-8">

                <p className="text-gray-500">
                    Ödemeler yükleniyor...
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
                        Ödemeler
                    </h1>

                    <p className="mt-1 text-gray-500">
                        Sistemde gerçekleşen ödeme işlemlerini görüntüleyin ve filtreleyin.
                    </p>

                </div>


                <div className="text-sm text-gray-500">

                    Toplam{" "}

                    <span className="font-semibold text-gray-900">
                        {totalElements}
                    </span>{" "}

                    ödeme

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

                            <option value="AUTHORIZED">
                                Provizyon Alındı
                            </option>

                            <option value="CAPTURED">
                                Tahsil Edildi
                            </option>

                            <option value="PARTIALLY_REFUNDED">
                                Kısmi İade
                            </option>

                            <option value="REFUNDED">
                                İade Edildi
                            </option>

                            <option value="FAILED">
                                Başarısız
                            </option>

                            <option value="REFUND_FAILED">
                                İade Başarısız
                            </option>

                        </select>

                    </div>


                    {/* TRANSACTION ID */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            İşlem ID
                        </label>

                        <input
                            type="text"
                            placeholder="Transaction ID"
                            value={transactionId}
                            onChange={(e) =>
                                setTransactionId(e.target.value)
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
                                    Ödeme ID
                                </th>

                                <th className="text-left px-5 py-4">
                                    Provision ID
                                </th>

                                <th className="text-left px-5 py-4">
                                    Tutar
                                </th>

                                <th className="text-left px-5 py-4">
                                    İade Tutarı
                                </th>

                                <th className="text-left px-5 py-4">
                                    Durum
                                </th>

                                <th className="text-left px-5 py-4">
                                    Ödeme Kuruluşu
                                </th>

                                <th className="text-left px-5 py-4">
                                    Transaction ID
                                </th>

                            </tr>

                        </thead>


                        <tbody>

                            {payments.length === 0 ? (

                                <tr>

                                    <td
                                        colSpan={7}
                                        className="
                                            text-center
                                            py-10
                                            text-gray-500
                                        "
                                    >
                                        Filtrelere uygun ödeme bulunamadı.
                                    </td>

                                </tr>

                            ) : (

                                payments.map((payment) => (

                                    <tr
                                        key={payment.id}
                                        className="
                                            border-b
                                            last:border-b-0
                                            hover:bg-gray-50
                                        "
                                    >

                                        {/* PAYMENT ID */}

                                        <td className="px-5 py-4">

                                            <span className="
                                                text-sm
                                                text-gray-600
                                                font-mono
                                            ">
                                                {payment.id}
                                            </span>

                                        </td>


                                        {/* PROVISION ID */}

                                        <td className="px-5 py-4">

                                            <span className="
                                                text-sm
                                                text-gray-600
                                                font-mono
                                            ">
                                                {payment.provisionId}
                                            </span>

                                        </td>


                                        {/* AMOUNT */}

                                        <td className="px-5 py-4">

                                            <span className="font-medium text-gray-900">
                                                {formatAmount(
                                                    payment.amount
                                                )}
                                            </span>

                                        </td>


                                        {/* REFUND */}

                                        <td className="px-5 py-4">

                                            <span className="text-gray-700">
                                                {formatAmount(
                                                    payment.refundAmount
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
                                                        payment.status
                                                    )}
                                                `}
                                            >
                                                {getStatusLabel(
                                                    payment.status
                                                )}
                                            </span>

                                        </td>


                                        {/* PROVIDER */}

                                        <td className="px-5 py-4">

                                            <span className="font-medium">
                                                {formatProvider(
                                                    payment.providerType
                                                )}
                                            </span>

                                        </td>


                                        {/* TRANSACTION ID */}

                                        <td className="px-5 py-4">

                                            <span className="
                                                text-sm
                                                text-gray-600
                                                font-mono
                                            ">
                                                {payment.transactionId || "-"}
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

export default Payments;