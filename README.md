# GitHub Repository Explorer

## Overview
GitHub Repository Explorer is a Spring Boot application that provides a REST API to fetch and display GitHub repository information for any given user. The application retrieves non-forked repositories along with their branch details using the GitHub API.

## Features
- Fetch all non-forked repositories for a given GitHub username
- Retrieve branch information for each repository
- REST API with JSON responses
- Error handling for non-existent users and API issues

## Technical Stack
- Java 21
- Spring Boot 3.5.4
- Spring MVC
- Lombok
- Jackson for JSON processing
- RestTemplate for HTTP requests
- JUnit and Spring Test for testing

## API Endpoints

### Get User Repositories
```
GET /api/users/{username}/repos
``` 
Returns a list of repositories for the specified GitHub username.

#### Response Format
##### Success Response
```json
[
    {
        "repositoryName": "repository-name",
        "ownerLogin": "username",
        "branches": [
            {
                "name": "branch-name",
                "lastCommitSha": "commit-sha-hash"
            }
        ]
    }
]
```


#### Error Response Format
```json
{ 
    "status": 404, 
    "message": "Github user '{username}' not found" 
}
``` 

#### Status Codes
- `200 OK`: Successful request
- `404 Not Found`: When the specified GitHub user doesn't exist
- `500 Internal Server Error`: When there's an error processing the GitHub API response, possible if too many requests are made in period of time


## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.x

### Building the Application
```bash
  mvn clean install
``` 

### Running the Application
1. Clone the repository
2. Navigate to the project directory
3. Run using Maven:
```bash
  mvn spring-boot:run
``` 
The application will start on `http://localhost:8080`

### Running Tests
```
mvn test
``` 

### Usage Example
To get repositories for a GitHub user:
```bash
  curl http://localhost:8080/api/users/hustlekit/repos
```
```bash
  curl http://localhost:8080/api/users/hustlekit123abc/repos
```
or through a browser:
```
http://localhost:8080/api/users/hustlekit/repos
http://localhost:8080/api/users/hustlekit123abc/repos
```

## Configuration
The application uses default Spring Boot configuration. No additional configuration is required to get started.

## Testing
The application includes integration tests that verify:
- Repository fetching for existing users
- Error handling for non-existent users

## Dependencies
- spring-boot-starter-web
- spring-boot-starter-tomcat
- lombok
- spring-boot-starter-test

## Building for Production
To build a WAR file for deployment:
```bash
  mvn clean package
``` 
The WAR file will be created in the `target` directory.
