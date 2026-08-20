import { useEffect, useState } from "react";

import {
  getActiveLocations,
  getLocationDetail,
  getNearbyLocations,
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

  const [locationLoading, setLocationLoading] = useState(false);
  const [userLocation, setUserLocation] = useState(null);

  // =====================================================
  // AKTİF İSTASYONLARI GETİR
  // =====================================================

  const fetchLocations = async () => {
    try {
      const response = await getActiveLocations();
      setLocations(response.data);
    } catch (err) {
      console.error("İstasyonlar alınamadı:", err);
      setError("İstasyonlar yüklenirken bir hata oluştu.");
    } finally {
      setLoading(false);
    }
  };

  // =====================================================
  // KULLANICI KONUMU
  // =====================================================

  const handleUseMyLocation = () => {
    if (!navigator.geolocation) {
      setError("Tarayıcınız konum bilgisini desteklemiyor.");
      return;
    }

    setLocationLoading(true);
    setError("");

    navigator.geolocation.getCurrentPosition(
      async (position) => {
        const { latitude, longitude } = position.coords;

        setUserLocation({
          latitude,
          longitude,
        });

        try {
          const response = await getNearbyLocations(
            latitude,
            longitude,
            10
          );

          setLocations(response.data);
        } catch (err) {
          console.error(
            "Yakındaki istasyonlar alınamadı:",
            err
          );

          setError(
            "Konumunuza yakın istasyonlar alınırken bir hata oluştu."
          );
        } finally {
          setLocationLoading(false);
        }
      },
      (error) => {
        console.error("Konum alınamadı:", error);

        setLocationLoading(false);

        if (error.code === error.PERMISSION_DENIED) {
          setError(
            "Konum izni verilmedi. Lütfen tarayıcıdan konum iznini açın."
          );
        } else if (error.code === error.POSITION_UNAVAILABLE) {
          setError("Konum bilgisi kullanılamıyor.");
        } else if (error.code === error.TIMEOUT) {
          setError("Konum alınırken zaman aşımı oluştu.");
        } else {
          setError("Konumunuz alınamadı.");
        }
      }
    );
  };

  // =====================================================
  // TÜM İSTASYONLARA GERİ DÖN
  // =====================================================

  const handleShowAllLocations = async () => {
    setUserLocation(null);
    setSelectedLocation(null);
    setLocationDetail(null);
    setSearch("");

    setLoading(true);

    await fetchLocations();
  };

  useEffect(() => {
    fetchLocations();
  }, []);

  // =====================================================
  // APPLE MAPS YOL TARİFİ
  // =====================================================

  const handleAppleMapsDirections = (location) => {
    if (!location?.latitude || !location?.longitude) {
      return;
    }

    const latitude = location.latitude;
    const longitude = location.longitude;

    const appleMapsUrl = `https://maps.apple.com/?daddr=${latitude},${longitude}`;

    window.open(
      appleMapsUrl,
      "_blank",
      "noopener,noreferrer"
    );
  };

  // =====================================================
  // İSTASYON SEÇ
  // =====================================================

  const handleSelectLocation = async (location) => {
    if (!location?.id) {
      return;
    }

    setSelectedLocation(location);
    setLocationDetail(null);
    setDetailLoading(true);

    try {
      const response = await getLocationDetail(location.id);
      setLocationDetail(response.data);
    } catch (err) {
      console.error("İstasyon detayı alınamadı:", err);
      setError("İstasyon detayları yüklenirken bir hata oluştu.");
    } finally {
      setDetailLoading(false);
    }
  };

  // =====================================================
  // DETAY PANELİNİ VE LİSTEYİ TAZELE
  // =====================================================

  const refreshAfterChargingStart = async () => {
    if (!selectedLocation?.id) return;

    try {
      const response = await getLocationDetail(selectedLocation.id);
      setLocationDetail(response.data);
    } catch (err) {
      console.error("İstasyon detayı yenilenemedi:", err);
    }

    fetchLocations();
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

  const filteredLocations = locations.filter((location) => {
    const searchText = search.toLowerCase().trim();

    if (!searchText) {
      return true;
    }

    return (
      location.name?.toLowerCase().includes(searchText) ||
      location.city?.toLowerCase().includes(searchText) ||
      location.address?.toLowerCase().includes(searchText)
    );
  });

  // =====================================================
  // BASİT İSTATİSTİKLER
  // =====================================================

  const totalLocations = locations.length;

  const totalAvailableConnectors = locations.reduce(
    (total, location) => {
      if (!location.availability) {
        return total;
      }

      return (
        total +
        location.availability.reduce(
          (sum, item) => sum + (item.availableCount || 0),
          0
        )
      );
    },
    0
  );

  // =====================================================
  // LOADING
  // =====================================================

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="text-4xl mb-3">⚡</div>

          <p className="text-gray-500">
            İstasyonlar yükleniyor...
          </p>
        </div>
      </div>
    );
  }

  // =====================================================
  // ERROR
  // =====================================================

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="bg-white border rounded-2xl shadow-sm p-8 text-center max-w-md">
          <div className="text-4xl mb-3">⚠️</div>

          <p className="text-red-500">
            {error}
          </p>
        </div>
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

      <header className="bg-white border-b sticky top-0 z-30">

        <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">

          {/* LOGO */}

          <div className="flex items-center gap-3">

            <div
              className="
                w-10
                h-10
                rounded-xl
                bg-blue-600
                text-white
                flex
                items-center
                justify-center
                text-xl
                shadow-sm
              "
            >
              ⚡
            </div>

            <div>
              <h1 className="text-xl font-bold text-gray-900">
                EV Charge
              </h1>

              <p className="text-xs text-gray-500">
                Elektrikli araç şarj istasyonları
              </p>
            </div>

          </div>

          {/* ADMIN LOGIN */}

          <a
            href="/login"
            className="
              flex
              items-center
              gap-2
              px-4
              py-2
              border
              border-gray-200
              rounded-xl
              text-sm
              font-medium
              text-gray-700
              bg-white
              hover:bg-gray-50
              hover:border-blue-300
              hover:text-blue-600
              transition
              shadow-sm
            "
          >
            <span
              className="
                w-7
                h-7
                rounded-lg
                bg-gray-100
                flex
                items-center
                justify-center
                text-sm
              "
            >
              ⚙️
            </span>

            <span>
              Yönetici Paneli
            </span>
          </a>

        </div>

      </header>


      {/* =================================================
          MAIN
      ================================================= */}

      <main className="max-w-7xl mx-auto px-6 py-8">

        {/* =================================================
            HERO / TITLE
        ================================================= */}

        <div className="mb-7">

          <div className="flex flex-col md:flex-row md:items-end md:justify-between gap-4">

            <div>

              <div
                className="
                  inline-flex
                  items-center
                  gap-2
                  px-3
                  py-1
                  rounded-full
                  bg-blue-50
                  text-blue-700
                  text-xs
                  font-medium
                  mb-3
                "
              >
                <span>⚡</span>
                <span>Elektrikli Araç Şarj Ağı</span>
              </div>

              <h2 className="text-3xl md:text-4xl font-bold text-gray-900">
                Şarj İstasyonu Bul
              </h2>

              <p className="mt-2 text-gray-500">
                Size en uygun şarj istasyonunu kolayca bulun.
              </p>

            </div>

          </div>

        </div>


        {/* =================================================
            QUICK STATS
        ================================================= */}

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">

          {/* ACTIVE LOCATIONS */}

          <div
            className="
              bg-white
              border
              border-gray-200
              rounded-2xl
              p-5
              shadow-sm
              hover:shadow-md
              transition
            "
          >

            <div className="flex items-center justify-between">

              <div>

                <p className="text-sm text-gray-500">
                  Aktif İstasyon
                </p>

                <p className="text-2xl font-bold text-gray-900 mt-1">
                  {totalLocations}
                </p>

              </div>

              <div
                className="
                  w-11
                  h-11
                  rounded-xl
                  bg-blue-50
                  text-blue-600
                  flex
                  items-center
                  justify-center
                  text-xl
                "
              >
                📍
              </div>

            </div>

            <p className="text-xs text-gray-400 mt-3">
              Sistemde aktif olarak yayınlanan istasyonlar
            </p>

          </div>


          {/* AVAILABLE CONNECTORS */}

          <div
            className="
              bg-white
              border
              border-gray-200
              rounded-2xl
              p-5
              shadow-sm
              hover:shadow-md
              transition
            "
          >

            <div className="flex items-center justify-between">

              <div>

                <p className="text-sm text-gray-500">
                  Müsait Şarj Noktası
                </p>

                <p className="text-2xl font-bold text-gray-900 mt-1">
                  {totalAvailableConnectors}
                </p>

              </div>

              <div
                className="
                  w-11
                  h-11
                  rounded-xl
                  bg-green-50
                  text-green-600
                  flex
                  items-center
                  justify-center
                  text-xl
                "
              >
                🔋
              </div>

            </div>

            <p className="text-xs text-gray-400 mt-3">
              Şu anda kullanılabilir şarj bağlantıları
            </p>

          </div>


          {/* SEARCH */}

          <div
            className="
              bg-white
              border
              border-gray-200
              rounded-2xl
              p-5
              shadow-sm
              hover:shadow-md
              transition
            "
          >

            <div className="flex items-center justify-between">

              <div>

                <p className="text-sm text-gray-500">
                  Hızlı Arama
                </p>

                <p className="text-lg font-bold text-gray-900 mt-1">
                  İstasyonunu keşfet
                </p>

              </div>

              <div
                className="
                  w-11
                  h-11
                  rounded-xl
                  bg-purple-50
                  text-purple-600
                  flex
                  items-center
                  justify-center
                  text-xl
                "
              >
                🔎
              </div>

            </div>

            <p className="text-xs text-gray-400 mt-3">
              Şehir veya adres bilgisiyle arama yapabilirsiniz
            </p>

          </div>

        </div>


        {/* =================================================
            HOW IT WORKS
        ================================================= */}

        <div
          className="
            bg-white
            border
            border-gray-200
            rounded-2xl
            p-6
            mb-6
            shadow-sm
          "
        >

          <div className="mb-5">

            <h3 className="text-lg font-bold text-gray-900">
              Nasıl Çalışır?
            </h3>

            <p className="text-sm text-gray-500 mt-1">
              Şarj işleminizi birkaç basit adımda başlatın.
            </p>

          </div>


          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">

            {/* STEP 1 */}

            <div className="flex items-start gap-4">

              <div
                className="
                  w-10
                  h-10
                  rounded-xl
                  bg-blue-600
                  text-white
                  flex
                  items-center
                  justify-center
                  font-bold
                  shrink-0
                "
              >
                1
              </div>

              <div>

                <h4 className="font-semibold text-gray-900">
                  İstasyonu Bul
                </h4>

                <p className="text-sm text-gray-500 mt-1">
                  Harita üzerinden veya arama alanını kullanarak
                  size uygun istasyonu bulun.
                </p>

              </div>

            </div>


            {/* STEP 2 */}

            <div className="flex items-start gap-4">

              <div
                className="
                  w-10
                  h-10
                  rounded-xl
                  bg-blue-600
                  text-white
                  flex
                  items-center
                  justify-center
                  font-bold
                  shrink-0
                "
              >
                2
              </div>

              <div>

                <h4 className="font-semibold text-gray-900">
                  Şarj Noktasını Seç
                </h4>

                <p className="text-sm text-gray-500 mt-1">
                  İstasyonu seçerek uygun EVSE ve connector
                  bilgilerini görüntüleyin.
                </p>

              </div>

            </div>


            {/* STEP 3 */}

            <div className="flex items-start gap-4">

              <div
                className="
                  w-10
                  h-10
                  rounded-xl
                  bg-blue-600
                  text-white
                  flex
                  items-center
                  justify-center
                  font-bold
                  shrink-0
                "
              >
                3
              </div>

              <div>

                <h4 className="font-semibold text-gray-900">
                  Şarjı Başlat
                </h4>

                <p className="text-sm text-gray-500 mt-1">
                  Uygun bağlantıyı seçerek şarj işleminizi
                  güvenli şekilde başlatın.
                </p>

              </div>

            </div>

          </div>

        </div>


        {/* =================================================
            SEARCH + LOCATION
        ================================================= */}

        <div className="flex flex-col md:flex-row gap-3 mb-6">

          <div className="relative flex-1">

            <span
              className="
                absolute
                left-4
                top-1/2
                -translate-y-1/2
                text-gray-400
              "
            >
              🔍
            </span>

            <input
              type="text"
              placeholder="İstasyon, şehir veya adres ara..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
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
                focus:border-blue-500
                transition
              "
            />

          </div>


          <button
            onClick={handleUseMyLocation}
            disabled={locationLoading}
            className="
              px-5
              py-3
              rounded-xl
              bg-blue-600
              text-white
              font-medium
              hover:bg-blue-700
              transition
              disabled:opacity-60
              disabled:cursor-not-allowed
              shadow-sm
            "
          >
            {locationLoading
              ? "📍 Konum aranıyor..."
              : "📍 Konumumu Kullan"}
          </button>


          {/* KONUM SONRASI TÜM İSTASYONLARI GÖSTER */}

          {userLocation && (
            <button
              onClick={handleShowAllLocations}
              className="
                px-5
                py-3
                rounded-xl
                bg-white
                border
                border-gray-200
                text-gray-700
                font-medium
                hover:bg-gray-50
                hover:border-gray-300
                transition
                shadow-sm
              "
            >
              🔄 Tüm İstasyonları Göster
            </button>
          )}

        </div>


        {/* =================================================
            VIEW SWITCH
        ================================================= */}

        <div className="flex justify-end mb-4">

          <div className="bg-white border rounded-lg p-1 flex shadow-sm">

            <button
              onClick={() => setView("map")}
              className={`
                px-4
                py-2
                rounded-md
                text-sm
                font-medium
                transition

                ${
                  view === "map"
                    ? "bg-gray-900 text-white"
                    : "text-gray-600 hover:bg-gray-100"
                }
              `}
            >
              🗺️ Harita
            </button>

            <button
              onClick={() => setView("list")}
              className={`
                px-4
                py-2
                rounded-md
                text-sm
                font-medium
                transition

                ${
                  view === "list"
                    ? "bg-gray-900 text-white"
                    : "text-gray-600 hover:bg-gray-100"
                }
              `}
            >
              ☰ Liste
            </button>

          </div>

        </div>


        {/* =================================================
            MAP VIEW
        ================================================= */}

        {view === "map" ? (

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">

            {/* GOOGLE MAP */}

            <div
              className="
                lg:col-span-2
                h-[600px]
                rounded-2xl
                border
                overflow-hidden
                relative
                bg-gray-200
                shadow-sm
              "
            >

              <GoogleMap
                locations={filteredLocations}
                selectedLocation={selectedLocation}
                onSelectLocation={handleSelectLocation}
                userLocation={userLocation}
              />


              {/* DETAIL LOADING */}

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


              {/* APPLE MAPS */}

              {selectedLocation?.latitude &&
                selectedLocation?.longitude && (
                  <button
                    onClick={() =>
                      handleAppleMapsDirections(selectedLocation)
                    }
                    className="
                      absolute
                      bottom-4
                      right-4
                      z-20
                      bg-white
                      border
                      border-gray-200
                      rounded-xl
                      shadow-lg
                      px-4
                      py-3
                      text-sm
                      font-medium
                      text-gray-700
                      hover:bg-gray-50
                      hover:border-gray-300
                      transition
                      flex
                      items-center
                      gap-2
                    "
                  >
                    🧭 Apple Maps ile Yol Tarifi
                  </button>
                )}


              {/* DETAIL PANEL */}

              {locationDetail && !detailLoading && (
                <LocationDetailPanel
                  location={locationDetail}
                  onClose={handleCloseDetail}
                  onLocationRefresh={refreshAfterChargingStart}
                />
              )}

            </div>


            {/* LOCATION LIST */}

            <div
              className="
                bg-white
                rounded-2xl
                border
                overflow-hidden
                h-[600px]
                overflow-y-auto
                shadow-sm
              "
            >

              {/* LIST HEADER */}

              <div className="p-5 border-b">

                <div className="flex items-center justify-between">

                  <div>

                    <h3 className="font-bold text-lg">
                      İstasyonlar
                    </h3>

                    <p className="text-sm text-gray-500 mt-1">
                      {filteredLocations.length} istasyon bulundu
                    </p>

                  </div>

                  <div
                    className="
                      w-9
                      h-9
                      rounded-lg
                      bg-blue-50
                      text-blue-600
                      flex
                      items-center
                      justify-center
                    "
                  >
                    ⚡
                  </div>

                </div>

              </div>


              {/* LIST */}

              <div>

                {filteredLocations.length === 0 ? (

                  <div className="p-6 text-center text-gray-500">

                    <div className="text-3xl mb-2">
                      📍
                    </div>

                    <p>
                      İstasyon bulunamadı.
                    </p>

                  </div>

                ) : (

                  filteredLocations.map((location) => (

                    <div
                      key={location.id}
                      onClick={() => handleSelectLocation(location)}
                      className={`
                        p-5
                        border-b
                        cursor-pointer
                        transition
                        hover:bg-gray-50

                        ${
                          selectedLocation?.id === location.id
                            ? "bg-blue-50 border-l-4 border-l-blue-600"
                            : ""
                        }
                      `}
                    >

                      <div className="flex items-start gap-3">

                        <div
                          className="
                            w-9
                            h-9
                            rounded-lg
                            bg-blue-50
                            flex
                            items-center
                            justify-center
                            shrink-0
                            text-sm
                          "
                        >
                          ⚡
                        </div>

                        <div className="min-w-0 flex-1">

                          <div className="flex items-start justify-between gap-2">

                            <h4 className="font-semibold text-gray-900">
                              {location.name}
                            </h4>

                            <span
                              className="
                                inline-flex
                                items-center
                                gap-1
                                px-2
                                py-1
                                rounded-full
                                bg-green-50
                                text-green-700
                                text-[11px]
                                font-medium
                                shrink-0
                              "
                            >
                              <span className="w-1.5 h-1.5 rounded-full bg-green-500"></span>
                              Aktif
                            </span>

                          </div>

                          <p className="text-sm text-gray-500 mt-1">
                            {location.address}
                          </p>

                          <p className="text-sm text-gray-500">
                            {location.city}
                          </p>

                        </div>

                      </div>


                      {/* AVAILABILITY */}

                      {location.availability?.length > 0 && (

                        <div className="mt-4 space-y-2">

                          {location.availability.map((item, index) => (

                            <div
                              key={index}
                              className="
                                flex
                                items-center
                                justify-between
                                gap-2
                                text-sm
                                bg-gray-50
                                rounded-lg
                                px-3
                                py-2
                              "
                            >

                              <span className="font-medium">
                                {item.powerType}
                              </span>

                              <span className="text-gray-500">
                                {item.availableCount}/{item.totalCount}{" "}
                                müsait
                              </span>

                              <span className="font-medium">
                                {item.unitPrice} TL/kWh
                              </span>

                            </div>

                          ))}

                        </div>

                      )}

                    </div>

                  ))

                )}

              </div>

            </div>

          </div>

        ) : (

          /* =================================================
              LIST VIEW
          ================================================= */

          <div className="bg-white rounded-2xl border overflow-hidden shadow-sm">

            {filteredLocations.length === 0 ? (

              <div className="p-8 text-center text-gray-500">

                <div className="text-3xl mb-2">
                  📍
                </div>

                <p>
                  İstasyon bulunamadı.
                </p>

              </div>

            ) : (

              filteredLocations.map((location) => (

                <div
                  key={location.id}
                  onClick={() => handleSelectLocation(location)}
                  className="
                    p-6
                    border-b
                    hover:bg-gray-50
                    cursor-pointer
                    transition
                  "
                >

                  <div className="flex items-start gap-4">

                    <div
                      className="
                        w-10
                        h-10
                        rounded-xl
                        bg-blue-50
                        flex
                        items-center
                        justify-center
                        shrink-0
                      "
                    >
                      ⚡
                    </div>

                    <div className="flex-1">

                      <div className="flex items-center justify-between gap-3">

                        <h3 className="text-lg font-semibold">
                          {location.name}
                        </h3>

                        <span
                          className="
                            inline-flex
                            items-center
                            gap-1.5
                            px-2.5
                            py-1
                            rounded-full
                            bg-green-50
                            text-green-700
                            text-xs
                            font-medium
                          "
                        >
                          <span className="w-1.5 h-1.5 rounded-full bg-green-500"></span>
                          Aktif
                        </span>

                      </div>

                      <p className="text-gray-500 mt-1">
                        {location.address}, {location.city}
                      </p>

                      {location.availability?.length > 0 && (

                        <div className="mt-4 space-y-2">

                          {location.availability.map((item, index) => (

                            <div
                              key={index}
                              className="
                                flex
                                flex-wrap
                                gap-6
                                text-sm
                                bg-gray-50
                                rounded-lg
                                px-4
                                py-3
                              "
                            >

                              <span className="font-medium">
                                {item.powerType}
                              </span>

                              <span>
                                {item.availableCount}/{item.totalCount} müsait
                              </span>

                              <span>
                                {item.unitPrice} TL/kWh
                              </span>

                            </div>

                          ))}

                        </div>

                      )}

                    </div>

                  </div>

                </div>

              ))

            )}

          </div>

        )}


        {/* =================================================
            BOTTOM INFORMATION
        ================================================= */}

        <div className="mt-8">

          {/* LOCATION CTA */}

          <div
            className="
              rounded-2xl
              bg-blue-600
              text-white
              p-6
              shadow-sm
              relative
              overflow-hidden
            "
          >

            <div className="relative z-10">

              <div
                className="
                  w-10
                  h-10
                  rounded-xl
                  bg-white/15
                  flex
                  items-center
                  justify-center
                  text-xl
                  mb-4
                "
              >
                📍
              </div>

              <h3 className="text-xl font-bold">
                Yakınındaki istasyonları keşfet
              </h3>

              <p className="text-blue-100 text-sm mt-2 max-w-md">
                Konumunuzu kullanarak size en yakın aktif
                şarj istasyonlarını hızlıca görüntüleyebilirsiniz.
              </p>

              <button
                onClick={handleUseMyLocation}
                disabled={locationLoading}
                className="
                  mt-5
                  bg-white
                  text-blue-600
                  px-4
                  py-2.5
                  rounded-xl
                  text-sm
                  font-semibold
                  hover:bg-blue-50
                  transition
                  disabled:opacity-60
                "
              >
                {locationLoading
                  ? "Konum aranıyor..."
                  : "Konumumu Kullan"}
              </button>

            </div>

            <div
              className="
                absolute
                -right-10
                -bottom-16
                w-40
                h-40
                rounded-full
                bg-white/10
              "
            />

          </div>

        </div>

      </main>


      {/* =================================================
          FOOTER
      ================================================= */}

      <footer className="border-t bg-white mt-12">

        <div
          className="
            max-w-7xl
            mx-auto
            px-6
            py-7
            flex
            flex-col
            md:flex-row
            items-center
            justify-between
            gap-4
          "
        >

          <div className="flex items-center gap-3">

            <div
              className="
                w-8
                h-8
                rounded-lg
                bg-blue-600
                text-white
                flex
                items-center
                justify-center
              "
            >
              ⚡
            </div>

            <div>

              <p className="font-semibold text-gray-900">
                EV Charge
              </p>

              <p className="text-xs text-gray-500">
                Elektrikli araç şarj istasyonu platformu
              </p>

            </div>

          </div>


          <div className="flex items-center gap-5 text-sm text-gray-500">

            <span>
              Aktif istasyonları keşfedin
            </span>

            <span className="hidden md:block">
              •
            </span>

            <a
              href="/login"
              className="hover:text-blue-600 transition"
            >
              Yönetici Girişi
            </a>

          </div>

        </div>

      </footer>

    </div>
  );
}

export default Home;