import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  getAdminLocationById,
  updateLocation,
} from "../../../api/adminLocationApi";

function LocationEdit() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const [form, setForm] = useState({
    ocpiLocationId: "",
    name: "",
    address: "",
    city: "",
    postalCode: "",
    country: "",
    latitude: "",
    longitude: "",
    timeZone: "",
  });

  useEffect(() => {
    const fetchLocation = async () => {
      try {
        setLoading(true);
        setError("");

        const response = await getAdminLocationById(id);

        const location = response.data;

        setForm({
          ocpiLocationId: location.ocpiLocationId || "", 
          name: location.name || "",
          address: location.address || "",
          city: location.city || "",
          postalCode: location.postalCode || "",
          country: location.country || "",
          latitude: location.latitude ?? "",
          longitude: location.longitude ?? "",
          timeZone: location.timeZone || "",
        });
      } catch (err) {
        console.error("İstasyon bilgileri alınamadı:", err);

        setError(
          err.response?.data?.message ||
            "İstasyon bilgileri alınırken bir hata oluştu."
        );
      } finally {
        setLoading(false);
      }
    };

    fetchLocation();
  }, [id]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      setSaving(true);
      setError("");

      const data = {
        name: form.name,
        address: form.address,
        city: form.city,
        postalCode: form.postalCode,
        country: form.country,
        latitude:
          form.latitude === "" ? null : Number(form.latitude),
        longitude:
          form.longitude === "" ? null : Number(form.longitude),
        timeZone: form.timeZone,
      };

      await updateLocation(id, data);

      navigate(`/admin/locations/${id}`);
    } catch (err) {
      console.error("İstasyon güncellenemedi:", err);

      setError(
        err.response?.data?.message ||
          "İstasyon güncellenirken bir hata oluştu."
      );
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div>
        <h1 className="text-2xl font-bold text-gray-900">
          İstasyon Düzenle
        </h1>

        <p className="mt-4 text-gray-500">
          İstasyon bilgileri yükleniyor...
        </p>
      </div>
    );
  }

  return (
    <div className="max-w-4xl">

      {/* HEADER */}

      <div className="mb-6">

        <button
          type="button"
          onClick={() => navigate(`/admin/locations/${id}`)}
          className="text-sm text-gray-500 hover:text-gray-900 mb-2"
        >
          ← İstasyon Detayına Dön
        </button>

        <h1 className="text-3xl font-bold text-gray-900">
          İstasyon Düzenle
        </h1>

        <p className="text-gray-500 mt-1">
          İstasyon bilgilerini güncelleyin.
        </p>

      </div>


      {/* ERROR */}

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg text-red-700">
          {error}
        </div>
      )}


      {/* FORM */}

      <form
        onSubmit={handleSubmit}
        className="bg-white border rounded-xl p-6 space-y-6"
      >

        {/* OCPI ID */}

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            OCPI Location ID
          </label>

          <input
            type="text"
            value={form.ocpiLocationId}
            disabled
            className="w-full border rounded-lg px-3 py-2 bg-gray-100 text-gray-500"
          />

          <p className="text-xs text-gray-500 mt-1">
            OCPI Location ID değiştirilemez.
          </p>
        </div>


        {/* NAME */}

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            İstasyon Adı
          </label>

          <input
            type="text"
            name="name"
            value={form.name}
            onChange={handleChange}
            required
            className="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>


        {/* ADDRESS */}

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Adres
          </label>

          <textarea
            name="address"
            value={form.address}
            onChange={handleChange}
            required
            rows="3"
            className="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>


        {/* CITY / POSTAL CODE */}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Şehir
            </label>

            <input
              type="text"
              name="city"
              value={form.city}
              onChange={handleChange}
              className="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>


          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Posta Kodu
            </label>

            <input
              type="text"
              name="postalCode"
              value={form.postalCode}
              onChange={handleChange}
              className="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

        </div>


        {/* COUNTRY / TIMEZONE */}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Ülke
            </label>

            <input
              type="text"
              name="country"
              value={form.country}
              onChange={handleChange}
              className="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>


          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Zaman Dilimi
            </label>

            <input
              type="text"
              name="timeZone"
              value={form.timeZone}
              onChange={handleChange}
              placeholder="Europe/Istanbul"
              className="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

        </div>


        {/* COORDINATES */}

        <div>

          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            Konum Bilgileri
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Enlem
              </label>

              <input
                type="number"
                step="any"
                name="latitude"
                value={form.latitude}
                onChange={handleChange}
                className="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>


            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Boylam
              </label>

              <input
                type="number"
                step="any"
                name="longitude"
                value={form.longitude}
                onChange={handleChange}
                className="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

          </div>

        </div>


        {/* BUTTONS */}

        <div className="flex justify-end gap-3 pt-4 border-t">

          <button
            type="button"
            onClick={() => navigate(`/admin/locations/${id}`)}
            disabled={saving}
            className="px-5 py-2 border rounded-lg hover:bg-gray-100 disabled:opacity-50"
          >
            İptal
          </button>

          <button
            type="submit"
            disabled={saving}
            className="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
          >
            {saving
              ? "Kaydediliyor..."
              : "Değişiklikleri Kaydet"}
          </button>

        </div>

      </form>

    </div>
  );
}

export default LocationEdit;