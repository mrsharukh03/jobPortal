import React, { useState } from "react";
import axiosInstance from "../Utilitys/axiosInstance";

const initialState = {
  phone: "",
  companyLogoUrl: "",
  linkedInProfile: "",
  companyWebsite: "",
  companyName: "",
  designation: "",
  location: "",
  industry: "",
  companySize: "",
  companyDescription: "",
  yearsOfExperience: 0,
  about: "",
  hiringSkills: []
};

function FillJobRecruiterProfile() {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState(initialState);
  const [message, setMessage] = useState(""); // Success message
  const [error, setError] = useState("");     // Error message

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSkills = (e) => {
    setFormData({
      ...formData,
      hiringSkills: e.target.value.split(",").map(skill => skill.trim())
    });
  };

  const next = () => setStep(step + 1);
  const prev = () => setStep(step - 1);

  const submitProfile = async () => {
    try {
      const response = await axiosInstance.post(
        "/recruiter/profile/update",
        formData, // send DTO fields directly
        { withCredentials: true }
      );

      setMessage(response.data); // Show backend success message
      setError(""); // clear previous errors

      // Optional redirect after 1.5 seconds
      setTimeout(() => {
        window.location.href = "/recruiter/dashboard";
      }, 1500);

    } catch (err) {
      if (err.response && err.response.data) {
        setError(err.response.data);
      } else {
        setError("Something went wrong");
      }
      setMessage(""); // clear previous success
    }
  };

  const progress = (step / 4) * 100;

  return (
    <main className="container mt-5 mb-5">
      <div className="row justify-content-center">
        <div className="col-md-8">

          {/* Card */}
          <div className="card shadow-lg border-0 rounded-4">

            {/* Header */}
            <div className="card-header bg-primary text-white text-center rounded-top-4">
              <h4 className="mb-0">Complete Recruiter Profile</h4>
              <small>Step {step} of 4</small>
            </div>

            {/* Progress */}
            <div className="progress rounded-0" style={{ height: "6px" }}>
              <div
                className="progress-bar bg-success"
                style={{ width: `${progress}%` }}
              />
            </div>

            {/* Body */}
            <div className="card-body p-4">

              {/* Display messages */}
              {message && <div className="alert alert-success">{message}</div>}
              {error && <div className="alert alert-danger">{error}</div>}

              {/* STEP 1 */}
              {step === 1 && (
                <>
                  <h5 className="mb-3">Contact Information</h5>

                  <div className="mb-3">
                    <label className="form-label">Phone *</label>
                    <input
                      className="form-control"
                      name="phone"
                      placeholder="Enter phone number"
                      onChange={handleChange}
                    />
                  </div>

                  <div className="text-end">
                    <button className="btn btn-primary" onClick={next}>
                      Next →
                    </button>
                  </div>
                </>
              )}

              {/* STEP 2 */}
              {step === 2 && (
                <>
                  <h5 className="mb-3">Company Information</h5>

                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Company Name *</label>
                      <input className="form-control" name="companyName" onChange={handleChange} />
                    </div>

                    <div className="col-md-6 mb-3">
                      <label className="form-label">Designation *</label>
                      <input className="form-control" name="designation" onChange={handleChange} />
                    </div>
                  </div>

                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Industry *</label>
                      <input className="form-control" name="industry" onChange={handleChange} />
                    </div>

                    <div className="col-md-6 mb-3">
                      <label className="form-label">Location *</label>
                      <input className="form-control" name="location" onChange={handleChange} />
                    </div>
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Company Size</label>
                    <select className="form-select" name="companySize" onChange={handleChange}>
                      <option value="">Select size</option>
                      <option>Startup</option>
                      <option>1-50</option>
                      <option>50-200</option>
                      <option>500+</option>
                    </select>
                  </div>

                  <div className="d-flex justify-content-between">
                    <button className="btn btn-outline-secondary" onClick={prev}>
                      ← Back
                    </button>
                    <button className="btn btn-primary" onClick={next}>
                      Next →
                    </button>
                  </div>
                </>
              )}

              {/* STEP 3 */}
              {step === 3 && (
                <>
                  <h5 className="mb-3">Online Presence</h5>

                  <div className="mb-3">
                    <label className="form-label">LinkedIn Profile</label>
                    <input className="form-control" name="linkedInProfile" onChange={handleChange} />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Company Website</label>
                    <input className="form-control" name="companyWebsite" onChange={handleChange} />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Company Logo URL</label>
                    <input className="form-control" name="companyLogoUrl" onChange={handleChange} />
                  </div>

                  <div className="d-flex justify-content-between">
                    <button className="btn btn-outline-secondary" onClick={prev}>
                      ← Back
                    </button>
                    <button className="btn btn-primary" onClick={next}>
                      Next →
                    </button>
                  </div>
                </>
              )}

              {/* STEP 4 */}
              {step === 4 && (
                <>
                  <h5 className="mb-3">Recruiter Details</h5>

                  <div className="mb-3">
                    <label className="form-label">Years of Experience</label>
                    <input
                      type="number"
                      className="form-control"
                      name="yearsOfExperience"
                      onChange={handleChange}
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">About You</label>
                    <textarea className="form-control" rows="3" name="about" onChange={handleChange} />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Company Description</label>
                    <textarea
                      className="form-control"
                      rows="3"
                      name="companyDescription"
                      onChange={handleChange}
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Hiring Skills</label>
                    <input
                      className="form-control"
                      placeholder="Java, React, HR"
                      onChange={handleSkills}
                    />
                  </div>

                  <div className="d-flex justify-content-between">
                    <button className="btn btn-outline-secondary" onClick={prev}>
                      ← Back
                    </button>
                    <button className="btn btn-success" onClick={submitProfile}>
                      Submit Profile
                    </button>
                  </div>
                </>
              )}

            </div>
          </div>
        </div>
      </div>
    </main>
  );
}

export default FillJobRecruiterProfile;
