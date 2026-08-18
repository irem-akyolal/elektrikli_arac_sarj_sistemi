import { Outlet, Link, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

function AdminLayout() {
  const { user, logout } = useAuth();
  const location = useLocation();

  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <div className="min-h-screen bg-gray-100 flex">

      {/* SIDEBAR */}
      <aside className="w-64 bg-gray-900 text-white min-h-screen">

        <div className="p-6 border-b border-gray-700">
          <h1 className="text-xl font-bold">
            ⚡ EV Charge
          </h1>

          <p className="text-xs text-gray-400 mt-1">
            Yönetim Paneli
          </p>
        </div>

        {/* USER */}
        <div className="p-5 border-b border-gray-700">
          <p className="font-medium">
            {user?.username}
          </p>

          <p className="text-xs text-gray-400 mt-1">
            {user?.role}
          </p>
        </div>

        {/* MENU */}
        <nav className="p-4 space-y-1">

          <Link
            to="/admin"
            className={`block px-4 py-3 rounded-lg ${
              isActive("/admin")
                ? "bg-blue-600"
                : "hover:bg-gray-800"
            }`}
          >
            Dashboard
          </Link>

          <Link
            to="/admin/locations"
            className="block px-4 py-3 rounded-lg hover:bg-gray-800"
          >
            İstasyonlar
          </Link>

          <Link
            to="/admin/evses"
            className="block px-4 py-3 rounded-lg hover:bg-gray-800"
          >
            EVSE
          </Link>

          <Link
            to="/admin/connectors"
            className="block px-4 py-3 rounded-lg hover:bg-gray-800"
          >
            Connector
          </Link>

          <Link
            to="/admin/sessions"
            className="block px-4 py-3 rounded-lg hover:bg-gray-800"
          >
            Şarj Oturumları
          </Link>

          <Link
            to="/admin/payments"
            className="block px-4 py-3 rounded-lg hover:bg-gray-800"
          >
            Ödemeler
          </Link>

          <Link
            to="/admin/provisions"
            className="block px-4 py-3 rounded-lg hover:bg-gray-800"
          >
            Provizyonlar
          </Link>

          {(user?.role === "SUPER_ADMIN" ||
            user?.role === "OPERATOR") && (
            <Link
              to="/admin/email-queue"
              className="block px-4 py-3 rounded-lg hover:bg-gray-800"
            >
              Email Geçmişi
            </Link>
          )}

          {/* SADECE SUPER ADMIN */}
          {user?.role === "SUPER_ADMIN" && (
            <Link
              to="/admin/users"
              className="block px-4 py-3 rounded-lg hover:bg-gray-800"
            >
              Kullanıcı Yönetimi
            </Link>
          )}

        </nav>

        {/* LOGOUT */}
        <div className="absolute bottom-0 w-64 p-4">
          <button
            onClick={logout}
            className="w-full px-4 py-3 rounded-lg bg-red-600 hover:bg-red-700"
          >
            Çıkış Yap
          </button>
        </div>

      </aside>

      {/* CONTENT */}
      <main className="flex-1 p-8">
        <Outlet />
      </main>

    </div>
  );
}

export default AdminLayout;