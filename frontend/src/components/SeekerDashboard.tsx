import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import styles from '../css/RecruiterDashboard.module.css';
import Navbar from './Navbar.jsx';
import Footer from './Footer.jsx';
import axiosInstance from "../contexts/axiosInstance.js";

// --- Icons ---
import { 
  MdDashboard, MdSearch, MdAssignment, MdBookmark, 
  MdLocationOn, MdBusiness, MdCheckCircle, MdHourglassEmpty, MdCancel 
} from "react-icons/md";

const SeekerDashboard = () => {
  const [activeTab, setActiveTab] = useState('overview');
  const [stats, setStats] = useState({ applied: 0, interviews: 0, saved: 0 });
  
  // Data States
  const [appliedJobs, setAppliedJobs] = useState([]);
  const [availableJobs, setAvailableJobs] = useState([]);
  const [savedJobs, setSavedJobs] = useState([]);
  const [loading, setLoading] = useState(false);

  // --- 1. Fetch Dashboard Data ---
  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      // 1. Get Applied Jobs
      const appRes = await axiosInstance.get("/seeker/applications"); 
      setAppliedJobs(appRes.data || []);

      // 2. Get Saved Jobs (Wishlist)
      const savedRes = await axiosInstance.get("/seeker/saved-jobs"); 
      setSavedJobs(savedRes.data || []);

      // 3. Update Stats
      setStats({
        applied: appRes.data?.length || 0,
        interviews: appRes.data?.filter(j => j.status === 'INTERVIEW').length || 0,
        saved: savedRes.data?.length || 0
      });

    } catch (error) {
      console.error("Error fetching dashboard data:", error);
    } finally {
      setLoading(false);
    }
  };

  // --- 2. Fetch Available Jobs (For Search Tab) ---
  const fetchAllJobs = async () => {
    setLoading(true);
    try {
      const res = await axiosInstance.get("/public/jobs/all"); // Public or Protected route
      setAvailableJobs(res.data || []);
    } catch (error) {
      console.error("Error fetching jobs:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (activeTab === 'overview' || activeTab === 'applications') {
      fetchDashboardData();
    }
    if (activeTab === 'findJobs') {
      fetchAllJobs();
    }
  }, [activeTab]);

  return (
    <>
      <Navbar />
      
      <div className={styles.dashboardWrapper}>
        <div className="container-fluid container-xl">
          <div className="row g-4">
            
            {/* --- SIDEBAR (Left) --- */}
            <div className="col-lg-3 col-md-4 d-none d-md-block">
              <div className={styles.sidebarCard}>
                <div className="mb-4 px-2 d-flex align-items-center gap-2">
                    <div className="bg-success rounded-circle p-1 d-flex justify-content-center align-items-center" style={{width: 28, height: 28}}>
                       <span className="text-white fw-bold" style={{fontSize: 14}}>S</span>
                    </div>
                    <span className="text-white fw-bold">Seeker Panel</span>
                </div>

                <div className="d-flex flex-column flex-grow-1">
                  <div className={`${styles.navItem} ${activeTab === 'overview' ? styles.activeNav : ''}`} onClick={() => setActiveTab('overview')}>
                    <MdDashboard size={20} /> Overview
                  </div>
                  <div className={`${styles.navItem} ${activeTab === 'findJobs' ? styles.activeNav : ''}`} onClick={() => setActiveTab('findJobs')}>
                    <MdSearch size={20} /> Find Jobs
                  </div>
                  <div className={`${styles.navItem} ${activeTab === 'applications' ? styles.activeNav : ''}`} onClick={() => setActiveTab('applications')}>
                    <MdAssignment size={20} /> My Applications
                  </div>
                  <div className={`${styles.navItem} ${activeTab === 'saved' ? styles.activeNav : ''}`} onClick={() => setActiveTab('saved')}>
                    <MdBookmark size={20} /> Saved Jobs
                  </div>
                </div>

                {/* Profile Completeness (Static for visual) */}
                <div className="mt-auto">
                    <div className="p-3 rounded-3" style={{background: 'rgba(255,255,255,0.05)', border: '1px solid rgba(255,255,255,0.05)'}}>
                       <div className="d-flex justify-content-between align-items-center mb-1">
                         <small className="text-white fw-bold">Profile Status</small>
                         <small className="text-success">100%</small>
                       </div>
                       <div className="progress" style={{height: 4, background: 'rgba(255,255,255,0.1)'}}>
                         <div className="progress-bar bg-success" style={{width: '100%'}}></div>
                       </div>
                    </div>
                </div>
              </div>
            </div>

            {/* --- MAIN CONTENT (Right) --- */}
            <div className="col-lg-9 col-md-8">
              
              {/* Mobile Tab Select */}
              <div className="d-md-none mb-3">
                 <select className={`form-select ${styles.glassInput}`} value={activeTab} onChange={(e) => setActiveTab(e.target.value)}>
                    <option value="overview">Overview</option>
                    <option value="findJobs">Find Jobs</option>
                    <option value="applications">My Applications</option>
                    <option value="saved">Saved Jobs</option>
                 </select>
              </div>

              {/* Dynamic Content */}
              {activeTab === 'overview' && <OverviewTab stats={stats} setActiveTab={setActiveTab} />}
              {activeTab === 'findJobs' && <FindJobsTab jobs={availableJobs} loading={loading} />}
              {activeTab === 'applications' && <ApplicationsTab applications={appliedJobs} loading={loading} />}
              {activeTab === 'saved' && <SavedJobsTab jobs={savedJobs} loading={loading} />}

            </div>

          </div>
        </div>
      </div>

      <Footer />
    </>
  );
};

