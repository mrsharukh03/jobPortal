import React, { useState } from "react";
import axiosInstance from "../contexts/axiosInstance";
import { FaBuilding, FaUserTie, FaCheckCircle, FaRegCircle, FaExclamationTriangle } from "react-icons/fa";

function UserRoleSeekerOrRecruiter() {
  const [loading, setLoading] = useState(false);
  const [hoveredRole, setHoveredRole] = useState(null);
  const [errorMessage, setErrorMessage] = useState(""); // Error store karne ke liye

  // --- API CALL HANDLER ---
  const handleRoleSelection = async (role) => {
    setLoading(true);
    setErrorMessage(""); // Purana error hatao

    try {
      // Backend: @PostMapping("/assign-role") with @PathParam("role")
      // URL banega: /user/assign-role?role=SEEKER
      const response = await axiosInstance.post(`/user/assign-role?role=${role}`);
      
      // Agar status 201 (CREATED) hai
      if (response.status === 201 || response.status === 200) {
        window.location.reload(); // Success hone par page reload
      }

    } catch (error) {
      console.error(`Error assigning ${role} role:`, error);
      
      // Backend se jo message aa raha hai (e.g. "Invalid User Type") wo show karo
      if (error.response && error.response.data) {
        setErrorMessage(error.response.data); 
      } else {
        setErrorMessage("Something went wrong. Please check your connection.");
      }
    } finally {
      setLoading(false);
    }
  };

  // --- STYLES ---
  const getCardStyle = (role) => {
    const isHovered = hoveredRole === role;
    const isRecruiter = role === 'RECRUITER';
    
    return {
      cursor: "pointer",
      transition: "all 0.3s ease-in-out",
      transform: isHovered ? "translateY(-10px)" : "translateY(0)",
      boxShadow: isHovered ? "0 1rem 3rem rgba(0,0,0,.15)" : "0 .5rem 1rem rgba(0,0,0,.05)",
      border: isHovered 
        ? `2px solid ${isRecruiter ? '#0d6efd' : '#198754'}` 
        : "2px solid #f0f0f0",
      backgroundColor: isHovered ? (isRecruiter ? '#f8faff' : '#f0fff4') : '#ffffff'
    };
  };

  return (
    <div className="min-vh-100 d-flex flex-column justify-content-center align-items-center bg-white py-5">
      
      {/* LOADING SPINNER */}
      {loading ? (
        <div className="text-center">
          <div className="spinner-border text-primary mb-3" style={{width: "3rem", height: "3rem"}} role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
          <h4 className="fw-light">Updating your profile...</h4>
        </div>
      ) : (
        
        <div className="container">
          
          {/* HEADER */}
          <div className="text-center mb-4">
            <h1 className="fw-bold text-dark mb-2">What brings you here today?</h1>
            <p className="text-muted fs-5">Select your role to continue.</p>
          </div>

          {/* ERROR ALERT DISPLAY */}
          {errorMessage && (
            <div className="row justify-content-center mb-4 animate-fade-in">
              <div className="col-12 col-md-8 col-lg-6">
                <div className="alert alert-danger d-flex align-items-center rounded-3 shadow-sm" role="alert">
                  <FaExclamationTriangle className="me-2 fs-5" />
                  <div>{errorMessage}</div>
                </div>
              </div>
            </div>
          )}

          <div className="row justify-content-center g-4">
            
            {/* === 1. RECRUITER CARD (Job Dene Wala) === */}
            <div className="col-12 col-md-6 col-lg-5 col-xl-4">
              <div 
                className="card h-100 p-4 rounded-4 position-relative"
                style={getCardStyle('RECRUITER')}
                onMouseEnter={() => setHoveredRole('RECRUITER')}
                onMouseLeave={() => setHoveredRole(null)}
                onClick={() => handleRoleSelection("RECRUITER")}
              >
                {/* Radio Circle UI */}
                <div className="position-absolute top-0 end-0 m-4">
                  {hoveredRole === 'RECRUITER' ? 
                    <FaCheckCircle className="text-primary fs-3" /> : 
                    <FaRegCircle className="text-muted fs-3 opacity-25" />
                  }
                </div>

                <div className="card-body text-start mt-2">
                  <div className="mb-4">
                    <div className={`rounded-4 d-flex align-items-center justify-content-center ${hoveredRole === 'RECRUITER' ? 'bg-primary text-white' : 'bg-light text-secondary'}`} 
                         style={{ width: "80px", height: "80px", transition: "all 0.3s" }}>
                      <FaBuilding className="fs-1" />
                    </div>
                  </div>
                  
                  <h3 className="card-title fw-bold mb-3">I'm here to Hire</h3>
                  <p className="card-text text-muted mb-0 fs-6">
                    I am a <strong>Recruiter / Employer</strong>. I want to post jobs, manage applications, and find talent for my company.
                  </p>
                </div>
              </div>
            </div>

            {/* === 2. SEEKER CARD (Job Lene Wala) === */}
            <div className="col-12 col-md-6 col-lg-5 col-xl-4">
              <div 
                className="card h-100 p-4 rounded-4 position-relative"
                style={getCardStyle('SEEKER')}
                onMouseEnter={() => setHoveredRole('SEEKER')}
                onMouseLeave={() => setHoveredRole(null)}
                onClick={() => handleRoleSelection("SEEKER")}
              >
                 {/* Radio Circle UI */}
                 <div className="position-absolute top-0 end-0 m-4">
                  {hoveredRole === 'SEEKER' ? 
                    <FaCheckCircle className="text-success fs-3" /> : 
                    <FaRegCircle className="text-muted fs-3 opacity-25" />
                  }
                </div>

                <div className="card-body text-start mt-2">
                  <div className="mb-4">
                    <div className={`rounded-4 d-flex align-items-center justify-content-center ${hoveredRole === 'SEEKER' ? 'bg-success text-white' : 'bg-light text-secondary'}`} 
                         style={{ width: "80px", height: "80px", transition: "all 0.3s" }}>
                      <FaUserTie className="fs-1" />
                    </div>
                  </div>

                  <h3 className="card-title fw-bold mb-3">I'm looking for a Job</h3>
                  <p className="card-text text-muted mb-0 fs-6">
                    I am a <strong>Job Seeker</strong>. I want to build my profile, upload my resume, and apply to top companies.
                  </p>
                </div>
              </div>
            </div>

          </div>
        </div>
      )}
    </div>
  );
}

export default UserRoleSeekerOrRecruiter;