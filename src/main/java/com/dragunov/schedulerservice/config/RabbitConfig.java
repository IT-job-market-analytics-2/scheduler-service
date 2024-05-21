package com.dragunov.schedulerservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
@Slf4j
public class RabbitConfig {

    private final Environment env;

    @Value("${rabbitmq.exchange.scheduled-tasks-exchange}")
    private String exchange;

    @Value("${rabbitmq.queue-names.analytics-builder}")
    private String analyticsBuilder;

    @Value("${rabbitmq.queue-names.vacancy-import}")
    private String vacancyImport;

    @Autowired
    public RabbitConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue analyticsBuilderQueue() {
        return new Queue(analyticsBuilder);
    }

    @Bean
    public Queue vacancyImportQueue() {
        return new Queue(vacancyImport);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(env.getProperty("spring.rabbitmq.host"));
        connectionFactory.setUsername(Objects.requireNonNull(env.getProperty("spring.rabbitmq.username")));
        connectionFactory.setPassword(Objects.requireNonNull(env.getProperty("spring.rabbitmq.password")));
        connectionFactory.setPort(env.getProperty("spring.rabbitmq.port", Integer.class));
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
