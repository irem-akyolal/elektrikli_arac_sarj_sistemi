import { Outlet, Link, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

function AdminLayout() {
  const { user, logout } = useAuth();
  const location = useLocation();

  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <div className="h-screen bg-gray-100 flex overflow-hidden">

      {/* SIDEBAR */}
      <aside className="w-64 bg-gray-900 text-white h-screen flex-shrink-0 flex flex-col">

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
        <nav className="p-4 space-y-1 flex-1">

          <Link
            to="/admin"
            className={`flex items-center gap-3 px-4 py-3 rounded-lg ${
              isActive("/admin")
                ? "bg-blue-600"
                : "hover:bg-gray-800"
            }`}
          >
            📊
            <span>Dashboard</span>
          </Link>

          <Link
            to="/admin/locations"
            className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-800"
          >
            📍
            <span>İstasyonlar</span>
          </Link>

          <Link
            to="/admin/evses"
            className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-800"
          >
            ⚡
            <span>EVSE</span>
          </Link>

          <Link
            to="/admin/connectors"
            className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-800"
          >
            🔌
            <span>Connector</span>
          </Link>

          <Link
            to="/admin/sessions"
            className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-800"
          >
            🔋
            <span>Şarj Oturumları</span>
          </Link>

          <Link
            to="/admin/payments"
            className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-800"
          >
            💳
            <span>Ödemeler</span>
          </Link>

          <Link
            to="/admin/provisions"
            className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-800"
          >
            🛡️
            <span>Provizyonlar</span>
          </Link>

          {(user?.role === "SUPER_ADMIN" ||
            user?.role === "OPERATOR") && (
            <Link
              to="/admin/email-queue"
              className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-800"
            >
              📧
              <span>Email Geçmişi</span>
            </Link>
          )}

          {/* SADECE SUPER ADMIN */}
          {user?.role === "SUPER_ADMIN" && (
            <>
              <Link
                to="/admin/users"
                className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-800"
              >
                👥
                <span>Kullanıcı Yönetimi</span>
              </Link>

              <Link
                to="/admin/audit-logs"
                className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-gray-800"
              >
                📋
                <span>Sistem Logları</span>
              </Link>
            </>
          )}

        </nav>

        {/* LOGOUT */}
        <div className="p-4 border-t border-gray-700 flex-shrink-0">
          <button
            onClick={logout}
            className="w-full px-4 py-3 rounded-lg bg-red-600 hover:bg-red-700"
          >
            🚪 Çıkış Yap
          </button>
        </div>

      </aside>

      {/* CONTENT */}
      <main className="flex-1 min-w-0 h-screen overflow-y-auto p-8">
        <Outlet />
      </main>

    </div>
  );
}

export default AdminLayout;