package com.hastesoft.ghrepo.controller;

import com.hastesoft.ghrepo.dto.RepositoryDto;
import com.hastesoft.ghrepo.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class GithubController {
    
    private final GithubService githubService;
    
    @GetMapping("/{username}/repos")
    public ResponseEntity<List<RepositoryDto>> getUserRepositories(@PathVariable String username) {
        List<RepositoryDto> repositories = githubService.getUserRepositories(username);
        return ResponseEntity.ok(repositories);
    }
}
