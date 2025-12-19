import style from "../css/Home.module.css";
import { FaMapMarkerAlt, FaBuilding, FaMoneyBillWave } from "react-icons/fa";

function FeaturedJob({ featuredJobs }) {
  return (
    <section className="container my-5">
      <h2 className="mb-4 text-center">Featured Jobs</h2>

      <div className="row gy-4">
        {featuredJobs.map((job) => (
          <div key={job.id} className="col-12 col-sm-6 col-md-4 col-lg-3">
            <div className="card shadow-lg h-100 border-0 rounded-4 p-3">
              <div className="d-flex align-items-center justify-content-center mb-3">
                <img
                  src={job.logo}
                  alt={`${job.company} Logo`}
                  height="60"
                  width="60"
                  className={`rounded-circle ${style.companyLogo}`}
                />
              </div>
              <div className="text-center">
                <h5 className="fw-bold">{job.name}</h5>
              </div>
              <div className="mt-3 ps-3">
                <p className="mb-2">
                  <FaMapMarkerAlt className="me-2 text-primary" />
                  {job.location}
                </p>
                <p className="mb-2">
                  <FaBuilding className="me-2 text-secondary" />
                  {job.company}
                </p>
                <p className="mb-3">
                  <FaMoneyBillWave className="me-2 text-success" />
                  {job.salary} LPA
                </p>
              </div>
              <div className="text-center">
                <button className="btn btn-outline-primary w-75 rounded-pill">
                  Apply Now
                </button>
              </div>
            </div>
          </div>
        ))}

        {/* Promo Card */}
        <div className="col-12 col-sm-6 col-md-4 col-lg-3">
          <div className="card h-100 bg-primary text-white border-0 shadow-sm rounded-4 d-flex flex-column justify-content-center p-4 text-center">
            <h4 className="fw-bold">
              Over <span className="text-warning">10,000+</span> Jobs Posted
              Every Month
            </h4>
            <p className="mt-3">
              Join the platform and discover opportunities tailored to your
              skills.
            </p>
            <button className="btn btn-light mt-3 rounded-pill px-4 mx-auto">
              Explore Jobs
            </button>
          </div>
        </div>
      </div>
    </section>
  );
}

export default FeaturedJob;
