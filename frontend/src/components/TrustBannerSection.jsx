import styles from '../css/TrustBannerSection.module.css';
import { FaUsers, FaBriefcase, FaCheckCircle } from 'react-icons/fa';

function TrustBannerSection() {
  return (
    <section className={styles.trustBanner}>
      <div className={styles.overlay}></div>

      <div className="container text-white text-center py-5 position-relative">
        <h2 className="display-5 fw-bold mb-3">
          Trusted by <span className="text-warning">Thousands</span> of Job Seekers & Recruiters
        </h2>
        <p className="lead mb-4">
          Build your team or find your dream job — all in one platform.
        </p>

        <div className="d-flex justify-content-center gap-4 flex-wrap mt-4 mb-5">
          <button className="btn btn-primary btn-lg shadow px-4">Find Jobs</button>
          <button className="btn btn-outline-light btn-lg shadow px-4">Post a Job</button>
        </div>

        {/* Social Proof Stats */}
        <div className="row justify-content-center text-center g-4">
          <div className="col-6 col-md-3">
            <div className={styles.statCard}>
              <FaUsers size={32} className="mb-2 text-primary" />
              <h5 className="fw-bold mb-0">10,000+</h5>
              <small>Hired Candidates</small>
            </div>
          </div>
          <div className="col-6 col-md-3">
            <div className={styles.statCard}>
              <FaBriefcase size={32} className="mb-2 text-success" />
              <h5 className="fw-bold mb-0">5,000+</h5>
              <small>Jobs Posted</small>
            </div>
          </div>
          <div className="col-6 col-md-3">
            <div className={styles.statCard}>
              <FaCheckCircle size={32} className="mb-2 text-warning" />
              <h5 className="fw-bold mb-0">2,000+</h5>
              <small>Verified Recruiters</small>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

export default TrustBannerSection;
