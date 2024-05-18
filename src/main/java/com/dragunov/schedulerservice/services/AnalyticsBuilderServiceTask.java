package com.dragunov.schedulerservice.services;

import com.dragunov.schedulerservice.dto.AnalyticsBuilderServiceTaskDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnalyticsBuilderServiceTask implements Runnable {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.routing-keys.analytics-builder}")
    private String routingKey;

    @Value("${rabbitmq.exchange.scheduled-tasks-exchange}")
    private String exchange;

    @Override
    public void run() {
        rabbitTemplate.convertAndSend(exchange, routingKey, new AnalyticsBuilderServiceTaskDto());
        log.info("-> start build analytics");
    }
}
