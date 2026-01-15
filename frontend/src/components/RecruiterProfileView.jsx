import React, { useEffect, useState } from "react";
import axiosInstance from "../Utilitys/axiosInstance";
import {
  FaLinkedin, FaTwitter, FaGlobe, FaCheckCircle, FaCamera, 
  FaBriefcase, FaUsers, FaChartLine, FaCrown, FaBuilding, FaUserPlus, FaMapMarkerAlt, FaPen
} from "react-icons/fa";
import { MdEmail, MdPhone, MdVerified } from "react-icons/md";
import styles from "../css/Recruiter.module.css"; // Ensure path matches

/* ===== MOCK DATA (Replace with API data later) ===== */
const mockData = {
  bannerUrl: "https://images.unsplash.com/photo-1497215728101-856f4ea42174?auto=format&fit=crop&w=1950&q=80",
  socials: { linkedin: "#", twitter: "#", website: "#" },
  plan: {
    current: "Enterprise",
    billing: "Yearly",
    renewalDate: "24 Aug 2026",
  },
  usage: {
    jobPosts: { used: 12, total: 20 },
    cvViews: { used: 450, total: 1000 },
    emails: { used: 120, total: 500 }
  },
  team: [
    { name: "Rahul V.", role: "HR Manager", img: "https://i.pravatar.cc/150?u=1" },
    { name: "Sneha G.", role: "Tech Lead", img: "https://i.pravatar.cc/150?u=2" }
  ]
};

