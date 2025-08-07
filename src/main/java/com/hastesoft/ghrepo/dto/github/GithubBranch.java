package com.hastesoft.ghrepo.dto.github;

import lombok.Data;

@Data
public class GithubBranch {
    
    private String name;
    private GithubCommit commit;
}
