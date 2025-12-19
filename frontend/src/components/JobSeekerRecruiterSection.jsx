import { useEffect } from 'react';
import {
  FaUserGraduate,
  FaBullhorn,
  FaPlusCircle,
  FaLaptopCode
} from 'react-icons/fa';

import styles from '../css/JobSeekerRecruiter.module.css';

function JobSeekerRecruiterSection() {
  useEffect(() => {
    const elements = document.querySelectorAll(`.${styles.fadeIn}`);
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add(styles.visible);
          }
        });
      },
      { threshold: 0.3 }
    );

    elements.forEach((el) => observer.observe(el));

    return () => {
      elements.forEach((el) => observer.unobserve(el));
    };
  }, []);

  return (
    <section className={`container my-5 ${styles.section}`}>
      <h2 className="text-center mb-5">For Job Seekers, Recruiters & Learners</h2>

      <div className={`row ${styles.contentRow}`}>
        {/* ✅ Job Seekers */}
        <div className={`col-md-4 p-4 ${styles.card} ${styles.fadeIn}`}>
          <div className={styles.iconCircle}>
            <FaUserGraduate size={40} color="#0d6efd" />
          </div>
          <h3 className="mb-3">Job Seekers</h3>
          <ul className={styles.list}>
            <li><FaPlusCircle className={styles.listIcon} /> Discover thousands of verified jobs</li>
            <li><FaPlusCircle className={styles.listIcon} /> Personalized job recommendations</li>
            <li><FaPlusCircle className={styles.listIcon} /> Easy application process</li>
          </ul>
          <button className="btn btn-primary mt-3">Browse Jobs</button>
        </div>

        {/* ✅ Recruiters */}
        <div className={`col-md-4 p-4 ${styles.card} ${styles.fadeIn}`}>
          <div className={styles.iconCircle}>
            <FaBullhorn size={40} color="#dc3545" />
          </div>
          <h3 className="mb-3">Recruiters</h3>
          <ul className={styles.list}>
            <li><FaPlusCircle className={styles.listIcon} /> Post jobs quickly and easily</li>
            <li><FaPlusCircle className={styles.listIcon} /> Access a large talent pool</li>
            <li><FaPlusCircle className={styles.listIcon} /> Manage applications efficiently</li>
          </ul>
          <button className="btn btn-danger mt-3">Post a Job</button>
        </div>

        {/* ✅ Training & Internships */}
        <div className={`col-md-4 p-4 ${styles.card} ${styles.fadeIn}`}>
          <div className={styles.iconCircle}>
            <FaLaptopCode size={40} color="#28a745" />
          </div>
          <h3 className="mb-3">Training & Internships</h3>
          <ul className={styles.list}>
            <li><FaPlusCircle className={styles.listIcon} /> Skill-based training programs</li>
            <li><FaPlusCircle className={styles.listIcon} /> Real-world internship opportunities</li>
            <li><FaPlusCircle className={styles.listIcon} /> Learn & earn with projects</li>
          </ul>
          <button className="btn btn-success mt-3">Explore Training</button>
        </div>
      </div>
    </section>
  );
}

export default JobSeekerRecruiterSection;
