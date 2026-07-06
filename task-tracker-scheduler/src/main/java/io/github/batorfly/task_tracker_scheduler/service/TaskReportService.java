package io.github.batorfly.task_tracker_scheduler.service;

import io.github.batorfly.task_tracker_scheduler.client.BackendClient;
import io.github.batorfly.task_tracker_scheduler.config.SchedulerProperties;
import io.github.batorfly.task_tracker_scheduler.dto.email.EmailCommand;
import io.github.batorfly.task_tracker_scheduler.dto.task.TaskDto;
import io.github.batorfly.task_tracker_scheduler.dto.user.UserWithTasksDto;
import io.github.batorfly.task_tracker_scheduler.service.kafka.EmailCommandPublisher;
import io.github.batorfly.task_tracker_scheduler.util.ReportPeriodResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskReportService {
    private final BackendClient backendClient;
    private final ReportPeriodResolver periodResolver;
    private final EmailReportFactory emailReportFactory;
    private final EmailCommandPublisher emailCommandPublisher;
    private final SchedulerProperties schedulerProperties;

    public int publishDailyReports() {
        ReportPeriodResolver.ReportPeriod period = periodResolver.previousDay(schedulerProperties.reports().zone());
        List<UserWithTasksDto> users = backendClient.loadUsersWithTasks();
        int published = 0;

        for (UserWithTasksDto user : users) {
            Set<TaskDto> tasks = user.tasks();
            if (tasks == null || tasks.isEmpty()) {
                continue;
            }

            List<TaskDto> completedTasks = tasks.stream()
                    .filter(TaskDto::done)
                    .filter(task -> period.contains(task.completionTime()))
                    .toList();
            List<TaskDto> unfinishedTasks = tasks.stream()
                    .filter(task -> !task.done())
                    .toList();

            List<EmailCommand> commands = emailReportFactory.create(user, completedTasks, unfinishedTasks);
            for (EmailCommand command : commands) {
                emailCommandPublisher.publish(command);
                published++;
            }
        }

        log.info("Prepared daily reports for {} user(s), period [{} - {})",
                users.size(), period.start(), period.end());
        return published;
    }
}
