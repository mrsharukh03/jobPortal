import styles from '../css/Footer.module.css';
import {
  FaFacebookF,
  FaTwitter,
  FaLinkedinIn,
  FaInstagram,
  FaPaperPlane,
  FaCode
} from 'react-icons/fa';
import { Link } from 'react-router-dom'; // Ensure react-router-dom is installed

function Footer() {
  return (
    <footer className={styles.footer}>
      <div className={styles.container}>
        
        {/* Main Grid Content */}
        <div className={styles.row}>
          
          {/* Column 1: Brand Info */}
          <div className={styles.col}>
            <div className={styles.logo}>
              <img 
                src="https://cdn-icons-png.flaticon.com/512/6956/6956763.png" 
                alt="Logo" 
                width="32"
                style={{ filter: 'brightness(0) invert(1)' }} // Makes icon white
              />
              <span>Job<span style={{color: '#3b82f6'}}>.Portal</span></span>
            </div>
            <p className={styles.description}>
              Connecting talent with opportunity. The most trusted platform for job seekers and recruiters to grow together.
            </p>
            <div className={styles.socialIcons}>
              <a href="#" className={styles.iconLink} aria-label="Facebook"><FaFacebookF /></a>
              <a href="#" className={styles.iconLink} aria-label="Twitter"><FaTwitter /></a>
              <a href="#" className={styles.iconLink} aria-label="LinkedIn"><FaLinkedinIn /></a>
              <a href="https://www.instagram.com/@javashark_" target="_blank" rel="noreferrer" className={styles.iconLink} aria-label="Instagram">
                <FaInstagram />
              </a>
            </div>
          </div>

          {/* Column 2: Candidates */}
          <div className={styles.col}>
            <h5>For Candidates</h5>
            <ul className={styles.linkList}>
              <li><Link to="/jobs">Browse Jobs</Link></li>
              <li><Link to="/companies">Browse Companies</Link></li>
              <li><Link to="/courses">Free Courses</Link></li>
              <li><Link to="/dashboard">Candidate Dashboard</Link></li>
              <li><Link to="/resume-builder">Resume Builder</Link></li>
            </ul>
          </div>

          {/* Column 3: Recruiters & Support */}
          <div className={styles.col}>
            <h5>For Recruiters</h5>
            <ul className={styles.linkList}>
              <li><Link to="/post-job">Post a Job</Link></li>
              <li><Link to="/candidates">Browse Talent</Link></li>
              <li><Link to="/pricing">Pricing Plans</Link></li>
              <li><Link to="/contact">Contact Support</Link></li>
              <li><Link to="/terms">Privacy Policy</Link></li>
            </ul>
          </div>

          {/* Column 4: Newsletter */}
          <div className={styles.col}>
            <h5>Stay Updated</h5>
            <p className={styles.newsletterText}>
              Subscribe to our newsletter to get the latest job alerts and industry insights.
            </p>
            <form className={styles.newsletterForm} onSubmit={(e) => e.preventDefault()}>
              <input 
                type="email" 
                placeholder="Enter your email" 
                className={styles.input}
              />
              <button type="submit" className={styles.submitBtn}>
                <FaPaperPlane size={14} />
              </button>
            </form>
          </div>
        </div>

        {/* Developer Branding Section (Styled as a distinct badge) */}
        <div className={styles.developerSection}>
          <div className="d-flex align-items-center gap-2">
             <FaCode color="#3b82f6" size={20} />
             <h6 className={styles.devTitle}>Designed & Developed by Mohammad Sharukh</h6>
          </div>
          <p className={styles.devSubtitle}>
            Looking for a high-performance website? Ready to make your business online?
          </p>
          <a href="mailto:developerindia03@gmail.com" className={styles.devContact}>
            Hire Me: developerindia03@gmail.com
          </a>
        </div>

      </div>

      {/* Copyright Bar */}
      <div className={styles.bottomBar}>
        <div className={styles.container}>
           &copy; {new Date().getFullYear()} Job.Portal. All rights reserved.
        </div>
      </div>
    </footer>
  );
}

export default Footer;