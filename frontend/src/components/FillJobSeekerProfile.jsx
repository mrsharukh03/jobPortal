import React, { useState } from "react";
import axiosInstance from "../contexts/axiosInstance";
import { 
  FaUser, FaBriefcase, FaGraduationCap, FaCode, FaFileUpload, FaCheck, FaPlus, FaTrash, FaLightbulb, FaArrowLeft, FaArrowRight, FaSave 
} from "react-icons/fa";
import styles from "../css/FillProfileSeeker.module.css"; 

// ==========================================
// 🛠️ FIX 1: REUSABLE COMPONENT (OUTSIDE MAIN)
// ==========================================
const InputField = ({ label, type="text", value, onChange, error, placeholder, required=false, ...props }) => (
  <div className="mb-3">
    <label className={styles.label}>
      {label} {required && <span className="text-danger">*</span>}
    </label>
    <input 
      type={type} 
      className={`form-control ${styles.input} ${error ? "is-invalid" : ""}`} 
      value={value} 
      onChange={onChange}
      placeholder={placeholder}
      {...props}
    />
    {error && <div className="invalid-feedback">{error}</div>}
  </div>
);

// 🛠️ HELPER: 18+ Date Validation
const getMaxDate = () => {
    const d = new Date();
    d.setFullYear(d.getFullYear() - 18);
    return d.toISOString().split("T")[0];
};

