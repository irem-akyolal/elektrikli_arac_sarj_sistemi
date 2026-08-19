import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../../context/AuthContext";
import {
  getAdminLocations,
  activateLocation,
  deactivateLocation,
} from "../../../api/adminLocationApi";

function LocationsPage() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [locations, setLocations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [name, setName] = useState("");
  const [city, setCity] = useState("");
  const [active, setActive] = useState("");

  const [page, setPage] = useState(0);

  const fetchLocations = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await getAdminLocations({
        name: name || undefined,
        city: city || undefined,
        active: active === "" ? undefined : active === "true",
        page,
        size: 20,
        sort: "createdAt,desc",
      });

      setLocations(response.data);
    } catch (err) {
      console.error("İstasyonlar alınamadı:", err);
      setError("İstasyonlar yüklenirken bir hata oluştu.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLocations();
  }, [page]);

  const handleSearch = (e) => {
    e.preventDefault();

    if (page === 0) {
      fetchLocations();
    } else {
      setPage(0);
    }
  };

  const handleClearFilters = async () => {
    setName("");
    setCity("");
    setActive("");

    if (page === 0) {
      try {
        setLoading(true);
        setError("");

        const response = await getAdminLocations({
          page: 0,
          size: 20,
          sort: "createdAt,desc",
        });

        setLocations(response.data);
      } catch (err) {
        console.error("İstasyonlar alınamadı:", err);
        setError("İstasyonlar yüklenirken bir hata oluştu.");
      } finally {
        setLoading(false);
      }
    } else {
      setPage(0);
    }
  };

  const handleActivate = async (id) => {
    try {
      await activateLocation(id);
      await fetchLocations();
    } catch (err) {
      console.error("İstasyon aktifleştirilemedi:", err);
      alert("İstasyon aktifleştirilemedi.");
    }
  };

  const handleDeactivate = async (id) => {
    try {
      await deactivateLocation(id);
      await fetchLocations();
    } catch (err) {
      console.error("İstasyon pasifleştirilemedi:", err);

      const message =
        err.response?.data?.message ||
        "İstasyon pasifleştirilemedi.";

      alert(message);
    }
  };

  const canEdit =
    user?.role === "SUPER_ADMIN" ||
    user?.role === "OPERATOR";

  const canChangeStatus =
    user?.role === "SUPER_ADMIN";

  if (loading) {
    return (
      <div>
        <h1 className="text-2xl font-bold">
          İstasyon Yönetimi
        </h1>

        <p className="mt-4 text-gray-500">
          İstasyonlar yükleniyor...
        </p>
      </div>
    );
  }

  if (error) {
    return (
      <div>
        <h1 className="text-2xl font-bold">
          İstasyon Yönetimi
        </h1>

        <p className="mt-4 text-red-500">
          {error}
        </p>
      </div>
    );
  }

  return (
    <div>

      {/* HEADER */}

      <div className="flex items-center justify-between mb-6">

        <div>
          <h1 className="text-3xl font-bold text-gray-900">
            İstasyon Yönetimi
          </h1>

          <p className="text-gray-500 mt-1">
            Şarj istasyonlarını görüntüleyin ve yönetin.
          </p>
        </div>

      </div>


      {/* FILTERS */}

      <form
        onSubmit={handleSearch}
        className="
          bg-white
          border
          rounded-xl
          p-5
          mb-6
          flex
          flex-col
          md:flex-row
          gap-3
        "
      >

        <input
          type="text"
          placeholder="İstasyon adı"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="
            border
            rounded-lg
            px-3
            py-2
            flex-1
          "
        />

        <input
          type="text"
          placeholder="Şehir"
          value={city}
          onChange={(e) => setCity(e.target.value)}
          className="
            border
            rounded-lg
            px-3
            py-2
            flex-1
          "
        />

        <select
          value={active}
          onChange={(e) => setActive(e.target.value)}
          className="
            border
            rounded-lg
            px-3
            py-2
          "
        >
          <option value="">
            Tüm durumlar
          </option>

          <option value="true">
            Aktif
          </option>

          <option value="false">
            Pasif
          </option>
        </select>

        <button
          type="submit"
          className="
            px-5
            py-2
            bg-gray-900
            text-white
            rounded-lg
            hover:bg-gray-800
          "
        >
          Filtrele
        </button>

        <button
          type="button"
          onClick={handleClearFilters}
          className="
            px-5
            py-2
            border
            rounded-lg
            hover:bg-gray-100
          "
        >
          Temizle
        </button>

      </form>


      {/* TABLE */}

      <div className="bg-white border rounded-xl overflow-hidden">

        <table className="w-full">

          <thead className="bg-gray-50 border-b">

            <tr>

              <th className="text-left px-5 py-4 text-sm font-semibold">
                İstasyon
              </th>

              <th className="text-left px-5 py-4 text-sm font-semibold">
                Şehir
              </th>

              <th className="text-left px-5 py-4 text-sm font-semibold">
                Durum
              </th>

              <th className="text-right px-5 py-4 text-sm font-semibold">
                İşlemler
              </th>

            </tr>

          </thead>

          <tbody>

            {locations.content?.length === 0 ? (

              <tr>
                <td
                  colSpan="4"
                  className="text-center py-10 text-gray-500"
                >
                  İstasyon bulunamadı.
                </td>
              </tr>

            ) : (

              locations.content?.map((location) => (

                <tr
                  key={location.id}
                  className="border-b last:border-b-0 hover:bg-gray-50"
                >

                  <td className="px-5 py-4">

                    <p className="font-medium text-gray-900">
                      {location.name}
                    </p>

                    <p className="text-sm text-gray-500">
                      {location.address}
                    </p>

                  </td>

                  <td className="px-5 py-4 text-gray-600">
                    {location.city}
                  </td>

                  <td className="px-5 py-4">

                    <span
                      className={`px-3 py-1 rounded-full text-xs font-medium ${
                        location.active
                          ? "bg-green-100 text-green-700"
                          : "bg-gray-100 text-gray-600"
                      }`}
                    >
                      {location.active
                        ? "Aktif"
                        : "Pasif"}
                    </span>

                  </td>

                  <td className="px-5 py-4">

                    <div className="flex justify-end gap-2">

                      <button
                        type="button"
                        onClick={() =>
                          navigate(`/admin/locations/${location.id}`)
                        }
                        className="
                          px-3
                          py-1.5
                          border
                          rounded-lg
                          text-sm
                          hover:bg-gray-100
                        "
                      >
                        Detay
                      </button>


                      {canEdit && (
                        <button
                          type="button"
                          onClick={() =>
                            navigate(`/admin/locations/${location.id}/edit`)
                          }
                          className="
                            px-3
                            py-1.5
                            border
                            rounded-lg
                            text-sm
                            hover:bg-gray-100
                          "
                        >
                          Düzenle
                        </button>
                      )}


                      {canChangeStatus &&
                        location.active && (
                          <button
                            type="button"
                            onClick={() =>
                              handleDeactivate(location.id)
                            }
                            className="
                              px-3
                              py-1.5
                              bg-red-600
                              text-white
                              rounded-lg
                              text-sm
                              hover:bg-red-700
                            "
                          >
                            Pasifleştir
                          </button>
                        )}


                      {canChangeStatus &&
                        !location.active && (
                          <button
                            type="button"
                            onClick={() =>
                              handleActivate(location.id)
                            }
                            className="
                              px-3
                              py-1.5
                              bg-green-600
                              text-white
                              rounded-lg
                              text-sm
                              hover:bg-green-700
                            "
                          >
                            Aktifleştir
                          </button>
                        )}

                    </div>

                  </td>

                </tr>

              ))

            )}

          </tbody>

        </table>

      </div>


      {/* PAGINATION */}

      <div className="flex justify-between items-center mt-5">

        <p className="text-sm text-gray-500">
          Toplam {locations.totalElements ?? 0} istasyon
        </p>

        <div className="flex gap-2">

          <button
            disabled={locations.first}
            onClick={() => setPage((p) => p - 1)}
            className="px-4 py-2 border rounded-lg disabled:opacity-40"
          >
            Önceki
          </button>

          <button
            disabled={locations.last}
            onClick={() => setPage((p) => p + 1)}
            className="px-4 py-2 border rounded-lg disabled:opacity-40"
          >
            Sonraki
          </button>

        </div>

      </div>

    </div>
  );
}

export default LocationsPage;