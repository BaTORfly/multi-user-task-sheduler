package io.github.batorfly.task_tracker_backend.service.authority;

import io.github.batorfly.task_tracker_backend.domain.user.Authority;
import io.github.batorfly.task_tracker_backend.repository.user.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * authorityService version 1.0
 */
@Service
@RequiredArgsConstructor @Slf4j
public class AuthorityServiceImpl implements AuthorityService {
    private final AuthorityRepository authorityRepository;

    @Override
    public void save(Authority authority) {
        log.info("Saving authority with role {}", authority.getAuthority());
        authorityRepository.save(authority);
    }
}
