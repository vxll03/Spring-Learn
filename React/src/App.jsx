import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import "./App.css";

import Auth from "./components/Auth/Auth";

function App() {
  return (
    <Router>
      <Link to={"/"}>Home</Link>
      <Link to="/login">Вход</Link>

      <Routes>
        <Route path="/" />
        <Route path="/login" element={<Auth />} />
      </Routes>
    </Router>
  );
}

export default App;
