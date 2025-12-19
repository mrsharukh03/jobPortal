import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import Home from "./Screens/Home";
import Login from "./Screens/Login";
import NotFound from "./Screens/NotFound";
import VerifyEmail from "./Screens/VerifyEmail";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Dashboard from "./Screens/Dashboard";
import ProtectedRoute from "./Utilitys/ProtectedRoute";
import Logout from "./Screens/Logout";
import Profile from "./Screens/Profile";
import "tailwindcss";
function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/logout" element={<Logout />} />
        <Route path="/verify-email/:token" element={<VerifyEmail />} />
        <Route path="*" element={<NotFound />} />

        {/* Protected Routes */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <Profile />
            </ProtectedRoute>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;
