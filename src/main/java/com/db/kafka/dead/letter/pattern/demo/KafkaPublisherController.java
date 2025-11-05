package com.db.kafka.dead.letter.pattern.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class KafkaPublisherController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/create")
    public String createOrder(@RequestBody String orderMessage) {
        kafkaTemplate.send("createOrder", orderMessage);
        return "Order created and published to createOrder topic: " + orderMessage;
    }
}
