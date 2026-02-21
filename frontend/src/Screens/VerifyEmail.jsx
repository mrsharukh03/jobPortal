import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axiosInstance from "../contexts/axiosInstance";

export default function VerifyEmail() {
    const { token } = useParams();

    const [email, setEmail] = useState("");
    const [message, setMessage] = useState("");
    const [status, setStatus] = useState(""); // success | error
    const [loading, setLoading] = useState(false);

    // 🔹 Auto verify when token exists
    useEffect(() => {
        if (token) {
            verifyEmailToken();
        }
    }, [token]);

    const verifyEmailToken = async () => {
        setLoading(true);
        try {
            const response = await axiosInstance.post(
                "/auth/email-verify",
                { token }
            );
            setMessage(response.data.message);
            setStatus("success");
        } catch (error) {
            setMessage(
                error.response?.data?.message || "Email verification failed."
            );
            setStatus("error");
        } finally {
            setLoading(false);
        }
    };

    // 🔹 Resend verification link
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const response = await axiosInstance.post(
                "/auth/email-verify/resend",
                { email }
            );

            setMessage(response.data.message);
            setStatus("success");
        } catch (error) {
            setMessage(
                error.response?.data?.message ||
                "Failed to resend verification email."
            );
            setStatus("error");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="d-flex justify-content-center align-items-center vh-100 bg-light">
            <div className="card shadow p-4" style={{ maxWidth: "420px", width: "100%" }}>
                <div className="card-body text-center">
                    <h4 className="mb-3">Email Verification</h4>

                    {/* Loader */}
                    {loading && (
                        <div className="spinner-border text-primary mb-3" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                    )}

                    {/* Message */}
                    {message && (
                        <div
                            className={`alert ${
                                status === "success" ? "alert-success" : "alert-danger"
                            }`}
                        >
                            {message}
                        </div>
                    )}

                    {/* Show resend form ONLY if error */}
                    {!loading && status === "error" && (
                        <>
                            <p className="text-muted mt-3">
                                Enter your email to receive a new verification link
                            </p>

                            <form onSubmit={handleSubmit}>
                                <div className="mb-3 text-start">
                                    <label className="form-label">Email address</label>
                                    <input
                                        type="email"
                                        className="form-control"
                                        placeholder="Enter your email"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        required
                                    />
                                </div>

                                <button
                                    type="submit"
                                    className="btn btn-primary w-100"
                                    disabled={loading}
                                >
                                    Resend Verification Link
                                </button>
                            </form>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}
