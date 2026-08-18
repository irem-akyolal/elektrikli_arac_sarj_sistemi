import { useState } from "react";

function PaymentCardForm({ submitting, onBack, onSubmit }) {
  const [cardHolderName, setCardHolderName] = useState("");
  const [cardNumber, setCardNumber] = useState("");
  const [expireMonth, setExpireMonth] = useState("");
  const [expireYear, setExpireYear] = useState("");
  const [cvc, setCvc] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    setError("");

    if (!cardHolderName.trim()) {
      setError("Kart sahibi adı boş olamaz");
      return;
    }

    const cleanedCardNumber = cardNumber.replace(/\s/g, "");
    if (cleanedCardNumber.length < 15 || cleanedCardNumber.length > 16) {
      setError("Geçerli bir kart numarası giriniz");
      return;
    }

    if (!expireMonth || !expireYear) {
      setError("Son kullanma tarihi boş olamaz");
      return;
    }

    if (cvc.length < 3 || cvc.length > 4) {
      setError("Geçerli bir CVC giriniz");
      return;
    }

    onSubmit({
      cardHolderName,
      cardNumber: cleanedCardNumber,
      expireMonth,
      expireYear,
      cvc,
    });
  };

  return (
    <form onSubmit={handleSubmit} className="p-6">
      <h2 className="text-xl font-bold text-gray-900 mb-2">
        Ödeme Bilgileri
      </h2>

      <p className="text-gray-500 mb-6">
        Provizyon tutarı, kartınızdan geçici olarak bloke edilecektir.
      </p>

      <div className="space-y-4 mb-6">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Kart Üzerindeki İsim
          </label>
          <input
            type="text"
            value={cardHolderName}
            onChange={(e) => setCardHolderName(e.target.value)}
            placeholder="AD SOYAD"
            className="w-full border rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Kart Numarası
          </label>
          <input
            type="text"
            inputMode="numeric"
            value={cardNumber}
            onChange={(e) => setCardNumber(e.target.value)}
            placeholder="4242 4242 4242 4242"
            maxLength={19}
            className="w-full border rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div className="grid grid-cols-3 gap-3">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Ay
            </label>
            <input
              type="text"
              inputMode="numeric"
              value={expireMonth}
              onChange={(e) => setExpireMonth(e.target.value)}
              placeholder="12"
              maxLength={2}
              className="w-full border rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Yıl
            </label>
            <input
              type="text"
              inputMode="numeric"
              value={expireYear}
              onChange={(e) => setExpireYear(e.target.value)}
              placeholder="2028"
              maxLength={4}
              className="w-full border rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              CVC
            </label>
            <input
              type="text"
              inputMode="numeric"
              value={cvc}
              onChange={(e) => setCvc(e.target.value)}
              placeholder="123"
              maxLength={4}
              className="w-full border rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>
      </div>

      {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

      <div className="flex gap-3">
        <button
          type="button"
          onClick={onBack}
          disabled={submitting}
          className="flex-1 py-3 rounded-xl border border-gray-300 font-semibold text-gray-700 hover:bg-gray-50 transition disabled:opacity-40"
        >
          Geri
        </button>

        <button
          type="submit"
          disabled={submitting}
          className="
            flex-1
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
          {submitting ? "İşleniyor..." : "Onayla ve Başlat"}
        </button>
      </div>
    </form>
  );
}

export default PaymentCardForm;