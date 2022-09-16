package com.msquare.flabook.common.configurations;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	@Value("${spring.rabbitmq.host}") String server;
	@Value("${spring.rabbitmq.port}") int port;
	@Value("${spring.rabbitmq.username}") String username;
	@Value("${spring.rabbitmq.password}") String password;

    @Bean
	ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(server, port);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		return connectionFactory;

	}

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setBatchListener(true); // configures a BatchMessageListenerAdapter
        factory.setBatchSize(500);
        factory.setReceiveTimeout(5000L);
        factory.setStartConsumerMinInterval(5000L);
        factory.setConsumerBatchEnabled(true);
        factory.setMaxConcurrentConsumers(1);
        factory.setConcurrentConsumers(1);
        factory.setRecoveryInterval(5000L);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory2() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setReceiveTimeout(5000L);
        factory.setStartConsumerMinInterval(5000L);
        factory.setMaxConcurrentConsumers(1);
        factory.setConcurrentConsumers(1);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    @Bean()
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                  MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue myQueue() {
        return new Queue("postingReadQueue");
    }

    @Bean
    public Queue loginQueue() {
        return new Queue("loginQueue");
    }

    @Bean
    public Queue myQueue2() {
        return new Queue("updateReadQueue");
    }

    @Bean
    public Queue myQueue3() {
        return new Queue("postingReceivingQueue");
    }

    @Bean
    public Queue myQueue4() {
        return new Queue("updateReceivingQueue");
    }

    @Bean
    public Queue myQueue5() {
        return new Queue("searchQueryQueue");
    }

    @Bean
    public Queue myQueue6() {
        return new Queue("updateSearchQueryQueue");
    }

    @Bean
    public Queue myQueue7() {
        return new Queue("deleteS3Queue",true, false, false);
    }

}

