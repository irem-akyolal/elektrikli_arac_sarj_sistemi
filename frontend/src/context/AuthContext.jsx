import { createContext, useContext, useState } from "react";
import { login as loginApi } from "../api/authApi";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem("user");
    return stored ? JSON.parse(stored) : null;
  });

  const login = async (username, password) => {
    const res = await loginApi(username, password);
    const { token, username: uname, role } = res.data;

    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify({ username: uname, role }));
    setUser({ username: uname, role });

    return { username: uname, role };
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);