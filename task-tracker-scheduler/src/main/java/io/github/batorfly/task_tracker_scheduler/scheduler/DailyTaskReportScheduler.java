package io.github.batorfly.task_tracker_scheduler.scheduler;

import io.github.batorfly.task_tracker_scheduler.service.TaskReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyTaskReportScheduler {
    private final TaskReportService taskReportService;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Scheduled(cron = "${scheduler.reports.daily-cron}", zone = "${scheduler.reports.zone}")
    public void publishDailyReports() {
        if (!running.compareAndSet(false, true)) {
            log.warn("Daily task report job is already running, skipping concurrent launch");
            return;
        }

        try {
            int publishedMessages = taskReportService.publishDailyReports();
            log.info("Daily task report job finished. Published {} email command(s)", publishedMessages);
        } catch (RuntimeException e) {
            log.error("Daily task report job failed", e);
        } finally {
            running.set(false);
        }
    }
}
