import { useEffect, useState } from "react";
import { checkLoginStatus, logout } from "../Utilitys/help";
import { useNavigate } from "react-router-dom";

function Logout() {
  const [isAuthenticated, setIsAuthenticated] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const performLogout = async () => {
      const result = await checkLoginStatus();
      setIsAuthenticated(result.authenticated);

      if (result.authenticated) {
        await logout();
      }

      setTimeout(() => {
        navigate("/login");
      }, 1500);
    };

    performLogout();
  }, [navigate]);

  return (
  <div
    className="d-flex flex-column justify-content-center align-items-center"
    style={{ height: "100vh", background: "#f8f9fa" }}
  >
    <div className="text-center">
      <div
        className="spinner-border text-primary"
        role="status"
        style={{ width: "3rem", height: "3rem" }}
      >
        <span className="visually-hidden">Logging out...</span>
      </div>

      {isAuthenticated === null ? (
        <h5 className="mt-4 text-muted">Checking authentication...</h5>
      ) : isAuthenticated ? (
        <h5 className="mt-4 text-muted">Logging you out, please wait...</h5>
      ) : (
        <h5 className="mt-4 text-muted">You're not logged in. Redirecting...</h5>
      )}
    </div>
  </div>
);

}

export default Logout;
