import style from "../css/Home.module.css";
import Navbar from "../components/Navbar";
import FeaturedJob from "../components/FeaturedJobs";
import BrowseByCategory from "../components/BrowseByCategory";
import JobSeekerRecruiterSection from "../components/JobSeekerRecruiterSection";
import TrustBannerSection from "../components/TrustBannerSection";
import Footer from "../components/Footer";
import Autocomplete from "@mui/material/Autocomplete";
import TextField from "@mui/material/TextField";
import SearchForm from "../components/SearchForm";

function Home() {
  const featuredJob = [
    {
      id: 1,
      name: "Software Engineer",
      salary: "8$ - 24$",
      location: "Banglure, Karnatka",
      company: "Microsoft",
      logo: "https://cdn-icons-png.flaticon.com/512/732/732221.png",
    },
    {
      id: 2,
      name: "DevOops Enginner",
      salary: "10$ - 25$",
      location: "New Delhi, India",
      company: "Facebook",
      logo: "https://cdn-icons-png.flaticon.com/512/5968/5968764.png",
    },
    {
      id: 3,
      name: "AI/ML Enginner",
      salary: "20$ - 35$",
      location: "Mumbai, Maharastra",
      company: "Google",
      logo: "https://cdn-icons-png.flaticon.com/512/300/300221.png",
    },
  ];

  return (
    <>
      <Navbar />
      <main className={style.main}>
        {/* ✅ Banner Section */}
        <section className={style.banner}>
          <div className={style.overlay}></div>
          <div className="container position-relative text-white text-center py-5">
            <h1 className="display-4 fw-bold mb-3">Find Your Dream Job Here</h1>
            <p className="lead mb-5">
              Explore thousands of jobs from top companies worldwide.
            </p>
            {/* <form
              className={`row g-3 justify-content-center ${style.searchBox}`}
            >
              <div className="col-md-4 position-relative">
                <input
                  type="text"
                  className={`form-control ps-5 ${style.inputWithIcon}`}
                  placeholder="Job title or keyword"
                />
                <span className={style.icon}>
                  <i className="bi bi-briefcase-fill"></i>
                </span>
              </div>

              <div className="col-md-3 position-relative">
                <input
                  type="text"
                  className={`form-control ps-5 ${style.inputWithIcon}`}
                  placeholder="Location"
                />
                <span className={style.icon}>
                  <i className="bi bi-geo-alt-fill"></i>
                </span>
              </div>

              <div className="col-md-3 position-relative">
                <select className={`form-select ps-5 ${style.inputWithIcon}`}>
                  <option>All Categories</option>
                  <option>Engineering</option>
                  <option>Design</option>
                  <option>Marketing</option>
                </select>
                <span className={style.icon}>
                  <i className="bi bi-list-ul"></i>
                </span>
              </div>

              <div className="col-md-2 d-grid">
                <button type="submit" className="btn btn-primary btn-lg shadow">
                  Search
                </button>
              </div>
            </form> */}
            <SearchForm />
          </div>
        </section>

        {/* ✅ Featured Jobs */}
        <FeaturedJob featuredJobs={featuredJob} />

        {/* ✅ Browse by Category */}
        <BrowseByCategory />
        <JobSeekerRecruiterSection />
        <TrustBannerSection />
        <Footer />
      </main>
    </>
  );
}

export default Home;
