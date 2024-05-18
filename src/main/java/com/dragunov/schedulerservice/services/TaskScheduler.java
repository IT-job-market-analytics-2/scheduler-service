package com.dragunov.schedulerservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TaskScheduler {

    private final AnalyticsBuilderServiceTask analyticsBuilderServiceTask;

    private final VacancyImportServiceTask vacancyImportServiceTask;

    private final ScheduledExecutorService scheduledExecutorService;

    @Value("${time-to-scheduled.init-delay}")
    private int initDelay;

    @Value("${time-to-scheduled.delay}")
    private int delay;

    @Value("${time-to-scheduled.delay-for-analytics}")
    private int analyticsDelay;

    @EventListener(ApplicationReadyEvent.class)
    public void startScheduler() {
        scheduledExecutorService.scheduleWithFixedDelay(vacancyImportServiceTask, initDelay, delay, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(analyticsBuilderServiceTask, initDelay + analyticsDelay, delay + analyticsDelay, TimeUnit.SECONDS);
    }
}


