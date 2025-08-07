package com.hastesoft.ghrepo.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GithubRepository {
    
    private String name;
    private GithubOwner owner;
    @JsonProperty("fork")
    private boolean fork;
}
