import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Login from "../pages/auth/Login";
import Dashboard from "../pages/admin/Dashboard";
import Home from "../pages/public/Home";

import ProtectedRoute from "./ProtectedRoute";
import RoleProtectedRoute from "./RoleProtectedRoute";
import LocationsPage from "../pages/admin/locations/LocationsPage";
import Evses from "../pages/admin/evses/Evses";
import Connectors from "../pages/admin/connectors/Connectors";
import Sessions from "../pages/admin/sessions/Sessions";
import Payments from "../pages/admin/payments/Payments";
import Provisions from "../pages/admin/provisions/Provisions";
import EmailQueue from "../pages/admin/emailQueue/EmailQueue";
import Users from "../pages/admin/users/Users";
import AuditLogs from "../pages/admin/auditLogs/AuditLogs";
import LocationDetail from "../pages/admin/locations/LocationDetail";
import LocationEdit from "../pages/admin/locations/LocationEdit";
import SessionTracking from "../pages/public/SessionTracking";


import AdminLayout from "../components/admin/AdminLayout";

function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>

        {/* PUBLIC */}

        <Route path="/" element={<Home />} />

        <Route path="/login" element={<Login />} />

        <Route path="/session/:sessionId" element={<SessionTracking />} />


        {/* ADMIN */}

        <Route element={<ProtectedRoute />}>

          <Route path="/admin" element={<AdminLayout />}>

            {/* Dashboard */}
            <Route index element={<Dashboard />} />


            {/* Locations */}
            <Route
              path="locations"
              element={<LocationsPage />}
            />
            
            <Route
              path="locations/:id"
              element={<LocationDetail />}
            />

            <Route
              path="locations/:id/edit"
              element={<LocationEdit />}
            />


            {/* EVSE */}
            <Route
              path="evses"
              element={<Evses />}
            />


            <Route
              path="connectors"
              element={<Connectors />}
            />


            <Route
              path="sessions"
              element={<Sessions />}
            />


            <Route
              path="payments"
              element={<Payments />}
            />


            <Route
              path="provisions"
              element={<Provisions />}
            />


            <Route element={
              <RoleProtectedRoute
                allowedRoles={["SUPER_ADMIN", "OPERATOR"]}
              />
            }>
              <Route
                path="email-queue"
                element={<EmailQueue />}
              />
            </Route>


            {/* Admin Users */}
            <Route element={
              <RoleProtectedRoute
                allowedRoles={["SUPER_ADMIN"]}
              />
            }>
              <Route
                path="users"
                element={<Users />}
              />
            </Route>


            {/* Audit Logs */}
            <Route element={
              <RoleProtectedRoute
                allowedRoles={["SUPER_ADMIN", "OPERATOR", "VIEWER"]}
              />
            }>
              <Route
                path="audit-logs"
                element={<AuditLogs />}
              />
            </Route>

          </Route>

        </Route>


        {/* UNKNOWN */}

        <Route
          path="*"
          element={<Navigate to="/" replace />}
        />

      </Routes>
    </BrowserRouter>
  );
}

export default AppRouter;