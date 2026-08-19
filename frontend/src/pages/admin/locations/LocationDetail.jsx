import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getAdminLocationDetail } from "../../../api/adminLocationApi";

function LocationDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [location, setLocation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchLocation = async () => {
      try {
        setLoading(true);
        setError("");

        const response = await getAdminLocationDetail(id);

        console.log("İstasyon detay:", response.data);

        setLocation(response.data);
      } catch (err) {
        console.error("İstasyon detayı alınamadı:", err);

        setError(
          err.response?.data?.message ||
            "İstasyon detayı alınırken bir hata oluştu."
        );
      } finally {
        setLoading(false);
      }
    };

    fetchLocation();
  }, [id]);

  if (loading) {
    return (
      <div>
        <h1 className="text-2xl font-bold text-gray-900">
          İstasyon Detayı
        </h1>

        <p className="mt-4 text-gray-500">
          İstasyon bilgileri yükleniyor...
        </p>
      </div>
    );
  }

  if (error) {
    return (
      <div>
        <h1 className="text-2xl font-bold text-gray-900">
          İstasyon Detayı
        </h1>

        <p className="mt-4 text-red-500">
          {error}
        </p>

        <button
          onClick={() => navigate("/admin/locations")}
          className="mt-4 px-4 py-2 bg-gray-900 text-white rounded-lg hover:bg-gray-800"
        >
          İstasyonlara Dön
        </button>
      </div>
    );
  }

  if (!location) {
    return null;
  }

  const evses = location.evses ?? [];

  return (
    <div className="space-y-6">

      {/* HEADER */}

      <div className="flex items-center justify-between">

        <div>
          <button
            onClick={() => navigate("/admin/locations")}
            className="text-sm text-gray-500 hover:text-gray-900 mb-2"
          >
            ← İstasyonlara Dön
          </button>

          <h1 className="text-3xl font-bold text-gray-900">
            {location.name}
          </h1>

          <p className="text-gray-500 mt-1">
            İstasyon detayları ve bağlı şarj noktaları
          </p>
        </div>

        <span
          className={`px-4 py-2 rounded-full text-sm font-medium ${
            location.active
              ? "bg-green-100 text-green-700"
              : "bg-gray-100 text-gray-600"
          }`}
        >
          {location.active ? "Aktif" : "Pasif"}
        </span>

      </div>


      {/* LOCATION INFORMATION */}

      <div className="bg-white border rounded-xl p-6">

        <h2 className="text-xl font-semibold text-gray-900 mb-5">
          İstasyon Bilgileri
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">

          <div>
            <p className="text-sm text-gray-500">
              İstasyon Adı
            </p>

            <p className="font-medium text-gray-900 mt-1">
              {location.name || "-"}
            </p>
          </div>


          <div>
            <p className="text-sm text-gray-500">
              OCPI Location ID
            </p>

            <p className="font-medium text-gray-900 mt-1">
              {location.ocpiLocationId || "-"}
            </p>
          </div>


          <div>
            <p className="text-sm text-gray-500">
              Adres
            </p>

            <p className="font-medium text-gray-900 mt-1">
              {location.address || "-"}
            </p>
          </div>


          <div>
            <p className="text-sm text-gray-500">
              Şehir
            </p>

            <p className="font-medium text-gray-900 mt-1">
              {location.city || "-"}
            </p>
          </div>


          <div>
            <p className="text-sm text-gray-500">
              Posta Kodu
            </p>

            <p className="font-medium text-gray-900 mt-1">
              {location.postalCode || "-"}
            </p>
          </div>


          <div>
            <p className="text-sm text-gray-500">
              Ülke
            </p>

            <p className="font-medium text-gray-900 mt-1">
              {location.country || "-"}
            </p>
          </div>


          <div>
            <p className="text-sm text-gray-500">
              Enlem
            </p>

            <p className="font-medium text-gray-900 mt-1">
              {location.latitude ?? "-"}
            </p>
          </div>


          <div>
            <p className="text-sm text-gray-500">
              Boylam
            </p>

            <p className="font-medium text-gray-900 mt-1">
              {location.longitude ?? "-"}
            </p>
          </div>


          <div>
            <p className="text-sm text-gray-500">
              Zaman Dilimi
            </p>

            <p className="font-medium text-gray-900 mt-1">
              {location.timeZone || "-"}
            </p>
          </div>

        </div>

      </div>


      {/* EVSE / CONNECTORS */}

      <div className="bg-white border rounded-xl overflow-hidden">

        <div className="p-6 border-b">

          <div className="flex items-center justify-between">

            <div>
              <h2 className="text-xl font-semibold text-gray-900">
                EVSE ve Connector'lar
              </h2>

              <p className="text-sm text-gray-500 mt-1">
                Bu istasyona bağlı EVSE ve connector bilgileri
              </p>
            </div>

            <span className="px-3 py-1 bg-gray-100 rounded-full text-sm text-gray-600">
              {evses.length} EVSE
            </span>

          </div>

        </div>


        {evses.length === 0 ? (

          <div className="p-10 text-center text-gray-500">
            Bu istasyona bağlı EVSE bulunmuyor.
          </div>

        ) : (

          <div className="divide-y">

            {evses.map((evse) => {

              const connectors = evse.connectors ?? [];

              return (
                <div
                  key={evse.id}
                  className="p-6"
                >

                  {/* EVSE HEADER */}

                  <div className="flex items-center justify-between mb-4">

                    <div>

                      <h3 className="font-semibold text-gray-900">
                        EVSE {evse.evseId || "-"}
                      </h3>

                      <p className="text-xs text-gray-500 mt-1">
                        UID: {evse.ocpiEvseUid || "-"}
                      </p>

                    </div>

                    <span
                      className={`px-3 py-1 rounded-full text-xs font-medium ${
                        evse.status === "AVAILABLE"
                          ? "bg-green-100 text-green-700"
                          : evse.status === "CHARGING"
                          ? "bg-blue-100 text-blue-700"
                          : "bg-gray-100 text-gray-600"
                      }`}
                    >
                      {evse.status || "Bilinmiyor"}
                    </span>

                  </div>


                  {/* CONNECTORS */}

                  {connectors.length === 0 ? (

                    <div className="p-5 bg-gray-50 rounded-lg text-sm text-gray-500">
                      Bu EVSE'ye bağlı connector bulunmuyor.
                    </div>

                  ) : (

                    <div className="overflow-x-auto">

                      <table className="w-full">

                        <thead className="bg-gray-50 border-b">

                          <tr>

                            <th className="text-left px-4 py-3 text-sm font-semibold">
                              Connector
                            </th>

                            <th className="text-left px-4 py-3 text-sm font-semibold">
                              Standart
                            </th>

                            <th className="text-left px-4 py-3 text-sm font-semibold">
                              Format
                            </th>

                            <th className="text-left px-4 py-3 text-sm font-semibold">
                              Güç Tipi
                            </th>

                            <th className="text-left px-4 py-3 text-sm font-semibold">
                              Maks. Güç
                            </th>

                            <th className="text-left px-4 py-3 text-sm font-semibold">
                              Birim Fiyat
                            </th>

                            <th className="text-left px-4 py-3 text-sm font-semibold">
                              Durum
                            </th>

                          </tr>

                        </thead>


                        <tbody>

                          {connectors.map((connector) => (

                            <tr
                              key={connector.id}
                              className="border-b last:border-b-0 hover:bg-gray-50"
                            >

                              <td className="px-4 py-4">

                                <p className="font-medium text-gray-900">
                                  {connector.connectorId || "-"}
                                </p>

                                <p className="text-xs text-gray-500 mt-1">
                                  ID: {connector.id}
                                </p>

                              </td>


                              <td className="px-4 py-4 text-gray-700">
                                {connector.standard || "-"}
                              </td>


                              <td className="px-4 py-4 text-gray-700">
                                {connector.format || "-"}
                              </td>


                              <td className="px-4 py-4 text-gray-700">
                                {connector.powerType || "-"}
                              </td>


                              <td className="px-4 py-4 text-gray-700">
                                {connector.maxElectricPowerWatt != null
                                  ? `${connector.maxElectricPowerWatt} W`
                                  : "-"}
                              </td>


                              <td className="px-4 py-4 text-gray-700">
                                {connector.unitPrice != null
                                  ? `${connector.unitPrice} ₺ / kWh`
                                  : "-"}
                              </td>


                              <td className="px-4 py-4">

                                <div className="flex flex-col gap-1">

                                  <span
                                    className={`w-fit px-3 py-1 rounded-full text-xs font-medium ${
                                      connector.charging
                                        ? "bg-blue-100 text-blue-700"
                                        : connector.status === "AVAILABLE"
                                        ? "bg-green-100 text-green-700"
                                        : "bg-gray-100 text-gray-600"
                                    }`}
                                  >
                                    {connector.charging
                                      ? "Şarj Ediyor"
                                      : connector.status || "Bilinmiyor"}
                                  </span>

                                </div>

                              </td>

                            </tr>

                          ))}

                        </tbody>

                      </table>

                    </div>

                  )}

                </div>
              );
            })}

          </div>

        )}

      </div>

    </div>
  );
}

export default LocationDetail;