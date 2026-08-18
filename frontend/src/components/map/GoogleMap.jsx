import { useEffect, useRef } from "react";

import { loadGoogleMaps } from "../../utils/googleMapsLoader";

function GoogleMap({
  locations,
  selectedLocation,
  onSelectLocation,
  userLocation,
}) {
  const mapRef = useRef(null);

  const mapInstanceRef = useRef(null);

  const markersRef = useRef([]);

  const userMarkerRef = useRef(null);

  /*
   * =====================================================
   * MARKERLARI TEMİZLE
   * =====================================================
   */

  const clearMarkers = () => {
    markersRef.current.forEach((marker) => {
      marker.map = null;
    });

    markersRef.current = [];
  };


  /*
   * =====================================================
   * MARKERLARI OLUŞTUR
   * =====================================================
   */

  const createMarkers = (
    map,
    AdvancedMarkerElement
  ) => {
    clearMarkers();

    if (!locations || locations.length === 0) {
      return;
    }

    locations.forEach((location) => {

      if (
        typeof location.latitude !== "number" ||
        typeof location.longitude !== "number"
      ) {
        return;
      }

      const marker =
        new AdvancedMarkerElement({
          map,

          position: {
            lat: location.latitude,
            lng: location.longitude,
          },

          title: location.name,
          gmpClickable: true,
        });


      /*
       * =================================================
       * MARKER TIKLAMA
       * =================================================
       */

        marker.addEventListener("gmp-click", () => {
        onSelectLocation(location);
      }      );


      markersRef.current.push(marker);
    });
  };


  /*
   * =====================================================
   * GOOGLE MAPS BAŞLAT
   * =====================================================
   */

  useEffect(() => {

    let cancelled = false;

    const initMap = async () => {

      try {

        const mapId =
          import.meta.env.VITE_GOOGLE_MAPS_MAP_ID;


        if (!mapId) {

          console.error(
            "Google Maps Map ID bulunamadı."
          );

          return;
        }


        /*
         * Google Maps ve Marker kütüphanelerini
         * merkezi loader üzerinden alıyoruz.
         */

        const {
          Map,
          AdvancedMarkerElement,
        } = await loadGoogleMaps();


        if (cancelled) {
          return;
        }


        if (!mapRef.current) {
          return;
        }


        /*
         * =================================================
         * HARİTAYI OLUŞTUR
         * =================================================
         */

        const map = new Map(
          mapRef.current,
          {
            center: {
              lat: 41.0082,
              lng: 28.9784,
            },

            zoom: 11,

            mapId,

            mapTypeControl: false,

            streetViewControl: false,

            fullscreenControl: true,
          }
        );


        mapInstanceRef.current = map;


        /*
         * =================================================
         * MARKERLARI OLUŞTUR
         * =================================================
         */

        createMarkers(
          map,
          AdvancedMarkerElement
        );


      } catch (error) {

        console.error(
          "Google Maps başlatılırken hata oluştu:",
          error
        );

      }

    };


    initMap();


    /*
     * =====================================================
     * CLEANUP
     * =====================================================
     */

    return () => {

      cancelled = true;

      clearMarkers();

      mapInstanceRef.current = null;

    };

  }, []);


  /*
   * =====================================================
   * LOCATIONS DEĞİŞİNCE MARKERLARI GÜNCELLE
   * =====================================================
   */

  useEffect(() => {

    const updateMarkers = async () => {

      const map =
        mapInstanceRef.current;


      if (!map) {
        return;
      }


      try {

        const {
          AdvancedMarkerElement,
        } = await loadGoogleMaps();


        createMarkers(
          map,
          AdvancedMarkerElement
        );


      } catch (error) {

        console.error(
          "Markerlar güncellenirken hata oluştu:",
          error
        );

      }

    };


    updateMarkers();

  }, [locations]);


  /*
   * =====================================================
   * SEÇİLEN İSTASYONA GİT
   * =====================================================
   */

  useEffect(() => {
  const map = mapInstanceRef.current;

  if (!map || !userLocation) {
    return;
  }

  const updateUserLocation = async () => {
    try {
      const { AdvancedMarkerElement } = await loadGoogleMaps();

      const position = {
        lat: userLocation.latitude,
        lng: userLocation.longitude,
      };

      // Önceki kullanıcı markerını temizle
      if (userMarkerRef.current) {
        userMarkerRef.current.map = null;
      }

      // Kullanıcı konumunu gösteren basit marker
      const markerElement = document.createElement("div");

      markerElement.style.width = "16px";
      markerElement.style.height = "16px";
      markerElement.style.backgroundColor = "#2563eb";
      markerElement.style.border = "3px solid white";
      markerElement.style.borderRadius = "50%";
      markerElement.style.boxShadow =
        "0 1px 6px rgba(0,0,0,0.3)";

      userMarkerRef.current =
        new AdvancedMarkerElement({
          map,
          position,
          content: markerElement,
          title: "Konumunuz",
        });

      // Haritayı kullanıcı konumuna götür
      map.panTo(position);
      map.setZoom(14);

    } catch (error) {
      console.error(
        "Kullanıcı konumu haritada gösterilemedi:",
        error
      );
    }
  };

  updateUserLocation();

}, [userLocation]);


  /*
   * =====================================================
   * GOOGLE MAP CONTAINER
   * =====================================================
   */

  return (
    <div
      ref={mapRef}
      className="w-full h-full"
    />
  );
}

export default GoogleMap;