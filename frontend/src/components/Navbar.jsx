// Navbar.jsx
import { FaUserCircle, FaBars, FaTimes } from "react-icons/fa";
import style from "../css/Navbar.module.css";
import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState, useRef } from "react";
import { MdMessage, MdDashboard, MdSettings, MdLogout, MdPerson } from "react-icons/md";
import { useAuth } from "../contexts/AuthContext";

function Navbar() {
  const navigate = useNavigate();
  const { user, loading, logout } = useAuth();

  // ------------------------
  // STATE
  // ------------------------
  const isAuthenticated = !!user;

  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [notificationOpen, setNotificationOpen] = useState(false);
  const [showAlerts, setShowAlerts] = useState(false);
  const [alerts, setAlerts] = useState([]);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const dropdownRef = useRef(null);
  const notificationRef = useRef(null);

  // ------------------------
  // AUTH BASED ALERTS
  // ------------------------
  useEffect(() => {
    if (isAuthenticated) {
      setAlerts(["Welcome back!", "3 new job alerts today"]);
      setShowAlerts(true);
    } else {
      setShowAlerts(false);
      setAlerts([]);
    }
  }, [isAuthenticated]);

  // ------------------------
  // CLOSE DROPDOWNS ON OUTSIDE CLICK
  // ------------------------
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setDropdownOpen(false);
      }
      if (notificationRef.current && !notificationRef.current.contains(e.target)) {
        setNotificationOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // ------------------------
  // LOGOUT (COOKIE BASED)
  // ------------------------
  const handleLogout = async () => {
    await logout(); // backend clears cookies
    closeMenus();
    navigate("/login");
  };

  const closeMenus = () => {
    setDropdownOpen(false);
    setNotificationOpen(false);
    setIsMobileMenuOpen(false);
  };

  if (loading) return null; // auth flicker avoid

  return (
    <>
      {/* --- ALERT BAR --- */}
      {isAuthenticated && showAlerts && (
        <div className={style.alertBar}>
          <div className="container-fluid d-flex align-items-center position-relative h-100">
            <span className="badge bg-white text-danger fw-bold ms-3 me-3 z-2">
              IMPORTANT
            </span>

            <div className={style.tickerWrap}>
              <div className={style.ticker}>
                {alerts.map((alert, idx) => (
                  <div key={idx} className={style.tickerItem}>
                    • {alert}
                  </div>
                ))}
              </div>
            </div>

            <button
              className="btn-close btn-close-white position-absolute end-0 me-3 z-2"
              onClick={() => setShowAlerts(false)}
            ></button>
          </div>
        </div>
      )}

      {/* --- MAIN NAVBAR --- */}
      <nav className={`navbar navbar-expand-lg sticky-top ${style.glassNavbar}`}>
        <div className="container">
          {/* LOGO */}
          <Link className="navbar-brand d-flex align-items-center fw-bold fs-4 text-white" to="/">
            <div
              className="bg-primary rounded-circle p-1 me-2 d-flex align-items-center justify-content-center"
              style={{ width: 40, height: 40 }}
            >
              <img
                src="https://cdn-icons-png.flaticon.com/512/6956/6956763.png"
                height="28"
                width="28"
                alt="Logo"
                style={{ filter: "brightness(0) invert(1)" }}
              />
            </div>
            Job<span className="text-primary">.Portal</span>
          </Link>

          {/* MOBILE TOGGLE */}
          <button
            className="navbar-toggler border-0 text-white"
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
          >
            {isMobileMenuOpen ? <FaTimes size={24} /> : <FaBars size={24} />}
          </button>

          {/* NAV LINKS */}
          <div className={`collapse navbar-collapse ${isMobileMenuOpen ? "show" : ""}`}>
            <ul className="navbar-nav ms-auto align-items-lg-center gap-2">
              {["Jobs", "Internships", "Courses", "Contact"].map((item) => (
                <li className="nav-item" key={item}>
                  <Link
                    className={`${style.navLink} nav-link`}
                    to={`/${item.toLowerCase()}`}
                    onClick={closeMenus}
                  >
                    {item}
                  </Link>
                </li>
              ))}

              {/* NOTIFICATIONS */}
              {isAuthenticated && (
                <li className="nav-item position-relative mx-2" ref={notificationRef}>
                  <button
                    className="btn btn-link text-white p-2 position-relative"
                    onClick={() => setNotificationOpen(!notificationOpen)}
                  >
                    <MdMessage size={24} />
                    <span className={style.redDot}></span>
                  </button>

                  {notificationOpen && (
                    <div
                      className={`${style.glassDropdown} dropdown-menu show`}
                      style={{ right: 0, minWidth: "280px" }}
                    >
                      <h6 className="dropdown-header text-white">Notifications</h6>
                      <Link className="dropdown-item" to="/messages">📨 New Message</Link>
                      <Link className="dropdown-item" to="/jobs">📢 Job Alerts</Link>
                    </div>
                  )}
                </li>
              )}

              {/* PROFILE / LOGIN */}
              <li className="nav-item position-relative" ref={dropdownRef}>
                {!isAuthenticated ? (
                  <Link to="/login">
                    <button className="btn btn-primary rounded-pill px-4 fw-bold">
                      <FaUserCircle className="me-2" /> Login
                    </button>
                  </Link>
                ) : (
                  <>
                    <button
                      className="btn p-0 border-0"
                      onClick={() => setDropdownOpen(!dropdownOpen)}
                    >
                      <img
                        src={
                          user?.profileImage ||
                          "https://cdn-icons-png.flaticon.com/512/149/149071.png"
                        }
                        className={`rounded-circle ${style.profileImg}`}
                        width="42"
                        height="42"
                        alt="profile"
                      />
                    </button>

                    {dropdownOpen && (
                      <div className={`${style.glassDropdown} dropdown-menu show`} style={{ right: 0 }}>
                        <div className="dropdown-header text-white fw-bold">
                          {user?.fullName || "User"}
                        </div>

                        <Link className="dropdown-item" to="/dashboard">
                          <MdDashboard className="me-2" /> Dashboard
                        </Link>
                        <Link className="dropdown-item" to="/profile">
                          <MdPerson className="me-2" /> Profile
                        </Link>
                        <Link className="dropdown-item" to="/settings">
                          <MdSettings className="me-2" /> Settings
                        </Link>

                        <div className="dropdown-divider"></div>

                        <button
                          className="dropdown-item text-danger fw-bold"
                          onClick={handleLogout}
                        >
                          <MdLogout className="me-2" /> Logout
                        </button>
                      </div>
                    )}
                  </>
                )}
              </li>
            </ul>
          </div>
        </div>
      </nav>
    </>
  );
}

export default Navbar;
