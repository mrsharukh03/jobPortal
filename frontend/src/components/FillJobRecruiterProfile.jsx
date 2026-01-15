import React, { useState } from "react";
import axiosInstance from "../Utilitys/axiosInstance";
import {
  FaUserTie,
  FaBuilding,
  FaBriefcase,
  FaGlobe,
  FaCheck,
  FaArrowRight,
  FaArrowLeft
} from "react-icons/fa";
import styles from "../css/FillRecruiter.module.css";

const initialState = {
  phone: "",
  designation: "",
  location: "",
  yearsOfExperience: "",
  about: "",

  companyName: "",
  companyWebsite: "",
  companyLogoUrl: "",
  industry: "",
  companySize: "",
  companyDescription: "",

  linkedInProfile: "",
  hiringSkills: ""
};

function FillJobRecruiterProfile() {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState(initialState);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const steps = [
    { id: 1, label: "Basic Info", icon: <FaUserTie /> },
    { id: 2, label: "Company Details", icon: <FaBuilding /> },
    { id: 3, label: "Professional", icon: <FaBriefcase /> },
    { id: 4, label: "Social & Review", icon: <FaGlobe /> }
  ];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    setErrors({ ...errors, [name]: "" }); // live clear error
  };

  const isValidURL = (url) => {
    if (!url) return true;
    try {
      new URL(url);
      return true;
    } catch {
      return false;
    }
  };

  /* Step-wise validation for Next button */
  const validateStep = () => {
    const newErrors = {};

    if (step === 1) {
      if (!/^[0-9]{10,15}$/.test(formData.phone))
        newErrors.phone = "Phone must be 10–15 digits (no +91)";
      if (!formData.designation.trim())
        newErrors.designation = "Designation is required";
      if (!formData.location.trim())
        newErrors.location = "Location is required";
    }

    if (step === 2) {
      if (!formData.companyName.trim())
        newErrors.companyName = "Company name is required";
      if (!formData.industry.trim())
        newErrors.industry = "Industry is required";
      if (formData.companyDescription.length > 500)
        newErrors.companyDescription =
          "Company description cannot exceed 500 characters";
      if (formData.companyWebsite && !isValidURL(formData.companyWebsite))
        newErrors.companyWebsite = "Invalid website URL";
      if (formData.companyLogoUrl && !isValidURL(formData.companyLogoUrl))
        newErrors.companyLogoUrl = "Invalid logo URL";
    }

    if (step === 3) {
      if (formData.yearsOfExperience && Number(formData.yearsOfExperience) < 0)
        newErrors.yearsOfExperience = "Experience must be positive";
      if (formData.about.length > 300)
        newErrors.about = "About cannot exceed 300 characters";
    }

    if (step === 4) {
      if (formData.linkedInProfile && !isValidURL(formData.linkedInProfile))
        newErrors.linkedInProfile = "Invalid LinkedIn URL";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const next = () => {
    if (validateStep()) setStep(step + 1);
  };

  const prev = () => setStep(step - 1);

  const submitProfile = async () => {
    // final validation for all steps
    const allErrors = {};

    // Step 1
    if (!/^[0-9]{10,15}$/.test(formData.phone))
      allErrors.phone = "Phone must be 10–15 digits (no +91)";
    if (!formData.designation.trim())
      allErrors.designation = "Designation is required";
    if (!formData.location.trim())
      allErrors.location = "Location is required";

    // Step 2
    if (!formData.companyName.trim())
      allErrors.companyName = "Company name is required";
    if (!formData.industry.trim()) allErrors.industry = "Industry is required";
    if (formData.companyDescription.length > 500)
      allErrors.companyDescription =
        "Company description cannot exceed 500 characters";
    if (formData.companyWebsite && !isValidURL(formData.companyWebsite))
      allErrors.companyWebsite = "Invalid website URL";
    if (formData.companyLogoUrl && !isValidURL(formData.companyLogoUrl))
      allErrors.companyLogoUrl = "Invalid logo URL";

    // Step 3
    if (formData.yearsOfExperience && Number(formData.yearsOfExperience) < 0)
      allErrors.yearsOfExperience = "Experience must be positive";
    if (formData.about.length > 300)
      allErrors.about = "About cannot exceed 300 characters";

    // Step 4
    if (formData.linkedInProfile && !isValidURL(formData.linkedInProfile))
      allErrors.linkedInProfile = "Invalid LinkedIn URL";

    setErrors(allErrors);
    if (Object.keys(allErrors).length > 0) return alert("Fix errors first");

    setLoading(true);
    try {
      const payload = {
        ...formData,
        yearsOfExperience: Number(formData.yearsOfExperience),
        hiringSkills: formData.hiringSkills
          ? formData.hiringSkills.split(",").map((s) => s.trim())
          : []
      };

      await axiosInstance.post("/recruiter/profile/update", payload);

      alert("Profile Completed Successfully!");
      window.location.reload();
    } catch (err) {
      console.error(err);
      alert("Failed to update profile");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        {/* SIDEBAR */}
        <div className={styles.sidebar}>
          <div className={styles.logoArea}>
            Recruiter<span style={{ color: "white" }}>.Hub</span>
          </div>

          <div className={styles.stepsContainer}>
            {steps.map((s) => (
              <div
                key={s.id}
                className={`${styles.step} ${
                  step === s.id ? styles.active : ""
                }`}
              >
                <div className={styles.stepIcon}>
                  {step > s.id ? <FaCheck /> : s.icon}
                </div>
                <div className={styles.stepLabel}>{s.label}</div>
              </div>
            ))}
          </div>

          <div style={{ fontSize: "0.8rem", opacity: 0.5 }}>
            © 2026 JobPortal Inc.
          </div>
        </div>

        {/* CONTENT */}
        <div className={styles.content}>
          {/* STEP 1 */}
          {step === 1 && (
            <>
              <div className={styles.header}>
                <h2 className={styles.title}>Let's start with you.</h2>
                <p className={styles.subtitle}>
                  Enter your contact details and role.
                </p>
              </div>

              <div className={styles.grid}>
                <div>
                  <label className={styles.label}>Phone Number</label>
                  <input
                    className={styles.input}
                    name="phone"
                    value={formData.phone}
                    onChange={handleChange}
                    placeholder="9876543210"
                  />
                  <small style={{ color: "red" }}>{errors.phone}</small>
                </div>

                <div>
                  <label className={styles.label}>Designation</label>
                  <input
                    className={styles.input}
                    name="designation"
                    value={formData.designation}
                    onChange={handleChange}
                  />
                  <small style={{ color: "red" }}>{errors.designation}</small>
                </div>

                <div className={styles.fullWidth}>
                  <label className={styles.label}>Location</label>
                  <input
                    className={styles.input}
                    name="location"
                    value={formData.location}
                    onChange={handleChange}
                  />
                  <small style={{ color: "red" }}>{errors.location}</small>
                </div>
              </div>
            </>
          )}

          {/* STEP 2 */}
          {step === 2 && (
            <>
              <div className={styles.header}>
                <h2 className={styles.title}>Company Information</h2>
              </div>

              <div className={styles.grid}>
                <div className={styles.fullWidth}>
                  <label className={styles.label}>Company Name</label>
                  <input
                    className={styles.input}
                    name="companyName"
                    value={formData.companyName}
                    onChange={handleChange}
                  />
                  <small style={{ color: "red" }}>{errors.companyName}</small>
                </div>

                <div>
                  <label className={styles.label}>Industry</label>
                  <input
                    className={styles.input}
                    name="industry"
                    value={formData.industry}
                    onChange={handleChange}
                  />
                  <small style={{ color: "red" }}>{errors.industry}</small>
                </div>

                <div>
                  <label className={styles.label}>Company Size</label>
                  <select
                    className={styles.select}
                    name="companySize"
                    value={formData.companySize}
                    onChange={handleChange}
                  >
                    <option value="">Select Size</option>
                    <option>Startup (1-10)</option>
                    <option>Small (10-50)</option>
                    <option>Medium (50-200)</option>
                    <option>Enterprise (500+)</option>
                  </select>
                </div>

                <div className={styles.fullWidth}>
                  <label className={styles.label}>Company Description</label>
                  <textarea
                    className={styles.textarea}
                    rows="3"
                    name="companyDescription"
                    value={formData.companyDescription}
                    onChange={handleChange}
                  />
                  <small style={{ color: "red" }}>
                    {errors.companyDescription}
                  </small>
                </div>
              </div>
            </>
          )}

          {/* STEP 3 */}
          {step === 3 && (
            <>
              <div className={styles.header}>
                <h2 className={styles.title}>Professional Details</h2>
              </div>

              <div className={styles.grid}>
                <div>
                  <label className={styles.label}>Experience (Years)</label>
                  <input
                    type="number"
                    className={styles.input}
                    name="yearsOfExperience"
                    value={formData.yearsOfExperience}
                    onChange={handleChange}
                  />
                  <small style={{ color: "red" }}>
                    {errors.yearsOfExperience}
                  </small>
                </div>

                <div className={styles.fullWidth}>
                  <label className={styles.label}>Hiring Skills</label>
                  <input
                    className={styles.input}
                    name="hiringSkills"
                    value={formData.hiringSkills}
                    onChange={handleChange}
                  />
                </div>

                <div className={styles.fullWidth}>
                  <label className={styles.label}>About You</label>
                  <textarea
                    className={styles.textarea}
                    rows="4"
                    name="about"
                    value={formData.about}
                    onChange={handleChange}
                  />
                  <small style={{ color: "red" }}>{errors.about}</small>
                </div>
              </div>
            </>
          )}

          {/* STEP 4 */}
          {step === 4 && (
            <>
              <div className={styles.header}>
                <h2 className={styles.title}>Final Touches</h2>
              </div>

              <div className={styles.grid}>
                <div className={styles.fullWidth}>
                  <label className={styles.label}>LinkedIn Profile</label>
                  <input
                    className={styles.input}
                    name="linkedInProfile"
                    value={formData.linkedInProfile}
                    onChange={handleChange}
                  />
                  <small style={{ color: "red" }}>
                    {errors.linkedInProfile}
                  </small>
                </div>
              </div>
            </>
          )}

          {/* FOOTER */}
          <div className={styles.footer}>
            {step > 1 ? (
              <button className={styles.btnBack} onClick={prev}>
                <FaArrowLeft /> Back
              </button>
            ) : (
              <div />
            )}

            {step < 4 ? (
              <button className={styles.btnNext} onClick={next}>
                Next <FaArrowRight />
              </button>
            ) : (
              <button
                className={styles.btnNext}
                onClick={submitProfile}
                disabled={loading}
              >
                {loading ? "Creating..." : "Complete Profile"}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default FillJobRecruiterProfile;
