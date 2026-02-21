import React, { useEffect, useState } from "react";
import axiosInstance from "../contexts/axiosInstance";
import { 
  FaEnvelope, FaPhone, FaMapMarkerAlt, FaLinkedin, FaGithub, FaGlobe, 
  FaPen, FaPlus, FaTrash, FaTimes, FaBriefcase, FaGraduationCap
} from "react-icons/fa";
import styles from "../css/SeekerProfileView.module.css"; 

export default function SeekerProfileView() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshKey, setRefreshKey] = useState(0); 

  // Modal States
  const [activeModal, setActiveModal] = useState(null); 
  const [formData, setFormData] = useState({});

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await axiosInstance.get("/seeker/current-profile");
        setProfile(res.data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, [refreshKey]);

  // --- HELPER FUNCTIONS ---
  const formatDate = (dateString) => {
      if(!dateString) return "Present";
      return new Date(dateString).toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
  };

  const handleEditClick = (section, data = {}) => {
    setFormData(data); 
    setActiveModal(section);
  };

  const closeModal = () => {
    setActiveModal(null);
    setFormData({});
  };

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const triggerRefresh = () => setRefreshKey(k => k + 1);

  // --- API ACTIONS ---

  // 1. Personal Info
  const savePersonal = async () => {
    try {
      await axiosInstance.patch("/seeker/update/personal-details", formData);
      await axiosInstance.patch("/seeker/update-professional", formData); 
      closeModal();
      triggerRefresh();
    } catch(e) { alert("Update failed"); }
  };

  // 2. Skills
  const addSkill = async () => {
    if(!formData.newSkill) return;
    const updatedSkills = [...(profile.skills || []), formData.newSkill];
    try {
      await axiosInstance.patch("/seeker/update-professional", { ...profile, skills: updatedSkills });
      closeModal();
      triggerRefresh();
    } catch(e) { alert("Failed to add skill"); }
  };

  const deleteSkill = async (skillToDelete) => {
    if(!window.confirm("Remove this skill?")) return;
    const updatedSkills = profile.skills.filter(s => s !== skillToDelete);
    try {
      await axiosInstance.patch("/seeker/update-professional", { ...profile, skills: updatedSkills });
      triggerRefresh();
    } catch(e) { alert("Failed to delete skill"); }
  };

  // 3. Experience (Add & Delete)
  const saveExperience = async () => {
    try {
        await axiosInstance.post("/seeker/experience/add", formData);
        closeModal();
        triggerRefresh();
    } catch(e) { alert("Failed to add experience"); }
  };

  const deleteExperience = async (id) => {
      if(!window.confirm("Delete this experience?")) return;
      try {
          // Assuming you have a delete endpoint. If not, you might need to update the whole list via patch
          await axiosInstance.delete(`/seeker/experience/delete/${id}`); 
          triggerRefresh();
      } catch(e) { 
          // Fallback if no delete endpoint: alert user
          alert("Delete feature depends on backend API."); 
      }
  };

  // 4. Education (Add & Delete)
  const saveEducation = async () => {
    try {
        await axiosInstance.post("/seeker/education/add", formData);
        closeModal();
        triggerRefresh();
    } catch(e) { alert("Failed to add education"); }
  };

  const deleteEducation = async (id) => {
    if(!window.confirm("Delete this education?")) return;
    try {
        await axiosInstance.delete(`/seeker/education/delete/${id}`);
        triggerRefresh();
    } catch(e) { alert("Delete feature depends on backend API."); }
  };


  if (loading) return <div className="text-center mt-5"><div className="spinner-border text-primary"></div></div>;
  if (!profile) return null;

  return (
    <div className={styles.container}>
      {/* === HERO SECTION === */}
      <div className="container pt-4">
        <div className={styles.card}>
          <div className={styles.banner}></div>
          <div className={styles.profileImgContainer}>
             <img 
                src={profile.profileImage ? `data:image/jpeg;base64,${profile.profileImage}` : "https://via.placeholder.com/150"} 
                className={styles.profileImg} alt="Profile"
             />
          </div>
          <div className="text-end p-3">
             <FaPen className={styles.editIcon} onClick={() => handleEditClick('personal', profile)} title="Edit Personal Info"/>
          </div>
          
          <div className="px-4 pb-4 mt-5">
            <h2 className="fw-bold mb-0">{profile.firstName} {profile.lastName}</h2>
            <p className="text-dark fs-5 mb-1">{profile.jobTitle || "Open to Work"}</p>
            <p className="text-muted small mb-3">
              <FaMapMarkerAlt/> {profile.currentLocation} &bull; <span className="text-primary fw-bold" style={{cursor:'pointer'}}>Contact Info</span>
            </p>
            
            <div className="d-flex gap-3 mb-3 text-muted">
               <small><FaEnvelope/> {profile.email}</small>
               <small><FaPhone/> {profile.phone}</small>
            </div>

            <div className="d-flex gap-2">
               {profile.linkedinProfile && <a href={profile.linkedinProfile} target="_blank" className="btn btn-sm btn-outline-primary"><FaLinkedin/> LinkedIn</a>}
               {profile.githubProfile && <a href={profile.githubProfile} target="_blank" className="btn btn-sm btn-outline-dark"><FaGithub/> GitHub</a>}
               {profile.portfolioUrl && <a href={profile.portfolioUrl} target="_blank" className="btn btn-sm btn-outline-success"><FaGlobe/> Portfolio</a>}
            </div>
          </div>
        </div>

        <div className="row">
            {/* === LEFT SIDEBAR === */}
            <div className="col-lg-4">
                {/* SKILLS */}
                <div className={styles.card}>
                    <div className="p-3 border-bottom d-flex justify-content-between align-items-center">
                        <h6 className="fw-bold m-0">Skills</h6>
                        <FaPlus className={styles.editIcon} onClick={() => handleEditClick('skills')} title="Add Skill"/>
                    </div>
                    <div className="p-3">
                        <div className="d-flex flex-wrap gap-2">
                            {profile.skills?.map((s, i) => (
                                <span key={i} className={styles.skillBadge}>
                                    {s} 
                                    <FaTimes className="ms-2 text-danger" style={{cursor:'pointer'}} onClick={() => deleteSkill(s)}/>
                                </span>
                            ))}
                            {!profile.skills?.length && <p className="text-muted small">Add your top skills.</p>}
                        </div>
                    </div>
                </div>

                {/* RESUME */}
                <div className={styles.card}>
                     <div className="p-3 border-bottom"><h6 className="fw-bold m-0">Resume</h6></div>
                     <div className="p-3 text-center">
                        <p className="small text-muted mb-2">Last updated recently</p>
                        <button className="btn btn-outline-primary w-100 rounded-pill"><FaEnvelope/> Download CV</button>
                     </div>
                </div>
            </div>

            {/* === MAIN CONTENT === */}
            <div className="col-lg-8">
                {/* BIO */}
                <div className={styles.card}>
                    <div className="p-3 d-flex justify-content-between">
                        <h5 className="fw-bold">About</h5>
                        <FaPen className={styles.editIcon} onClick={() => handleEditClick('bio', { bio: profile.bio })}/>
                    </div>
                    <div className="px-3 pb-3">
                        <p className="text-muted" style={{whiteSpace:'pre-line'}}>{profile.bio || "Add a summary to highlight your personality and work ethic."}</p>
                    </div>
                </div>

                {/* EXPERIENCE */}
                <div className={styles.card}>
                    <div className="p-3 d-flex justify-content-between">
                        <h5 className="fw-bold">Experience</h5>
                        <FaPlus className={styles.editIcon} onClick={() => handleEditClick('experience')} title="Add Experience"/>
                    </div>
                    <div className="px-3 pb-3">
                        <div className={styles.timeline}>
                            {profile.experience?.map((exp, i) => (
                                <div key={i} className={styles.timelineItem}>
                                    <div className="d-flex justify-content-between">
                                        <div>
                                            <h6 className="fw-bold mb-0">{exp.jobTitle}</h6>
                                            <span className="text-dark small fw-semibold">{exp.companyName}</span>
                                            <div className="text-muted small my-1">
                                                {formatDate(exp.startDate)} - {formatDate(exp.endDate)}
                                            </div>
                                        </div>
                                        {/* Delete Icon for Experience */}
                                        <FaTrash className={styles.deleteIcon} onClick={() => deleteExperience(exp.id)} title="Delete"/>
                                    </div>
                                    <p className="text-muted small mb-0 mt-2">{exp.description}</p>
                                </div>
                            ))}
                            {(!profile.experience || profile.experience.length === 0) && <p className="text-muted text-center py-2">No experience added yet.</p>}
                        </div>
                    </div>
                </div>

                {/* EDUCATION */}
                <div className={styles.card}>
                    <div className="p-3 d-flex justify-content-between">
                        <h5 className="fw-bold">Education</h5>
                        <FaPlus className={styles.editIcon} onClick={() => handleEditClick('education')} title="Add Education"/>
                    </div>
                    <div className="px-3 pb-3">
                         {profile.education?.map((edu, i) => (
                             <div key={i} className="d-flex gap-3 mb-3 border-bottom pb-3">
                                 <div className="bg-light p-3 rounded d-flex align-items-center" style={{height: 'fit-content'}}>
                                     <FaGraduationCap size={24} className="text-secondary"/>
                                 </div>
                                 <div className="flex-grow-1">
                                     <div className="d-flex justify-content-between">
                                         <div>
                                            <h6 className="fw-bold mb-0">{edu.collegeName}</h6>
                                            <div className="text-dark small">{edu.degree} - {edu.fieldOfStudy}</div>
                                         </div>
                                         <FaTrash className={styles.deleteIcon} onClick={() => deleteEducation(edu.id)} title="Delete"/>
                                     </div>
                                     <div className="text-muted small mt-1">
                                        {edu.startYear} - {edu.endYear} &bull; Grade: <span className="text-dark fw-semibold">{edu.gradeValue} {edu.gradeType}</span>
                                     </div>
                                 </div>
                             </div>
                         ))}
                         {(!profile.education || profile.education.length === 0) && <p className="text-muted text-center py-2">No education details added.</p>}
                    </div>
                </div>
            </div>
        </div>
      </div>

      {/* ================= MODALS ================= */}
      
      {/* 1. PERSONAL INFO MODAL */}
      {activeModal === 'personal' && (
        <div className={styles.modalOverlay} onClick={closeModal}>
            <div className={styles.modalContent} onClick={e => e.stopPropagation()}>
                <div className="d-flex justify-content-between mb-3 border-bottom pb-2">
                    <h5 className="fw-bold">Edit Intro</h5>
                    <FaTimes style={{cursor:'pointer'}} onClick={closeModal}/>
                </div>
                <div className="row g-3">
                    <div className="col-md-6">
                        <label className="form-label small fw-bold">Phone</label>
                        <input name="phone" className="form-control" value={formData.phone || ''} onChange={handleInputChange}/>
                    </div>
                    <div className="col-md-6">
                        <label className="form-label small fw-bold">Current Location</label>
                        <input name="currentLocation" className="form-control" value={formData.currentLocation || ''} onChange={handleInputChange}/>
                    </div>
                    <div className="col-12">
                        <label className="form-label small fw-bold">LinkedIn URL</label>
                        <input name="linkedinProfile" className="form-control" value={formData.linkedinProfile || ''} onChange={handleInputChange}/>
                    </div>
                    <div className="col-12">
                        <label className="form-label small fw-bold">GitHub URL</label>
                        <input name="githubProfile" className="form-control" value={formData.githubProfile || ''} onChange={handleInputChange}/>
                    </div>
                    <div className="col-12">
                        <label className="form-label small fw-bold">Portfolio URL</label>
                        <input name="portfolioUrl" className="form-control" value={formData.portfolioUrl || ''} onChange={handleInputChange}/>
                    </div>
                </div>
                <div className="d-flex justify-content-end gap-2 mt-4">
                    <button className="btn btn-light" onClick={closeModal}>Cancel</button>
                    <button className="btn btn-primary px-4" onClick={savePersonal}>Save</button>
                </div>
            </div>
        </div>
      )}

      {/* 2. BIO MODAL */}
      {activeModal === 'bio' && (
        <div className={styles.modalOverlay} onClick={closeModal}>
            <div className={styles.modalContent} onClick={e => e.stopPropagation()}>
                <h5 className="fw-bold">Edit About</h5>
                <p className="text-muted small">You can write about your years of experience, industry, or skills.</p>
                <textarea name="bio" rows="6" className="form-control mt-2" value={formData.bio || ''} onChange={handleInputChange}></textarea>
                <div className="d-flex justify-content-end gap-2 mt-3">
                    <button className="btn btn-light" onClick={closeModal}>Cancel</button>
                    <button className="btn btn-primary px-4" onClick={savePersonal}>Save</button>
                </div>
            </div>
        </div>
      )}

      {/* 3. ADD SKILL MODAL */}
      {activeModal === 'skills' && (
        <div className={styles.modalOverlay} onClick={closeModal}>
            <div className={styles.modalContent} onClick={e => e.stopPropagation()}>
                <h5 className="fw-bold">Add New Skill</h5>
                <input name="newSkill" className="form-control mt-3" placeholder="Ex: Java, Spring Boot, React" onChange={handleInputChange} autoFocus/>
                <div className="d-flex justify-content-end gap-2 mt-3">
                    <button className="btn btn-light" onClick={closeModal}>Cancel</button>
                    <button className="btn btn-primary px-4" onClick={addSkill}>Add</button>
                </div>
            </div>
        </div>
      )}

      {/* 4. ADD EXPERIENCE MODAL */}
      {activeModal === 'experience' && (
        <div className={styles.modalOverlay} onClick={closeModal}>
            <div className={styles.modalContent} onClick={e => e.stopPropagation()}>
                <h5 className="fw-bold mb-3">Add Experience</h5>
                <div className="row g-3">
                    <div className="col-12">
                        <label className="form-label small fw-bold">Job Title *</label>
                        <input name="jobTitle" className="form-control" onChange={handleInputChange}/>
                    </div>
                    <div className="col-12">
                         <label className="form-label small fw-bold">Company Name *</label>
                        <input name="companyName" className="form-control" onChange={handleInputChange}/>
                    </div>
                    <div className="col-6">
                        <label className="form-label small fw-bold">Start Date</label>
                        <input type="date" name="startDate" className="form-control" onChange={handleInputChange}/>
                    </div>
                    <div className="col-6">
                        <label className="form-label small fw-bold">End Date</label>
                        <input type="date" name="endDate" className="form-control" onChange={handleInputChange}/>
                    </div>
                    <div className="col-12">
                        <label className="form-label small fw-bold">Description</label>
                        <textarea name="description" rows="3" className="form-control" onChange={handleInputChange}></textarea>
                    </div>
                </div>
                <div className="d-flex justify-content-end gap-2 mt-4">
                    <button className="btn btn-light" onClick={closeModal}>Cancel</button>
                    <button className="btn btn-primary px-4" onClick={saveExperience}>Save</button>
                </div>
            </div>
        </div>
      )}

      {/* 5. ADD EDUCATION MODAL */}
      {activeModal === 'education' && (
        <div className={styles.modalOverlay} onClick={closeModal}>
            <div className={styles.modalContent} onClick={e => e.stopPropagation()}>
                <h5 className="fw-bold mb-3">Add Education</h5>
                <div className="row g-3">
                    <div className="col-12">
                        <label className="form-label small fw-bold">School / University *</label>
                        <input name="collegeName" className="form-control" onChange={handleInputChange}/>
                    </div>
                    <div className="col-6">
                        <label className="form-label small fw-bold">Degree</label>
                        <input name="degree" className="form-control" placeholder="Ex: Bachelor's" onChange={handleInputChange}/>
                    </div>
                    <div className="col-6">
                        <label className="form-label small fw-bold">Field of Study</label>
                        <input name="fieldOfStudy" className="form-control" placeholder="Ex: Computer Science" onChange={handleInputChange}/>
                    </div>
                    <div className="col-6">
                        <label className="form-label small fw-bold">Start Year</label>
                        <input name="startYear" type="number" className="form-control" onChange={handleInputChange}/>
                    </div>
                    <div className="col-6">
                        <label className="form-label small fw-bold">End Year</label>
                        <input name="endYear" type="number" className="form-control" onChange={handleInputChange}/>
                    </div>
                    <div className="col-6">
                        <label className="form-label small fw-bold">Grade Type</label>
                        <select name="gradeType" className="form-control" onChange={handleInputChange} defaultValue="CGPA">
                            <option value="CGPA">CGPA</option>
                            <option value="Percentage">Percentage</option>
                        </select>
                    </div>
                    <div className="col-6">
                        <label className="form-label small fw-bold">Grade Value</label>
                        <input name="gradeValue" className="form-control" onChange={handleInputChange}/>
                    </div>
                </div>
                <div className="d-flex justify-content-end gap-2 mt-4">
                    <button className="btn btn-light" onClick={closeModal}>Cancel</button>
                    <button className="btn btn-primary px-4" onClick={saveEducation}>Save</button>
                </div>
            </div>
        </div>
      )}

    </div>
  );
}