export default function FillJobSeekerProfile() {
  const [currentStep, setCurrentStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  // --- STATE 1: PERSONAL ---
  const [personalData, setPersonalData] = useState({
    phone: "", gender: "Male", dob: "", marriageStatus: "SINGLE", currentLocation: ""
  });

  // --- STATE 2: PROFESSIONAL & SKILLS ---
  const [profData, setProfData] = useState({
    bio: "", linkedinProfile: "", githubProfile: "", portfolioUrl: "", 
    expectedSalary: "", noticePeriod: "", languages: "", preferredLocations: ""
  });
  const [skillInput, setSkillInput] = useState("");
  const [skills, setSkills] = useState([]);

  // --- STATE 3: EDUCATION (List + Form) ---
  const [educationList, setEducationList] = useState([]);
  const [eduForm, setEduForm] = useState({
    collegeName: "", degree: "", fieldOfStudy: "", 
    startYear: "", endYear: "", gradeType: "CGPA", gradeValue: ""
  });

  // --- STATE 4: EXPERIENCE (List + Form) ---
  const [experienceList, setExperienceList] = useState([]);
  const [expForm, setExpForm] = useState({
    jobTitle: "", companyName: "", startDate: "", endDate: "", description: ""
  });

  // --- STATE 5: DOCUMENTS ---
  const [resume, setResume] = useState(null);
  const [profileImg, setProfileImg] = useState(null);

  // ==================== HANDLERS ====================

  const handleNext = () => { setErrors({}); setCurrentStep(prev => prev + 1); };
  const handleBack = () => { setErrors({}); setCurrentStep(prev => prev - 1); };

  // 1️⃣ SAVE PERSONAL DETAILS
  const savePersonal = async () => {
    let newErrors = {};
    if (!/^\d{10,12}$/.test(personalData.phone)) newErrors.phone = "Valid 10-digit phone required";
    if (!personalData.dob) newErrors.dob = "Date of Birth required";
    if (!personalData.currentLocation) newErrors.currentLocation = "Location required";
    
    // Age Check
    if(personalData.dob) {
        const selected = new Date(personalData.dob);
        const minAge = new Date();
        minAge.setFullYear(minAge.getFullYear() - 18);
        if(selected > minAge) newErrors.dob = "You must be 18+ years old";
    }

    if (Object.keys(newErrors).length > 0) { setErrors(newErrors); return; }

    setLoading(true);
    try {
      // Backend expects ISO Date string
      await axiosInstance.patch("/seeker/update/personal-details", {
          ...personalData,
          dob: personalData.dob 
      });
      handleNext();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to save personal info");
    } finally { setLoading(false); }
  };

  // 2️⃣ SAVE PROFESSIONAL DETAILS
  const saveProfessional = async () => {
    setLoading(true);
    try {
      const payload = {
          ...profData,
          // Split comma separated strings to arrays
          languages: profData.languages.split(",").map(s=>s.trim()).filter(s=>s),
          preferredLocations: profData.preferredLocations.split(",").map(s=>s.trim()).filter(s=>s),
          skills: skills 
      };
      await axiosInstance.patch("/seeker/update-professional", payload);
      handleNext();
    } catch (err) { alert("Failed to save professional info"); } 
    finally { setLoading(false); }
  };

  // 3️⃣ ADD EDUCATION (Fixed gradeValue Validation)
  const addEducation = async () => {
    // 🛠️ FIX: Ensure gradeValue is checked
    if(!eduForm.collegeName || !eduForm.degree || !eduForm.gradeValue) {
        setErrors({...errors, edu: "College, Degree and Grades are mandatory"});
        return;
    }
    setLoading(true);
    try {
        const res = await axiosInstance.post("/seeker/education/add", eduForm);
        // Add returned DTO to list
        setEducationList([...educationList, res.data || eduForm]);
        // Reset Form
        setEduForm({ 
            collegeName: "", degree: "", fieldOfStudy: "", 
            startYear: "", endYear: "", gradeType: "CGPA", gradeValue: "" 
        });
        setErrors({});
    } catch(e) { 
        console.error(e);
        alert("Failed: " + (e.response?.data?.message || "Server Error")); 
    } 
    finally { setLoading(false); }
  };

  // 4️⃣ ADD EXPERIENCE
  const addExperience = async () => {
    if(!expForm.jobTitle || !expForm.companyName) {
        setErrors({...errors, exp: "Job Title and Company are required"});
        return;
    }
    setLoading(true);
    try {
        const res = await axiosInstance.post("/seeker/experience/add", expForm);
        setExperienceList([...experienceList, res.data || expForm]);
        setExpForm({ jobTitle: "", companyName: "", startDate: "", endDate: "", description: "" });
        setErrors({});
    } catch(e) { alert("Failed to add experience"); } 
    finally { setLoading(false); }
  };

// 5️⃣ UPLOAD DOCUMENTS (Fixed Header Issue)
  const uploadDocs = async () => {
      if(!resume) return alert("Resume is required");
      setLoading(true);
      try {
          const formData = new FormData();
          formData.append("resume", resume);
          if(profileImg) formData.append("profileImage", profileImg);
          
          // 🛠️ FIX: Header ko explicitly override karein
          await axiosInstance.post("/seeker/upload-documents", formData, {
              headers: {
                  "Content-Type": "multipart/form-data", // Ye explicitly batana padega agar axiosInstance me JSON default hai
              }
          });
          
          alert("🎉 Profile Setup Complete!");
          window.location.href = "/dashboard";
      } catch(e) { 
          console.error(e);
          alert("Upload failed. Ensure files are within size limits."); 
      } 
      finally { setLoading(false); }
  };

  // --- SKILL HELPER ---
  const addSkill = () => {
      if(skillInput && !skills.includes(skillInput)) {
          setSkills([...skills, skillInput]);
          setSkillInput("");
      }
  };

  // --- STEPS CONFIG ---
  const steps = [
      {id:1, label:"Personal", icon:<FaUser/>},
      {id:2, label:"Professional", icon:<FaBriefcase/>},
      {id:3, label:"Education", icon:<FaGraduationCap/>},
      {id:4, label:"Experience", icon:<FaCode/>},
      {id:5, label:"Documents", icon:<FaFileUpload/>}
  ];

  return (
    <div className={styles.container}>
      <div className="container py-5">
        <div className={styles.wizardCard}>
          
          {/* SIDEBAR */}
          <div className={styles.sidebar}>
            <h4 className="mb-4 ps-2 fw-bold text-primary">Job Portal</h4>
            {steps.map(step => (
                <div key={step.id} className={`${styles.stepItem} ${currentStep===step.id?styles.active:''} ${currentStep>step.id?styles.completed:''}`}>
                    <div className={styles.stepNumber}>{currentStep>step.id?<FaCheck size={10}/>:step.id}</div>
                    <span>{step.label}</span>
                </div>
            ))}
          </div>

          {/* MAIN CONTENT AREA */}
          <div className={styles.contentArea}>
            
            {/* --- STEP 1: PERSONAL --- */}
            {currentStep === 1 && (
                <div className="animate-fade">
                    <h3 className={styles.sectionTitle}>Personal Details</h3>
                    <div className={styles.formGrid}>
                        <InputField label="Phone" value={personalData.phone} onChange={e=>setPersonalData({...personalData, phone:e.target.value})} error={errors.phone} placeholder="e.g. 9876543210" required/>
                        
                        <div className="mb-3">
                            <label className={styles.label}>Date of Birth <span className="text-danger">*</span></label>
                            <input type="date" className={`form-control ${styles.input} ${errors.dob?"is-invalid":""}`} 
                                value={personalData.dob} 
                                max={getMaxDate()} 
                                onChange={e=>setPersonalData({...personalData, dob:e.target.value})} 
                            />
                            {errors.dob && <div className="invalid-feedback">{errors.dob}</div>}
                        </div>

                        <div className="mb-3">
                            <label className={styles.label}>Gender</label>
                            <select className={styles.select} value={personalData.gender} onChange={e=>setPersonalData({...personalData, gender:e.target.value})}>
                                <option value="Male">Male</option>
                                <option value="Female">Female</option>
                                <option value="Other">Other</option>
                            </select>
                        </div>

                        <div className="mb-3">
                            <label className={styles.label}>Marital Status</label>
                            <select className={styles.select} value={personalData.marriageStatus} onChange={e=>setPersonalData({...personalData, marriageStatus:e.target.value})}>
                                <option value="SINGLE">Single</option>
                                <option value="MARRIED">Married</option>
                                <option value="DIVORCED">Divorced</option>
                                <option value="WIDOWED">Widowed</option>
                            </select>
                        </div>
                        <div className={styles.fullWidth}>
                            <InputField label="Current Location" value={personalData.currentLocation} onChange={e=>setPersonalData({...personalData, currentLocation:e.target.value})} error={errors.currentLocation} placeholder="City, State" required/>
                        </div>
                    </div>
                    <div className={styles.actionButtons}>
                        <div></div>
                        <button className={styles.btnPrimary} onClick={savePersonal} disabled={loading}>
                            {loading ? "Saving..." : <>Save & Next <FaArrowRight/></>}
                        </button>
                    </div>
                </div>
            )}

            {/* --- STEP 2: PROFESSIONAL --- */}
            {currentStep === 2 && (
                <div className="animate-fade">
                    <h3 className={styles.sectionTitle}>Professional Info</h3>
                    
                    {/* Skills Input */}
                    <div className="mb-4 p-3 bg-light rounded border">
                        <label className={styles.label}>Key Skills <FaLightbulb className="text-warning"/></label>
                        <div className="d-flex gap-2 mb-2">
                            <input className="form-control" value={skillInput} onChange={e=>setSkillInput(e.target.value)} placeholder="e.g. Java, Spring Boot, React"/>
                            <button className="btn btn-dark" onClick={addSkill}><FaPlus/> Add</button>
                        </div>
                        <div className="d-flex flex-wrap gap-2">
                            {skills.map((s,i)=>(<span key={i} className="badge bg-primary p-2 d-flex align-items-center gap-2">{s} <FaTrash style={{cursor:"pointer"}} onClick={()=>setSkills(skills.filter(sk=>sk!==s))}/></span>))}
                            {skills.length === 0 && <small className="text-muted">No skills added.</small>}
                        </div>
                    </div>

                    <div className={styles.formGrid}>
                        <InputField label="LinkedIn URL" value={profData.linkedinProfile} onChange={e=>setProfData({...profData, linkedinProfile:e.target.value})}/>
                        <InputField label="Portfolio / GitHub" value={profData.portfolioUrl} onChange={e=>setProfData({...profData, portfolioUrl:e.target.value})}/>
                        <InputField label="Expected Salary (LPA)" type="number" value={profData.expectedSalary} onChange={e=>setProfData({...profData, expectedSalary:e.target.value})}/>
                        <InputField label="Notice Period" value={profData.noticePeriod} onChange={e=>setProfData({...profData, noticePeriod:e.target.value})} placeholder="e.g. 15 Days"/>
                        <div className={styles.fullWidth}>
                             <InputField label="Languages (Comma separated)" value={profData.languages} onChange={e=>setProfData({...profData, languages:e.target.value})} placeholder="English, Hindi"/>
                        </div>
                        <div className={styles.fullWidth}>
                             <InputField label="Preferred Locations" value={profData.preferredLocations} onChange={e=>setProfData({...profData, preferredLocations:e.target.value})} placeholder="Pune, Bangalore, Remote"/>
                        </div>
                        <div className={styles.fullWidth}>
                             <label className={styles.label}>Bio</label>
                             <textarea className="form-control" rows="3" value={profData.bio} onChange={e=>setProfData({...profData, bio:e.target.value})} placeholder="Short summary about yourself..."/>
                        </div>
                    </div>
                    <div className={styles.actionButtons}>
                        <button className={styles.btnSecondary} onClick={handleBack}><FaArrowLeft/> Back</button>
                        <button className={styles.btnPrimary} onClick={saveProfessional} disabled={loading}>Next <FaArrowRight/></button>
                    </div>
                </div>
            )}

            {/* --- STEP 3: EDUCATION --- */}
            {currentStep === 3 && (
                <div className="animate-fade">
                    <h3 className={styles.sectionTitle}>Education</h3>
                    
                    {/* Display List */}
                    {educationList.length > 0 && (
                        <div className="mb-3">
                            {educationList.map((ed, i)=>(
                                <div key={i} className="alert alert-light border d-flex justify-content-between align-items-center">
                                    <div>
                                        <strong>{ed.degree}</strong> - {ed.collegeName} <br/>
                                        <small className="text-muted">{ed.startYear} - {ed.endYear} | {ed.gradeValue} {ed.gradeType}</small>
                                    </div>
                                    <FaGraduationCap className="text-muted"/>
                                </div>
                            ))}
                        </div>
                    )}

                    {/* Add Form */}
                    <div className="bg-light p-4 border rounded mb-3">
                        <h6 className="text-primary mb-3"><FaPlus/> Add New Education</h6>
                        <div className={styles.formGrid}>
                            <div className={styles.fullWidth}>
                                <InputField label="College / Institute" value={eduForm.collegeName} onChange={e=>setEduForm({...eduForm, collegeName:e.target.value})} placeholder="e.g. IIT Bombay"/>
                            </div>
                            <InputField label="Degree" value={eduForm.degree} onChange={e=>setEduForm({...eduForm, degree:e.target.value})} placeholder="e.g. B.Tech"/>
                            <InputField label="Field of Study" value={eduForm.fieldOfStudy} onChange={e=>setEduForm({...eduForm, fieldOfStudy:e.target.value})} placeholder="e.g. Computer Science"/>
                            
                            <InputField label="Start Year" type="number" value={eduForm.startYear} onChange={e=>setEduForm({...eduForm, startYear:e.target.value})}/>
                            <InputField label="End Year" type="number" value={eduForm.endYear} onChange={e=>setEduForm({...eduForm, endYear:e.target.value})}/>
                            
                            {/* 🛠️ FIX: Added Grade Inputs to fix Validation Error */}
                            <div>
                                <label className={styles.label}>Grade Type</label>
                                <select className={styles.select} value={eduForm.gradeType} onChange={e=>setEduForm({...eduForm, gradeType:e.target.value})}>
                                    <option>CGPA</option>
                                    <option>Percentage</option>
                                </select>
                            </div>
                            <InputField label="Value" value={eduForm.gradeValue} onChange={e=>setEduForm({...eduForm, gradeValue:e.target.value})} placeholder="e.g. 8.5 or 85%"/>
                            
                            <div className={styles.fullWidth}>
                                {errors.edu && <div className="text-danger mb-2 text-center">{errors.edu}</div>}
                                <button className="btn btn-outline-primary w-100" onClick={addEducation} disabled={loading}>
                                    {loading ? "Adding..." : "Add Education"}
                                </button>
                            </div>
                        </div>
                    </div>
                    <div className={styles.actionButtons}>
                        <button className={styles.btnSecondary} onClick={handleBack}>Back</button>
                        <button className={styles.btnPrimary} onClick={handleNext}>Next</button>
                    </div>
                </div>
            )}

            {/* --- STEP 4: EXPERIENCE --- */}
            {currentStep === 4 && (
                <div className="animate-fade">
                    <h3 className={styles.sectionTitle}>Work Experience</h3>
                    
                    {experienceList.map((ex, i)=>(
                        <div key={i} className="alert alert-light border">
                            <strong>{ex.jobTitle}</strong> at {ex.companyName} <br/>
                            <small className="text-muted">{ex.startDate} to {ex.endDate || "Present"}</small>
                        </div>
                    ))}

                    <div className="bg-light p-4 border rounded mb-3">
                        <h6 className="text-primary mb-3"><FaPlus/> Add Experience</h6>
                        <div className={styles.formGrid}>
                            <InputField label="Job Title" value={expForm.jobTitle} onChange={e=>setExpForm({...expForm, jobTitle:e.target.value})}/>
                            <InputField label="Company Name" value={expForm.companyName} onChange={e=>setExpForm({...expForm, companyName:e.target.value})}/>
                            
                            <div className="mb-3">
                                <label className={styles.label}>Start Date</label>
                                <input type="date" className="form-control" value={expForm.startDate} onChange={e=>setExpForm({...expForm, startDate:e.target.value})}/>
                            </div>
                            <div className="mb-3">
                                <label className={styles.label}>End Date (Leave empty if working)</label>
                                <input type="date" className="form-control" value={expForm.endDate} onChange={e=>setExpForm({...expForm, endDate:e.target.value})}/>
                            </div>
                            
                            <div className={styles.fullWidth}>
                                <label className={styles.label}>Description</label>
                                <textarea className="form-control" rows="2" value={expForm.description} onChange={e=>setExpForm({...expForm, description:e.target.value})}/>
                            </div>

                            <div className={styles.fullWidth}>
                                {errors.exp && <div className="text-danger mb-2 text-center">{errors.exp}</div>}
                                <button className="btn btn-outline-primary w-100" onClick={addExperience} disabled={loading}>Add Experience</button>
                            </div>
                        </div>
                    </div>
                    <div className={styles.actionButtons}>
                        <button className={styles.btnSecondary} onClick={handleBack}>Back</button>
                        <button className={styles.btnPrimary} onClick={handleNext}>Next</button>
                    </div>
                </div>
            )}

            {/* --- STEP 5: DOCUMENTS --- */}
            {currentStep === 5 && (
                <div className="animate-fade">
                    <h3 className={styles.sectionTitle}>Upload Documents</h3>
                    <p className="text-muted">Only PDF, DOC, DOCX and JPG/PNG are allowed.</p>

                    <div className="mb-4 p-4 border border-dashed rounded text-center">
                        <label className={styles.label}>Resume (CV) <span className="text-danger">*</span></label>
                        <input type="file" className="form-control mt-2" accept=".pdf,.doc,.docx" onChange={e => setResume(e.target.files[0])}/>
                    </div>

                    <div className="mb-4 p-4 border border-dashed rounded text-center">
                        <label className={styles.label}>Profile Picture</label>
                        <input type="file" className="form-control mt-2" accept="image/*" onChange={e => setProfileImg(e.target.files[0])}/>
                    </div>

                    <div className={styles.actionButtons}>
                        <button className={styles.btnSecondary} onClick={handleBack}>Back</button>
                        <button className={styles.btnPrimary} onClick={uploadDocs} disabled={loading}>
                            {loading ? "Uploading..." : <><FaSave/> Finish Profile</>}
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