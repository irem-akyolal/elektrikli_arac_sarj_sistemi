import { useState } from "react";

const KWH_OPTIONS = [50, 70, 100, 120];

function ProvisionSelector({ connector, onBack, onNext }) {
  const [selectedKwh, setSelectedKwh] = useState(null);

  const unitPrice = connector.unitPrice != null ? Number(connector.unitPrice) : 0;

  const calculateAmount = (kwh) => (kwh * unitPrice).toFixed(2);

  const handleContinue = () => {
    if (!selectedKwh) return;
    onNext(Number(calculateAmount(selectedKwh)));
  };

  return (
    <div className="p-6">
      <h2 className="text-xl font-bold text-gray-900 mb-2">
        Provizyon Seçin
      </h2>

      <p className="text-gray-500 mb-6">
        Tahmini enerji miktarına göre bir provizyon tutarı seçin. Gerçek
        tüketim farklı olursa fark iade edilir veya ek tahsilat yapılır.
      </p>

      <div className="grid grid-cols-2 gap-3 mb-6">
        {KWH_OPTIONS.map((kwh) => (
          <button
            key={kwh}
            onClick={() => setSelectedKwh(kwh)}
            className={`
              border
              rounded-xl
              p-4
              text-left
              transition

              ${
                selectedKwh === kwh
                  ? "border-blue-600 bg-blue-50"
                  : "border-gray-200 hover:border-gray-300"
              }
            `}
          >
            <p className="text-lg font-bold text-gray-900">{kwh} kWh</p>
            <p className="text-sm text-gray-500 mt-1">
              ≈ {calculateAmount(kwh)} TL
            </p>
          </button>
        ))}
      </div>

      {unitPrice === 0 && (
        <p className="text-amber-600 text-sm mb-4">
          Bu konnektör için birim fiyat bilgisi bulunamadı.
        </p>
      )}

      <div className="flex gap-3">
        <button
          onClick={onBack}
          className="flex-1 py-3 rounded-xl border border-gray-300 font-semibold text-gray-700 hover:bg-gray-50 transition"
        >
          Geri
        </button>

        <button
          onClick={handleContinue}
          disabled={!selectedKwh}
          className="
            flex-1
            py-3
            rounded-xl
            bg-blue-600
            text-white
            font-semibold
            hover:bg-blue-700
            transition
            disabled:opacity-40
            disabled:cursor-not-allowed
          "
        >
          Devam Et
        </button>
      </div>
    </div>
  );
}

export default ProvisionSelector;