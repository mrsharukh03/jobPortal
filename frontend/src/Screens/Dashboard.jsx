import { useEffect, useState } from "react";
import RecruiterDashboard from "../components/RecruiterDashboard";
import SeekerDashboard from "../components/SeekerDashboard";
import axiosInstance from "../contexts/axiosInstance";

export default function Dashboard() {

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
    const rawRole = String(roleData?.role || ""); 
    
    let userRole = "USER";
    if (rawRole.includes("RECRUITER")) {
        userRole = "RECRUITER";
    } else if (rawRole.includes("SEEKER")) {
        userRole = "SEEKER";
    }
  return (
    <div>
      {
        userRole === "SEEKER" ? <SeekerDashboard/>:<RecruiterDashboard/>
      }
    </div>
  );
}


