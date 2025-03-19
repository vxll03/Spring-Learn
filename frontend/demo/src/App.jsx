import styles from "./App.module.scss";
import { useEffect, useState } from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  useLocation,
  Navigate,
} from "react-router-dom";

import Auth from "./components/Auth/Auth";
import Cookies from "js-cookie";
import Header from "./components/Header/Header.jsx";
import Api from "./routes/api.js";
import Main from "./components/Main/Main.jsx";

function InnerApp() {
  const currentLocation = useLocation();
  const [isAuth, setIsAuth] = useState(true);
  useEffect(() => {
    setIsAuth(Cookies.get("isAuth"));
  }, []);

  const handleLogout = async () => {
    const api = new Api();
    await api.logoutRequest(setIsAuth);
  };

  return (
    <>
      {currentLocation.pathname !== "/auth" && (
        <Header handleLogout={handleLogout} isAuth={isAuth} />
      )}

      <Routes>
        <Route path="/" element={<Main />} />
        <Route
          path="/auth"
          element={
            !isAuth ? <Auth setIsAuth={setIsAuth} /> : <Navigate to="/" />
          }
        />
      </Routes>
    </>
  );
}

function App() {
  return (
    <Router>
      <InnerApp />
    </Router>
  );
}

export default App;
