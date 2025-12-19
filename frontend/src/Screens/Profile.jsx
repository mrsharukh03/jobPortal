import React, { useEffect, useState } from "react";
import axiosInstance from "../Utilitys/axiosInstance";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import UserRoleSeekerOrRecruiter from "../components/UserRoleSeekerOrRecruiter";
import FillJobSeekerProfile from "../components/FillJobSeekerProfile";
import FillJobRecruiterProfile from "../components/FillJobRecruiterProfile";
import styles from "../css/Profile.module.css";
import RecruiterProfileView from "../components/RecruiterProfileView";

export default function Profile() {
  const [roleData, setRoleData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchUserRoles = async () => {
      try {
        const response = await axiosInstance.get("/user/profileStatusAndRole");
        setRoleData(response.data);
        setLoading(false);
      } catch (err) {
        console.error("Error fetching roles:", err);
        setError("Failed to fetch user roles. Please login again.");
        setLoading(false);
      }
    };

    fetchUserRoles();
  }, []);

  if (loading)
    return (
      <div className="d-flex justify-content-center align-items-center vh-100">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );

  if (error) return <div className="p-3 text-danger">{error}</div>;

  const showProfileForm = roleData?.profileCompletion === "false";

  return (
    <div>
      <Navbar />
      <main className={`${styles.main}`}>
        {showProfileForm ? (
          <>
            {/* Role selection component (only for general user) */}
            {roleData?.role === "USER" && <UserRoleSeekerOrRecruiter />}

            {/* Job Seeker profile form */}
            {roleData?.role === "SEEKER" && <FillJobSeekerProfile />}

            {/* Job Recruiter profile form */}
            {roleData?.role === "RECRUITER" && <FillJobRecruiterProfile />}
          </>
        ) : (
          <div className="text-center mt-5">
            {roleData?.role === "RECRUITER" && <RecruiterProfileView />}
            {roleData?.role === "SEEKER" && <h3>Hello Seeker! Your profile is complete.</h3>}
            {roleData?.role === "USER" && <h3>Welcome! Please choose your role.</h3>}
          </div>
        )}
      </main>
      <Footer />
    </div>
  );
}
