function ConfirmStartModal({ connector, onCancel, onConfirm }) {
  return (
    <div className="p-6">
      <h2 className="text-xl font-bold text-gray-900 mb-2">
        Şarj Başlatılsın mı?
      </h2>

      <p className="text-gray-500 mb-6">
        Bu konnektörde şarj başlatmak istediğinize emin misiniz?
      </p>

      <div className="bg-gray-50 rounded-xl p-4 mb-6 space-y-2">
        <div className="flex justify-between text-sm">
          <span className="text-gray-500">Standart</span>
          <span className="font-semibold text-gray-900">
            {connector.standard || "-"}
          </span>
        </div>

        <div className="flex justify-between text-sm">
          <span className="text-gray-500">Güç Tipi</span>
          <span className="font-semibold text-gray-900">
            {connector.powerType || "-"}
          </span>
        </div>

        <div className="flex justify-between text-sm">
          <span className="text-gray-500">Birim Fiyat</span>
          <span className="font-semibold text-gray-900">
            {connector.unitPrice != null
              ? `${connector.unitPrice} TL/kWh`
              : "-"}
          </span>
        </div>
      </div>

      <div className="flex gap-3">
        <button
          onClick={onCancel}
          className="
            flex-1
            py-3
            rounded-xl
            border
            border-gray-300
            font-semibold
            text-gray-700
            hover:bg-gray-50
            transition
          "
        >
          Vazgeç
        </button>

        <button
          onClick={onConfirm}
          className="
            flex-1
            py-3
            rounded-xl
            bg-green-600
            text-white
            font-semibold
            hover:bg-green-700
            transition
          "
        >
          Evet, Devam Et
        </button>
      </div>
    </div>
  );
}

export default ConfirmStartModal;