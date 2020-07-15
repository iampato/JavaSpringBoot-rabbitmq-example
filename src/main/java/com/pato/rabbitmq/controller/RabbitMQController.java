package com.pato.rabbitmq.controller;

import com.pato.rabbitmq.models.User;
import com.pato.rabbitmq.service.RabbitMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class RabbitMQController {

    @Autowired
    RabbitMQSender rabbitMQSender;

    @GetMapping(value = "/producer")
    public String producer(@RequestParam("userName") String userName, @RequestParam("userId") String userId) {

        User user = new User();
        user.setuserId(userId);
        user.setuserName(userName);
        rabbitMQSender.send(user);

        return "Message sent to the RabbitMQ  Successfully";
    }
}
