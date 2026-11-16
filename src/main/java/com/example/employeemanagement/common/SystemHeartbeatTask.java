package com.example.employeemanagement.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Lab 8: mỗi 30 giây log "System running".
// fixedRate: tính từ lúc lần trước BẮT ĐẦU; fixedDelay: tính từ lúc lần trước KẾT THÚC; cron: theo lịch.
// Chu kỳ đọc từ app.heartbeat-interval (Spring 6.1+ nhận dạng "30s", "1m" ngoài ISO-8601 "PT30S").
@Component
public class SystemHeartbeatTask {

    private static final Logger log = LoggerFactory.getLogger(SystemHeartbeatTask.class);

    @Scheduled(fixedRateString = "${app.heartbeat-interval:30s}", initialDelayString = "${app.heartbeat-interval:30s}")
    public void logHeartbeat() {
        log.info("System running");
    }
}
