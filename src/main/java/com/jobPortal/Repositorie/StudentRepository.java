package com.jobPortal.Repositorie;

import com.jobPortal.Model.Users.Student;
import com.jobPortal.Model.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {

    Student findByUser(User user);
    boolean existsByUser(User user);
}