export default function RecruiterProfileView() {
  const [loading, setLoading] = useState(true);
  const [recruiter, setRecruiter] = useState(null);
  const [user, setUser] = useState(null);
  
  // UI States
  const [activeTab, setActiveTab] = useState("overview");
  const [planCycle, setPlanCycle] = useState("yearly"); // monthly | yearly

  useEffect(() => {
    const fetchAll = async () => {
      try {
        const r = await axiosInstance.get("/recruiter/profile");
        const u = await axiosInstance.get("/user/profile");
        setRecruiter(r.data);
        setUser(u.data);
        setLoading(false);
      } catch (error) {
        console.error("Error", error);
        setLoading(false);
      }
    };
    fetchAll();
  }, []);

  if (loading) return <div className="vh-100 d-flex justify-content-center align-items-center"><div className="spinner-border text-primary" /></div>;

  return (
    <div className={styles.pageContainer}>
      
      {/* 1. HERO HEADER */}
      <div className={styles.coverSection}>
        <button className="btn btn-light btn-sm position-absolute top-0 end-0 m-4 rounded-pill shadow-sm fw-bold">
          <FaCamera className="me-2"/> Edit Cover
        </button>
      </div>

      <div className="container px-4">
        <div className="row">
          {/* PROFILE IMAGE & INFO BLOCK */}
          <div className="col-12 text-center text-lg-start d-lg-flex align-items-end mb-4">
            <div className={styles.profileImageContainer}>
              <img 
                src={user.profileURL || "https://cdn-icons-png.flaticon.com/512/3135/3135715.png"} 
                className={`rounded-circle ${styles.profileImage}`} 
                alt="Profile" 
              />
              {user.verified && <MdVerified className={styles.verifiedBadge} title="Verified Business" />}
            </div>
            
            <div className="ms-lg-4 mt-3 mt-lg-0 mb-2 flex-grow-1">
              <h2 className="fw-bold mb-0 text-dark">{recruiter.companyName || user.fullName}</h2>
              <p className="text-muted mb-2">{recruiter.designation} • {recruiter.location || "India"}</p>
              
              <div className="d-flex justify-content-center justify-content-lg-start gap-3 mt-2">
                <a href={mockData.socials.linkedin} className="text-primary"><FaLinkedin size={20}/></a>
                <a href={mockData.socials.twitter} className="text-info"><FaTwitter size={20}/></a>
                <a href={mockData.socials.website} className="text-dark"><FaGlobe size={20}/></a>
              </div>
            </div>

            <div className="d-flex gap-2 mt-3 mt-lg-0">
               <button className="btn btn-outline-dark rounded-pill px-4 fw-bold">Public View</button>
               <button className="btn btn-primary rounded-pill px-4 fw-bold"><FaPen className="me-2"/> Edit Profile</button>
            </div>
          </div>
        </div>

        {/* 2. MAIN GRID LAYOUT */}
        <div className="row g-4">
          
          {/* LEFT SIDEBAR (Quick Stats & Verification) */}
          <div className="col-lg-3 order-2 order-lg-1">
            {/* Quick Stats */}
            <div className={`${styles.card} p-4 mb-4`}>
              <h6 className="fw-bold text-uppercase small text-muted mb-3">Dashboard Stats</h6>
              
              <div className="d-flex align-items-center mb-3">
                 <div className="bg-primary bg-opacity-10 p-2 rounded-3 text-primary me-3"><FaBriefcase/></div>
                 <div>
                    <h5 className="mb-0 fw-bold">12</h5>
                    <small className="text-muted">Active Jobs</small>
                 </div>
              </div>
              <div className="d-flex align-items-center mb-3">
                 <div className="bg-success bg-opacity-10 p-2 rounded-3 text-success me-3"><FaUsers/></div>
                 <div>
                    <h5 className="mb-0 fw-bold">850</h5>
                    <small className="text-muted">Total Applicants</small>
                 </div>
              </div>
              <div className="d-flex align-items-center">
                 <div className="bg-warning bg-opacity-10 p-2 rounded-3 text-warning me-3"><FaChartLine/></div>
                 <div>
                    <h5 className="mb-0 fw-bold">1.2k</h5>
                    <small className="text-muted">Profile Views</small>
                 </div>
              </div>
            </div>

            {/* Verification Widget */}
            <div className={`${styles.card} p-3 mb-4 border-start border-4 border-success`}>
               <div className="d-flex align-items-center">
                  <FaCheckCircle className="text-success fs-4 me-3"/>
                  <div>
                     <h6 className="fw-bold mb-0 text-dark">KYC Verified</h6>
                     <small className="text-muted">Your identity is verified.</small>
                  </div>
               </div>
            </div>
            
            {/* Contact Info */}
            <div className={`${styles.card} p-4`}>
               <h6 className="fw-bold text-uppercase small text-muted mb-3">Contact Details</h6>
               <p className="mb-2 small"><MdEmail className="me-2 text-primary"/> {user.email}</p>
               <p className="mb-2 small"><MdPhone className="me-2 text-primary"/> {user.phone || "Not added"}</p>
               <p className="mb-0 small"><FaMapMarkerAlt className="me-2 text-primary"/> {recruiter.location || "Remote"}</p>
            </div>
          </div>

          {/* MIDDLE CONTENT (Tabs & Details) */}
          <div className="col-lg-6 order-1 order-lg-2">
             <div className={`${styles.card} min-vh-50`}>
                {/* Custom Tabs */}
                <div className={styles.customTabs}>
                   <button onClick={() => setActiveTab('overview')} className={`${styles.tabBtn} ${activeTab === 'overview' ? styles.activeTab : ''}`}>Overview</button>
                   <button onClick={() => setActiveTab('jobs')} className={`${styles.tabBtn} ${activeTab === 'jobs' ? styles.activeTab : ''}`}>Posted Jobs</button>
                   <button onClick={() => setActiveTab('team')} className={`${styles.tabBtn} ${activeTab === 'team' ? styles.activeTab : ''}`}>Team</button>
                </div>

                <div className="p-4">
                    {/* OVERVIEW CONTENT */}
                    {activeTab === 'overview' && (
                        <div className="animate-fade">
                            <h5 className="fw-bold mb-3">About Company</h5>
                            <p className="text-muted mb-4" style={{lineHeight: '1.7'}}>
                                {recruiter.about || "We are a leading tech company focusing on AI and Blockchain solutions. We value innovation and creativity in our employees."}
                            </p>

                            <h5 className="fw-bold mb-3">Company Details</h5>
                            <div className="row g-3">
                                <div className="col-sm-6">
                                    <div className="p-3 bg-light rounded-3">
                                        <small className="text-muted d-block mb-1">Industry</small>
                                        <span className="fw-bold text-dark"><FaBuilding className="me-2 text-secondary"/> {recruiter.industry || "Technology"}</span>
                                    </div>
                                </div>
                                <div className="col-sm-6">
                                    <div className="p-3 bg-light rounded-3">
                                        <small className="text-muted d-block mb-1">Company Size</small>
                                        <span className="fw-bold text-dark"><FaUsers className="me-2 text-secondary"/> {recruiter.companySize || "50-100"} Employees</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                    
                    {/* TEAM CONTENT */}
                    {activeTab === 'team' && (
                         <div className="animate-fade">
                             <div className="d-flex justify-content-between mb-3">
                                 <h6 className="fw-bold">Hiring Managers</h6>
                                 <button className="btn btn-sm btn-light text-primary fw-bold"><FaUserPlus/> Add Member</button>
                             </div>
                             {mockData.team.map((m, i) => (
                                 <div key={i} className="d-flex align-items-center p-2 border-bottom">
                                     <img src={m.img} className="rounded-circle me-3" width="45" height="45" alt="user"/>
                                     <div>
                                         <h6 className="fw-bold mb-0">{m.name}</h6>
                                         <small className="text-muted">{m.role}</small>
                                     </div>
                                 </div>
                             ))}
                         </div>
                    )}
                    
                    {/* JOBS CONTENT PLACEHOLDER */}
                    {activeTab === 'jobs' && (
                        <div className="text-center py-5">
                            <div className="bg-light rounded-circle d-inline-block p-4 mb-3">
                                <FaBriefcase size={30} className="text-muted"/>
                            </div>
                            <h6>You have posted 12 jobs</h6>
                            <button className="btn btn-primary btn-sm rounded-pill mt-2">Manage Jobs</button>
                        </div>
                    )}
                </div>
             </div>
          </div>

          {/* RIGHT SIDEBAR (Premium Plan & Usage) */}
          <div className="col-lg-3 order-3 order-lg-3">
             
             {/* Plan Card */}
             <div className={`${styles.card} ${styles.premiumGradient} p-4 mb-4 text-center position-relative`}>
                <FaCrown className="text-white opacity-25 position-absolute top-0 start-0 m-2" size={100} style={{transform: 'rotate(-20deg)', left: '-20px'}} />
                
                <h6 className="text-uppercase text-white text-opacity-75 fw-bold ls-1 mb-2">Current Plan</h6>
                <h2 className="text-white fw-bold mb-3">{mockData.plan.current}</h2>
                
                {/* Monthly/Yearly Toggle */}
                <div className={styles.planToggle} onClick={() => setPlanCycle(planCycle === 'monthly' ? 'yearly' : 'monthly')}>
                    <div className={`${styles.planOption} ${planCycle === 'monthly' ? styles.active : ''}`}>Monthly</div>
                    <div className={`${styles.planOption} ${planCycle === 'yearly' ? styles.active : ''}`}>Yearly</div>
                </div>
                
                <p className="text-white text-opacity-75 small mt-3 mb-0">
                    Renews on: <strong>{mockData.plan.renewalDate}</strong>
                </p>
                <button className="btn btn-white text-primary w-100 mt-3 rounded-pill fw-bold shadow-sm">Upgrade Plan</button>
             </div>

             {/* Usage Stats (Replaces 'Credits Left') */}
             <div className={`${styles.card} p-4`}>
                <h6 className="fw-bold mb-4">Plan Usage</h6>
                
                {/* Item 1: Job Posts */}
                <div className="mb-4">
                    <div className="d-flex justify-content-between small mb-1">
                        <span className="text-muted">Job Posts</span>
                        <span className="fw-bold">{mockData.usage.jobPosts.used}/{mockData.usage.jobPosts.total}</span>
                    </div>
                    <div className={styles.usageBar}>
                        <div className={styles.usageFill} style={{width: '60%', background: '#4f46e5'}}></div>
                    </div>
                </div>

                {/* Item 2: CV Views */}
                <div className="mb-4">
                    <div className="d-flex justify-content-between small mb-1">
                        <span className="text-muted">CV Views</span>
                        <span className="fw-bold text-warning">{mockData.usage.cvViews.used} left</span>
                    </div>
                    <div className={styles.usageBar}>
                        <div className={styles.usageFill} style={{width: '45%', background: '#f59e0b'}}></div>
                    </div>
                </div>

                {/* Item 3: Emails */}
                <div>
                    <div className="d-flex justify-content-between small mb-1">
                        <span className="text-muted">InMail Credits</span>
                        <span className="fw-bold">{mockData.usage.emails.used}/{mockData.usage.emails.total}</span>
                    </div>
                    <div className={styles.usageBar}>
                        <div className={styles.usageFill} style={{width: '25%', background: '#10b981'}}></div>
                    </div>
                </div>

             </div>
          </div>

        </div>
      </div>
    </div>
  );
}