package com.hastesoft.ghrepo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hastesoft.ghrepo.dto.BranchDto;
import com.hastesoft.ghrepo.dto.RepositoryDto;
import com.hastesoft.ghrepo.dto.github.GithubBranch;
import com.hastesoft.ghrepo.dto.github.GithubRepository;
import com.hastesoft.ghrepo.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubService {

    private static final String GITHUB_API_URL = "https://api.github.com";
    private static final String USERS_PATH = "/users";
    private static final String REPOS_PATH = "/repos";
    private static final String BRANCHES_PATH = "/branches";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<RepositoryDto> getUserRepositories(String username) {
        try {
            List<GithubRepository> repositories = fetchRepositories(username);
            return repositories.stream()
                    .filter(repository -> !repository.isFork())
                    .map(repository -> mapToRepositoryDto(repository, username))
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException.NotFound e) {
            throw createUserNotFoundException(username);
        } catch (JsonProcessingException e) {
            throw createGithubApiException("repositories", e);
        }
    }

    private List<GithubRepository> fetchRepositories(String username) throws JsonProcessingException {
        log.info("Fetch repositories for user: {}", username);
        String repositoryUrl = buildUserRepositoriesUrl(username);
        ResponseEntity<String> response = restTemplate.getForEntity(repositoryUrl, String.class);
        return parseResponse(response.getBody(), new TypeReference<>() {});
    }

    private RepositoryDto mapToRepositoryDto(GithubRepository repository, String username) {
        List<BranchDto> branches = getBranches(username, repository.getName());
        return RepositoryDto.builder()
                .repositoryName(repository.getName())
                .ownerLogin(repository.getOwner().getLogin())
                .branches(branches)
                .build();
    }

    private List<BranchDto> getBranches(String username, String repositoryName) {
        try {
            return fetchBranches(username, repositoryName);
        } catch (HttpClientErrorException e) {
            log.warn("Could not fetch branches for repository: {}/{}", username, repositoryName);
            return List.of();
        } catch (JsonProcessingException e) {
            throw createGithubApiException("branches", e);
        }
    }

    private List<BranchDto> fetchBranches(String username, String repositoryName) throws JsonProcessingException {
        log.info("Fetch branches for repository: {}/{}", username, repositoryName);
        String branchesUrl = buildRepositoryBranchesUrl(username, repositoryName);
        ResponseEntity<String> response = restTemplate.getForEntity(branchesUrl, String.class);
        List<GithubBranch> branches = parseResponse(response.getBody(), new TypeReference<>() {});
        return branches.stream()
                .map(this::mapToBranchDto)
                .collect(Collectors.toList());
    }

    private BranchDto mapToBranchDto(GithubBranch branch) {
        return BranchDto.builder()
                .name(branch.getName())
                .lastCommitSha(branch.getCommit().getSha())
                .build();
    }

    private String buildUserRepositoriesUrl(String username) {
        return GITHUB_API_URL + USERS_PATH + "/" + username + "/repos";
    }

    private String buildRepositoryBranchesUrl(String username, String repositoryName) {
        return GITHUB_API_URL + REPOS_PATH + "/" + username + "/" + repositoryName + BRANCHES_PATH;
    }

    private <T> T parseResponse(String response, TypeReference<T> typeReference) throws JsonProcessingException {
        return objectMapper.readValue(response, typeReference);
    }

    private UserNotFoundException createUserNotFoundException(String username) {
        log.warn("GitHub user '{}' not found", username);
        return new UserNotFoundException("Github user '" + username + "' not found");
    }

    private RuntimeException createGithubApiException(String context, Exception e) {
        log.error("Error parsing Github API {} response", context, e);
        return new RuntimeException("Error parsing Github API " + context + " response", e);
    }
}
