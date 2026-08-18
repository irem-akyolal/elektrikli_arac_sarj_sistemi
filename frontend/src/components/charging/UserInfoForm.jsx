import { useState, useEffect } from "react";

const STORAGE_KEY = "ev_charge_user_info";

function UserInfoForm({ initialData, onBack, onNext }) {
  const [plateNumber, setPlateNumber] = useState(initialData.plateNumber || "");
  const [email, setEmail] = useState(initialData.email || "");
  const [error, setError] = useState("");

  useEffect(() => {
    if (!initialData.plateNumber && !initialData.email) {
      try {
        const stored = localStorage.getItem(STORAGE_KEY);
        if (stored) {
          const parsed = JSON.parse(stored);
          setPlateNumber(parsed.plateNumber || "");
          setEmail(parsed.email || "");
        }
      } catch {
        // localStorage bozuksa sessizce geç
      }
    }
  }, []);

  const handleSubmit = (e) => {
    e.preventDefault();
    setError("");

    if (!plateNumber.trim()) {
      setError("Plaka boş olamaz");
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setError("Geçerli bir e-posta adresi giriniz");
      return;
    }

    localStorage.setItem(
      STORAGE_KEY,
      JSON.stringify({ plateNumber, email })
    );

    onNext({ plateNumber, email });
  };

  return (
    <form onSubmit={handleSubmit} className="p-6">
      <h2 className="text-xl font-bold text-gray-900 mb-2">
        Araç ve İletişim Bilgileri
      </h2>

      <p className="text-gray-500 mb-6">
        Fatura bu e-posta adresine gönderilecektir.
      </p>

      <div className="space-y-4 mb-6">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Araç Plakası
          </label>
          <input
            type="text"
            value={plateNumber}
            onChange={(e) => setPlateNumber(e.target.value.toUpperCase())}
            placeholder="34 ABC 123"
            className="w-full border rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            E-posta Adresi
          </label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="ornek@email.com"
            className="w-full border rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </div>

      {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

      <div className="flex gap-3">
        <button
          type="button"
          onClick={onBack}
          className="flex-1 py-3 rounded-xl border border-gray-300 font-semibold text-gray-700 hover:bg-gray-50 transition"
        >
          Geri
        </button>

        <button
          type="submit"
          className="flex-1 py-3 rounded-xl bg-blue-600 text-white font-semibold hover:bg-blue-700 transition"
        >
          Devam Et
        </button>
      </div>
    </form>
  );
}

export default UserInfoForm;