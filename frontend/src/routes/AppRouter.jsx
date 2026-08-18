import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Login from "../pages/auth/Login";
import Dashboard from "../pages/admin/Dashboard";
import Home from "../pages/public/Home";

import ProtectedRoute from "./ProtectedRoute";
import RoleProtectedRoute from "./RoleProtectedRoute";
import LocationsPage from "../pages/admin/locations/LocationsPage";
import Evses from "../pages/admin/evses/Evses";
import Connectors from "../pages/admin/connectors/Connectors";


import AdminLayout from "../components/admin/AdminLayout";

function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>

        {/* PUBLIC */}

        <Route path="/" element={<Home />} />

        <Route path="/login" element={<Login />} />


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


            {/* EVSE */}
            <Route
              path="evses"
              element={<Evses />}
            />


            <Route
              path="connectors"
              element={<Connectors />}
            />


            {/* Sessions */}
            <Route
              path="sessions"
              element={
                <div>
                  Charging Sessions
                </div>
              }
            />


            {/* Payments */}
            <Route
              path="payments"
              element={
                <div>
                  Payments
                </div>
              }
            />


            {/* Provisions */}
            <Route
              path="provisions"
              element={
                <div>
                  Provisions
                </div>
              }
            />


            {/* Email Queue */}
            <Route element={
              <RoleProtectedRoute
                allowedRoles={["SUPER_ADMIN", "OPERATOR"]}
              />
            }>
              <Route
                path="email-queue"
                element={
                  <div>
                    Email History
                  </div>
                }
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
                element={
                  <div>
                    Admin Users
                  </div>
                }
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