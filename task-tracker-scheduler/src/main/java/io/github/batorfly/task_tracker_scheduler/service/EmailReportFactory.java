package io.github.batorfly.task_tracker_scheduler.service;

import io.github.batorfly.task_tracker_scheduler.dto.email.EmailCommand;
import io.github.batorfly.task_tracker_scheduler.dto.task.TaskDto;
import io.github.batorfly.task_tracker_scheduler.dto.user.UserWithTasksDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class EmailReportFactory {

    public List<EmailCommand> create(UserWithTasksDto user, List<TaskDto> completedTasks, List<TaskDto> unfinishedTasks) {
        List<EmailCommand> commands = new ArrayList<>();
        if (!completedTasks.isEmpty()) {
            commands.add(buildCompletedTasksEmail(user, completedTasks));
        }
        if (!unfinishedTasks.isEmpty()) {
            commands.add(buildUnfinishedTasksEmail(user, unfinishedTasks));
        }
        return commands;
    }

    private EmailCommand buildCompletedTasksEmail(UserWithTasksDto user, List<TaskDto> tasks) {
        String header = "You've completed %d task(s) yesterday!".formatted(tasks.size());
        String text = "Good day, %s!\nYou've completed %d task(s) yesterday:\n%s"
                .formatted(fullName(user), tasks.size(), renderTasks(tasks));
        return new EmailCommand(user.email(), header, text);
    }

    private EmailCommand buildUnfinishedTasksEmail(UserWithTasksDto user, List<TaskDto> tasks) {
        String header = "You've got %d unfinished task(s)!".formatted(tasks.size());
        String text = "Good night, %s!\nYou've got %d unfinished task(s):\n%s"
                .formatted(fullName(user), tasks.size(), renderTasks(tasks));
        return new EmailCommand(user.email(), header, text);
    }

    private String renderTasks(List<TaskDto> tasks) {
        List<TaskDto> sortedTasks = tasks.stream()
                .sorted(Comparator.comparing(TaskDto::id, Comparator.nullsLast(Long::compareTo)))
                .toList();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sortedTasks.size(); i++) {
            TaskDto task = sortedTasks.get(i);
            builder.append(i + 1)
                    .append(". ")
                    .append(task.title());
            if (task.description() != null && !task.description().isBlank()) {
                builder.append(": ").append(task.description());
            }
            if (i < sortedTasks.size() - 1) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    private String fullName(UserWithTasksDto user) {
        String firstName = user.firstName() == null ? "" : user.firstName();
        String lastName = user.lastName() == null ? "" : user.lastName();
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isBlank() ? user.email() : fullName;
    }
}
