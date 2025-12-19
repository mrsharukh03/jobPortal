import { useEffect, useState } from 'react';
import Auth from '../components/Auth';
import Footer from '../components/Footer';
import Navbar from '../components/Navbar';
import styles from '../css/Auth.module.css';
import { checkLoginStatus } from '../Utilitys/help';
import { Navigate } from 'react-router-dom';

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
    <main className={styles.main}>
        <Navbar/>
    <div className={`${styles.authWrapper}`}>
      <div className={styles.overlay}></div>
      <Auth/>
      </div>
      <Footer/>
    </main>
  );
}

export default Login;
