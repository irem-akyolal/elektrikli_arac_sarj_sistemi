import { useState, useEffect, useCallback } from "react";
import { useParams, Link } from "react-router-dom";
import {
  getChargingSession,
  markConnectorRemoved,
} from "../../api/chargingSessionApi";

const POLL_INTERVAL_MS = 3000;

const STATUS_LABELS = {
  STARTED: "Şarj Başlatılıyor",
  CHARGING: "Şarj Oluyor",
  COMPLETED: "Şarj Tamamlandı",
  CLOSED: "Oturum Kapatıldı",
};

function SessionTracking() {
  const { sessionId } = useParams();

  const [session, setSession] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [removing, setRemoving] = useState(false);
  const [removeError, setRemoveError] = useState("");

  const fetchSession = useCallback(async () => {
    try {
      const response = await getChargingSession(sessionId);
      setSession(response.data);
      setError("");
    } catch (err) {
      console.error("Oturum bilgisi alınamadı:", err);
      setError("Oturum bilgisi alınamadı.");
    } finally {
      setLoading(false);
    }
  }, [sessionId]);

  useEffect(() => {
    fetchSession();
  }, [fetchSession]);

  // Oturum hâlâ aktifken (CLOSED değilken) periyodik olarak güncelle
  useEffect(() => {
    if (!session || session.status === "CLOSED") {
      return;
    }

    const interval = setInterval(fetchSession, POLL_INTERVAL_MS);
    return () => clearInterval(interval);
  }, [session, fetchSession]);

  const handleRemoveConnector = async () => {
    setRemoving(true);
    setRemoveError("");

    try {
      const response = await markConnectorRemoved(sessionId);
      setSession(response.data);
    } catch (err) {
      const backendMessage =
        err.response?.data?.message ||
        "Konnektör çıkarma işlemi başarısız oldu. Lütfen tekrar deneyin.";
      setRemoveError(backendMessage);
    } finally {
      setRemoving(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-500">Oturum bilgisi yükleniyor...</p>
      </div>
    );
  }

  if (error || !session) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center gap-4">
        <p className="text-red-500">{error || "Oturum bulunamadı."}</p>
        <Link to="/" className="text-blue-600 hover:underline">
          Ana sayfaya dön
        </Link>
      </div>
    );
  }

  const statusLabel = STATUS_LABELS[session.status] || session.status;

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-lg w-full max-w-md p-6">
        <p className="text-xs font-medium text-blue-600 uppercase tracking-wide mb-1">
          Şarj Oturumu
        </p>

        <h1 className="text-2xl font-bold text-gray-900 mb-6">
          {statusLabel}
        </h1>

        <div className="bg-gray-50 rounded-xl p-4 space-y-2 mb-6">
          <div className="flex justify-between text-sm">
            <span className="text-gray-500">Plaka</span>
            <span className="font-semibold text-gray-900">
              {session.plateNumber}
            </span>
          </div>

          <div className="flex justify-between text-sm">
            <span className="text-gray-500">E-posta</span>
            <span className="font-semibold text-gray-900">
              {session.email}
            </span>
          </div>

          {session.energyConsumedKwh != null && (
            <div className="flex justify-between text-sm">
              <span className="text-gray-500">Tüketim</span>
              <span className="font-semibold text-gray-900">
                {session.energyConsumedKwh} kWh
              </span>
            </div>
          )}
        </div>

        {/* STARTED / CHARGING */}
        {(session.status === "STARTED" || session.status === "CHARGING") && (
          <div className="text-center py-4">
            <div className="animate-pulse text-4xl mb-3">⚡</div>
            <p className="text-gray-500">
              Aracınız şarj oluyor, bu sayfa otomatik güncellenir.
            </p>
          </div>
        )}

        {/* COMPLETED — kabloyu çıkarma bekleniyor */}
        {session.status === "COMPLETED" && (
          <div>
            <div className="bg-amber-50 border border-amber-200 rounded-xl p-4 mb-4">
              <p className="text-amber-800 text-sm font-medium">
                Şarj tamamlandı. Konnektörün çıkarılması bekleniyor.
              </p>
            </div>

            {removeError && (
              <p className="text-red-500 text-sm mb-4">{removeError}</p>
            )}

            <button
              onClick={handleRemoveConnector}
              disabled={removing}
              className="
                w-full
                py-3
                rounded-xl
                bg-green-600
                text-white
                font-semibold
                hover:bg-green-700
                transition
                disabled:opacity-60
                disabled:cursor-not-allowed
              "
            >
              {removing ? "İşleniyor..." : "Kabloyu Çıkardım"}
            </button>
          </div>
        )}

        {/* CLOSED — işlem tamamen bitti */}
        {session.status === "CLOSED" && (
          <div className="text-center py-4">
            <div className="text-5xl mb-3">✅</div>
            <p className="text-gray-900 font-semibold mb-1">
              İşlem tamamlandı
            </p>
            <p className="text-gray-500 text-sm mb-6">
              Faturanız {session.email} adresine gönderildi.
            </p>
            <Link
              to="/"
              className="
                inline-block
                py-3
                px-6
                rounded-xl
                bg-blue-600
                text-white
                font-semibold
                hover:bg-blue-700
                transition
              "
            >
              Ana Sayfaya Dön
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}

export default SessionTracking;