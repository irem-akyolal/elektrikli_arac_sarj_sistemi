
import { useEffect, useState } from "react";

import {
  getActiveLocations,
  getLocationDetail,
} from "../../api/locationApi";

import GoogleMap from "../../components/map/GoogleMap";
import LocationDetailPanel from "../../components/location/LocationDetailPanel";

function Home() {
  const [locations, setLocations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [view, setView] = useState("map");

  const [selectedLocation, setSelectedLocation] = useState(null);
  const [locationDetail, setLocationDetail] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);

  // =====================================================
  // AKTİF İSTASYONLARI GETİR
  // =====================================================

  useEffect(() => {
    const fetchLocations = async () => {
      try {
        const response = await getActiveLocations();

        setLocations(response.data);
      } catch (err) {
        console.error("İstasyonlar alınamadı:", err);

        setError(
          "İstasyonlar yüklenirken bir hata oluştu."
        );
      } finally {
        setLoading(false);
      }
    };

    fetchLocations();
  }, []);

  // =====================================================
  // İSTASYON SEÇ
  // =====================================================

  const handleSelectLocation = async (location) => {
    if (!location?.id) {
      return;
    }

    // Haritada seçili istasyonu göster
    setSelectedLocation(location);

    // Önce eski detay bilgisini temizle
    setLocationDetail(null);

    // Loading başlat
    setDetailLoading(true);

    try {
      const response = await getLocationDetail(location.id);

      setLocationDetail(response.data);
    } catch (err) {
      console.error(
        "İstasyon detayı alınamadı:",
        err
      );

      setError(
        "İstasyon detayları yüklenirken bir hata oluştu."
      );
    } finally {
      setDetailLoading(false);
    }
  };

  // =====================================================
  // DETAY PANELİNİ KAPAT
  // =====================================================

  const handleCloseDetail = () => {
    setLocationDetail(null);
    setSelectedLocation(null);
  };

  // =====================================================
  // ARAMA
  // =====================================================

  const filteredLocations = locations.filter(
    (location) => {
      const searchText = search
        .toLowerCase()
        .trim();

      if (!searchText) {
        return true;
      }

      return (
        location.name
          ?.toLowerCase()
          .includes(searchText) ||

        location.city
          ?.toLowerCase()
          .includes(searchText) ||

        location.address
          ?.toLowerCase()
          .includes(searchText)
      );
    }
  );

  // =====================================================
  // LOADING
  // =====================================================

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-500">
          İstasyonlar yükleniyor...
        </p>
      </div>
    );
  }

  // =====================================================
  // ERROR
  // =====================================================

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-red-500">
          {error}
        </p>
      </div>
    );
  }

  // =====================================================
  // PAGE
  // =====================================================

  return (
    <div className="min-h-screen bg-gray-50">

      {/* =================================================
          HEADER
      ================================================= */}

      <header className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">

          <div>
            <h1 className="text-xl font-bold text-gray-900">
              ⚡ EV Charge
            </h1>

            <p className="text-xs text-gray-500">
              Elektrikli araç şarj istasyonları
            </p>
          </div>

          <a
            href="/login"
            className="
              text-sm
              font-medium
              text-gray-600
              hover:text-gray-900
            "
          >
            Yönetici Girişi
          </a>

        </div>
      </header>


      {/* =================================================
          MAIN
      ================================================= */}

      <main className="max-w-7xl mx-auto px-6 py-8">

        {/* TITLE */}

        <div className="mb-6">

          <h2 className="text-3xl font-bold text-gray-900">
            Şarj İstasyonu Bul
          </h2>

          <p className="mt-2 text-gray-500">
            Size en uygun şarj istasyonunu bulun.
          </p>

        </div>


        {/* =================================================
            SEARCH + LOCATION
        ================================================= */}

        <div className="flex flex-col md:flex-row gap-3 mb-6">

          <div className="relative flex-1">

            <span className="absolute left-4 top-1/2 -translate-y-1/2">
              🔍
            </span>

            <input
              type="text"
              placeholder="İstasyon, şehir veya adres ara..."
              value={search}
              onChange={(e) =>
                setSearch(e.target.value)
              }
              className="
                w-full
                bg-white
                border
                border-gray-200
                rounded-xl
                py-3
                pl-11
                pr-4
                outline-none
                focus:ring-2
                focus:ring-blue-500
              "
            />

          </div>


          <button
            className="
              px-5
              py-3
              rounded-xl
              bg-blue-600
              text-white
              font-medium
              hover:bg-blue-700
              transition
            "
          >
            📍 Konumumu Kullan
          </button>

        </div>


        {/* =================================================
            VIEW SWITCH
        ================================================= */}

        <div className="flex justify-end mb-4">

          <div className="bg-white border rounded-lg p-1 flex">

            <button
              onClick={() => setView("map")}
              className={`
                px-4
                py-2
                rounded-md
                text-sm
                font-medium

                ${
                  view === "map"
                    ? "bg-gray-900 text-white"
                    : "text-gray-600 hover:bg-gray-100"
                }
              `}
            >
              Harita
            </button>


            <button
              onClick={() => setView("list")}
              className={`
                px-4
                py-2
                rounded-md
                text-sm
                font-medium

                ${
                  view === "list"
                    ? "bg-gray-900 text-white"
                    : "text-gray-600 hover:bg-gray-100"
                }
              `}
            >
              Liste
            </button>

          </div>

        </div>


        {/* =================================================
            MAP VIEW
        ================================================= */}

        {view === "map" ? (

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">

            {/* =================================================
                GOOGLE MAP
            ================================================= */}

            <div
              className="
                lg:col-span-2
                h-[600px]
                rounded-2xl
                border
                overflow-hidden
                relative
                bg-gray-200
              "
            >

              <GoogleMap
                locations={filteredLocations}
                selectedLocation={selectedLocation}
                onSelectLocation={
                  handleSelectLocation
                }
              />


              {/* =================================================
                  DETAIL LOADING
              ================================================= */}

              {detailLoading && (

                <div
                  className="
                    absolute
                    top-4
                    right-4
                    z-20
                    bg-white
                    rounded-xl
                    shadow-lg
                    px-4
                    py-3
                    text-sm
                    text-gray-600
                  "
                >
                  İstasyon bilgileri yükleniyor...
                </div>

              )}


              {/* =================================================
                  DETAIL PANEL
              ================================================= */}

              {locationDetail &&
                !detailLoading && (

                  <LocationDetailPanel
                    location={locationDetail}
                    onClose={
                      handleCloseDetail
                    }
                  />

                )}

            </div>


            {/* =================================================
                LOCATION LIST
            ================================================= */}

            <div
              className="
                bg-white
                rounded-2xl
                border
                overflow-hidden
                h-[600px]
                overflow-y-auto
              "
            >

              {/* LIST HEADER */}

              <div className="p-5 border-b">

                <h3 className="font-bold text-lg">
                  İstasyonlar
                </h3>

                <p className="text-sm text-gray-500 mt-1">
                  {filteredLocations.length} istasyon bulundu
                </p>

              </div>


              {/* LIST */}

              <div>

                {filteredLocations.length === 0 ? (

                  <div className="p-6 text-center text-gray-500">
                    İstasyon bulunamadı.
                  </div>

                ) : (

                  filteredLocations.map(
                    (location) => (

                      <div
                        key={location.id}
                        onClick={() =>
                          handleSelectLocation(
                            location
                          )
                        }
                        className={`
                          p-5
                          border-b
                          cursor-pointer
                          transition
                          hover:bg-gray-50

                          ${
                            selectedLocation?.id ===
                            location.id
                              ? "bg-blue-50 border-l-4 border-l-blue-600"
                              : ""
                          }
                        `}
                      >

                        <h4 className="font-semibold text-gray-900">
                          {location.name}
                        </h4>


                        <p className="text-sm text-gray-500 mt-1">
                          {location.address}
                        </p>


                        <p className="text-sm text-gray-500">
                          {location.city}
                        </p>


                        {/* AVAILABILITY */}

                        {location.availability?.length > 0 && (

                          <div className="mt-4 space-y-2">

                            {location.availability.map(
                              (item, index) => (

                                <div
                                  key={index}
                                  className="
                                    flex
                                    items-center
                                    justify-between
                                    gap-2
                                    text-sm
                                  "
                                >

                                  <span className="font-medium">
                                    {item.powerType}
                                  </span>


                                  <span className="text-gray-500">
                                    {item.availableCount}/
                                    {item.totalCount}
                                    {" "}müsait
                                  </span>


                                  <span className="font-medium">
                                    {item.unitPrice}
                                    {" "}TL/kWh
                                  </span>

                                </div>

                              )
                            )}

                          </div>

                        )}

                      </div>

                    )
                  )

                )}

              </div>

            </div>

          </div>

        ) : (

          /* =================================================
              LIST VIEW
          ================================================= */

          <div className="bg-white rounded-2xl border overflow-hidden">

            {filteredLocations.length === 0 ? (

              <div className="p-8 text-center text-gray-500">
                İstasyon bulunamadı.
              </div>

            ) : (

              filteredLocations.map(
                (location) => (

                  <div
                    key={location.id}
                    onClick={() =>
                      handleSelectLocation(
                        location
                      )
                    }
                    className="
                      p-6
                      border-b
                      hover:bg-gray-50
                      cursor-pointer
                      transition
                    "
                  >

                    <h3 className="text-lg font-semibold">
                      {location.name}
                    </h3>


                    <p className="text-gray-500 mt-1">
                      {location.address},{" "}
                      {location.city}
                    </p>


                    {location.availability?.length > 0 && (

                      <div className="mt-4 space-y-2">

                        {location.availability.map(
                          (item, index) => (

                            <div
                              key={index}
                              className="
                                flex
                                gap-6
                                text-sm
                              "
                            >

                              <span>
                                {item.powerType}
                              </span>


                              <span>
                                {item.availableCount}/
                                {item.totalCount}
                                {" "}müsait
                              </span>


                              <span>
                                {item.unitPrice}
                                {" "}TL/kWh
                              </span>

                            </div>

                          )
                        )}

                      </div>

                    )}

                  </div>

                )
              )

            )}

          </div>

        )}

      </main>

    </div>
  );
}

export default Home;

