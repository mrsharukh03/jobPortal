import React from "react";
import axiosInstance from "../Utilitys/axiosInstance";

function UserRoleSeekerOrRecruiter() {
  const setRoleSeeker = async () => {
    try {
      await axiosInstance.post('/user/assign-role?role=SEEKER');
      alert("Role 'SEEKER' assigned successfully!");
      window.location.reload();
    } catch (error) {
      console.error("Error assigning SEEKER role:", error);
      alert("Failed to assign role SEEKER. Please try again.");
    }
  };

  const setRoleRecruiter = async () => {
    try {
      await axiosInstance.post('/user/assign-role?role=RECRUITER');
      alert("Role 'RECRUITER' assigned successfully!");
      window.location.reload();
    } catch (error) {
      console.error("Error assigning RECRUITER role:", error);
      alert("Failed to assign role RECRUITER. Please try again.");
    }
  };

  return (
    <div className="container mt-4 d-flex justify-content-center  align-items-center">
    <div className="card mx-auto shadow-sm"
      style={{ maxWidth: "400px", borderRadius: "12px" }}
    >
      <div className="card-body text-center">
        <h2 className="card-title mb-4">Welcome! Please select your role</h2>

        <div className="d-grid gap-3">
          <button
            className="btn btn-primary btn-lg"
            onClick={setRoleRecruiter}
            style={{ borderRadius: "8px" }}
          >
            I’m hiring (Recruiter)
          </button>

          <button
            className="btn btn-success btn-lg"
            onClick={setRoleSeeker}
            style={{ borderRadius: "8px" }}
          >
            I’m looking for a job (Seeker)
          </button>
        </div>
      </div>
    </div>
    </div>
  );
}

export default UserRoleSeekerOrRecruiter;
