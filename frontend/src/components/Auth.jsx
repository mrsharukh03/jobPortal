// Auth.jsx
import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { FaUser, FaEnvelope, FaLock, FaGoogle, FaGithub } from "react-icons/fa";
import { useAuth } from "../contexts/AuthContext";
import styles from "../css/Auth.module.css";

function Auth() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, signup } = useAuth();

  const redirectTo = location.state?.from || "/";

  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    password: "",
  });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState("");
  const [loading, setLoading] = useState(false);

  // ---------------- VALIDATION ----------------
  const validate = () => {
    const newErrors = {};

    if (!isLogin && !formData.fullName.trim()) {
      newErrors.fullName = "Full Name is required";
    }

    if (!formData.email.trim()) {
      newErrors.email = "Email is required";
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = "Invalid email address";
    }

    if (!formData.password) {
      newErrors.password = "Password is required";
    } else if (formData.password.length < 6) {
      newErrors.password = "Password must be at least 6 characters";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // ---------------- INPUT CHANGE ----------------
  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));

    if (errors[e.target.name]) {
      setErrors((prev) => ({ ...prev, [e.target.name]: "" }));
    }
    if (serverError) setServerError("");
  };

  // ---------------- SUBMIT ----------------
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    setServerError("");

    try {
      if (isLogin) {
        // ✅ LOGIN (cookie based)
        await login(formData.email, formData.password);
        navigate(redirectTo, { replace: true });
      } else {
        // ✅ SIGNUP
        await signup(formData.fullName, formData.email, formData.password);
        alert("Signup successful! Please login.");
        setIsLogin(true);
        setFormData({ fullName: "", email: "", password: "" });
      }
    } catch (err) {
      setServerError(
        err?.response?.data?.message || "Something went wrong. Try again."
      );
    } finally {
      setLoading(false);
    }
  };

  // ---------------- TOGGLE ----------------
  const toggleMode = () => {
    setIsLogin(!isLogin);
    setErrors({});
    setServerError("");
    setFormData({ fullName: "", email: "", password: "" });
  };

  return (
    <div className={styles.authCard}>
      <div className={styles.header}>
        <h2>{isLogin ? "Welcome Back" : "Create Account"}</h2>
        <p className={styles.subtitle}>
          {isLogin
            ? "Enter your details to access your account"
            : "Start your journey with us today"}
        </p>
      </div>

      <form onSubmit={handleSubmit} noValidate className={styles.form}>
        {/* FULL NAME */}
        {!isLogin && (
          <div className={styles.inputGroup}>
            <div
              className={`${styles.inputWrapper} ${
                errors.fullName ? styles.errorBorder : ""
              }`}
            >
              <FaUser className={styles.icon} />
              <input
                type="text"
                name="fullName"
                placeholder="Full Name"
                value={formData.fullName}
                onChange={handleChange}
              />
            </div>
            {errors.fullName && (
              <span className={styles.errorMsg}>{errors.fullName}</span>
            )}
          </div>
        )}

        {/* EMAIL */}
        <div className={styles.inputGroup}>
          <div
            className={`${styles.inputWrapper} ${
              errors.email ? styles.errorBorder : ""
            }`}
          >
            <FaEnvelope className={styles.icon} />
            <input
              type="email"
              name="email"
              placeholder="Email Address"
              value={formData.email}
              onChange={handleChange}
            />
          </div>
          {errors.email && (
            <span className={styles.errorMsg}>{errors.email}</span>
          )}
        </div>

        {/* PASSWORD */}
        <div className={styles.inputGroup}>
          <div
            className={`${styles.inputWrapper} ${
              errors.password ? styles.errorBorder : ""
            }`}
          >
            <FaLock className={styles.icon} />
            <input
              type="password"
              name="password"
              placeholder="Password"
              value={formData.password}
              onChange={handleChange}
            />
          </div>
          {errors.password && (
            <span className={styles.errorMsg}>{errors.password}</span>
          )}
        </div>

        {/* SERVER ERROR */}
        {serverError && (
          <div className={styles.serverError}>{serverError}</div>
        )}

        {/* FORGOT PASSWORD */}
        {isLogin && (
          <div className={styles.forgotPass}>
            <a href="/forgot-password">Forgot Password?</a>
          </div>
        )}

        {/* SUBMIT */}
        <button
          type="submit"
          className={styles.submitBtn}
          disabled={loading}
        >
          {loading ? (
            <span className={styles.loader}></span>
          ) : isLogin ? (
            "Sign In"
          ) : (
            "Sign Up"
          )}
        </button>
      </form>

      {/* DIVIDER */}
      <div className={styles.divider}>
        <span>Or continue with</span>
      </div>

      {/* SOCIAL */}
      <div className={styles.socialButtons}>
        <button className={styles.socialBtn}>
          <FaGoogle color="#DB4437" /> <span>Google</span>
        </button>
        <button className={styles.socialBtn}>
          <FaGithub color="#333" /> <span>GitHub</span>
        </button>
      </div>

      {/* SWITCH */}
      <p className={styles.footerText}>
        {isLogin ? "Don't have an account?" : "Already have an account?"}
        <span onClick={toggleMode} className={styles.link}>
          {isLogin ? "Sign Up" : "Log In"}
        </span>
      </p>
    </div>
  );
}

export default Auth;
