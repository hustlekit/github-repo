package com.hastesoft.ghrepo.dto.github;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GithubCommit {
    
    private String sha;
}
