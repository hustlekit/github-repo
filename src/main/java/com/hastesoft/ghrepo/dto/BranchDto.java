package com.hastesoft.ghrepo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BranchDto {
    
    private String name;
    private String lastCommitSha;
}
