import { useCallback, useEffect, useState } from "react";
import api from "../../api/axios";

function Dashboard() {
    const [summary, setSummary] = useState(null);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [error, setError] = useState("");
    const [lastUpdated, setLastUpdated] = useState(null);

    const fetchDashboard = useCallback(async (isRefresh = false) => {
        try {
            if (isRefresh) {
                setRefreshing(true);
            } else {
                setLoading(true);
            }

            setError("");

            const response = await api.get("/dashboard/summary");

            setSummary(response.data);
            setLastUpdated(new Date());

        } catch (err) {
            console.error("Dashboard verileri alınamadı:", err);

            setError(
                err.response?.data?.message ||
                "Dashboard verileri yüklenirken bir hata oluştu."
            );
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    }, []);

    useEffect(() => {
        fetchDashboard();

        const interval = setInterval(() => {
            fetchDashboard(true);
        }, 30000);

        return () => clearInterval(interval);
    }, [fetchDashboard]);

    const formatMoney = (value) => {
        return new Intl.NumberFormat("tr-TR", {
            style: "currency",
            currency: "TRY",
            minimumFractionDigits: 2,
        }).format(value ?? 0);
    };

    const formatLastUpdated = () => {
        if (!lastUpdated) {
            return "-";
        }

        return lastUpdated.toLocaleTimeString("tr-TR", {
            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit",
        });
    };

    const getConnectorAvailabilityPercentage = () => {
        if (!summary || summary.totalConnectors === 0) {
            return 0;
        }

        return Math.round(
            (summary.availableConnectors /
                summary.totalConnectors) *
                100
        );
    };

    if (loading) {
        return (
            <div className="min-h-full bg-gray-50 p-8">
                <div className="animate-pulse space-y-6">

                    <div className="h-10 bg-gray-200 rounded-lg w-72" />

                    <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-5">
                        {[1, 2, 3, 4].map((item) => (
                            <div
                                key={item}
                                className="h-36 bg-white rounded-2xl border"
                            />
                        ))}
                    </div>

                    <div className="grid grid-cols-1 xl:grid-cols-3 gap-5">
                        {[1, 2, 3].map((item) => (
                            <div
                                key={item}
                                className="h-64 bg-white rounded-2xl border"
                            />
                        ))}
                    </div>

                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-full bg-gray-50 p-8">
                <div className="bg-white border border-red-200 rounded-2xl p-8">
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center">
                            <span className="text-red-600 text-lg">
                                !
                            </span>
                        </div>

                        <div>
                            <h2 className="font-semibold text-gray-900">
                                Dashboard yüklenemedi
                            </h2>

                            <p className="text-sm text-gray-500 mt-1">
                                {error}
                            </p>
                        </div>
                    </div>

                    <button
                        onClick={() => fetchDashboard()}
                        className="
                            mt-5
                            px-4
                            py-2
                            rounded-lg
                            bg-gray-900
                            text-white
                            text-sm
                            hover:bg-gray-800
                        "
                    >
                        Tekrar Dene
                    </button>
                </div>
            </div>
        );
    }

    if (!summary) {
        return null;
    }

    const availabilityPercentage =
        getConnectorAvailabilityPercentage();

    return (
        <div className="min-h-full bg-gray-50 p-6 lg:p-8">

            {/* HEADER */}
            <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4 mb-8">

                <div>
                    <div className="flex items-center gap-3">

                        <h1 className="text-3xl font-bold text-gray-900">
                            Sistem Dashboard
                        </h1>

                        <span className="
                            inline-flex
                            items-center
                            gap-2
                            px-3
                            py-1
                            rounded-full
                            bg-green-50
                            text-green-700
                            text-xs
                            font-medium
                        ">
                            <span className="w-2 h-2 rounded-full bg-green-500" />
                            Sistem aktif
                        </span>

                    </div>

                    <p className="text-gray-500 mt-2">
                        Elektrikli araç şarj ağının anlık operasyon durumu
                    </p>
                </div>

                <div className="flex items-center gap-3">

                    <div className="text-right hidden sm:block">
                        <p className="text-xs text-gray-400">
                            Son güncelleme
                        </p>

                        <p className="text-sm font-medium text-gray-700">
                            {formatLastUpdated()}
                        </p>
                    </div>

                    <button
                        onClick={() => fetchDashboard(true)}
                        disabled={refreshing}
                        className="
                            flex
                            items-center
                            gap-2
                            px-4
                            py-2.5
                            bg-white
                            border
                            border-gray-200
                            rounded-xl
                            text-sm
                            font-medium
                            text-gray-700
                            hover:bg-gray-50
                            disabled:opacity-50
                        "
                    >
                        <span
                            className={
                                refreshing
                                    ? "animate-spin"
                                    : ""
                            }
                        >
                            ↻
                        </span>

                        Yenile
                    </button>

                </div>

            </div>


            {/* TOP SUMMARY CARDS */}
            <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-5 mb-6">

                {/* LOCATIONS */}
                <div className="
                    bg-white
                    border
                    border-gray-200
                    rounded-2xl
                    p-5
                    shadow-sm
                ">
                    <div className="flex items-start justify-between">

                        <div>
                            <p className="text-sm text-gray-500">
                                İstasyonlar
                            </p>

                            <p className="text-3xl font-bold text-gray-900 mt-2">
                                {summary.activeLocations}
                                <span className="text-lg text-gray-400 font-normal">
                                    {" "}/ {summary.totalLocations}
                                </span>
                            </p>

                            <p className="text-xs text-gray-400 mt-2">
                                Aktif istasyon
                            </p>
                        </div>

                        <div className="
                            w-11
                            h-11
                            rounded-xl
                            bg-blue-50
                            flex
                            items-center
                            justify-center
                        ">
                            <span className="text-blue-600 text-xl">
                                ◉
                            </span>
                        </div>

                    </div>

                    <div className="mt-4 h-1.5 bg-gray-100 rounded-full overflow-hidden">
                        <div
                            className="h-full bg-blue-500 rounded-full"
                            style={{
                                width:
                                    summary.totalLocations > 0
                                        ? `${Math.round(
                                            (summary.activeLocations /
                                                summary.totalLocations) *
                                            100
                                        )}%`
                                        : "0%",
                            }}
                        />
                    </div>
                </div>


                {/* CONNECTORS */}
                <div className="
                    bg-white
                    border
                    border-gray-200
                    rounded-2xl
                    p-5
                    shadow-sm
                ">
                    <div className="flex items-start justify-between">

                        <div>
                            <p className="text-sm text-gray-500">
                                Kullanılabilir Connector
                            </p>

                            <p className="text-3xl font-bold text-gray-900 mt-2">
                                {summary.availableConnectors}
                                <span className="text-lg text-gray-400 font-normal">
                                    {" "}/ {summary.totalConnectors}
                                </span>
                            </p>

                            <p className="text-xs text-gray-400 mt-2">
                                %{availabilityPercentage} kullanılabilir
                            </p>
                        </div>

                        <div className="
                            w-11
                            h-11
                            rounded-xl
                            bg-emerald-50
                            flex
                            items-center
                            justify-center
                        ">
                            <span className="text-emerald-600 text-xl">
                                ⚡
                            </span>
                        </div>

                    </div>

                    <div className="mt-4 h-1.5 bg-gray-100 rounded-full overflow-hidden">
                        <div
                            className="h-full bg-emerald-500 rounded-full"
                            style={{
                                width: `${availabilityPercentage}%`,
                            }}
                        />
                    </div>
                </div>


                {/* ACTIVE SESSIONS */}
                <div className="
                    bg-gray-900
                    rounded-2xl
                    p-5
                    shadow-sm
                    text-white
                ">
                    <div className="flex items-start justify-between">

                        <div>
                            <p className="text-sm text-gray-400">
                                Aktif Şarj
                            </p>

                            <p className="text-4xl font-bold mt-2">
                                {summary.activeSessions}
                            </p>

                            <p className="text-xs text-gray-400 mt-2">
                                Şu anda şarj oluyor
                            </p>
                        </div>

                        <div className="
                            w-11
                            h-11
                            rounded-xl
                            bg-white/10
                            flex
                            items-center
                            justify-center
                        ">
                            <span className="text-xl">
                                ⚡
                            </span>
                        </div>

                    </div>

                    <div className="flex items-center gap-2 mt-5 text-xs text-gray-400">
                        <span className="w-2 h-2 rounded-full bg-green-400 animate-pulse" />
                        Canlı izleme
                    </div>
                </div>


                {/* TODAY SESSIONS */}
                <div className="
                    bg-white
                    border
                    border-gray-200
                    rounded-2xl
                    p-5
                    shadow-sm
                ">
                    <div className="flex items-start justify-between">

                        <div>
                            <p className="text-sm text-gray-500">
                                Bugün Tamamlanan
                            </p>

                            <p className="text-3xl font-bold text-gray-900 mt-2">
                                {summary.todayCompletedSessions}
                            </p>

                            <p className="text-xs text-gray-400 mt-2">
                                Tamamlanan şarj oturumu
                            </p>
                        </div>

                        <div className="
                            w-11
                            h-11
                            rounded-xl
                            bg-violet-50
                            flex
                            items-center
                            justify-center
                        ">
                            <span className="text-violet-600 text-xl">
                                ✓
                            </span>
                        </div>

                    </div>
                </div>

            </div>


            {/* SECOND ROW */}
            <div className="grid grid-cols-1 xl:grid-cols-3 gap-5 mb-6">

                {/* LIVE STATUS */}
                <div className="
                    xl:col-span-2
                    bg-white
                    border
                    border-gray-200
                    rounded-2xl
                    p-6
                    shadow-sm
                ">

                    <div className="flex items-center justify-between mb-6">

                        <div>
                            <h2 className="text-lg font-semibold text-gray-900">
                                Operasyon Durumu
                            </h2>

                            <p className="text-sm text-gray-500 mt-1">
                                Şarj ağının mevcut durumu
                            </p>
                        </div>

                        <span className="
                            px-3
                            py-1.5
                            rounded-lg
                            bg-gray-50
                            text-xs
                            text-gray-500
                        ">
                            Canlı
                        </span>

                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">

                        {/* Active Locations */}
                        <div className="
                            rounded-xl
                            bg-gray-50
                            p-5
                        ">
                            <div className="flex items-center gap-2 mb-3">

                                <span className="w-2.5 h-2.5 rounded-full bg-blue-500" />

                                <span className="text-sm text-gray-500">
                                    Aktif İstasyon
                                </span>

                            </div>

                            <p className="text-2xl font-bold text-gray-900">
                                {summary.activeLocations}
                            </p>

                            <p className="text-xs text-gray-400 mt-1">
                                Toplam {summary.totalLocations}
                            </p>
                        </div>


                        {/* Available Connectors */}
                        <div className="
                            rounded-xl
                            bg-gray-50
                            p-5
                        ">
                            <div className="flex items-center gap-2 mb-3">

                                <span className="w-2.5 h-2.5 rounded-full bg-emerald-500" />

                                <span className="text-sm text-gray-500">
                                    Kullanılabilir
                                </span>

                            </div>

                            <p className="text-2xl font-bold text-gray-900">
                                {summary.availableConnectors}
                            </p>

                            <p className="text-xs text-gray-400 mt-1">
                                Connector
                            </p>
                        </div>


                        {/* Active Sessions */}
                        <div className="
                            rounded-xl
                            bg-gray-50
                            p-5
                        ">
                            <div className="flex items-center gap-2 mb-3">

                                <span className="w-2.5 h-2.5 rounded-full bg-orange-500 animate-pulse" />

                                <span className="text-sm text-gray-500">
                                    Aktif Şarj
                                </span>

                            </div>

                            <p className="text-2xl font-bold text-gray-900">
                                {summary.activeSessions}
                            </p>

                            <p className="text-xs text-gray-400 mt-1">
                                Devam eden oturum
                            </p>
                        </div>

                    </div>


                    {/* NETWORK CAPACITY */}
                    <div className="mt-6">

                        <div className="flex justify-between mb-2">

                            <span className="text-sm font-medium text-gray-700">
                                Connector kullanılabilirliği
                            </span>

                            <span className="text-sm font-semibold text-gray-900">
                                %{availabilityPercentage}
                            </span>

                        </div>

                        <div className="h-2 bg-gray-100 rounded-full overflow-hidden">

                            <div
                                className="h-full bg-gray-900 rounded-full transition-all duration-500"
                                style={{
                                    width: `${availabilityPercentage}%`,
                                }}
                            />

                        </div>

                    </div>

                </div>


                {/* REVENUE */}
                <div className="
                    bg-white
                    border
                    border-gray-200
                    rounded-2xl
                    p-6
                    shadow-sm
                ">

                    <div className="flex items-center justify-between">

                        <div>
                            <h2 className="text-lg font-semibold text-gray-900">
                                Gelir
                            </h2>

                            <p className="text-sm text-gray-500 mt-1">
                                Ödeme özeti
                            </p>
                        </div>

                        <div className="
                            w-11
                            h-11
                            rounded-xl
                            bg-green-50
                            flex
                            items-center
                            justify-center
                        ">
                            <span className="text-green-600 text-xl">
                                ₺
                            </span>
                        </div>

                    </div>


                    <div className="mt-7">

                        <p className="text-xs text-gray-400">
                            Bugünkü gelir
                        </p>

                        <p className="text-3xl font-bold text-gray-900 mt-1">
                            {formatMoney(summary.todayRevenue)}
                        </p>

                    </div>


                    <div className="
                        mt-6
                        pt-5
                        border-t
                        border-gray-100
                    ">

                        <p className="text-xs text-gray-400">
                            Toplam gelir
                        </p>

                        <p className="text-xl font-semibold text-gray-700 mt-1">
                            {formatMoney(summary.totalRevenue)}
                        </p>

                    </div>

                </div>

            </div>


            {/* BOTTOM */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">

                {/* DATA QUALITY */}
                <div className="
                    bg-white
                    border
                    border-gray-200
                    rounded-2xl
                    p-6
                    shadow-sm
                ">

                    <div className="flex items-start justify-between">

                        <div>
                            <h2 className="text-lg font-semibold text-gray-900">
                                Veri Kalitesi
                            </h2>

                            <p className="text-sm text-gray-500 mt-1">
                                Connector bilgilerinin kontrolü
                            </p>
                        </div>

                        <div className={`
                            w-11
                            h-11
                            rounded-xl
                            flex
                            items-center
                            justify-center
                            ${
                                summary.unknownConnectorDataCount > 0
                                    ? "bg-yellow-50"
                                    : "bg-green-50"
                            }
                        `}>
                            <span className={`
                                text-xl
                                ${
                                    summary.unknownConnectorDataCount > 0
                                        ? "text-yellow-600"
                                        : "text-green-600"
                                }
                            `}>
                                {summary.unknownConnectorDataCount > 0
                                    ? "!"
                                    : "✓"}
                            </span>
                        </div>

                    </div>


                    <div className="mt-6 flex items-center justify-between">

                        <div>
                            <p className="text-3xl font-bold text-gray-900">
                                {summary.unknownConnectorDataCount}
                            </p>

                            <p className="text-sm text-gray-500 mt-1">
                                Eksik / bilinmeyen connector verisi
                            </p>
                        </div>

                        <span className={`
                            px-3
                            py-1.5
                            rounded-lg
                            text-xs
                            font-medium
                            ${
                                summary.unknownConnectorDataCount > 0
                                    ? "bg-yellow-50 text-yellow-700"
                                    : "bg-green-50 text-green-700"
                            }
                        `}>
                            {summary.unknownConnectorDataCount > 0
                                ? "Kontrol gerekli"
                                : "Veriler normal"}
                        </span>

                    </div>

                </div>


                {/* SYSTEM MONITOR */}
                <div className="
                    bg-gray-900
                    rounded-2xl
                    p-6
                    shadow-sm
                    text-white
                ">

                    <div className="flex items-center justify-between">

                        <div>
                            <h2 className="text-lg font-semibold">
                                Sistem İzleme
                            </h2>

                            <p className="text-sm text-gray-400 mt-1">
                                Platform genel durumu
                            </p>
                        </div>

                        <span className="
                            flex
                            items-center
                            gap-2
                            px-3
                            py-1.5
                            rounded-lg
                            bg-green-500/10
                            text-green-400
                            text-xs
                        ">
                            <span className="w-2 h-2 rounded-full bg-green-400" />
                            Online
                        </span>

                    </div>


                    <div className="grid grid-cols-2 gap-4 mt-6">

                        <div className="
                            bg-white/5
                            rounded-xl
                            p-4
                        ">
                            <p className="text-xs text-gray-400">
                                Toplam Connector
                            </p>

                            <p className="text-2xl font-bold mt-1">
                                {summary.totalConnectors}
                            </p>
                        </div>


                        <div className="
                            bg-white/5
                            rounded-xl
                            p-4
                        ">
                            <p className="text-xs text-gray-400">
                                Aktif Oturum
                            </p>

                            <p className="text-2xl font-bold mt-1">
                                {summary.activeSessions}
                            </p>
                        </div>

                    </div>


                    <div className="mt-5 text-xs text-gray-500">
                        Dashboard verileri otomatik olarak 30 saniyede bir
                        güncellenir.
                    </div>

                </div>

            </div>

        </div>
    );
}

export default Dashboard;