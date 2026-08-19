import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { startChargingSession, markSessionAsCharging } from "../../api/chargingSessionApi";

import ConfirmStartModal from "./ConfirmStartModal";
import UserInfoForm from "./UserInfoForm";
import ProvisionSelector from "./ProvisionSelector";
import PaymentCardForm from "./PaymentCardForm";

function ChargingFlowModal({ connector, onClose, onSuccess }) {
  const [step, setStep] = useState("confirm");
  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [result, setResult] = useState(null);
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    plateNumber: "",
    email: "",
    requestedAmount: null,
    paymentCard: {
      cardHolderName: "",
      cardNumber: "",
      expireMonth: "",
      expireYear: "",
      cvc: "",
    },
  });

  const handleSubmit = async (paymentCard) => {
    setSubmitting(true);
    setErrorMessage("");

    try {
      const response = await startChargingSession({
        connectorId: connector.id,
        plateNumber: formData.plateNumber,
        email: formData.email,
        requestedAmount: formData.requestedAmount,
        paymentCard,
      });

      // Mock ortamda fiziksel "şarj başladı" bildirimini simüle ediyoruz
      try {
        await markSessionAsCharging(response.data.id);
      } catch (chargingErr) {
        console.warn("Charging durumuna geçiş başarısız:", chargingErr);
        // Bu adım başarısız olsa bile session zaten başlatıldı, kullanıcıya success göster
      }

      setResult(response.data);
      setStep("success");

      // Üst component'e haber ver — connector durumu tazelensin
      onSuccess?.();
    } catch (err) {
      const backendMessage =
        err.response?.data?.message ||
        "Şarj başlatılırken bir hata oluştu. Lütfen tekrar deneyin.";
      setErrorMessage(backendMessage);
      setStep("error");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div
      className="
        fixed inset-0 z-50
        bg-black/50
        flex items-center justify-center
        p-4
      "
    >
      <div
        className="
          bg-white
          rounded-2xl
          shadow-2xl
          w-full
          max-w-md
          max-h-[90vh]
          overflow-y-auto
        "
      >
        {step === "confirm" && (
          <ConfirmStartModal
            connector={connector}
            onCancel={onClose}
            onConfirm={() => setStep("userInfo")}
          />
        )}

        {step === "userInfo" && (
          <UserInfoForm
            initialData={formData}
            onBack={() => setStep("confirm")}
            onNext={(data) => {
              setFormData((prev) => ({ ...prev, ...data }));
              setStep("provision");
            }}
          />
        )}

        {step === "provision" && (
          <ProvisionSelector
            connector={connector}
            onBack={() => setStep("userInfo")}
            onNext={(requestedAmount) => {
              setFormData((prev) => ({ ...prev, requestedAmount }));
              setStep("payment");
            }}
          />
        )}

        {step === "payment" && (
          <PaymentCardForm
            submitting={submitting}
            onBack={() => setStep("provision")}
            onSubmit={(paymentCard) => {
              setFormData((prev) => ({ ...prev, paymentCard }));
              handleSubmit(paymentCard);
            }}
          />
        )}
        {step === "success" && (
          <div className="p-6 text-center">
            <div className="text-5xl mb-4">✅</div>
            <h2 className="text-xl font-bold text-gray-900 mb-2">
              Şarj Başlatıldı
            </h2>
            <p className="text-gray-500 mb-6">
              Oturum numaranız: {result?.id}
            </p>
            <button
              onClick={() => navigate(`/session/${result.id}`)}
              className="w-full py-3 rounded-xl bg-blue-600 text-white font-semibold hover:bg-blue-700 transition"
            >
              Oturumu Görüntüle
            </button>
          </div>
        )}

        {step === "error" && (
          <div className="p-6 text-center">
            <div className="text-5xl mb-4">❌</div>
            <h2 className="text-xl font-bold text-gray-900 mb-2">
              İşlem Başarısız
            </h2>
            <p className="text-gray-500 mb-6">{errorMessage}</p>
            <div className="flex gap-3">
              <button
                onClick={() => setStep("payment")}
                className="flex-1 py-3 rounded-xl border border-gray-300 font-semibold hover:bg-gray-50 transition"
              >
                Tekrar Dene
              </button>
              <button
                onClick={onClose}
                className="flex-1 py-3 rounded-xl bg-gray-900 text-white font-semibold hover:bg-gray-800 transition"
              >
                Vazgeç
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default ChargingFlowModal;