// --- 1. OVERVIEW TAB ---
const OverviewTab = ({ stats, setActiveTab }) => {
  return (
    <div className={styles.fadeIn}>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
           <h4 className="text-white fw-bold mb-0">Hello, Seeker!</h4>
           <small className="text-white-50">Here is your daily activity report.</small>
        </div>
        <button className={`btn ${styles.btnGradient}`} onClick={() => setActiveTab('findJobs')}>
          Find New Jobs
        </button>
      </div>

      <div className="row g-3">
         {/* Card 1: Applied */}
         <div className="col-sm-6 col-lg-4">
            <div className={styles.glassPanel}>
               <div className="d-flex justify-content-between">
                  <div>
                    <small className="text-white-50 text-uppercase fw-bold" style={{fontSize: 11}}>Applied Jobs</small>
                    <h2 className="text-white fw-bold mt-2">{stats.applied}</h2>
                  </div>
                  <div className="bg-primary bg-opacity-25 p-3 rounded-circle text-primary" style={{height: 'fit-content'}}>
                      <MdAssignment size={24} />
                  </div>
               </div>
            </div>
         </div>

         {/* Card 2: Interviews */}
         <div className="col-sm-6 col-lg-4">
            <div className={styles.glassPanel}>
               <div className="d-flex justify-content-between">
                  <div>
                    <small className="text-white-50 text-uppercase fw-bold" style={{fontSize: 11}}>Interviews</small>
                    <h2 className="text-white fw-bold mt-2">{stats.interviews}</h2>
                  </div>
                  <div className="bg-warning bg-opacity-25 p-3 rounded-circle text-warning" style={{height: 'fit-content'}}>
                      <MdCheckCircle size={24} />
                  </div>
               </div>
            </div>
         </div>

         {/* Card 3: Saved */}
         <div className="col-sm-6 col-lg-4">
            <div className={styles.glassPanel}>
               <div className="d-flex justify-content-between">
                  <div>
                    <small className="text-white-50 text-uppercase fw-bold" style={{fontSize: 11}}>Saved Jobs</small>
                    <h2 className="text-white fw-bold mt-2">{stats.saved}</h2>
                  </div>
                  <div className="bg-danger bg-opacity-25 p-3 rounded-circle text-danger" style={{height: 'fit-content'}}>
                      <MdBookmark size={24} />
                  </div>
               </div>
            </div>
         </div>
      </div>
    </div>
  );
};

