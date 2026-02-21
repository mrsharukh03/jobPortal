import { useEffect } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import Auth from "../components/Auth";
import Footer from "../components/Footer";
import Navbar from "../components/Navbar";
import styles from "../css/Auth.module.css";

function Login() {
  const { user, loading } = useAuth();
  const location = useLocation();

  // jahan se aaya tha (ya home)
  const redirectTo = location.state?.from || "/";

  // jab already logged in ho
  if (!loading && user) {
    return <Navigate to={redirectTo} replace />;
  }

  return (
    <div className={styles.pageContainer}>
      <Navbar />
      <main className={styles.mainContent}>
        <Auth />
      </main>
      <Footer />
    </div>
  );
}

export default Login;
