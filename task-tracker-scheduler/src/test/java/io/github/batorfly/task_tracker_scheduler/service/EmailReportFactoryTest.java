package io.github.batorfly.task_tracker_scheduler.service;

import io.github.batorfly.task_tracker_scheduler.dto.email.EmailCommand;
import io.github.batorfly.task_tracker_scheduler.dto.task.TaskDto;
import io.github.batorfly.task_tracker_scheduler.dto.user.UserWithTasksDto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

class EmailReportFactoryTest {
    private final EmailReportFactory factory = new EmailReportFactory();

    @Test
    void includesAllTasksInEmailText() {
        UserWithTasksDto user = new UserWithTasksDto(
                1L,
                "user@example.com",
                "John",
                "Smith",
                Set.of()
        );
        List<TaskDto> unfinishedTasks = LongStream.rangeClosed(1, 7)
                .mapToObj(id -> new TaskDto(id, "Task " + id, "Description " + id, false, null))
                .toList();

        List<EmailCommand> commands = factory.create(user, List.of(), unfinishedTasks);

        assertThat(commands).hasSize(1);
        assertThat(commands.get(0).text())
                .contains("1. Task 1: Description 1")
                .contains("7. Task 7: Description 7")
                .doesNotContain("...and");
    }
}
