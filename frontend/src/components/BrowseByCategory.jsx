import { 
  FaLaptopCode, 
  FaPaintBrush, 
  FaBullhorn, 
  FaChartLine,
  FaDatabase,
  FaMobileAlt,
  FaChartPie,
  FaUserTie,
  FaRobot,
  FaGlobe
} from 'react-icons/fa';

const categories = [
  { id: 1, name: "Engineering", icon: <FaLaptopCode size={24} />, jobs: 1234 },
  { id: 2, name: "Design", icon: <FaPaintBrush size={24} />, jobs: 678 },
  { id: 3, name: "Marketing", icon: <FaBullhorn size={24} />, jobs: 980 },
  { id: 4, name: "Sales", icon: <FaChartLine size={24} />, jobs: 450 },
  { id: 5, name: "Data Science", icon: <FaDatabase size={24} />, jobs: 540 },
  { id: 6, name: "Mobile Dev", icon: <FaMobileAlt size={24} />, jobs: 310 },
  { id: 7, name: "Analytics", icon: <FaChartPie size={24} />, jobs: 420 },
  { id: 8, name: "Management", icon: <FaUserTie size={24} />, jobs: 290 },
  { id: 9, name: "AI/ML", icon: <FaRobot size={24} />, jobs: 350 },
  { id: 10, name: "Global", icon: <FaGlobe size={24} />, jobs: 180 },
];

function BrowseByCategory() {
  return (
    <section className="container my-5">
      <h2 className="mb-4">Browse by Category</h2>

      <div 
        style={{ 
          display: 'flex', 
          overflowX: 'auto', 
          gap: '1rem', 
          paddingBottom: '0.5rem',
          scrollbarWidth: 'thin',  // Firefox
          scrollbarColor: '#ccc transparent',
        }}
        className="category-scrollbar"
      >
        {categories.map(cat => (
          <div
            key={cat.id}
            className="category-card rounded shadow-sm d-flex flex-column align-items-center text-dark flex-shrink-0"
            style={{ 
              backgroundColor: 'whitesmoke', 
              cursor: 'pointer', 
              transition: 'transform 0.3s',
              width: '120px',
              padding: '1rem',
              minHeight: '120px',
              justifyContent: 'center',
              userSelect: 'none',
            }}
            onMouseEnter={e => e.currentTarget.style.transform = 'scale(1.1)'}
            onMouseLeave={e => e.currentTarget.style.transform = 'scale(1)'}
          >
            {cat.icon}
            <h6 className="mt-2 mb-1 text-center">{cat.name}</h6>
            <p className="mb-0 text-muted" style={{ fontSize: '0.8rem', textAlign: 'center' }}>
              {cat.jobs} jobs
            </p>
          </div>
        ))}
      </div>
    </section>
  );
}

export default BrowseByCategory;
