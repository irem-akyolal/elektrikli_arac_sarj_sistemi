import { useEffect, useState } from "react";
import { useAuth } from "../../../context/AuthContext";

import {
    getAdminConnectors,
    updateConnectorUnitPrice,
} from "../../../api/adminConnectorApi";

function Connectors() {

    const { user } = useAuth();

    const [connectors, setConnectors] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // Filters
    const [standard, setStandard] = useState("");
    const [powerType, setPowerType] = useState("");
    const [evseId, setEvseId] = useState("");

    // Pagination
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    // Price editing
    const [editingPriceId, setEditingPriceId] = useState(null);
    const [newPrice, setNewPrice] = useState("");

    // SUPER_ADMIN ve OPERATOR yönetebilir.
    // VIEWER sadece görüntüleyebilir.
    const canManage =
        user?.role === "SUPER_ADMIN" ||
        user?.role === "OPERATOR";


    const fetchConnectors = async () => {

        try {

            setLoading(true);
            setError("");

            const response = await getAdminConnectors({
                standard: standard || undefined,
                powerType: powerType || undefined,
                evseId: evseId || undefined,
                page,
                size: 20,
                sort: "createdAt,desc",
            });

            setConnectors(response.data.content || []);
            setTotalPages(response.data.totalPages || 0);

        } catch (err) {

            console.error(
                "Connector'lar alınamadı:",
                err
            );

            setError(
                "Connector'lar yüklenirken bir hata oluştu."
            );

        } finally {

            setLoading(false);

        }
    };


    useEffect(() => {

        fetchConnectors();

    }, [page]);


    const handleFilter = () => {

        setPage(0);

        // Zaten 0. sayfadaysak useEffect tetiklenmez.
        // Bu yüzden doğrudan tekrar çağırıyoruz.
        if (page === 0) {
            fetchConnectors();
        }
    };


    const handlePriceEdit = (connector) => {

        setEditingPriceId(connector.id);

        setNewPrice(
            connector.unitPrice ?? ""
        );
    };


    const handlePriceCancel = () => {

        setEditingPriceId(null);
        setNewPrice("");

    };


    const handlePriceSave = async (id) => {

        if (!newPrice || Number(newPrice) <= 0) {

            alert(
                "Birim fiyat 0'dan büyük olmalıdır."
            );

            return;
        }

        try {

            await updateConnectorUnitPrice(
                id,
                newPrice
            );

            setEditingPriceId(null);
            setNewPrice("");

            await fetchConnectors();

        } catch (err) {

            console.error(
                "Birim fiyat güncellenemedi:",
                err
            );

            alert(
                "Birim fiyat güncellenemedi."
            );
        }
    };


    if (loading) {

        return (
            <div className="p-8">

                <p className="text-gray-500">
                    Connector'lar yükleniyor...
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
                        Connector Yönetimi
                    </h1>

                    <p className="mt-1 text-gray-500">
                        EVSE'lere bağlı connector'ları yönetin.
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
                        + Connector Ekle
                    </button>

                )}

            </div>


            {/* FILTER */}

            <div className="bg-white border rounded-xl p-5 mb-6">

                <div className="flex flex-col md:flex-row gap-3">


                    {/* STANDARD */}

                    <select
                        value={standard}
                        onChange={(e) =>
                            setStandard(e.target.value)
                        }
                        className="
                            border
                            rounded-lg
                            px-3
                            py-2
                            bg-white
                        "
                    >

                        <option value="">
                            Tüm Standartlar
                        </option>

                        <option value="IEC_62196_T2">
                            IEC_62196_T2
                        </option>

                        <option value="IEC_62196_T2_COMBO">
                            IEC_62196_T2_COMBO
                        </option>

                        <option value="CHADEMO">
                            CHADEMO
                        </option>

                        <option value="IEC_62196_T3A">
                            IEC_62196_T3A
                        </option>

                        <option value="IEC_62196_T3C">
                            IEC_62196_T3C
                        </option>

                    </select>


                    {/* POWER TYPE */}

                    <select
                        value={powerType}
                        onChange={(e) =>
                            setPowerType(e.target.value)
                        }
                        className="
                            border
                            rounded-lg
                            px-3
                            py-2
                            bg-white
                        "
                    >

                        <option value="">
                            Tüm Güç Tipleri
                        </option>

                        <option value="AC_1_PHASE">
                            AC_1_PHASE
                        </option>

                        <option value="AC_3_PHASE">
                            AC_3_PHASE
                        </option>

                        <option value="DC">
                            DC
                        </option>

                    </select>


                    {/* EVSE ID */}

                    <input
                        type="text"
                        placeholder="EVSE ID"
                        value={evseId}
                        onChange={(e) =>
                            setEvseId(e.target.value)
                        }
                        className="
                            border
                            rounded-lg
                            px-3
                            py-2
                            flex-1
                        "
                    />


                    {/* FILTER BUTTON */}

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
                                OCPI Connector ID
                            </th>

                            <th className="text-left px-5 py-4">
                                EVSE ID
                            </th>

                            <th className="text-left px-5 py-4">
                                Standard
                            </th>

                            <th className="text-left px-5 py-4">
                                Format
                            </th>

                            <th className="text-left px-5 py-4">
                                Güç Tipi
                            </th>

                            <th className="text-left px-5 py-4">
                                Maks. Güç
                            </th>

                            <th className="text-left px-5 py-4">
                                Birim Fiyat
                            </th>

                            {canManage && (

                                <th className="text-left px-5 py-4">
                                    İşlem
                                </th>

                            )}

                        </tr>

                    </thead>


                    <tbody>

                        {connectors.length === 0 ? (

                            <tr>

                                <td
                                    colSpan={canManage ? 8 : 7}
                                    className="
                                        text-center
                                        py-8
                                        text-gray-500
                                    "
                                >
                                    Connector bulunamadı.
                                </td>

                            </tr>

                        ) : (

                            connectors.map((connector) => (

                                <tr
                                    key={connector.id}
                                    className="
                                        border-b
                                        last:border-b-0
                                    "
                                >

                                    {/* OCPI CONNECTOR ID */}

                                    <td className="px-5 py-4 font-medium">

                                        {connector.ocpiConnectorId}

                                    </td>


                                    {/* EVSE ID */}

                                    <td className="px-5 py-4 text-gray-600">

                                        {connector.evseId}

                                    </td>


                                    {/* STANDARD */}

                                    <td className="px-5 py-4">

                                        <span
                                            className="
                                                px-3
                                                py-1
                                                rounded-full
                                                text-xs
                                                font-medium
                                                bg-blue-100
                                                text-blue-700
                                            "
                                        >

                                            {connector.standard}

                                        </span>

                                    </td>


                                    {/* FORMAT */}

                                    <td className="px-5 py-4">

                                        {connector.format}

                                    </td>


                                    {/* POWER TYPE */}

                                    <td className="px-5 py-4">

                                        {connector.powerType}

                                    </td>


                                    {/* MAX POWER */}

                                    <td className="px-5 py-4">

                                        {connector.maxElectricPowerWatt
                                            ? `${connector.maxElectricPowerWatt} W`
                                            : "-"
                                        }

                                    </td>


                                    {/* UNIT PRICE */}

                                    <td className="px-5 py-4">

                                        {editingPriceId === connector.id ? (

                                            <div className="flex items-center gap-2">

                                                <input
                                                    type="number"
                                                    min="0"
                                                    step="0.01"
                                                    value={newPrice}
                                                    onChange={(e) =>
                                                        setNewPrice(
                                                            e.target.value
                                                        )
                                                    }
                                                    className="
                                                        border
                                                        rounded-lg
                                                        px-2
                                                        py-1
                                                        w-24
                                                    "
                                                />

                                                <button
                                                    onClick={() =>
                                                        handlePriceSave(
                                                            connector.id
                                                        )
                                                    }
                                                    className="
                                                        text-green-600
                                                        font-medium
                                                    "
                                                >
                                                    Kaydet
                                                </button>

                                                <button
                                                    onClick={
                                                        handlePriceCancel
                                                    }
                                                    className="
                                                        text-gray-500
                                                    "
                                                >
                                                    İptal
                                                </button>

                                            </div>

                                        ) : (

                                            <span>

                                                {connector.unitPrice}
                                                {" ₺"}

                                            </span>

                                        )}

                                    </td>


                                    {/* ACTION */}

                                    {canManage && (

                                        <td className="px-5 py-4">

                                            {editingPriceId !== connector.id && (

                                                <button
                                                    onClick={() =>
                                                        handlePriceEdit(
                                                            connector
                                                        )
                                                    }
                                                    className="
                                                        text-blue-600
                                                        hover:text-blue-800
                                                        font-medium
                                                    "
                                                >
                                                    Fiyat Düzenle
                                                </button>

                                            )}

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
                        Önceki
                    </button>


                    <span className="text-sm text-gray-600">

                        Sayfa {page + 1} / {totalPages}

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
                        Sonraki
                    </button>

                </div>

            )}

        </div>

    );
}

export default Connectors;