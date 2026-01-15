import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaUser, FaEnvelope, FaLock, FaGoogle, FaGithub } from 'react-icons/fa';
import axiosInstance from '../Utilitys/axiosInstance';
import styles from '../css/Auth.module.css';

function Auth() {
  const navigate = useNavigate();
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({ fullName: '', email: '', password: '' });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [loading, setLoading] = useState(false);

  const validate = () => {
    const newErrors = {};
    if (!isLogin && !formData.fullName.trim()) newErrors.fullName = 'Full Name is required';
    if (!formData.email.trim()) newErrors.email = 'Email is required';
    else if (!/\S+@\S+\.\S+/.test(formData.email)) newErrors.email = 'Invalid email address';
    if (!formData.password) newErrors.password = 'Password is required';
    else if (formData.password.length < 6) newErrors.password = 'Min 6 chars required';
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
    // Clear errors on type
    if (errors[e.target.name]) setErrors(prev => ({ ...prev, [e.target.name]: '' }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setServerError('');
    if (!validate()) return;

    setLoading(true);
    try {
      const endpoint = isLogin ? '/auth/login' : '/auth/signup';
      const payload = isLogin 
        ? { email: formData.email, password: formData.password }
        : formData;

      const response = await axiosInstance.post(endpoint, payload, { withCredentials: true });

      if (response.status === 200 || response.status === 201) {
        if (isLogin) {
          navigate('/profile');
        } else {
          alert('Signup successful! Please login.');
          setIsLogin(true);
        }
      }
    } catch (error) {
      console.error(error);
      setServerError(error?.response?.data?.message || 'Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const toggleMode = () => {
    setIsLogin(!isLogin);
    setErrors({});
    setServerError('');
    setFormData({ fullName: '', email: '', password: '' });
  };

  return (
    <div className={styles.authCard}>
      <div className={styles.header}>
        <h2>{isLogin ? 'Welcome Back' : 'Create Account'}</h2>
        <p className={styles.subtitle}>
          {isLogin ? 'Enter your details to access your account' : 'Start your journey with us today'}
        </p>
      </div>

      <form onSubmit={handleSubmit} noValidate className={styles.form}>
        {!isLogin && (
          <div className={styles.inputGroup}>
            <div className={`${styles.inputWrapper} ${errors.fullName ? styles.errorBorder : ''}`}>
              <FaUser className={styles.icon} />
              <input
                type="text"
                name="fullName"
                placeholder="Full Name"
                value={formData.fullName}
                onChange={handleChange}
              />
            </div>
            {errors.fullName && <span className={styles.errorMsg}>{errors.fullName}</span>}
          </div>
        )}

        <div className={styles.inputGroup}>
          <div className={`${styles.inputWrapper} ${errors.email ? styles.errorBorder : ''}`}>
            <FaEnvelope className={styles.icon} />
            <input
              type="email"
              name="email"
              placeholder="Email Address"
              value={formData.email}
              onChange={handleChange}
            />
          </div>
          {errors.email && <span className={styles.errorMsg}>{errors.email}</span>}
        </div>

        <div className={styles.inputGroup}>
          <div className={`${styles.inputWrapper} ${errors.password ? styles.errorBorder : ''}`}>
            <FaLock className={styles.icon} />
            <input
              type="password"
              name="password"
              placeholder="Password"
              value={formData.password}
              onChange={handleChange}
            />
          </div>
          {errors.password && <span className={styles.errorMsg}>{errors.password}</span>}
        </div>

        {serverError && <div className={styles.serverError}>{serverError}</div>}

        {isLogin && (
          <div className={styles.forgotPass}>
            <a href="#">Forgot Password?</a>
          </div>
        )}

        <button type="submit" className={styles.submitBtn} disabled={loading}>
          {loading ? <span className={styles.loader}></span> : (isLogin ? 'Sign In' : 'Sign Up')}
        </button>
      </form>

      <div className={styles.divider}>
        <span>Or continue with</span>
      </div>

      <div className={styles.socialButtons}>
        <button className={styles.socialBtn}>
          <FaGoogle color="#DB4437" /> <span>Google</span>
        </button>
        <button className={styles.socialBtn}>
          <FaGithub color="#333" /> <span>GitHub</span>
        </button>
      </div>

      <p className={styles.footerText}>
        {isLogin ? "Don't have an account?" : 'Already have an account?'}
        <span onClick={toggleMode} className={styles.link}>
          {isLogin ? 'Sign Up' : 'Log In'}
        </span>
      </p>
    </div>
  );
}

export default Auth;