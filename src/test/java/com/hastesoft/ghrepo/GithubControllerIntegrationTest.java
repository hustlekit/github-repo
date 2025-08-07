package com.hastesoft.ghrepo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class GithubControllerIntegrationTest {
    
    private static final String LOCAL_SERVER_URL = "http://localhost:8080";
    
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new TestRestTemplate();
    }
    
    @Test
    void shouldReturnRepositoriesForExistingUser() {
        // Given
        String existingUsername = "hustlekit";
        log.info("Testing with existing user: {}", existingUsername);

        // When
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                LOCAL_SERVER_URL + "/api/users/{username}/repos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                existingUsername
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();

        response.getBody().forEach(repo -> {
            assertThat(repo.get("repositoryName")).isNotNull();
            assertThat(repo.get("ownerLogin")).isEqualTo(existingUsername);
            assertThat(repo.get("branches")).isInstanceOf(List.class);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> branches = (List<Map<String, Object>>) repo.get("branches");
            branches.forEach(branch -> {
                assertThat(branch.get("name")).isNotNull();
                assertThat(branch.get("lastCommitSha")).isNotNull();
            });
        });
    }

    @Test
    void shouldReturn404ForNonExistingUser() {
        // Given:
        String nonExistingUsername = "hustlekit123abc";
        log.info("Testing with non-existing user: {}", nonExistingUsername);

        // When:
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                LOCAL_SERVER_URL + "/api/users/{username}/repos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                nonExistingUsername
        );

        // Then:
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(404);
        assertThat(response.getBody().get("message").toString()).contains("not found");
    }
}
