import { useState } from "react";
import ChargingFlowModal from "../charging/ChargingFlowModal";

function LocationDetailPanel({ location, onClose, onLocationRefresh }) {
  const [chargingConnector, setChargingConnector] = useState(null);

  if (!location) {
    return null;
  }

  const getStatusText = (status) => {
    switch (status) {
      case "AVAILABLE":
        return "Müsait";
      case "CHARGING":
        return "Şarj oluyor";
      case "OFFLINE":
        return "Çevrim dışı";
      case "OUTOFORDER":
        return "Arızalı";
      case "BLOCKED":
        return "Engelli";
      case "INOPERATIVE":
        return "Devre dışı";
      case "UNKNOWN":
        return "Bilinmiyor";
      default:
        return status || "Bilinmiyor";
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "AVAILABLE":
        return "bg-green-100 text-green-700";
      case "CHARGING":
        return "bg-blue-100 text-blue-700";
      case "OUTOFORDER":
      case "INOPERATIVE":
      case "BLOCKED":
        return "bg-red-100 text-red-700";
      case "OFFLINE":
        return "bg-gray-200 text-gray-600";
      default:
        return "bg-gray-100 text-gray-600";
    }
  };

const openDirections = () => {
  if (
    typeof location.latitude !== "number" ||
    typeof location.longitude !== "number"
  ) {
    return;
  }

  const latitude = location.latitude;
  const longitude = location.longitude;

  const userAgent = navigator.userAgent || navigator.vendor || "";

  const isAppleDevice =
    /iPad|iPhone|iPod|Macintosh/.test(userAgent) &&
    !window.MSStream;

  if (isAppleDevice) {
    const appleMapsUrl =
      `https://maps.apple.com/?daddr=${latitude},${longitude}`;

    window.open(appleMapsUrl, "_blank");
  } else {
    const googleMapsUrl =
      `https://www.google.com/maps/dir/?api=1` +
      `&destination=${latitude},${longitude}`;

    window.open(googleMapsUrl, "_blank");
  }
};

  return (
    <div
      className="
        absolute
        inset-y-0
        right-0
        w-full
        sm:w-[400px]
        bg-white
        shadow-2xl
        z-20
        overflow-y-auto
      "
    >
      {/* ================================
          HEADER
      ================================= */}

      <div className="sticky top-0 bg-white border-b z-10">
        <div className="flex items-start justify-between p-5">
          <div className="pr-4">
            <p className="text-xs font-medium text-blue-600 uppercase tracking-wide">
              Şarj İstasyonu
            </p>
            <h2 className="text-xl font-bold text-gray-900 mt-1">
              {location.name}
            </h2>
          </div>

          <button
            onClick={onClose}
            className="
              w-9
              h-9
              rounded-full
              flex
              items-center
              justify-center
              text-gray-500
              hover:bg-gray-100
              hover:text-gray-900
              transition
            "
          >
            ✕
          </button>
        </div>
      </div>

      {/* ================================
          CONTENT
      ================================= */}

      <div className="p-5">
        {/* ================================
            ADDRESS
        ================================= */}

        <div className="mb-6">
          <div className="flex gap-3">
            <div className="text-xl">📍</div>

            <div>
              <p className="font-medium text-gray-900">
                {location.address}
              </p>

              <p className="text-sm text-gray-500 mt-1">
                {location.city}
                {location.postalCode ? `, ${location.postalCode}` : ""}
              </p>

              {location.country && (
                <p className="text-xs text-gray-400 mt-1">
                  {location.country}
                </p>
              )}
            </div>
          </div>
        </div>

        {/* ================================
            STATION STATUS
        ================================= */}

        <div className="mb-6">
          <h3 className="font-semibold text-gray-900 mb-3">
            İstasyon Durumu
          </h3>

          <div className="flex items-center gap-2">
            <span
              className={`
                inline-flex
                items-center
                px-3
                py-1.5
                rounded-full
                text-sm
                font-medium
                ${
                  location.active
                    ? "bg-green-100 text-green-700"
                    : "bg-red-100 text-red-700"
                }
              `}
            >
              <span className="mr-2">●</span>
              {location.active ? "Aktif" : "Aktif değil"}
            </span>
          </div>
        </div>

        {/* ================================
            CONNECTORS
        ================================= */}

        <div>
          <div className="flex items-center justify-between mb-3">
            <h3 className="font-semibold text-gray-900">Şarj Noktaları</h3>
            <span className="text-sm text-gray-500">
              {location.connectors?.length || 0} adet
            </span>
          </div>

          {!location.connectors || location.connectors.length === 0 ? (
            <div className="border rounded-xl p-4 text-sm text-gray-500">
              Bu istasyona ait şarj noktası bulunamadı.
            </div>
          ) : (
            <div className="space-y-3">
              {location.connectors.map((connector, index) => (
                <div
                  key={connector.id || connector.connectorId || index}
                  className="
                    border
                    rounded-xl
                    p-4
                    bg-gray-50
                  "
                >
                  {/* ==========================
                      CONNECTOR HEADER
                  =========================== */}

                  <div className="flex items-center justify-between gap-3">
                    <div>
                      <p className="font-semibold text-gray-900">
                        🔌 Connector {index + 1}
                      </p>

                      <p className="text-xs text-gray-500 mt-1">
                        ID: {connector.connectorId || "-"}
                      </p>
                    </div>

                    <span
                      className={`
                        px-2.5
                        py-1
                        rounded-full
                        text-xs
                        font-medium
                        whitespace-nowrap
                        ${getStatusClass(connector.status)}
                      `}
                    >
                      {getStatusText(connector.status)}
                    </span>
                  </div>

                  {/* ==========================
                      CONNECTOR DETAILS
                  =========================== */}

                  <div className="mt-4 grid grid-cols-2 gap-3">
                    {/* STANDARD */}
                    <div className="bg-white rounded-lg p-3">
                      <p className="text-xs text-gray-500">Standart</p>
                      <p className="text-sm font-semibold text-gray-900 mt-1">
                        {connector.standard || "-"}
                      </p>
                    </div>

                    {/* POWER TYPE */}
                    <div className="bg-white rounded-lg p-3">
                      <p className="text-xs text-gray-500">Güç Tipi</p>
                      <p className="text-sm font-semibold text-gray-900 mt-1">
                        {connector.powerType || "-"}
                      </p>
                    </div>

                    {/* MAX POWER */}
                    <div className="bg-white rounded-lg p-3">
                      <p className="text-xs text-gray-500">Maksimum Güç</p>
                      <p className="text-sm font-semibold text-gray-900 mt-1">
                        {connector.maxPowerWatt != null
                          ? `${(connector.maxPowerWatt / 1000).toFixed(1)} kW`
                          : "-"}
                      </p>
                    </div>

                    {/* PRICE */}
                    <div className="bg-white rounded-lg p-3">
                      <p className="text-xs text-gray-500">Birim Fiyat</p>
                      <p className="text-sm font-semibold text-gray-900 mt-1">
                        {connector.unitPrice != null
                          ? `${connector.unitPrice} TL/kWh`
                          : "-"}
                      </p>
                    </div>
                  </div>

                  {/* ==========================
                      ŞARJ BAŞLAT BUTONU
                  =========================== */}

                  {connector.status === "AVAILABLE" && (
                    <button
                      onClick={() => setChargingConnector(connector)}
                      className="
                        w-full
                        mt-3
                        py-2.5
                        rounded-lg
                        bg-green-600
                        text-white
                        text-sm
                        font-semibold
                        hover:bg-green-700
                        transition
                      "
                    >
                      ⚡ Şarj Başlat
                    </button>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>

        {/* ================================
            DIRECTIONS
        ================================= */}

        <button
          onClick={openDirections}
          className="
            w-full
            mt-6
            py-3
            rounded-xl
            bg-blue-600
            text-white
            font-semibold
            hover:bg-blue-700
            transition
          "
        >
          📍 Yol Tarifi Al
        </button>
      </div>

      {/* ================================
          ŞARJ BAŞLATMA AKIŞI (MODAL)
      ================================= */}

      {chargingConnector && (
        <ChargingFlowModal
          connector={chargingConnector}
          onClose={() => setChargingConnector(null)}
          onSuccess={onLocationRefresh}
        />
      )}
    </div>
  );
}

export default LocationDetailPanel;