import React, { useState } from "react";
import axiosInstance from "../Utilitys/axiosInstance";
import { 
  FaUser, FaBriefcase, FaGraduationCap, FaCode, FaFileUpload, FaCheck, FaPlus 
} from "react-icons/fa";
import styles from "../css/FillProfileSeeker.module.css";

export default function FillJobSeekerProfile() {
  const [currentStep, setCurrentStep] = useState(1);
  const [loading, setLoading] = useState(false);

  // --- STEP 1: PERSONAL DETAILS ---
  const [personalData, setPersonalData] = useState({
    phone: "",
    gender: "Male",
    dob: "",
    marriageStatus: "SINGLE",
    currentLocation: "",
  });

  // --- STEP 2: PROFESSIONAL INFO ---
  const [profData, setProfData] = useState({
    bio: "",
    linkedinProfile: "",
    githubProfile: "",
    portfolioUrl: "",
    expectedSalary: "",
    noticePeriod: "",
    languages: "", // Comma separated for UI
    preferredLocations: "", // Comma separated
    currentlyWorking: false
  });

  // --- STEP 3 & 4: Education & Experience (List Logic) ---
  const [educationList, setEducationList] = useState([]); // To show added items
  const [eduForm, setEduForm] = useState({
    degree: "", fieldOfStudy: "", collegeName: "", country: "", startYear: "", endYear: "", gradeType: "CGPA", gradeValue: ""
  });

  const [experienceList, setExperienceList] = useState([]);
  const [expForm, setExpForm] = useState({
    jobTitle: "", companyName: "", startDate: "", endDate: "", location: "", description: ""
  });

  // --- STEP 5: DOCUMENTS ---
  const [profileImg, setProfileImg] = useState(null);
  const [resume, setResume] = useState(null);

  // ===== HANDLERS =====

  const handleNext = () => setCurrentStep((prev) => prev + 1);
  const handleBack = () => setCurrentStep((prev) => prev - 1);

  // 1. Save Personal Info
  const savePersonal = async () => {
    setLoading(true);
    try {
      // Mapping frontend state to backend Entity fields
      await axiosInstance.patch("/seeker/update-personal", {
        phone: personalData.phone,
        gender: personalData.gender,
        DOB: personalData.dob, // Matches LocalDate
        marriageStatus: personalData.marriageStatus,
        currentLocation: personalData.currentLocation
      });
      handleNext();
    } catch (err) {
      alert("Error saving personal info");
    } finally { setLoading(false); }
  };

  // 2. Save Professional Info
  const saveProfessional = async () => {
    setLoading(true);
    try {
      // Convert comma strings to Arrays for List<String>
      const langs = profData.languages.split(",").map(s => s.trim());
      const locs = profData.preferredLocations.split(",").map(s => s.trim());

      await axiosInstance.patch("/seeker/update-professional", {
        ...profData,
        languages: langs,
        preferredLocations: locs
      });
      handleNext();
    } catch (err) {
      alert("Error saving professional info");
    } finally { setLoading(false); }
  };

  // 3. Add Education (One by One)
  const addEducation = async () => {
    if(!eduForm.degree || !eduForm.collegeName) return alert("Fill required fields");
    setLoading(true);
    try {
      const res = await axiosInstance.post("/seeker/education/add", eduForm);
      setEducationList([...educationList, res.data]); // Add saved item to list
      // Reset form
      setEduForm({ degree: "", fieldOfStudy: "", collegeName: "", country: "", startYear: "", endYear: "", gradeType: "CGPA", gradeValue: "" });
    } catch (err) {
      alert("Failed to add education");
    } finally { setLoading(false); }
  };

  // 4. Add Experience (One by One)
  const addExperience = async () => {
    if(!expForm.jobTitle) return alert("Fill required fields");
    setLoading(true);
    try {
      const res = await axiosInstance.post("/seeker/experience/add", expForm);
      setExperienceList([...experienceList, res.data]);
      setExpForm({ jobTitle: "", companyName: "", startDate: "", endDate: "", location: "", description: "" });
    } catch (err) {
      alert("Failed to add experience");
    } finally { setLoading(false); }
  };

  // 5. Upload Docs
  const uploadDocs = async () => {
    setLoading(true);
    try {
      const formData = new FormData();
      if(profileImg) formData.append("profileImage", profileImg);
      if(resume) formData.append("resume", resume);
      
      await axiosInstance.post("/seeker/upload-documents", formData, {
        headers: { "Content-Type": "multipart/form-data" }
      });
      alert("Profile Completed Successfully!");
      window.location.reload();
    } catch (err) {
      alert("Upload failed");
    } finally { setLoading(false); }
  };

  // ===== RENDER STEPS =====

  const renderSidebar = () => {
    const steps = [
      { id: 1, label: "Personal Info", icon: <FaUser /> },
      { id: 2, label: "Professional", icon: <FaBriefcase /> },
      { id: 3, label: "Education", icon: <FaGraduationCap /> },
      { id: 4, label: "Experience", icon: <FaCode /> }, // Can change icon
      { id: 5, label: "Documents", icon: <FaFileUpload /> },
    ];

    return (
      <div className={styles.sidebar}>
        <h4 className="mb-4 ps-2">Profile Setup</h4>
        {steps.map((step) => (
          <div 
            key={step.id} 
            className={`${styles.stepItem} ${currentStep === step.id ? styles.active : ''} ${currentStep > step.id ? styles.completed : ''}`}
            onClick={() => step.id < currentStep && setCurrentStep(step.id)} // Only allow going back
          >
            <div className={styles.stepNumber}>
              {currentStep > step.id ? <FaCheck size={12}/> : step.id}
            </div>
            <span>{step.label}</span>
          </div>
        ))}
      </div>
    );
  };

  return (
    <div className={styles.container}>
      <div className="container">
        <div className={styles.wizardCard}>
          {renderSidebar()}
          
          <div className={styles.contentArea}>
            
            {/* --- STEP 1: PERSONAL --- */}
            {currentStep === 1 && (
              <div className="animate-fade">
                <h2 className={styles.sectionTitle}>Personal Details</h2>
                <p className={styles.sectionSubtitle}>Let's start with the basics.</p>
                
                <div className={styles.formGrid}>
                  <div>
                    <label className={styles.label}>Phone Number</label>
                    <input type="tel" className={styles.input} value={personalData.phone} onChange={e => setPersonalData({...personalData, phone: e.target.value})} />
                  </div>
                  <div>
                    <label className={styles.label}>Date of Birth</label>
                    <input type="date" className={styles.input} value={personalData.dob} onChange={e => setPersonalData({...personalData, dob: e.target.value})} />
                  </div>
                  <div>
                    <label className={styles.label}>Gender</label>
                    <select className={styles.select} value={personalData.gender} onChange={e => setPersonalData({...personalData, gender: e.target.value})}>
                      <option value="Male">Male</option>
                      <option value="Female">Female</option>
                      <option value="Other">Other</option>
                    </select>
                  </div>
                  <div>
                    <label className={styles.label}>Marital Status</label>
                    <select className={styles.select} value={personalData.marriageStatus} onChange={e => setPersonalData({...personalData, marriageStatus: e.target.value})}>
                      <option value="SINGLE">Single</option>
                      <option value="MARRIED">Married</option>
                    </select>
                  </div>
                  <div className={styles.fullWidth}>
                    <label className={styles.label}>Current Location</label>
                    <input type="text" className={styles.input} placeholder="City, State" value={personalData.currentLocation} onChange={e => setPersonalData({...personalData, currentLocation: e.target.value})} />
                  </div>
                </div>

                <div className={styles.actionButtons}>
                  <div></div> {/* Spacer */}
                  <button className={styles.btnPrimary} onClick={savePersonal} disabled={loading}>
                    {loading ? "Saving..." : "Save & Next"}
                  </button>
                </div>
              </div>
            )}

            {/* --- STEP 2: PROFESSIONAL --- */}
            {currentStep === 2 && (
              <div className="animate-fade">
                <h2 className={styles.sectionTitle}>Professional Info</h2>
                <p className={styles.sectionSubtitle}>Tell recruiters about your work preferences.</p>

                <div className={styles.formGrid}>
                  <div className={styles.fullWidth}>
                    <label className={styles.label}>Bio / Summary</label>
                    <textarea className={styles.textarea} rows="3" value={profData.bio} onChange={e => setProfData({...profData, bio: e.target.value})}></textarea>
                  </div>
                  
                  {/* Links */}
                  <div>
                    <label className={styles.label}>LinkedIn URL</label>
                    <input type="url" className={styles.input} value={profData.linkedinProfile} onChange={e => setProfData({...profData, linkedinProfile: e.target.value})} />
                  </div>
                  <div>
                    <label className={styles.label}>GitHub URL</label>
                    <input type="url" className={styles.input} value={profData.githubProfile} onChange={e => setProfData({...profData, githubProfile: e.target.value})} />
                  </div>
                  
                  {/* Preferences */}
                  <div>
                     <label className={styles.label}>Expected Salary (LPA)</label>
                     <input type="number" className={styles.input} value={profData.expectedSalary} onChange={e => setProfData({...profData, expectedSalary: e.target.value})} />
                  </div>
                  <div>
                     <label className={styles.label}>Notice Period</label>
                     <input type="text" className={styles.input} placeholder="e.g. 15 Days, Immediate" value={profData.noticePeriod} onChange={e => setProfData({...profData, noticePeriod: e.target.value})} />
                  </div>

                  <div className={styles.fullWidth}>
                     <label className={styles.label}>Languages (Comma separated)</label>
                     <input type="text" className={styles.input} placeholder="English, Hindi, Spanish" value={profData.languages} onChange={e => setProfData({...profData, languages: e.target.value})} />
                  </div>
                  <div className={styles.fullWidth}>
                     <label className={styles.label}>Preferred Locations (Comma separated)</label>
                     <input type="text" className={styles.input} placeholder="Pune, Bangalore, Remote" value={profData.preferredLocations} onChange={e => setProfData({...profData, preferredLocations: e.target.value})} />
                  </div>
                </div>

                <div className={styles.actionButtons}>
                  <button className={styles.btnSecondary} onClick={handleBack}>Back</button>
                  <button className={styles.btnPrimary} onClick={saveProfessional} disabled={loading}>
                     {loading ? "Saving..." : "Save & Next"}
                  </button>
                </div>
              </div>
            )}

            {/* --- STEP 3: EDUCATION (Skippable) --- */}
            {currentStep === 3 && (
              <div className="animate-fade">
                <h2 className={styles.sectionTitle}>Education</h2>
                <p className={styles.sectionSubtitle}>Add your qualifications (Skippable if already added).</p>

                {/* List of Added Educations */}
                {educationList.map((edu, idx) => (
                    <div key={idx} className={styles.addedItem}>
                        <strong>{edu.degree}</strong> in {edu.fieldOfStudy} from {edu.collegeName}
                    </div>
                ))}

                <div className="bg-light p-3 rounded-3 mb-3 border">
                    <h6 className="mb-3 text-primary">Add New Education</h6>
                    <div className={styles.formGrid}>
                        <div className={styles.fullWidth}>
                            <input className={styles.input} placeholder="College Name" value={eduForm.collegeName} onChange={e=>setEduForm({...eduForm, collegeName: e.target.value})} />
                        </div>
                        <div>
                            <input className={styles.input} placeholder="Degree (e.g. B.Tech)" value={eduForm.degree} onChange={e=>setEduForm({...eduForm, degree: e.target.value})} />
                        </div>
                        <div>
                            <input className={styles.input} placeholder="Field (e.g. CS)" value={eduForm.fieldOfStudy} onChange={e=>setEduForm({...eduForm, fieldOfStudy: e.target.value})} />
                        </div>
                        <div>
                            <input type="number" className={styles.input} placeholder="Start Year" value={eduForm.startYear} onChange={e=>setEduForm({...eduForm, startYear: e.target.value})} />
                        </div>
                        <div>
                            <input type="number" className={styles.input} placeholder="End Year" value={eduForm.endYear} onChange={e=>setEduForm({...eduForm, endYear: e.target.value})} />
                        </div>
                        <div>
                            <select className={styles.select} value={eduForm.gradeType} onChange={e=>setEduForm({...eduForm, gradeType: e.target.value})}>
                                <option>CGPA</option>
                                <option>Percentage</option>
                            </select>
                        </div>
                        <div>
                            <input className={styles.input} placeholder="Grade Value" value={eduForm.gradeValue} onChange={e=>setEduForm({...eduForm, gradeValue: e.target.value})} />
                        </div>
                        <div className={styles.fullWidth}>
                             <button className="btn btn-sm btn-outline-primary w-100" onClick={addEducation} disabled={loading}>
                                 <FaPlus className="me-2"/> {loading ? "Adding..." : "Add Education"}
                             </button>
                        </div>
                    </div>
                </div>

                <div className={styles.actionButtons}>
                  <button className={styles.btnSecondary} onClick={handleBack}>Back</button>
                  <div className="d-flex gap-3 align-items-center">
                     <span className={styles.skipLink} onClick={handleNext}>Skip / Next</span>
                     <button className={styles.btnPrimary} onClick={handleNext}>Next Step</button>
                  </div>
                </div>
              </div>
            )}

            {/* --- STEP 4: EXPERIENCE (Skippable) --- */}
            {currentStep === 4 && (
              <div className="animate-fade">
                <h2 className={styles.sectionTitle}>Work Experience</h2>
                <p className={styles.sectionSubtitle}>Skip this if you are a fresher.</p>

                {experienceList.map((exp, idx) => (
                    <div key={idx} className={styles.addedItem}>
                        <strong>{exp.jobTitle}</strong> at {exp.companyName}
                    </div>
                ))}

                <div className="bg-light p-3 rounded-3 mb-3 border">
                    <h6 className="mb-3 text-primary">Add Experience</h6>
                    <div className={styles.formGrid}>
                        <div className={styles.fullWidth}>
                            <input className={styles.input} placeholder="Job Title" value={expForm.jobTitle} onChange={e=>setExpForm({...expForm, jobTitle: e.target.value})} />
                        </div>
                        <div className={styles.fullWidth}>
                            <input className={styles.input} placeholder="Company Name" value={expForm.companyName} onChange={e=>setExpForm({...expForm, companyName: e.target.value})} />
                        </div>
                        <div>
                            <label className={styles.label}>Start Date</label>
                            <input type="date" className={styles.input} value={expForm.startDate} onChange={e=>setExpForm({...expForm, startDate: e.target.value})} />
                        </div>
                        <div>
                            <label className={styles.label}>End Date</label>
                            <input type="date" className={styles.input} value={expForm.endDate} onChange={e=>setExpForm({...expForm, endDate: e.target.value})} />
                        </div>
                        <div className={styles.fullWidth}>
                            <textarea className={styles.textarea} placeholder="Description" rows="2" value={expForm.description} onChange={e=>setExpForm({...expForm, description: e.target.value})}></textarea>
                        </div>
                        <div className={styles.fullWidth}>
                             <button className="btn btn-sm btn-outline-primary w-100" onClick={addExperience} disabled={loading}>
                                 <FaPlus className="me-2"/> {loading ? "Adding..." : "Add Experience"}
                             </button>
                        </div>
                    </div>
                </div>

                <div className={styles.actionButtons}>
                  <button className={styles.btnSecondary} onClick={handleBack}>Back</button>
                  <div className="d-flex gap-3 align-items-center">
                     <span className={styles.skipLink} onClick={handleNext}>I am a Fresher / Skip</span>
                     <button className={styles.btnPrimary} onClick={handleNext}>Next Step</button>
                  </div>
                </div>
              </div>
            )}

            {/* --- STEP 5: DOCUMENTS --- */}
            {currentStep === 5 && (
              <div className="animate-fade">
                <h2 className={styles.sectionTitle}>Upload Documents</h2>
                <p className={styles.sectionSubtitle}>Final step! Add your resume and photo.</p>

                <div className="mb-4">
                    <label className={styles.label}>Profile Picture</label>
                    <input type="file" className="form-control" accept="image/*" onChange={e => setProfileImg(e.target.files[0])} />
                </div>

                <div className="mb-4">
                    <label className={styles.label}>Resume (PDF/Doc)</label>
                    <input type="file" className="form-control" accept=".pdf,.doc,.docx" onChange={e => setResume(e.target.files[0])} />
                </div>

                <div className={styles.actionButtons}>
                  <button className={styles.btnSecondary} onClick={handleBack}>Back</button>
                  <button className={styles.btnPrimary} onClick={uploadDocs} disabled={loading}>
                     {loading ? "Uploading..." : "Finish Profile"}
                  </button>
                </div>
              </div>
            )}

          </div>
        </div>
      </div>
    </div>
  );
}