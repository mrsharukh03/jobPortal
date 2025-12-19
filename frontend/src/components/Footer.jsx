import styles from '../css/Footer.module.css';
import {
  FaFacebookF,
  FaTwitter,
  FaLinkedinIn,
  FaInstagram,
  FaEnvelope,
} from 'react-icons/fa';

function Footer() {
  return (
    <footer className={styles.footer}>
      <div className={`container ${styles.container}`}>

        {/* Logo & About */}
        <div className={styles.col}>
          <div className={styles.logo}>
            <img src="https://cdn-icons-png.flaticon.com/512/6956/6956763.png" alt="Logo" />
            <span>Job.Portal</span>
          </div>
          <p className={styles.description}>
            Empowering job seekers and recruiters with a seamless and trusted platform to connect, grow, and hire.
          </p>
          <div className={styles.socialIcons}>
            <a href="#"><FaFacebookF /></a>
            <a href="#"><FaTwitter /></a>
            <a href="#"><FaLinkedinIn /></a>
            <a href="https://www.instagram.com/@javashark_"><FaInstagram /></a>
          </div>
        </div>

        {/* Quick Links */}
        <div className={styles.col}>
          <h5>Quick Links</h5>
          <ul>
            <li><a href="#">Free Courses</a></li>
            <li><a href="#">Find Jobs</a></li>
            <li><a href="#">Post a Job</a></li>
            <li><a href="#">Carrers</a></li>
            <li><a href="#">Contact</a></li>
          </ul>
        </div>

        {/* Newsletter */}
        <div className={styles.col}>
          <h5>Stay Updated</h5>
          <p>Subscribe to get latest job updates and hiring trends.</p>
          <form className={styles.newsletterForm}>
            <input type="email" placeholder="Your email" />
            <button type="submit"><FaEnvelope /></button>
          </form>


        <div className={` mt-3 p-3 text-center ${styles.developerCard}`}>
          <p className='card-title'>This site developed by Mohammad Sharukh </p>
          <p className='card-title'>Ready to make your Bussness Online</p>
          <p className='card-title'>Contact : developerindia03@gmail.com</p>
        </div>


        </div>

      </div>

      <div className={styles.bottomBar}>
        <p>&copy; {new Date().getFullYear()} Job.Portal — All rights reserved.</p>
      </div>
    </footer>
  );
}

export default Footer;
