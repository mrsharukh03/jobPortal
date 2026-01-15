import { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import Auth from '../components/Auth';
import Footer from '../components/Footer';
import Navbar from '../components/Navbar';
import { checkLoginStatus } from '../Utilitys/help';
import styles from '../css/Auth.module.css';

function Login() {
  const [isAuthenticated, setIsAuthenticated] = useState(null);

  useEffect(() => {
    const checkAuth = async () => {
      const result = await checkLoginStatus();
      setIsAuthenticated(result.authenticated);
    };
    checkAuth();
  }, []);

  if (isAuthenticated) {
    return <Navigate to="/profile" replace />;
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