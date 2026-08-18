import {
  setOptions,
  importLibrary,
} from "@googlemaps/js-api-loader";

const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;

if (!apiKey) {
  console.error("Google Maps API key bulunamadı.");
}

setOptions({
  key: apiKey,
  v: "weekly",
});

export const loadGoogleMaps = async () => {
  const { Map } = await importLibrary("maps");

  const { AdvancedMarkerElement } =
    await importLibrary("marker");

  return {
    Map,
    AdvancedMarkerElement,
  };
};