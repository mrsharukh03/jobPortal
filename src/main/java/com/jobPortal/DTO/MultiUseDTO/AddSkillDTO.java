package com.jobPortal.DTO.MultiUseDTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@AllArgsConstructor
public class AddSkillDTO {
    @NotNull
    @NotEmpty
    String skillName;
}
