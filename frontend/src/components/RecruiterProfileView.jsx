import React, { useEffect, useState } from "react";
import axiosInstance from "../Utilitys/axiosInstance";
import {
  FaLinkedin,
  FaGlobe,
  FaEnvelope,
  FaCheckCircle,
  FaUserEdit,
  FaMoon,
  FaSun,
  FaToggleOn,
  FaToggleOff,
} from "react-icons/fa";

/* ===== Helper ===== */
const getInitials = (name = "") => {
  const parts = name.trim().split(" ");
  if (parts.length === 1) return parts[0][0]?.toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
};

export default function RecruiterProfileView() {
  const [recruiter, setRecruiter] = useState(null);
  const [user, setUser] = useState(null);
  const [dark, setDark] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAll = async () => {
      const r = await axiosInstance.get("/recruiter/profile");
      const u = await axiosInstance.get("/user/profile");

      setRecruiter(r.data);
      setUser(u.data);
      setLoading(false);
    };
    fetchAll();
  }, []);

  const toggleActive = async () => {
    await axiosInstance.post("/user/toggle-active");
    setUser({ ...user, active: !user.active });
  };

  if (loading)
    return (
      <div className="vh-100 d-flex justify-content-center align-items-center">
        <div className="spinner-border text-primary" />
      </div>
    );

  return (
    <div className={`min-vh-100 ${dark ? "bg-dark text-light" : "bg-light"}`}>
      <div className="container py-4">

        {/* ===== TOP BAR ===== */}
        <div className="card shadow-sm mb-4">
          <div className="card-body d-flex justify-content-between align-items-center flex-wrap">

            {/* USER INFO */}
            <div className="d-flex align-items-center">
              <div className="me-3 position-relative">
                {user.profileURL ? (
                  <img
                    src={user.profileURL}
                    alt="avatar"
                    className="rounded-circle border"
                    width="80"
                    height="80"
                    style={{ objectFit: "cover" }}
                  />
                ) : (
                  <div
                    className="rounded-circle bg-primary text-white d-flex justify-content-center align-items-center fw-bold"
                    style={{ width: 80, height: 80, fontSize: 28 }}
                  >
                    {getInitials(user.fullName)}
                  </div>
                )}

                {!user.active && (
                  <span className="badge bg-danger position-absolute top-0 start-100 translate-middle">
                    Blocked
                  </span>
                )}
              </div>

              <div>
                <h5 className="mb-1 fw-bold">
                  {user.fullName}
                  {user.verified && (
                    <FaCheckCircle className="text-primary ms-2" />
                  )}
                </h5>
                <small className="text-muted">
                  <FaEnvelope className="me-1" /> {user.email}
                </small>
              </div>
            </div>

            {/* ACTION BUTTONS */}
            <div className="d-flex gap-2 mt-3 mt-md-0">
              <button
                className="btn btn-outline-secondary btn-sm"
                onClick={() => setDark(!dark)}
              >
                {dark ? <FaSun /> : <FaMoon />}
              </button>

              <button
                className={`btn btn-sm ${
                  user.active ? "btn-success" : "btn-danger"
                }`}
                onClick={toggleActive}
              >
                {user.active ? <FaToggleOn /> : <FaToggleOff />}
              </button>

              <button
                className="btn btn-outline-primary btn-sm"
                data-bs-toggle="modal"
                data-bs-target="#editModal"
              >
                <FaUserEdit /> Edit
              </button>
            </div>
          </div>
        </div>

        {/* ===== COMPANY DETAILS ===== */}
        <div className="card shadow-sm mb-4">
          <div className="card-header fw-bold">Company Information</div>
          <div className="card-body row">
            <div className="col-md-6">
              <p><strong>Name:</strong> {recruiter.companyName}</p>
              <p><strong>Designation:</strong> {recruiter.designation}</p>
              <p><strong>Location:</strong> {recruiter.location}</p>
            </div>
            <div className="col-md-6">
              <p><strong>Industry:</strong> {recruiter.industry}</p>
              <p><strong>Size:</strong> {recruiter.companySize}</p>

              <div className="mt-2">
                {recruiter.linkedInProfile && (
                  <a
                    href={recruiter.linkedInProfile}
                    className="btn btn-sm btn-primary me-2"
                    target="_blank"
                    rel="noreferrer"
                  >
                    <FaLinkedin /> LinkedIn
                  </a>
                )}
                {recruiter.companyWebsite && (
                  <a
                    href={recruiter.companyWebsite}
                    className="btn btn-sm btn-success"
                    target="_blank"
                    rel="noreferrer"
                  >
                    <FaGlobe /> Website
                  </a>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* ===== RECRUITER INFO ===== */}
        <div className="card shadow-sm">
          <div className="card-header d-flex justify-content-between fw-bold">
            Recruiter Info
            <span
              className={`badge ${
                recruiter.profileComplete ? "bg-success" : "bg-warning"
              }`}
            >
              {recruiter.profileComplete ? "Complete" : "Incomplete"}
            </span>
          </div>
          <div className="card-body">
            <p><strong>Experience:</strong> {recruiter.yearsOfExperience} years</p>
            <p><strong>About:</strong> {recruiter.about}</p>
            <p><strong>Skills:</strong> {recruiter.hiringSkills?.join(", ")}</p>
          </div>
        </div>
      </div>

      {/* ===== EDIT MODAL ===== */}
      <div className="modal fade" id="editModal">
        <div className="modal-dialog modal-dialog-centered">
          <div className="modal-content">
            <div className="modal-header">
              <h5>Edit Profile</h5>
              <button className="btn-close" data-bs-dismiss="modal" />
            </div>
            <div className="modal-body">
              <input className="form-control mb-2" placeholder="Full Name" />
              <input className="form-control mb-2" placeholder="Company Name" />
              <textarea className="form-control" placeholder="About" />
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" data-bs-dismiss="modal">
                Cancel
              </button>
              <button className="btn btn-primary">Save</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
