package com.pato.rabbitmq.config;


import com.pato.rabbitmq.service.RabbitMQConsumer;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.username}")
    private String amqpUsername;
    @Value("${spring.rabbitmq.password}")
    private String amqpPwd;
    @Value("${spring.rabbitmq.host}")
    private String amqpHost;
    @Value("${spring.rabbitmq.virtual-host}")
    private String amqVhost;
    @Value("${spring.rabbitmq.port}")
    private int amqpPort;

    @Value("${javainuse.rabbitmq.queue}")
    String queueName;

    @Value("${javainuse.rabbitmq.exchange}")
    String exchangeName;

    @Value("${javainuse.rabbitmq.routingkey}")
    private String routingkey;


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(amqpHost);
        connectionFactory.setUsername(amqpUsername);
        connectionFactory.setPassword(amqpPwd);
        connectionFactory.setVirtualHost(amqVhost);
        connectionFactory.setHost(amqpHost);
        connectionFactory.setPort(amqpPort);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin admin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());

        rabbitAdmin.afterPropertiesSet();
        return rabbitAdmin;
    }

    @Bean
    Queue queue() {
        Queue queue = new Queue(queueName);
        queue.setAdminsThatShouldDeclare(admin());
        return queue;

    }

    @Bean
    DirectExchange exchange() {
        DirectExchange exchange = new DirectExchange(exchangeName);
        exchange.setAdminsThatShouldDeclare(admin());
        return exchange;
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingkey, null);
        return binding;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    public @Bean(name = "rabbitTemplate")
    RabbitTemplate queueRabbitTemplate() {
        return getRabbitTemplate(queueName, routingkey);
    }

    // reusable stuff nakuambia
    private RabbitTemplate getRabbitTemplate(String queue, String routingKeyA) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }


    //create MessageListenerContainer using custom connection factory
    @Bean
    MessageListenerContainer messageListenerContainer() {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(connectionFactory());
        simpleMessageListenerContainer.setQueues(queue());
        simpleMessageListenerContainer.setMessageListener(new RabbitMQConsumer());
        return simpleMessageListenerContainer;
    }

}