// --- 2. FIND JOBS TAB ---
const FindJobsTab = ({ jobs, loading }) => {
    const [filter, setFilter] = useState('');
    
    // Logic to Apply
    const handleApply = async (jobId) => {
        try {
            await axiosInstance.post(`/seeker/apply/${jobId}`);
            alert("Applied successfully!");
        } catch(e) {
            alert("Already applied or Error.");
        }
    }

    const filteredJobs = jobs.filter(job => 
        job.title.toLowerCase().includes(filter.toLowerCase()) || 
        job.location.toLowerCase().includes(filter.toLowerCase())
    );

    return (
        <div className={styles.fadeIn}>
            <div className={styles.glassPanel}>
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h5 className="fw-bold text-white mb-0">Latest Opportunities</h5>
                    <div className="input-group" style={{width: 250}}>
                        <span className="input-group-text bg-transparent border-secondary text-secondary"><MdSearch/></span>
                        <input 
                            type="text" 
                            className={`form-control bg-transparent border-secondary text-white shadow-none`} 
                            placeholder="Search role or city..." 
                            value={filter}
                            onChange={(e)=>setFilter(e.target.value)}
                        />
                    </div>
                </div>

                <div className="row g-3">
                    {loading ? <p className="text-white-50 text-center">Loading jobs...</p> : 
                     filteredJobs.length > 0 ? filteredJobs.map(job => (
                        <div key={job.id} className="col-md-6 col-xl-4">
                            <div className={`${styles.glassPanel} h-100 d-flex flex-column border-0 bg-white bg-opacity-10`}>
                                <div className="d-flex justify-content-between mb-2">
                                    <span className="badge bg-primary bg-opacity-75">{job.type}</span>
                                    <MdBookmark className="text-white-50" style={{cursor:'pointer'}} />
                                </div>
                                <h5 className="text-white fw-bold mb-1">{job.title}</h5>
                                <p className="text-white-50 small mb-2"><MdBusiness/> {job.companyName || "Tech Corp"}</p>
                                <p className="text-white-50 small mb-3"><MdLocationOn/> {job.location}</p>
                                <div className="mt-auto">
                                    <button className="btn btn-sm btn-primary w-100" onClick={() => handleApply(job.id)}>Apply Now</button>
                                </div>
                            </div>
                        </div>
                    )) : <p className="text-white-50 text-center">No jobs found matching your search.</p>}
                </div>
            </div>
        </div>
    )
}

// --- 3. APPLICATIONS TAB ---
const ApplicationsTab = ({ applications, loading }) => {
    
    // Status Badge Logic
    const getStatusBadge = (status) => {
        switch(status) {
            case 'APPLIED': return <span className="badge bg-primary bg-opacity-25 text-primary">Pending</span>;
            case 'INTERVIEW': return <span className="badge bg-warning bg-opacity-25 text-warning">Interview</span>;
            case 'REJECTED': return <span className="badge bg-danger bg-opacity-25 text-danger">Rejected</span>;
            case 'HIRED': return <span className="badge bg-success bg-opacity-25 text-success">Hired</span>;
            default: return <span className="badge bg-secondary">Unknown</span>;
        }
    }

    return (
        <div className={styles.fadeIn}>
           <div className={styles.glassPanel}>
             <h5 className="fw-bold text-white mb-4">Application History</h5>
             
             <div className="table-responsive">
                <table className="table align-middle">
                    <thead className="text-white-50 small border-bottom border-secondary">
                        <tr>
                            <th className="bg-transparent border-0 ps-3">Role</th>
                            <th className="bg-transparent border-0">Company</th>
                            <th className="bg-transparent border-0">Applied On</th>
                            <th className="bg-transparent border-0">Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        {loading ? <tr><td colSpan="4" className="text-center text-white py-4">Loading...</td></tr> : 
                         applications.length > 0 ? applications.map(app => (
                            <tr key={app.id} className={styles.tableRowGlass}>
                                <td className="bg-transparent border-0 py-3 ps-3 text-white fw-bold">{app.jobTitle}</td>
                                <td className="bg-transparent border-0 text-white-50">{app.companyName}</td>
                                <td className="bg-transparent border-0 text-white-50 small">{app.appliedDate}</td>
                                <td className="bg-transparent border-0">{getStatusBadge(app.status)}</td>
                            </tr>
                        )) : (
                            <tr><td colSpan="4" className="text-center text-white-50 py-4">You haven't applied to any jobs yet.</td></tr>
                        )}
                    </tbody>
                </table>
             </div>
           </div>
        </div>
    );
};

// --- 4. SAVED JOBS TAB ---
const SavedJobsTab = ({ jobs, loading }) => {
    return (
        <div className={styles.fadeIn}>
            <div className={styles.glassPanel}>
                <h5 className="fw-bold text-white mb-4">Saved Jobs</h5>
                {loading ? <p className="text-white-50">Loading...</p> : 
                 jobs.length > 0 ? (
                    <div className="row g-3">
                        {jobs.map(job => (
                            <div key={job.id} className="col-12">
                                <div className={`${styles.glassPanel} p-3 d-flex justify-content-between align-items-center bg-white bg-opacity-5`}>
                                    <div>
                                        <h6 className="text-white fw-bold mb-1">{job.title}</h6>
                                        <small className="text-white-50">{job.companyName} &bull; {job.location}</small>
                                    </div>
                                    <button className="btn btn-sm btn-outline-primary">Apply</button>
                                </div>
                            </div>
                        ))}
                    </div>
                 ) : (
                    <div className="text-center py-5">
                        <MdBookmark size={40} className="text-white-50 mb-3"/>
                        <p className="text-white-50">No saved jobs found.</p>
                    </div>
                 )}
            </div>
        </div>
    );
};

export default SeekerDashboard;