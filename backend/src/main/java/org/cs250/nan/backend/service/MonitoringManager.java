package org.cs250.nan.backend.service;

import lombok.Getter;
import org.cs250.nan.backend.config.AppProperties;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Starts/stops a recurring scan at runtime.
 * Stores all collected scan results in memory.
 */
@Service
public class MonitoringManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringManager.class);

    @Getter
    private boolean monitoringActive = false;
    private final ScanService scanService;
    private final TaskScheduler scheduler;
    private final AppProperties props;
    private final List<JSONObject> data = new ArrayList<>();
    private final AtomicReference<ScheduledFuture<?>> handle = new AtomicReference<>();

    public MonitoringManager(ScanService scanService,
                             @Qualifier("taskScheduler") TaskScheduler scheduler,
                             AppProperties props) {
        this.scanService = scanService;
        this.scheduler = scheduler;
        this.props = props;
    }

    public synchronized void startMonitoring() {
        if (monitoringActive) {
            LOGGER.info("Monitoring already active");
            return;
        }

        if (props.getMonitor().getScanInterval() <= 0) {
            LOGGER.warn("Monitoring interval is set to 0 or less, not starting");
            return;
        }

        monitoringActive = true;

        if (handle.get() != null && !handle.get().isCancelled()) {
            LOGGER.info("Already monitoring");
            return;
        }
        LOGGER.info("Starting monitoring every {}s", props.getMonitor().getScanInterval());
        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(
                this::executeScan,
                Duration.ofSeconds(props.getMonitor().getScanInterval())
        );
        handle.set(task);
    }

    public synchronized void stopMonitoring() {
        var task = handle.getAndSet(null);
        if (task != null) {
            task.cancel(true);
            LOGGER.info("Monitoring stopped");
            monitoringActive = false;
        }
    }

    private void executeScan() {
        scanService.scan(props.getMonitor())
                .thenAccept(results -> {
                    synchronized (data) {
                        data.addAll(results);
                    }
                    LOGGER.info("Monitoring scan: {} results", results.size());
                })
                .exceptionally(ex -> {
                    LOGGER.error("Monitoring scan failed", ex);
                    return null;
                });
    }

    public List<JSONObject> getMonitoringData() {
        synchronized (data) {
            return new ArrayList<>(data);
        }
    }
}
