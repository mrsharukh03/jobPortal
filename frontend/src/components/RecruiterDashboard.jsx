import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import styles from '../css/RecruiterDashboard.module.css';
import PostJob from '../Screens/PostJob.jsx';
import Navbar from './Navbar.jsx';
import Footer from './Footer.jsx';
import axiosInstance from "../contexts/axiosInstance.js";

// --- Icons ---
import { 
  MdDashboard, MdWork, MdList, MdAddCircle, MdSearch, MdEdit, MdDelete 
} from "react-icons/md";

const RecruiterDashboard = () => {
  const [activeTab, setActiveTab] = useState('analysis');
  const [jobs, setJobs] = useState([]); // State for API Data
  const [loading, setLoading] = useState(false);

  // --- 1. Fetch Jobs from Backend ---
  const fetchJobs = async () => {
    setLoading(true);
    try {
      // Calling your provided API
      const response = await axiosInstance.get("/recruiter/job/posted");
      if(Array.isArray(response.data)) {
         setJobs(response.data);
      }
    } catch (error) {
      console.error("Error fetching jobs:", error);
    } finally {
      setLoading(false);
    }
  };

  // Fetch when tab changes to jobList
  useEffect(() => {
    if (activeTab === 'jobList') {
      fetchJobs();
    }
  }, [activeTab]);

  return (
    <>
      <Navbar />
      
      <div className={styles.dashboardWrapper}>
        <div className="container-fluid container-xl">
          <div className="row g-4">
            
            {/* --- SIDEBAR (Fixed Left) --- */}
            <div className="col-lg-3 col-md-4 d-none d-md-block">
              <div className={styles.sidebarCard}>
                <div className="mb-4 px-2 d-flex align-items-center gap-2">
                   <div className="bg-primary rounded-circle p-1 d-flex justify-content-center align-items-center" style={{width: 28, height: 28}}>
                      <span className="text-white fw-bold" style={{fontSize: 14}}>H</span>
                   </div>
                   <span className="text-white fw-bold">Recruiter Panel</span>
                </div>

                <div className="d-flex flex-column flex-grow-1">
                  <div className={`${styles.navItem} ${activeTab === 'analysis' ? styles.activeNav : ''}`} onClick={() => setActiveTab('analysis')}>
                    <MdDashboard size={20} /> Dashboard
                  </div>
                  <div className={`${styles.navItem} ${activeTab === 'postJob' ? styles.activeNav : ''}`} onClick={() => setActiveTab('postJob')}>
                    <MdAddCircle size={20} /> Post New Job
                  </div>
                  <div className={`${styles.navItem} ${activeTab === 'jobList' ? styles.activeNav : ''}`} onClick={() => setActiveTab('jobList')}>
                    <MdList size={20} /> Manage Jobs
                  </div>
                </div>

                <div className="mt-auto">
                   <div className="p-3 rounded-3" style={{background: 'rgba(255,255,255,0.05)', border: '1px solid rgba(255,255,255,0.05)'}}>
                      <div className="d-flex justify-content-between align-items-center mb-1">
                        <small className="text-white fw-bold">Job Limit</small>
                        <small className="text-white-50">{jobs.length}/50</small>
                      </div>
                      <div className="progress" style={{height: 4, background: 'rgba(255,255,255,0.1)'}}>
                        <div className="progress-bar bg-primary" style={{width: `${(jobs.length/50)*100}%`}}></div>
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
                    <option value="analysis">Dashboard</option>
                    <option value="postJob">Post Job</option>
                    <option value="jobList">Job List</option>
                 </select>
              </div>

              {/* Dynamic Content */}
              {activeTab === 'analysis' && <AnalysisTab jobs={jobs} setActiveTab={setActiveTab} />}
              
              {activeTab === 'postJob' && (
                  <PostJob /> // Using your component, now compacted
              )}
              
              {activeTab === 'jobList' && (
                <JobListTab 
                  jobs={jobs} 
                  loading={loading}
                  onPostClick={() => setActiveTab('postJob')} 
                />
              )}

            </div>

          </div>
        </div>
      </div>

      <Footer />
    </>
  );
};

