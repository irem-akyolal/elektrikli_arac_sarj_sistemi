import { useEffect, useRef } from "react";
import {
  setOptions,
  importLibrary,
} from "@googlemaps/js-api-loader";

let googleMapsInitialized = false;

function GoogleMap({
  locations,
  selectedLocation,
  onSelectLocation,
}) {
  const mapRef = useRef(null);
  const mapInstanceRef = useRef(null);
  const markersRef = useRef([]);

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
        });


      /*
       * =================================================
       * MARKER TIKLAMA
       * =================================================
       *
       * AdvancedMarkerElement için
       * "click" yerine "gmp-click" kullanıyoruz.
       */
      marker.addEventListener(
        "gmp-click",
        () => {
          console.log(
            "MARKER TIKLANDI:",
            location
          );

          onSelectLocation(location);
        }
      );


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
        const apiKey =
          import.meta.env.VITE_GOOGLE_MAPS_API_KEY;

        const mapId =
          import.meta.env.VITE_GOOGLE_MAPS_MAP_ID;


        if (!apiKey) {
          console.error(
            "Google Maps API key bulunamadı."
          );
          return;
        }


        if (!mapId) {
          console.error(
            "Google Maps Map ID bulunamadı."
          );
          return;
        }


        /*
         * setOptions sadece bir kere.
         */
        if (!googleMapsInitialized) {
          setOptions({
            key: apiKey,
            v: "weekly",
          });

          googleMapsInitialized = true;
        }


        /*
         * Maps library
         */
        const { Map } =
          await importLibrary("maps");


        /*
         * Marker library
         */
        const {
          AdvancedMarkerElement,
        } = await importLibrary("marker");


        if (cancelled) {
          return;
        }


        if (!mapRef.current) {
          return;
        }


        /*
         * HARİTAYI OLUŞTUR
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
         * İLK MARKERLARI OLUŞTUR
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


    return () => {
      cancelled = true;

      clearMarkers();

      mapInstanceRef.current = null;
    };

  }, []);


  /*
   * =====================================================
   * LOCATIONS DEĞİŞTİĞİNDE MARKERLARI YENİLE
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
        } = await importLibrary("marker");


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
    const map =
      mapInstanceRef.current;

    if (!map || !selectedLocation) {
      return;
    }


    if (
      typeof selectedLocation.latitude !==
        "number" ||
      typeof selectedLocation.longitude !==
        "number"
    ) {
      return;
    }


    map.panTo({
      lat: selectedLocation.latitude,
      lng: selectedLocation.longitude,
    });


    map.setZoom(15);

  }, [selectedLocation]);


  return (
    <div
      ref={mapRef}
      className="w-full h-full"
    />
  );
}

export default GoogleMap;