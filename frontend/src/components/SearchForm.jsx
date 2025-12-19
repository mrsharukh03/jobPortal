import React, { useState, useEffect, useMemo } from "react";
import Autocomplete from "@mui/material/Autocomplete";
import TextField from "@mui/material/TextField";
import { FaSearch } from "react-icons/fa";
import debounce from "lodash.debounce";
import styles from "../css/SearchForm.module.css";

const mockJobTitles = [
  "Software Engineer",
  "DevOps Engineer",
  "AI/ML Engineer",
  "Frontend Developer",
  "Backend Developer",
  "Full Stack Developer",
];

const mockLocations = [
  "Mumbai",
  "Bangalore",
  "Delhi",
  "Pune",
  "Hyderabad",
  "Chennai",
];

function SearchForm() {
  const [jobQuery, setJobQuery] = useState("");
  const [locationQuery, setLocationQuery] = useState("");
  const [jobOptions, setJobOptions] = useState([]);
  const [locationOptions, setLocationOptions] = useState([]);

  const fetchMockJobs = useMemo(
    () =>
      debounce((query) => {
        if (!query) {
          setJobOptions([]);
          return;
        }
        const filtered = mockJobTitles.filter((title) =>
          title.toLowerCase().startsWith(query.toLowerCase())
        );
        setJobOptions(filtered);
      }, 300),
    []
  );

  const fetchMockLocations = useMemo(
    () =>
      debounce((query) => {
        if (!query) {
          setLocationOptions([]);
          return;
        }
        const filtered = mockLocations.filter((loc) =>
          loc.toLowerCase().startsWith(query.toLowerCase())
        );
        setLocationOptions(filtered);
      }, 300),
    []
  );

  useEffect(() => {
    fetchMockJobs(jobQuery);
  }, [jobQuery, fetchMockJobs]);

  useEffect(() => {
    fetchMockLocations(locationQuery);
  }, [locationQuery, fetchMockLocations]);

  useEffect(() => {
    return () => {
      fetchMockJobs.cancel();
      fetchMockLocations.cancel();
    };
  }, [fetchMockJobs, fetchMockLocations]);

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Job:", jobQuery);
    console.log("Location:", locationQuery);
  };

  return (
    <div className={styles.searchCard}>
      <form className={styles.searchForm} onSubmit={handleSubmit}>
        <div className={styles.formItem}>
          <Autocomplete
            freeSolo
            options={jobOptions}
            inputValue={jobQuery}
            onInputChange={(e, value) => setJobQuery(value)}
            clearOnBlur={false}
            filterOptions={(x) => x}
            renderInput={(params) => (
              <TextField
                {...params}
                fullWidth
                label="Job title or keyword"
                variant="outlined"
                sx={{
                  "& .MuiInputBase-root": {
                    backgroundColor: "rgba(255, 255, 255, 0.05)", // semi-transparent dark
                    borderRadius: "12px",
                  },
                  "& .MuiInputBase-input": {
                    color: "white",
                  },
                  "& .MuiInputLabel-root": {
                    color: "white",
                  },
                  "& .MuiOutlinedInput-notchedOutline": {
                    border: "none", // remove border
                  },
                  "&:hover .MuiOutlinedInput-notchedOutline": {
                    border: "none",
                  },
                  "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
                    border: "none",
                  },
                }}
              />
            )}
          />
        </div>

        <div className={styles.formItem}>
          <Autocomplete
            freeSolo
            options={locationOptions}
            inputValue={locationQuery}
            onInputChange={(e, value) => setLocationQuery(value)}
            clearOnBlur={false}
            filterOptions={(x) => x}
            renderInput={(params) => (
              <TextField
                {...params}
                fullWidth
                label="Location"
                variant="outlined"
                sx={{
                  "& .MuiInputBase-root": {
                    backgroundColor: "rgba(255, 255, 255, 0.05)", // semi-transparent dark
                    borderRadius: "12px",
                  },
                  "& .MuiInputBase-input": {
                    color: "white",
                  },
                  "& .MuiInputLabel-root": {
                    color: "white",
                  },
                  "& .MuiOutlinedInput-notchedOutline": {
                    border: "none", // remove border
                  },
                  "&:hover .MuiOutlinedInput-notchedOutline": {
                    border: "none",
                  },
                  "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
                    border: "none",
                  },
                }}
              />
            )}
          />
        </div>

        <div className={styles.formItem}>
          <button type="submit" className={styles.searchButton}>
            <FaSearch /> Search
          </button>
        </div>
      </form>
    </div>
  );
}

export default SearchForm;
