package com.pato.rabbitmq.controller;

import com.pato.rabbitmq.models.User;
import com.pato.rabbitmq.service.RabbitMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class RabbitMQController {

    @Autowired
    RabbitMQSender rabbitMQSender;

    @PostMapping(value = "/producer")
    public String producer(@RequestBody User user) {
        rabbitMQSender.send(user);

        return "Message sent to the RabbitMQ  Successfully";
    }
}
