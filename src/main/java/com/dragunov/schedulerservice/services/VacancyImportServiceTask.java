package com.dragunov.schedulerservice.services;

import com.dragunov.schedulerservice.dto.VacancyImportScheduledTaskDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class VacancyImportServiceTask implements Runnable {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.routing-keys.vacancy-import}")
    private String routingKey;

    @Value("${rabbitmq.exchange.scheduled-tasks-exchange}")
    private String exchange;

    @Value("${queries}")
    private List<String> queries;

    @Override
    public void run() {
        List<VacancyImportScheduledTaskDto> messages = new ArrayList<>(createDtoPackage(queries));
        log.info("-> Start vacancies import");
        for (VacancyImportScheduledTaskDto importDto : messages) {
            try {
                rabbitTemplate.convertAndSend(exchange, routingKey, importDto);
                log.info("Sent message: {}", importDto);
            } catch (AmqpException e) {
                log.error("Failed to send message: " + importDto, e);
            }
        }
        log.info("Send {} messages to vacancy import", messages.size());
    }

    private List<VacancyImportScheduledTaskDto> createDtoPackage(List<String> queries) {
        List<VacancyImportScheduledTaskDto> messages = new ArrayList<>();
        for (String query : queries) {
            for (int i = 0; i < 20; i++) {
                VacancyImportScheduledTaskDto importDto = new VacancyImportScheduledTaskDto();
                importDto.setQuery(query);
                importDto.setPageIndex(i);
                importDto.setPageSize(100);
                messages.add(importDto);
            }
        }
        return messages;
    }
}
