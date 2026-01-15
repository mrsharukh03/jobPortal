import React, { useState } from "react";
import axiosInstance from "../Utilitys/axiosInstance";
import styles from "../css/PostJob.module.css";
import { 
  FaBriefcase, FaMapMarkerAlt, FaMoneyBillWave, FaBuilding, 
  FaTools, FaTimes, FaExclamationCircle 
} from "react-icons/fa";

const PostJob = () => {
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: "", text: "" });
  const [skillInput, setSkillInput] = useState("");
  const [errors, setErrors] = useState({});

  const [formData, setFormData] = useState({
    title: "",
    description: "",
    location: "",
    type: "FULL_TIME",
    category: "",
    minSalary: "",
    maxSalary: "",
    experienceRequired: "",
    companyName: "",
    companyLogoUrl: "",
    lastDateToApply: "",
    requiredSkills: [],
  });

  // Handle input changes
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });

    if (errors[name]) {
      setErrors({ ...errors, [name]: null });
    }
  };

  // Handle skill input enter
  const handleSkillKeyDown = (e) => {
    if (e.key === "Enter" && skillInput.trim()) {
      e.preventDefault();
      if (!formData.requiredSkills.includes(skillInput.trim())) {
        const updatedSkills = [...formData.requiredSkills, skillInput.trim()];
        setFormData({ ...formData, requiredSkills: updatedSkills });
        if (updatedSkills.length > 0 && errors.requiredSkills) {
          setErrors({ ...errors, requiredSkills: null });
        }
      }
      setSkillInput("");
    }
  };

  const removeSkill = (skillToRemove) => {
    setFormData({
      ...formData,
      requiredSkills: formData.requiredSkills.filter((skill) => skill !== skillToRemove),
    });
  };

  // Submit form
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrors({});
    setMessage({ type: "", text: "" });

    // Client-side validation
    if (formData.requiredSkills.length === 0) {
      setErrors((prev) => ({ ...prev, requiredSkills: "At least one skill is required." }));
      setLoading(false);
      return;
    }

    // Prepare payload for backend
    const payload = {
      ...formData,
      minSalary: formData.minSalary ? parseInt(formData.minSalary) : null,
      maxSalary: formData.maxSalary ? parseInt(formData.maxSalary) : null,
      lastDateToApply: formData.lastDateToApply || null,
      type: formData.type, // Must match JobType enum: FULL_TIME, PART_TIME, INTERNSHIP, REMOTE
    };

    try {
      const response = await axiosInstance.post("/job/post", payload, {
        headers: { "Content-Type": "application/json" },
      });

      if (response.status === 200 || response.status === 201) {
        setMessage({ type: "success", text: "Job Posted Successfully!" });
        // Reset form
        setFormData({
          title: "", description: "", location: "", type: "FULL_TIME", category: "",
          minSalary: "", maxSalary: "", experienceRequired: "", companyName: "",
          companyLogoUrl: "", lastDateToApply: "", requiredSkills: [],
        });
        setSkillInput("");
        window.scrollTo(0, 0);
      }
    } catch (error) {
      console.error("Posting Error:", error);

      if (error.response && error.response.status === 400) {
        const backendErrors = error.response.data;
        if (typeof backendErrors === 'object') {
          setErrors(backendErrors);
        }
        setMessage({ type: "error", text: "Please fix the errors below." });
      } else {
        setMessage({ type: "error", text: "Something went wrong. Please try again." });
      }
      window.scrollTo(0, 0);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.postJobWrapper}>
      <div className={styles.glassCard}>

        {/* Header */}
        <div className={styles.cardHeader}>
          <div className="d-flex align-items-center gap-3">
            <div className="bg-primary rounded p-2 text-white"><FaBriefcase /></div>
            <div>
              <h5 className="mb-0 text-white fw-bold">Create New Job</h5>
              <small className="text-white-50">Details for your new opening</small>
            </div>
          </div>
        </div>

        <div className={styles.formSection}>
          {message.text && (
            <div className={`alert py-2 mb-3 ${message.type === 'success' ? 'alert-success' : 'alert-danger'}`}>
              {message.type === 'error' && <FaExclamationCircle className="me-2"/>}
              {message.text}
            </div>
          )}

          <form onSubmit={handleSubmit}>

            {/* Row 1 */}
            <div className="row g-3 mb-3">
              <div className="col-lg-4 col-md-6">
                <label className={styles.labelSmall}>Job Title *</label>
                <input 
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  className={`form-control ${styles.glassInput} ${errors.title ? 'is-invalid' : ''}`}
                  placeholder="e.g. React Dev"
                />
                {errors.title && <small className="text-danger">{errors.title}</small>}
              </div>
              <div className="col-lg-4 col-md-6">
                <label className={styles.labelSmall}>Company Name *</label>
                <div className={`input-group ${errors.companyName ? 'is-invalid' : ''}`}>
                  <span className={`input-group-text ${styles.inputIconBox}`}><FaBuilding /></span>
                  <input 
                    type="text"
                    name="companyName"
                    value={formData.companyName}
                    onChange={handleChange}
                    className={`form-control border-start-0 ${styles.glassInput} ${errors.companyName ? 'is-invalid' : ''}`}
                  />
                </div>
                {errors.companyName && <small className="text-danger">{errors.companyName}</small>}
              </div>
              <div className="col-lg-4 col-md-6">
                <label className={styles.labelSmall}>Category *</label>
                <input 
                  type="text"
                  name="category"
                  value={formData.category}
                  onChange={handleChange}
                  className={`form-control ${styles.glassInput} ${errors.category ? 'is-invalid' : ''}`}
                  placeholder="IT/Sales"
                />
                {errors.category && <small className="text-danger">{errors.category}</small>}
              </div>
            </div>

            {/* Row 2 */}
            <div className="row g-3 mb-3">
              <div className="col-lg-3 col-md-6">
                <label className={styles.labelSmall}>Location *</label>
                <div className={`input-group ${errors.location ? 'is-invalid' : ''}`}>
                  <span className={`input-group-text ${styles.inputIconBox}`}><FaMapMarkerAlt /></span>
                  <input
                    type="text"
                    name="location"
                    value={formData.location}
                    onChange={handleChange}
                    className={`form-control border-start-0 ${styles.glassInput} ${errors.location ? 'is-invalid' : ''}`}
                  />
                </div>
                {errors.location && <small className="text-danger">{errors.location}</small>}
              </div>
              <div className="col-lg-3 col-md-6">
                <label className={styles.labelSmall}>Type *</label>
                <select
                  name="type"
                  value={formData.type}
                  onChange={handleChange}
                  className={`form-select ${styles.glassSelect} ${errors.type ? 'is-invalid' : ''}`}
                >
                  {["FULL_TIME","PART_TIME","INTERNSHIP","REMOTE"].map(t => (
                    <option key={t} value={t}>{t.replace("_", " ")}</option>
                  ))}
                </select>
                {errors.type && <small className="text-danger">{errors.type}</small>}
              </div>
              <div className="col-lg-3 col-md-6">
                <label className={styles.labelSmall}>Experience *</label>
                <input
                  type="text"
                  name="experienceRequired"
                  value={formData.experienceRequired}
                  onChange={handleChange}
                  className={`form-control ${styles.glassInput} ${errors.experienceRequired ? 'is-invalid' : ''}`}
                  placeholder="e.g. 2 Yrs"
                />
                {errors.experienceRequired && <small className="text-danger">{errors.experienceRequired}</small>}
              </div>
              <div className="col-lg-3 col-md-6">
                <label className={styles.labelSmall}>Deadline *</label>
                <input
                  type="date"
                  name="lastDateToApply"
                  value={formData.lastDateToApply}
                  onChange={handleChange}
                  className={`form-control ${styles.glassInput} ${errors.lastDateToApply ? 'is-invalid' : ''}`}
                />
                {errors.lastDateToApply && <small className="text-danger">{errors.lastDateToApply}</small>}
              </div>
            </div>

            {/* Row 3: Salary & Logo */}
            <div className="row g-3 mb-3">
              <div className="col-lg-3 col-md-6">
                <label className={styles.labelSmall}>Min Salary</label>
                <div className="input-group">
                  <span className={`input-group-text ${styles.inputIconBox}`}><FaMoneyBillWave /></span>
                  <input
                    type="number"
                    name="minSalary"
                    value={formData.minSalary}
                    onChange={handleChange}
                    className={`form-control border-start-0 ${styles.glassInput}`}
                  />
                </div>
              </div>
              <div className="col-lg-3 col-md-6">
                <label className={styles.labelSmall}>Max Salary</label>
                <div className="input-group">
                  <span className={`input-group-text ${styles.inputIconBox}`}><FaMoneyBillWave /></span>
                  <input
                    type="number"
                    name="maxSalary"
                    value={formData.maxSalary}
                    onChange={handleChange}
                    className={`form-control border-start-0 ${styles.glassInput}`}
                  />
                </div>
              </div>
              <div className="col-lg-6 col-md-12">
                <label className={styles.labelSmall}>Logo URL</label>
                <input
                  type="url"
                  name="companyLogoUrl"
                  value={formData.companyLogoUrl}
                  onChange={handleChange}
                  className={`form-control ${styles.glassInput}`}
                  placeholder="https://..."
                />
              </div>
            </div>

            {/* Skills */}
            <div className="mb-3">
              <label className={styles.labelSmall}>Required Skills * (Press Enter)</label>
              <div className={`input-group mb-2 ${errors.requiredSkills ? 'is-invalid' : ''}`}>
                <span className={`input-group-text ${styles.inputIconBox}`}><FaTools /></span>
                <input
                  type="text"
                  className={`form-control border-start-0 ${styles.glassInput}`}
                  placeholder="Add skill..."
                  value={skillInput}
                  onChange={(e) => setSkillInput(e.target.value)}
                  onKeyDown={handleSkillKeyDown}
                />
              </div>
              {errors.requiredSkills && <small className="text-danger d-block mb-2">{errors.requiredSkills}</small>}

              <div className="d-flex flex-wrap gap-2">
                {formData.requiredSkills.map((skill, i) => (
                  <span key={i} className="badge bg-primary bg-opacity-25 text-white border border-primary border-opacity-25 px-2 py-1 d-flex align-items-center gap-2">
                    {skill} <FaTimes size={10} className="cursor-pointer" onClick={() => removeSkill(skill)} />
                  </span>
                ))}
              </div>
            </div>

            {/* Description */}
            <div className="mb-4">
              <label className={styles.labelSmall}>Description *</label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                className={`form-control ${styles.glassTextarea} ${errors.description ? 'is-invalid' : ''}`}
                rows="3"
              ></textarea>
              {errors.description && <small className="text-danger">{errors.description}</small>}
            </div>

            <div className="text-end">
              <button type="submit" className={styles.btnGradient} disabled={loading}>
                {loading ? "Posting..." : "🚀 Publish Job"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default PostJob;
