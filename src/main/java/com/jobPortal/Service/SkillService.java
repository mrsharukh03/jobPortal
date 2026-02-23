package com.jobPortal.Service;

import com.jobPortal.Repository.SeekerRepository;
import com.jobPortal.Repository.SkillRepository;
import com.jobPortal.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final SeekerRepository seeekerRepository;
    private final UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public SkillService(SkillRepository skillRepository, SeekerRepository seeekerRepository, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.seeekerRepository = seeekerRepository;
        this.userRepository = userRepository;
    }

}
