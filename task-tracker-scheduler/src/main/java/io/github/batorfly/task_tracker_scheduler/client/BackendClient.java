package io.github.batorfly.task_tracker_scheduler.client;

import io.github.batorfly.task_tracker_scheduler.config.BackendClientProperties;
import io.github.batorfly.task_tracker_scheduler.config.SchedulerProperties;
import io.github.batorfly.task_tracker_scheduler.dto.auth.LoginRequest;
import io.github.batorfly.task_tracker_scheduler.dto.auth.LoginResponse;
import io.github.batorfly.task_tracker_scheduler.dto.user.UserWithTasksDto;
import io.github.batorfly.task_tracker_scheduler.exception.BackendClientException;
import io.github.batorfly.task_tracker_scheduler.exception.SchedulerAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BackendClient {
    private final RestClient.Builder restClientBuilder;
    private final BackendClientProperties backendProperties;
    private final SchedulerProperties schedulerProperties;

    public List<UserWithTasksDto> loadUsersWithTasks() {
        SchedulerProperties.Credentials credentials = schedulerProperties.credentials();
        String accessToken = login(credentials.email(), credentials.password());
        return getUsersWithTasks(accessToken);
    }

    private String login(String email, String password) {
        try {
            LoginResponse response = restClientBuilder.build()
                    .post()
                    .uri(backendProperties.baseUrl() + backendProperties.authPath())
                    .body(new LoginRequest(email, password))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, clientResponse) -> {
                        throw new SchedulerAuthenticationException("Scheduler authentication failed with status "
                                + clientResponse.getStatusCode());
                    })
                    .body(LoginResponse.class);

            if (response == null || response.accessToken() == null || response.accessToken().isBlank()) {
                throw new SchedulerAuthenticationException("Backend returned an empty access token");
            }
            return response.accessToken();
        } catch (SchedulerAuthenticationException e) {
            throw e;
        } catch (RestClientException e) {
            throw new BackendClientException("Backend authentication request failed", e);
        }
    }

    private List<UserWithTasksDto> getUsersWithTasks(String accessToken) {
        try {
            List<UserWithTasksDto> users = restClientBuilder.build()
                    .get()
                    .uri(backendProperties.baseUrl() + backendProperties.usersPath())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new BackendClientException("Loading users failed with status "
                                + response.getStatusCode());
                    })
                    .body(new ParameterizedTypeReference<>() {
                    });

            return users == null ? List.of() : users;
        } catch (BackendClientException e) {
            throw e;
        } catch (RestClientException e) {
            throw new BackendClientException("Backend users request failed", e);
        }
    }
}
