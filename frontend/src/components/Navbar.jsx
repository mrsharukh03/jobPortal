import { FaUserCircle, FaBars, FaTimes } from "react-icons/fa";
import style from "../css/Navbar.module.css";
import { Link } from "react-router-dom";
import { useEffect, useState, useRef } from "react";
import { checkLoginStatus } from "../Utilitys/help";
import { MdMessage, MdDashboard, MdSettings, MdLogout, MdPerson } from "react-icons/md";
import axiosInstance from "../Utilitys/axiosInstance";

function Navbar() {
  // State Management
  const [isAuthenticated, setIsAuthenticated] = useState(null);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [notificationOpen, setNotificationOpen] = useState(false);
  const [showAlerts, setShowAlerts] = useState(false);
  const [alerts, setAlerts] = useState([]);
  const [profileImage, setProfileImage] = useState(null);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  // Refs for clicking outside
  const dropdownRef = useRef(null);
  const notificationRef = useRef(null);

  // 1. Fetch Alerts
  useEffect(() => {
    const fetchAlerts = async () => {
      try {
        const response = await axiosInstance.get("/user/alerts");
        const data = response.data;
        if (Array.isArray(data)) {
          setAlerts(data);
          setShowAlerts(data.length > 0);
        }
      } catch (error) {
        console.error("Error fetching alerts:", error);
      }
    };
    fetchAlerts();
  }, []);

  // 2. Check Auth Status
  useEffect(() => {
    const checkAuth = async () => {
      const result = await checkLoginStatus();
      setIsAuthenticated(result.authenticated);
    };
    checkAuth();
  }, []);

  // 3. Fetch User Profile
  useEffect(() => {
    const fetchUserProfile = async () => {
      try {
        const res = await axiosInstance.get("/user/profileUrl");
        setProfileImage(res.data?.profileImage || null);
      } catch (err) {
        console.error("Profile fetch error", err);
      }
    };

    if (isAuthenticated) {
      fetchUserProfile();
    }
  }, [isAuthenticated]);

  // 4. Handle Click Outside to close dropdowns
  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setDropdownOpen(false);
      }
      if (notificationRef.current && !notificationRef.current.contains(event.target)) {
        setNotificationOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Helper to close all menus
  const closeMenus = () => {
    setDropdownOpen(false);
    setNotificationOpen(false);
    setIsMobileMenuOpen(false);
  };

  return (
    <>
      {/* --- TOP ALERT BAR (Replaced Marquee with CSS Animation) --- */}
      {isAuthenticated && showAlerts && (
        <div className={style.alertBar}>
          <div className="container-fluid d-flex align-items-center position-relative h-100">
            <span className="badge bg-white text-danger fw-bold ms-3 me-3 z-2">IMPORTANT</span>
            <div className={style.tickerWrap}>
              <div className={style.ticker}>
                {alerts?.map((alert, index) => (
                  <div key={index} className={style.tickerItem}>
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
          {/* Logo */}
          <Link className="navbar-brand d-flex align-items-center fw-bold fs-4 text-white" to="/">
            <div className="bg-primary rounded-circle p-1 me-2 d-flex align-items-center justify-content-center" style={{width: 40, height: 40}}>
                 <img
                  src="https://cdn-icons-png.flaticon.com/512/6956/6956763.png"
                  height="28"
                  width="28"
                  alt="Logo"
                  style={{filter: 'brightness(0) invert(1)'}}
                />
            </div>
            <span style={{letterSpacing: '-0.5px'}}>Job<span className="text-primary">.Portal</span></span>
          </Link>

          {/* Mobile Toggler */}
          <button
            className="navbar-toggler border-0 text-white"
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            type="button"
          >
            {isMobileMenuOpen ? <FaTimes size={24} /> : <FaBars size={24} />}
          </button>

          {/* Nav Content */}
          <div className={`collapse navbar-collapse ${isMobileMenuOpen ? "show" : ""}`}>
            <ul className="navbar-nav ms-auto mb-2 mb-lg-0 align-items-lg-center gap-2">
              {/* Navigation Links */}
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

              {/* Spacer */}
              <li className="d-none d-lg-block mx-2 border-end border-secondary border-opacity-50" style={{height: '24px'}}></li>

              {/* Notification Bell */}
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
                    <div className={`${style.glassDropdown} dropdown-menu show`} style={{ position: "absolute", right: 0, minWidth: "280px" }}>
                      <div className="px-3 py-2 border-bottom border-secondary border-opacity-25">
                         <h6 className="mb-0 text-white fw-bold">Notifications</h6>
                      </div>
                      <Link className={`${style.dropdownItem} dropdown-item`} to="/messages" onClick={closeMenus}>
                        <span className="me-2">📨</span> New Message
                      </Link>
                      <Link className={`${style.dropdownItem} dropdown-item`} to="/jobs" onClick={closeMenus}>
                        <span className="me-2">📢</span> 3 new job alerts
                      </Link>
                      <Link className={`${style.dropdownItem} dropdown-item`} to="/profile" onClick={closeMenus}>
                        <span className="me-2">⚙️</span> Profile update needed
                      </Link>
                      <div className="dropdown-divider border-secondary border-opacity-25"></div>
                      <Link className="dropdown-item text-center text-primary fw-bold small py-2" to="/notifications" onClick={closeMenus}>
                        View All Activity
                      </Link>
                    </div>
                  )}
                </li>
              )}

              {/* Profile Dropdown or Login */}
              <li className="nav-item ms-lg-2 position-relative" ref={dropdownRef}>
                {!isAuthenticated ? (
                  <Link to="/login" onClick={closeMenus}>
                    <button className="btn btn-primary rounded-pill px-4 fw-bold shadow-sm d-flex align-items-center">
                      <FaUserCircle className="me-2" /> Login
                    </button>
                  </Link>
                ) : (
                  <>
                    <button
                      onClick={() => setDropdownOpen(!dropdownOpen)}
                      className="btn p-0 border-0 d-flex align-items-center"
                    >
                      <img
                        src={profileImage || "https://cdn-icons-png.flaticon.com/512/149/149071.png"}
                        alt="Profile"
                        className={`rounded-circle ${style.profileImg}`}
                        style={{ width: "42px", height: "42px", objectFit: "cover" }}
                      />
                    </button>

                    {dropdownOpen && (
                      <div className={`${style.glassDropdown} dropdown-menu show`} style={{ position: "absolute", right: 0, minWidth: "200px" }}>
                        <div className="px-3 py-2 text-center border-bottom border-secondary border-opacity-25 mb-2">
                             <p className="text-white fw-bold mb-0">Hello</p>
                             <small className="text-muted" style={{fontSize: '0.75rem'}}>View Profile</small>
                        </div>
                        
                        <Link className={`${style.dropdownItem} dropdown-item d-flex align-items-center`} to="/dashboard" onClick={closeMenus}>
                          <MdDashboard className="me-2" /> Dashboard
                        </Link>
                        <Link className={`${style.dropdownItem} dropdown-item d-flex align-items-center`} to="/profile" onClick={closeMenus}>
                          <MdPerson className="me-2" /> My Profile
                        </Link>
                        <Link className={`${style.dropdownItem} dropdown-item d-flex align-items-center`} to="/settings" onClick={closeMenus}>
                          <MdSettings className="me-2" /> Settings
                        </Link>
                        
                        <div className="dropdown-divider border-secondary border-opacity-25"></div>
                        
                        <Link to="/logout" onClick={closeMenus}>
                          <button className={`${style.dropdownItem} dropdown-item text-danger fw-bold d-flex align-items-center w-100`}>
                            <MdLogout className="me-2" /> Logout
                          </button>
                        </Link>
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