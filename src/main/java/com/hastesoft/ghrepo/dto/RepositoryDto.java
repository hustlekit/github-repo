package com.hastesoft.ghrepo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RepositoryDto {
    
    private String repositoryName;
    private String ownerLogin;
    private List<BranchDto> branches;
}
