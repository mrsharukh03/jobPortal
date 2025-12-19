import React from "react";
import { useNavigate } from "react-router-dom";
import style from "../css/NotFound.module.css";

function NotFound() {
  const navigate = useNavigate();

  return (
    <div className={style.notFoundPage}>
      <h1 className={style.title}>404</h1>
      <p className={style.subtitle}>Oops! The page you're looking for doesn't exist.</p>

      <img
        className={style.ghostImage}
        src="https://assets10.lottiefiles.com/private_files/lf30_jk6c1n2n.json"
        alt="Ghost"
        onError={(e) => {
          e.target.src = "https://i.imgur.com/qIufhof.png"; // fallback ghost image
        }}
      />

      <button
        className={style.backButton}
        onClick={() => navigate("/")}
      >
        Go Back Home
      </button>
    </div>
  );
}
export default NotFound;
