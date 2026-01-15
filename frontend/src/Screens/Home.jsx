import style from "../css/Home.module.css";
import Navbar from "../components/Navbar";
import FeaturedJob from "../components/FeaturedJobs";
import BrowseByCategory from "../components/BrowseByCategory";
import JobSeekerRecruiterSection from "../components/JobSeekerRecruiterSection";
import TrustBannerSection from "../components/TrustBannerSection";
import Footer from "../components/Footer";
import SearchForm from "../components/SearchForm"; 
// ✅ Removed unused Autocomplete/TextField imports

function Home() {
  // ✅ Fixed Typos and formatting for realistic data
  const featuredJob = [
    {
      id: 2,
      name: "DevOps Engineer", // Fixed Spelling (DevOops -> DevOps)
      salary: "$90k - $130k",
      location: "New Delhi, India",
      company: "Meta", // Facebook is now Meta (Optional but modern)
      logo: "https://cdn-icons-png.flaticon.com/512/5968/5968764.png",
    },
    {
      id: 3,
      name: "AI/ML Engineer",
      salary: "$100k - $150k",
      location: "Mumbai, Maharashtra", // Fixed Spelling
      company: "Google",
      logo: "https://cdn-icons-png.flaticon.com/512/300/300221.png",
    },
    {
      id: 4,
      name: "Frontend Developer",
      salary: "$70k - $110k",
      location: "Remote",
      company: "Netflix",
      logo: "https://cdn-icons-png.flaticon.com/512/2504/2504929.png",
    },
  ];

  return (
    <>
      <Navbar />
      <main className={style.main}>
        
        {/* ✅ Improved Banner Section */}
        <section className={style.banner}>
          <div className={style.overlay}></div>
          
          <div className={`container text-center ${style.bannerContent}`}>
            {/* Badge for modern look */}
            <span className={`badge rounded-pill px-3 py-2 mb-3 ${style.primaryBadge}`}>
              #1 Job Portal in India
            </span>

            <h1 className="display-4 fw-bold mb-3 text-white">
              Find Your <span className="text-primary">Dream Job</span> Today
            </h1>
            
            <p className="lead mb-5 text-light opacity-75">
              Connecting talent with opportunity. Explore thousands of jobs from top companies.
            </p>

            {/* ✅ Search Form in a Glass Container */}
            <div className={style.searchContainer}>
              <SearchForm />
            </div>
          </div>
        </section>

        {/* ✅ Featured Jobs */}
        {/* Added py-5 for better spacing */}
        <div className="py-5">
           <FeaturedJob featuredJobs={featuredJob} />
        </div>

        {/* ✅ Browse by Category */}
        <div className="bg-white py-5">
           <BrowseByCategory />
        </div>
        
        {/* ✅ Info Section */}
        <JobSeekerRecruiterSection />
        
        {/* ✅ Trust Banner */}
        <TrustBannerSection />
        
        <Footer />
      </main>
    </>
  );
}

export default Home;