import React, { useEffect, useState } from "react";
import axiosInstance from "../contexts/axiosInstance";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import UserRoleSeekerOrRecruiter from "../components/UserRoleSeekerOrRecruiter";
import FillJobSeekerProfile from "../components/FillJobSeekerProfile";
import FillJobRecruiterProfile from "../components/FillJobRecruiterProfile";
import RecruiterProfileView from "../components/RecruiterProfileView";
import SeekerProfileView from "../components/SeekerProfileView";

export default function Profile() {
  const [roleData, setRoleData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchUserRoles = async () => {
      try {
        const response = await axiosInstance.get("/user/profileStatusAndRole");
        console.log("Profile Status:", response.data); 
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

  // 🛠️ FIX START: Role Parsing Logic
  // Backend "[USER, SEEKER]" bhej raha hai, isliye hum check karenge ki usme kya included hai
  const rawRole = String(roleData?.role || ""); // Convert to string safely
  const isProfileComplete = roleData?.isProfileComplete === true;
  
  let userRole = "USER";
  if (rawRole.includes("RECRUITER")) {
      userRole = "RECRUITER";
  } else if (rawRole.includes("SEEKER")) {
      userRole = "SEEKER";
  }
  // 🛠️ FIX END

  // Render Logic Helper
  const renderContent = () => {
      // CASE 1: Role Assigned but Profile Incomplete -> Show Fill Form
      if (!isProfileComplete) {
          if (userRole === "SEEKER") return <FillJobSeekerProfile />;
          if (userRole === "RECRUITER") return <FillJobRecruiterProfile />;
      }

      // CASE 2: Profile Complete -> Show View Profile
      if (userRole === "SEEKER") {
          return (
            <div className="container mt-5 text-center">
              <SeekerProfileView/>
            </div>
          );
      }
      if (userRole === "RECRUITER") {
          return <RecruiterProfileView />;
      }

      // CASE 3: New User (Default fallback)
      return <UserRoleSeekerOrRecruiter />;
  };

  return (
    <div>
      <Navbar />
      <main style={{ minHeight: "80vh" }}>
        {renderContent()}
      </main>
      <Footer />
    </div>
  );
}