package com.project.ms_payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitConfig {

    public static final String PAYMENTS_EXCHANGE = "payments.events.x";
    public static final String PAYMENTS_QUEUE = "payments.events.q";
    public static final String PAYMENTS_DLX = "payments.events.dlx";
    public static final String PAYMENTS_DLQ = "payments.events.dlq";

    @Bean
    public TopicExchange paymentsExchange() {
        return ExchangeBuilder.topicExchange(PAYMENTS_EXCHANGE).durable(true).build();
    }

    @Bean
    public TopicExchange paymentsDlx() {
        return ExchangeBuilder.topicExchange(PAYMENTS_DLX).durable(true).build();
    }

    @Bean
    public Queue paymentsQueue() {
        return QueueBuilder.durable(PAYMENTS_QUEUE)
                .withArguments(Map.of(
                        "x-dead-letter-exchange", PAYMENTS_DLX,
                        "x-dead-letter-routing-key", "dlq.payments"
                ))
                .build();
    }

    @Bean
    public Queue paymentsDlq() {
        return QueueBuilder.durable(PAYMENTS_DLQ).build();
    }

    @Bean
    public Binding bindPaymentsQueue(TopicExchange paymentsExchange, Queue paymentsQueue) {
        return BindingBuilder.bind(paymentsQueue).to(paymentsExchange).with("payment.*");
    }

    @Bean
    public Binding bindPaymentsDlq(TopicExchange paymentsDlx, Queue paymentsDlq) {
        return BindingBuilder.bind(paymentsDlq).to(paymentsDlx).with("dlq.payments");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(converter);
        return template;
    }
}