package com.samuilolegovich.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.amqp.core.Queue;



@Configuration
@RequiredArgsConstructor
public class AmqpConfiguration {
    public static final String EMAIL_ROUTING_KEY = "email-publish";
    public static final String EMAIL_EXCHANGE = "email";
    public static final String EMAIL_QUEUE = "com.email.received";




    @Bean
    public Queue queue() {
        return new Queue(EMAIL_QUEUE);
    }



    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EMAIL_EXCHANGE, true, false);
    }



    @Bean
    @DependsOn({"topicExchange", "queue"})
    public Binding binding(Queue queue, TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(EMAIL_ROUTING_KEY);
    }



    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

}
