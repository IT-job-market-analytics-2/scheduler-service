package com.dragunov.schedulerservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
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
public class RabbitConfig {

    private final Environment env;

    @Value("${rabbitmq.exchange.scheduled-tasks-exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing-keys.analytics-builder-service-task}")
    private String analyticsBuilder;

    @Value("${rabbitmq.routing-keys.vacancy-import-service-task}")
    private String vacancyImport;

    @Autowired
    public RabbitConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(exchangeName);
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
    public Binding analyticsBinding(DirectExchange directExchange, Queue analyticsBuilderQueue) {
        return BindingBuilder.bind(analyticsBuilderQueue)
                .to(directExchange)
                .with(env.getProperty("rabbitmq.routing-keys.analytics-builder-service-task"));
    }

    @Bean
    public Binding vacancyImportBinding(DirectExchange directExchange, Queue vacancyImportQueue) {
        return BindingBuilder.bind(vacancyImportQueue)
                .to(directExchange)
                .with(env.getProperty("rabbitmq.routing-keys.vacancy-import-service-task"));
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
