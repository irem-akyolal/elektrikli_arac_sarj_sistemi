import { useEffect, useState } from "react";

import {
    getAdminEmailQueue,
} from "../../../api/adminEmailQueueApi";

function EmailQueue() {

    const [emails, setEmails] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // Filters
    const [status, setStatus] = useState("");
    const [recipient, setRecipient] = useState("");
    const [invoiceNumber, setInvoiceNumber] = useState("");
    const [createdAfter, setCreatedAfter] = useState("");
    const [createdBefore, setCreatedBefore] = useState("");

    // Pagination
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);


    const fetchEmails = async () => {

        try {

            setLoading(true);
            setError("");

            const response = await getAdminEmailQueue({

                status: status || undefined,

                recipient:
                    recipient.trim() || undefined,

                invoiceNumber:
                    invoiceNumber.trim() || undefined,

                createdAfter:
                    createdAfter
                        ? new Date(createdAfter).toISOString()
                        : undefined,

                createdBefore:
                    createdBefore
                        ? new Date(createdBefore).toISOString()
                        : undefined,

                page,

                size: 20,

                sort: "createdAt,desc",
            });


            setEmails(
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
                "Email geçmişi alınamadı:",
                err
            );

            setError(
                err.response?.data?.message ||
                "Email geçmişi yüklenirken bir hata oluştu."
            );

        } finally {

            setLoading(false);

        }
    };


    useEffect(() => {

        fetchEmails();

    }, [page]);


    const handleFilter = (e) => {

        e.preventDefault();

        setPage(0);

        if (page === 0) {
            fetchEmails();
        }
    };


    const handleClear = () => {

        setStatus("");
        setRecipient("");
        setInvoiceNumber("");
        setCreatedAfter("");
        setCreatedBefore("");

        setPage(0);

        setTimeout(() => {
            fetchEmails();
        }, 0);
    };


    const getStatusLabel = (status) => {

        switch (status) {

            case "PENDING":
                return "Bekliyor";

            case "PROCESSING":
                return "İşleniyor";

            case "SENT":
                return "Gönderildi";

            case "FAILED":
                return "Başarısız";

            default:
                return status || "-";
        }
    };


    const getStatusClass = (status) => {

        switch (status) {

            case "PENDING":
                return "bg-yellow-100 text-yellow-700";

            case "PROCESSING":
                return "bg-blue-100 text-blue-700";

            case "SENT":
                return "bg-green-100 text-green-700";

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


    if (loading) {

        return (
            <div className="p-8">

                <p className="text-gray-500">
                    Email geçmişi yükleniyor...
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
                        Email Geçmişi
                    </h1>

                    <p className="mt-1 text-gray-500">
                        Sistemde oluşturulan email gönderimlerini görüntüleyin ve filtreleyin.
                    </p>

                </div>


                <div className="text-sm text-gray-500">

                    Toplam{" "}

                    <span className="font-semibold text-gray-900">
                        {totalElements}
                    </span>{" "}

                    email

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

                            <option value="PROCESSING">
                                İşleniyor
                            </option>

                            <option value="SENT">
                                Gönderildi
                            </option>

                            <option value="FAILED">
                                Başarısız
                            </option>

                        </select>

                    </div>


                    {/* RECIPIENT */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Alıcı
                        </label>

                        <input
                            type="text"
                            placeholder="Alıcı e-posta adresi"
                            value={recipient}
                            onChange={(e) =>
                                setRecipient(e.target.value)
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


                    {/* INVOICE NUMBER */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Fatura No
                        </label>

                        <input
                            type="text"
                            placeholder="Fatura numarası"
                            value={invoiceNumber}
                            onChange={(e) =>
                                setInvoiceNumber(e.target.value)
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


                    {/* CREATED AFTER */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Başlangıç
                        </label>

                        <input
                            type="datetime-local"
                            value={createdAfter}
                            onChange={(e) =>
                                setCreatedAfter(e.target.value)
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


                    {/* CREATED BEFORE */}

                    <div>

                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Bitiş
                        </label>

                        <input
                            type="datetime-local"
                            value={createdBefore}
                            onChange={(e) =>
                                setCreatedBefore(e.target.value)
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
                                    Fatura No
                                </th>

                                <th className="text-left px-5 py-4">
                                    Alıcı
                                </th>

                                <th className="text-left px-5 py-4">
                                    Durum
                                </th>

                                <th className="text-left px-5 py-4">
                                    Deneme
                                </th>

                                <th className="text-left px-5 py-4">
                                    Son Hata
                                </th>

                                <th className="text-left px-5 py-4">
                                    Sonraki Deneme
                                </th>

                                <th className="text-left px-5 py-4">
                                    Gönderilme
                                </th>

                                <th className="text-left px-5 py-4">
                                    Oluşturulma
                                </th>

                            </tr>

                        </thead>


                        <tbody>

                            {emails.length === 0 ? (

                                <tr>

                                    <td
                                        colSpan={8}
                                        className="
                                            text-center
                                            py-10
                                            text-gray-500
                                        "
                                    >
                                        Filtrelere uygun email kaydı bulunamadı.
                                    </td>

                                </tr>

                            ) : (

                                emails.map((email) => (

                                    <tr
                                        key={email.id}
                                        className="
                                            border-b
                                            last:border-b-0
                                            hover:bg-gray-50
                                        "
                                    >

                                        {/* INVOICE */}

                                        <td className="px-5 py-4">

                                            <div className="flex flex-col">

                                                <span className="font-medium text-gray-900">
                                                    {email.invoiceNumber || "-"}
                                                </span>

                                                <span className="text-xs text-gray-400 mt-1">
                                                    {email.invoiceId || "-"}
                                                </span>

                                            </div>

                                        </td>


                                        {/* RECIPIENT */}

                                        <td className="px-5 py-4">

                                            <span className="text-sm">
                                                {email.recipient || "-"}
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
                                                        email.status
                                                    )}
                                                `}
                                            >
                                                {getStatusLabel(
                                                    email.status
                                                )}
                                            </span>

                                        </td>


                                        {/* ATTEMPT COUNT */}

                                        <td className="px-5 py-4">

                                            <span className="text-sm">
                                                {email.attemptCount}
                                            </span>

                                        </td>


                                        {/* LAST ERROR */}

                                        <td className="px-5 py-4">

                                            <span
                                                className="
                                                    text-sm
                                                    text-red-600
                                                    max-w-xs
                                                    block
                                                    truncate
                                                "
                                                title={email.lastError || ""}
                                            >
                                                {email.lastError || "-"}
                                            </span>

                                        </td>


                                        {/* NEXT ATTEMPT */}

                                        <td className="px-5 py-4 text-sm text-gray-600">

                                            {formatDate(
                                                email.nextAttemptAt
                                            )}

                                        </td>


                                        {/* SENT */}

                                        <td className="px-5 py-4 text-sm text-gray-600">

                                            {formatDate(
                                                email.sentAt
                                            )}

                                        </td>


                                        {/* CREATED */}

                                        <td className="px-5 py-4 text-sm text-gray-600">

                                            {formatDate(
                                                email.createdAt
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

export default EmailQueue;