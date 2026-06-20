package io.github.batorfly.task_tracker_backend.web.api.controller.user;

import io.github.batorfly.task_tracker_backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {
    private final UserService userService;
}
