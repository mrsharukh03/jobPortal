import React, { useState } from 'react';
import axiosInstance from '../contexts/axiosInstance';

export default function ForgetPassword() {

    const [email, setEmail] = useState("");
    const [response, setResponse] = useState(""); // fix: empty initial state
    const [error, setError] = useState("");
    const [isLoading,setLoading] = useState(false); // fix: false initial state


     const handelOnChangeResetLink = (e) => {
        setEmail(e.target.value);
     }

        const handelOnSubmitResetLink = async (e) => {
            e.preventDefault();
            setLoading(true);
            setError("");
            setResponse("");

            try {
                const res = await axiosInstance.post("/auth/password/forget", { email }, {
                    headers: { "Content-Type": "application/json" },
                });
                setResponse(res.data.message);
            } catch (error) {
                setError(error.response?.data?.message || "An error occurred");
            } finally {
                setLoading(false);
            }
        }

  return (
    <main className="d-flex min-vh-100 justify-content-center align-items-center bg-light">
      <div className="card shadow-sm w-50" style={{ maxWidth: '500px' }}>
        <div className="card-body p-5">
          <h2 className="text-center mb-4">Forgot Password?</h2>
          <p className="text-center text-muted mb-4">
            Enter your email address and we'll send you a link to reset your password.
          </p>

          <form onSubmit={handelOnSubmitResetLink}>
            {/* Show success message if exists */}
            {response && <div className="alert alert-success text-center" role="alert">{response}</div>}
            
            <div className="mb-3">
              <label htmlFor="email" className="form-label">Email Address</label>
              <input 
                type="email" 
                className="form-control" 
                id="email" 
                placeholder="name@example.com" 
                required 
                value={email}
                onChange={handelOnChangeResetLink}
              />
            </div>
            
            {/* Show error message if exists */}
            {error && <div className="text-danger text-center mb-3">{error}</div>}
            
            <button type="submit" className="btn btn-primary w-100" disabled={isLoading}>
             {isLoading ? (
                 <>
                    <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    Sending...
                 </>
             ) : "Send Reset Link" } 
            </button>
          </form>

          <div className="text-center mt-3">
            <a href="/login" className="text-decoration-none">Back to Login</a>
          </div>
        </div>
      </div>
    </main>
  );
}