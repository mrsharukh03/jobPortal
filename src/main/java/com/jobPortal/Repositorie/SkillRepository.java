package com.jobPortal.Repositorie;

import com.jobPortal.Model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill,Long> {
    Optional<Skill> findByNameIgnoreCase(String trim);
    List<Skill> findAllByIdIn(List<Long> ids);
}