// --- ANALYSIS TAB ---
const AnalysisTab = ({ jobs, setActiveTab }) => {
  // Simple Mock Stats + Real Count
  return (
    <div className={styles.fadeIn}>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
           <h4 className="text-white fw-bold mb-0">Overview</h4>
           <small className="text-white-50">Welcome back, Recruiter</small>
        </div>
        <button className={`btn ${styles.btnGradient}`} onClick={() => setActiveTab('postJob')}>
          + Create Job
        </button>
      </div>

      <div className="row g-3">
         <div className="col-sm-6 col-lg-4">
            <div className={styles.glassPanel}>
               <div className="d-flex justify-content-between">
                  <div>
                    <small className="text-white-50 text-uppercase fw-bold" style={{fontSize: 11}}>Total Jobs</small>
                    <h2 className="text-white fw-bold mt-2">{jobs.length}</h2>
                  </div>
                  <div className="bg-primary bg-opacity-25 p-3 rounded-circle text-primary" style={{height: 'fit-content'}}>
                     <MdWork size={24} />
                  </div>
               </div>
            </div>
         </div>
         {/* Add more cards here if needed */}
      </div>
    </div>
  );
};

// --- JOB LIST TAB (Using API Data) ---
const JobListTab = ({ jobs, loading, onPostClick }) => {
  const [filter, setFilter] = useState('');

  // Filtering based on Title
  const filteredJobs = jobs.filter(job => 
     job.title.toLowerCase().includes(filter.toLowerCase())
  );

  return (
    <div className={styles.fadeIn}>
       <div className={styles.glassPanel}>
         <div className="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
           <div>
             <h5 className="fw-bold text-white mb-1">Your Job Postings</h5>
             <small className="text-white-50">Manage active and closed listings</small>
           </div>
           
           <div className="d-flex gap-2">
              <div className="input-group" style={{width: 220}}>
                <span className="input-group-text bg-transparent border-end-0 border-secondary text-secondary"><MdSearch /></span>
                <input 
                  type="text" 
                  className={`form-control bg-transparent border-secondary text-white border-start-0 shadow-none`} 
                  placeholder="Search by title..." 
                  value={filter}
                  onChange={(e) => setFilter(e.target.value)}
                  style={{fontSize: '0.9rem'}}
                />
              </div>
           </div>
         </div>

         <div className="table-responsive">
           <table className="table align-middle">
             <thead className="text-white-50 small border-bottom border-secondary">
               <tr>
                 <th className="bg-transparent border-0 ps-3">Job Title</th>
                 <th className="bg-transparent border-0">Type</th>
                 <th className="bg-transparent border-0">Deadline</th>
                 <th className="bg-transparent border-0">Status</th>
                 <th className="bg-transparent border-0 text-end pe-3">Actions</th>
               </tr>
             </thead>
             <tbody>
               {loading ? (
                  <tr><td colSpan="5" className="text-center text-white py-4">Loading jobs...</td></tr>
               ) : filteredJobs.length > 0 ? (
                 filteredJobs.map(job => (
                   <tr key={job.id} className={styles.tableRowGlass}>
                     <td className="bg-transparent border-0 py-3 ps-3">
                       <div className="fw-bold text-white">{job.title}</div>
                       <small className="text-white-50">{job.location}</small>
                     </td>
                     <td className="bg-transparent border-0">
                        <span className="badge bg-light text-dark fw-normal">{job.type.replace('_', ' ')}</span>
                     </td>
                     <td className="bg-transparent border-0 text-white-50 small">
                        {job.lastDateToApply}
                     </td>
                     <td className="bg-transparent border-0">
                        <span className={`badge rounded-pill fw-normal ${job.status === 'OPEN' ? 'bg-success bg-opacity-25 text-success' : 'bg-danger bg-opacity-25 text-danger'}`}>
                          {job.status}
                        </span>
                     </td>
                     <td className="bg-transparent border-0 text-end pe-3">
                       <button className="btn btn-sm text-white-50 hover-white"><MdEdit /></button>
                       <button className="btn btn-sm text-danger"><MdDelete /></button>
                     </td>
                   </tr>
                 ))
               ) : (
                 <tr>
                    <td colSpan="5" className="text-center text-white-50 py-5">
                       No jobs found. <span className="text-primary cursor-pointer" onClick={onPostClick}>Post one now?</span>
                    </td>
                 </tr>
               )}
             </tbody>
           </table>
         </div>
       </div>
    </div>
  );
};

export default RecruiterDashboard;