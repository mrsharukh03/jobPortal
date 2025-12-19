// Auth.jsx
import { useState } from 'react';
import styles from '../css/Auth.module.css';
import { FaUser, FaEnvelope, FaLock, FaGoogle, FaGithub, FaMicrosoft } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../Utilitys/axiosInstance';

function Auth() {
  const navigate = useNavigate();

  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
  });

  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [loading, setLoading] = useState(false);

  const validate = () => {
    const newErrors = {};

    if (!isLogin && !formData.fullName.trim()) {
      newErrors.fullName = 'Full Name is required';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Email is invalid';
    }

    if (!formData.password) {
      newErrors.password = 'Password is required';
    } else if (formData.password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters';
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setServerError('');

    if (!validate()) return;

    setLoading(true);

    try {
      if (isLogin) {
        // Login API
        const response = await axiosInstance.post('/auth/login', {
          email: formData.email,
          password: formData.password,
        }, {
          withCredentials: true,
        });

        if (response.status === 200) {
          navigate('/profile');
        }
      } else {
        // Signup API
        const response = await axiosInstance.post('/auth/signup', {
          fullName: formData.fullName,
          email: formData.email,
          password: formData.password,
        });

        if (response.status === 200 || response.status === 201) {
          alert('Signup successful! Please login.');
          setIsLogin(true);
        }
      }
    } catch (error) {
      console.error(error);
      setServerError(error?.response?.data?.message || 'Something went wrong.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.authCard}>
      <div className={styles.authAside}>
        <h2>{isLogin ? 'Welcome Back!' : 'Join Us Today!'}</h2>
        <p>
          {isLogin
            ? 'Access thousands of jobs and opportunities tailored to your skills.'
            : 'Create your account and start your career journey with us.'}
        </p>
        <img className={`${styles.authimage}`}
          src={isLogin
            ? 'https://img.freepik.com/premium-vector/reset-password-concept-illustration_86047-1124.jpg?semt=ais_hybrid&w=740&q=80'
            : 'https://img.freepik.com/premium-vector/online-registration-illustration-design-concept-websites-landing-pages-other_108061-938.jpg?w=2000'}
          alt="Auth Illustration"
          style={{ maxWidth: '100%', height: 'auto' }}
        />
      </div>

      <div className={styles.authForm }>
        <h3 className='text-black'>{isLogin ? 'Log In to Your Account' : 'Sign Up for Free'}</h3>

        <form onSubmit={handleSubmit} noValidate>
          {!isLogin && (
            <div>
              <div className={styles.inputBox}>
                <FaUser className={styles.icon} />
                <input
                  type="text"
                  name="fullName"
                  placeholder="Full Name"
                  value={formData.fullName}
                  onChange={handleChange}
                  required
                  aria-invalid={errors.fullName ? "true" : "false"}
                />
              </div>
              {errors.fullName && <small className={styles.error}>{errors.fullName}</small>}
            </div>
          )}

          <div>
            <div className={styles.inputBox}>
              <FaEnvelope className={styles.icon} />
              <input
                type="email"
                name="email"
                placeholder="Email"
                value={formData.email}
                onChange={handleChange}
                required
                aria-invalid={errors.email ? "true" : "false"}
              />
            </div>
            {errors.email && <small className={styles.error}>{errors.email}</small>}
          </div>

          <div>
            <div className={styles.inputBox}>
              <FaLock className={styles.icon} />
              <input
                type="password"
                name="password"
                placeholder="Password"
                value={formData.password}
                onChange={handleChange}
                required
                aria-invalid={errors.password ? "true" : "false"}
              />
            </div>
            {errors.password && <small className={styles.error}>{errors.password}</small>}
          </div>
          {serverError && <p className='text-danger'>{serverError}</p>}

          {isLogin && (
            <div className={styles.forgotPassword}>
              <a href="#" onClick={(e) => { e.preventDefault(); alert('Forgot Password clicked!'); }}>
                Forgot Password?
              </a>
            </div>
          )}

          <button className="btn btn-primary w-100 rounded-pill mt-2" type="submit" disabled={loading}>
            {loading ? 'Please wait...' : isLogin ? 'Login' : 'Sign Up'}
          </button>
        </form>

        <div className="text-center my-3 text-muted">or continue with</div>

        <div className={styles.socialButtons}>
          <button className={`${styles.oauthBtn} ${styles.google}`}>
            <FaGoogle /> Google
          </button>
          <button className={`${styles.oauthBtn} ${styles.github}`}>
            <FaGithub /> GitHub
          </button>
          <button className={`${styles.oauthBtn} ${styles.microsoft}`}>
            <FaMicrosoft /> Microsoft
          </button>
        </div>

        <p className="text-center mt-4 text-muted">
          {isLogin ? "Don't have an account?" : 'Already have an account?'}{' '}
          <span className={styles.toggle} onClick={() => {
            setIsLogin(!isLogin);
            setErrors({});
            setFormData({ fullName: '', email: '', password: '' });
            setServerError('');
          }}>
            {isLogin ? 'Sign Up' : 'Login'}
          </span>
        </p>
      </div>
    </div>
  );
}

export default Auth;
