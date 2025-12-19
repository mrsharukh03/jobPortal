import { FaUserCircle } from "react-icons/fa";
import style from "../css/Navbar.module.css";
import { Link } from "react-router-dom";
import { useEffect, useState, useRef } from "react";
import { checkLoginStatus } from "../Utilitys/help";
import { MdMessage } from "react-icons/md";
import { GoDotFill } from "react-icons/go";
import axiosInstance from "../Utilitys/axiosInstance";

function Navbar() {
  const [isAuthenticated, setIsAuthenticated] = useState(null);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [notificationOpen, setNotificationOpen] = useState(false);
  const [showAlerts, setShowAlerts] = useState(false);
  const [alerts, setAlerts] = useState([]);
  const [profileImage, setProfileImage] = useState(null); 

  const dropdownRef = useRef(null);
  const notificationRef = useRef(null);

  useEffect(() => {
    const fetchAlerts = async () => {
      try {
        const response = await axiosInstance.get("/user/alerts");
        const data = response.data;
        if (Array.isArray(data)) {
          setAlerts(data);
          setShowAlerts(data.length > 0);
        } else {
          console.error("Alerts data is not an array:", data);
        }
      } catch (error) {
        console.error("Error fetching alerts:", error);
      }
    };

    fetchAlerts();
  }, []);

  useEffect(() => {
    const checkAuth = async () => {
      const result = await checkLoginStatus();
      setIsAuthenticated(result.authenticated);
    };
    checkAuth();
  }, []);

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

  useEffect(() => {
    function handleClickOutside(event) {
      const clickedOutsideDropdown =
        dropdownRef.current && !dropdownRef.current.contains(event.target);
      const clickedOutsideNotification =
        notificationRef.current && !notificationRef.current.contains(event.target);

      if (clickedOutsideDropdown && clickedOutsideNotification) {
        setDropdownOpen(false);
        setNotificationOpen(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  return (
    <div>
      {isAuthenticated && showAlerts && (
        <div className="bg text-danger d-flex justify-content-between align-items-center">
          <marquee behavior="scroll" direction="left">
            <ul className={`mb-0 d-flex ${style.alertlist}`}>
              <li className="me-4 fw-bold">Info - </li>
              {alerts?.map((alert, index) => (
                <li key={index} className="me-4">
                  {alert}
                </li>
              ))}
            </ul>
          </marquee>
          <button
            className="btn-close me-3"
            onClick={() => setShowAlerts(false)}
          ></button>
        </div>
      )}

      <nav className={`navbar navbar-expand-lg mt-4 shadow-sm fixed-top ${style["glass-navbar"]}`}>
        <div className="container">
          <Link className="navbar-brand d-flex align-items-center fw-bold fs-4 text-white" to="/">
            <img
              src="https://cdn-icons-png.flaticon.com/512/6956/6956763.png"
              height="40"
              width="40"
              alt="Logo"
              className="me-2"
            />
            Job.Portal
          </Link>
          <button
            className="navbar-toggler border-0"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#navbarContent"
            aria-controls="navbarContent"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <span className="navbar-toggler-icon"></span>
          </button>

          <div className="collapse navbar-collapse" id="navbarContent">
            <ul className="navbar-nav ms-auto mb-2 mb-lg-0 align-items-lg-center">
              <li className="nav-item px-2">
                <a className="nav-link text-white fw-medium" href="#">Jobs</a>
              </li>
              <li className="nav-item px-2">
                <a className="nav-link text-white fw-medium" href="#">Internships</a>
              </li>
              <li className="nav-item px-2">
                <a className="nav-link text-white fw-medium" href="#">Courses</a>
              </li>
              <li className="nav-item px-2">
                <a className="nav-link text-white fw-medium" href="#">Contact</a>
              </li>

              <li className="nav-item ms-lg-4 mt-2 mt-lg-0 position-relative" ref={dropdownRef}>
                {!isAuthenticated ? (
                  <Link to="/login">
                    <button className="btn btn-light rounded-pill px-4 d-flex align-items-center">
                      <FaUserCircle className="me-2" />
                      Login
                    </button>
                  </Link>
                ) : (
                  <>
                    <button
                      onClick={() => setDropdownOpen((prev) => !prev)}
                      className="btn btn-light rounded-circle d-flex align-items-center justify-content-center p-0"
                      style={{ width: "40px", height: "40px" }}
                    >
                      <img
                        src={profileImage || "/src/assets/default-profile.png"}
                        alt="Profile"
                        className="rounded-circle"
                        style={{
                          width: "36px",
                          height: "36px",
                          objectFit: "cover",
                        }}
                      />
                    </button>

                    {dropdownOpen && (
                      <ul
                        className="dropdown-menu dropdown-menu-end show"
                        style={{
                          position: "absolute",
                          top: "50px",
                          right: 0,
                          minWidth: "150px",
                        }}
                      >
                        <li>
                          <Link className="dropdown-item" to="/dashboard" onClick={() => setDropdownOpen(false)}>
                            Dashboard
                          </Link>
                        </li>
                        <li>
                          <Link className="dropdown-item" to="/profile" onClick={() => setDropdownOpen(false)}>
                            View Profile
                          </Link>
                        </li>
                        <li>
                          <Link className="dropdown-item" to="/settings" onClick={() => setDropdownOpen(false)}>
                            Settings
                          </Link>
                        </li>
                        <li>
                          <hr className="dropdown-divider" />
                        </li>
                        <li>
                          <Link to="/logout">
                            <button className="dropdown-item text-danger text-center">
                              Logout
                            </button>
                          </Link>
                        </li>
                      </ul>
                    )}
                  </>
                )}
              </li>

              {isAuthenticated && (
                <li className="nav-item ms-lg-4 mt-2 mt-lg-0 position-relative" ref={notificationRef}>
                  <div className="position-relative">
                    <p className="position-absolute text-danger" style={{ top: "-5px", right: "-5px" }}>
                      <GoDotFill size={20} />
                    </p>
                    <button
                      className="btn"
                      style={{ width: "50px", height: "50px" }}
                      aria-haspopup="true"
                      onClick={() => setNotificationOpen((prev) => !prev)}
                    >
                      <MdMessage size={28} color="white" />
                    </button>

                    {notificationOpen && (
                      <ul
                        className="dropdown-menu dropdown-menu-end show"
                        style={{
                          position: "absolute",
                          top: "50px",
                          right: 0,
                          minWidth: "250px",
                        }}
                        onClick={(e) => e.stopPropagation()}
                      >
                        <li>
                          <Link className="dropdown-item" to="/messages" onClick={() => setNotificationOpen(false)}>
                            📨 New Message
                          </Link>
                        </li>
                        <li>
                          <Link className="dropdown-item" to="/jobs" onClick={() => setNotificationOpen(false)}>
                            📢 3 new job alerts
                          </Link>
                        </li>
                        <li>
                          <Link className="dropdown-item" to="/profile" onClick={() => setNotificationOpen(false)}>
                            ⚙️ Profile update needed
                          </Link>
                        </li>
                        <li>
                          <hr className="dropdown-divider" />
                        </li>
                        <li>
                          <Link className="dropdown-item text-center text-primary" to="/notifications" onClick={() => setNotificationOpen(false)}>
                            View All
                          </Link>
                        </li>
                      </ul>
                    )}
                  </div>
                </li>
              )}
            </ul>
          </div>
        </div>
      </nav>
    </div>
  );
}

export default Navbar;
