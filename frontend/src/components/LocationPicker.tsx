import React, { useState, useEffect, useCallback } from 'react';
import { MapContainer, TileLayer, useMapEvents } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { Loader2, MapPin, Navigation } from 'lucide-react';
import { Button } from './ui/button';
import { MapplsLocationInput } from './MapplsLocationInput';

// Fix for default marker icon missing in Leaflet + Webpack/Vite
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

interface LocationPickerProps {
  onConfirm: (location: string, lat: number, lng: number) => void;
  initialLat?: number;
  initialLng?: number;
  initialAddress?: string;
}

// Component to handle map center updates and drag events
const MapController = ({
  onCenterChange
}: {
  onCenterChange: (lat: number, lng: number) => void
}) => {
  const map = useMapEvents({
    dragend: () => {
      const center = map.getCenter();
      onCenterChange(center.lat, center.lng);
    },
    zoomend: () => {
      const center = map.getCenter();
      onCenterChange(center.lat, center.lng);
    }
  });
  return null;
};

export const LocationPicker: React.FC<LocationPickerProps> = ({
  onConfirm,
  initialLat = 18.5204, // Default to Pune
  initialLng = 73.8567,
  initialAddress = ''
}) => {
  const [center, setCenter] = useState({ lat: initialLat, lng: initialLng });
  const [address, setAddress] = useState(initialAddress);
  const [isGeocoding, setIsGeocoding] = useState(false);
  const [mapInstance, setMapInstance] = useState<L.Map | null>(null);

  // Reverse geocode when center changes
  const fetchAddress = useCallback(async (lat: number, lng: number) => {
    setIsGeocoding(true);
    try {
      const response = await fetch(
        `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`
      );
      const data = await response.json();
      if (data && data.display_name) {
        setAddress(data.display_name);
      } else {
        setAddress(`${lat.toFixed(6)}, ${lng.toFixed(6)}`);
      }
    } catch (error) {
      console.error('Geocoding error:', error);
      setAddress(`${lat.toFixed(6)}, ${lng.toFixed(6)}`);
    } finally {
      setIsGeocoding(false);
    }
  }, []);

  // Initial load
  useEffect(() => {
    if (!initialAddress) {
      fetchAddress(initialLat, initialLng);
    }
  }, []);

  const handleCenterChange = (lat: number, lng: number) => {
    setCenter({ lat, lng });
    fetchAddress(lat, lng);
  };

  const handleLocateMe = () => {
    if (!navigator.geolocation) return;
    navigator.geolocation.getCurrentPosition((pos) => {
      const { latitude, longitude } = pos.coords;
      setCenter({ lat: latitude, lng: longitude });
      fetchAddress(latitude, longitude);
      mapInstance?.flyTo([latitude, longitude], 16);
    });
  };

  const handleSearchSelect = (_loc: string, coords?: { lat: number; lng: number }) => {
    if (coords) {
      setCenter(coords);
      fetchAddress(coords.lat, coords.lng);
      mapInstance?.flyTo([coords.lat, coords.lng], 16);
    }
  };

  return (
    <div className="relative w-full h-[600px] bg-gray-100 rounded-xl overflow-hidden border border-gray-200 shadow-inner">
      {/* Search Bar Overlay */}
      <div className="absolute top-4 left-4 right-4 z-[1000]">
        <div className="bg-white rounded-lg shadow-lg">
          <MapplsLocationInput
            onLocationSelect={handleSearchSelect}
            className="border-0 focus:ring-0"
            defaultValue={address}
          />
        </div>
      </div>

      <MapContainer
        center={[initialLat, initialLng]}
        zoom={15}
        style={{ height: '100%', width: '100%' }}
        ref={setMapInstance}
        zoomControl={false}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        <MapController onCenterChange={handleCenterChange} />
      </MapContainer>

      {/* Center Fixed Pin */}
      <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 z-[900] pointer-events-none -mt-4">
        <div className="relative">
          <MapPin className="h-10 w-10 text-black fill-current drop-shadow-xl" />
          <div className="absolute top-full left-1/2 transform -translate-x-1/2 w-2 h-2 bg-black/50 rounded-full blur-[2px]" />
        </div>
      </div>

      {/* Locate Me Button Overlay on Map - Matches User Image Style */}
      <div className="absolute bottom-48 left-1/2 transform -translate-x-1/2 z-[1000] w-full max-w-xs px-4">
        <Button
          type="button"
          onClick={handleLocateMe}
          className="w-full bg-green-900/90 hover:bg-green-900 text-green-400 border border-green-700/50 backdrop-blur-sm shadow-xl flex items-center justify-center gap-2 py-6 rounded-xl text-lg font-medium transition-all"
        >
          <Navigation className="h-5 w-5 fill-current animate-pulse" />
          Use current location
        </Button>
      </div>

      {/* Locate Me Floating Icon (Secondary) - Moved up to avoid overlap */}
      <button
        onClick={handleLocateMe}
        className="absolute bottom-48 right-4 z-[1000] bg-white p-3 rounded-full shadow-lg hover:bg-gray-50 transition-colors hidden md:block"
        title="Use my location"
      >
        <Navigation className="h-6 w-6 text-blue-600" />
      </button>

      {/* Bottom Card */}
      <div className="absolute bottom-0 left-0 right-0 z-[1000] bg-white p-6 rounded-t-2xl shadow-[0_-4px_20px_rgba(0,0,0,0.1)]">
        <div className="flex flex-col gap-4">
          {/* Address Display - Matches User Image Style */}
          <div className="text-center relative">
            <div className="absolute -top-12 left-1/2 transform -translate-x-1/2 bg-white px-4 py-2 rounded-lg shadow-md border border-gray-100 text-sm font-medium text-gray-800 whitespace-nowrap">
              Your service will be provided here
              <div className="absolute bottom-0 left-1/2 transform -translate-x-1/2 translate-y-1/2 rotate-45 w-3 h-3 bg-white border-r border-b border-gray-100"></div>
            </div>

            {isGeocoding ? (
              <div className="flex items-center justify-center gap-2 py-2">
                <Loader2 className="h-4 w-4 animate-spin text-blue-600" />
                <span className="text-gray-500">Fetching address...</span>
              </div>
            ) : (
              <div className="space-y-1">
                <h3 className="text-xl font-bold text-gray-900 line-clamp-1">
                  {address ? address.split(',')[0] : "Select Location"}
                </h3>
                <p className="text-sm text-gray-500 line-clamp-1">
                  {address || "Move pin to your exact location"}
                </p>
              </div>
            )}
          </div>

          <Button
            className="w-full py-6 text-lg font-semibold shadow-lg shadow-blue-600/20"
            onClick={() => onConfirm(address, center.lat, center.lng)}
            disabled={isGeocoding}
          >
            Confirm Location
          </Button>
        </div>
      </div>
    </div>
  );
};
