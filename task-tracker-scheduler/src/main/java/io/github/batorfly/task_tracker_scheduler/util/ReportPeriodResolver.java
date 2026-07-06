package io.github.batorfly.task_tracker_scheduler.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class ReportPeriodResolver {

    public ReportPeriod previousDay(String zone) {
        ZoneId zoneId = ZoneId.of(zone);
        LocalDate today = LocalDate.now(zoneId);
        Instant start = today.minusDays(1).atStartOfDay(zoneId).toInstant();
        Instant end = today.atStartOfDay(zoneId).toInstant();
        return new ReportPeriod(start, end);
    }

    public record ReportPeriod(Instant start, Instant end) {
        public boolean contains(Instant value) {
            return value != null && !value.isBefore(start) && value.isBefore(end);
        }
    }
}
