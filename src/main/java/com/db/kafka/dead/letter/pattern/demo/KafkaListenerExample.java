package com.db.kafka.dead.letter.pattern.demo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class KafkaListenerExample {

    private static final Logger log = LoggerFactory.getLogger(KafkaListenerExample.class);
    private static final int MAX_RETRY_COUNT = 5;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Counter createOrderCounter;
    private final Counter retryCounter;
    private final Counter deadLetterCounter;
    private final Counter successCounter;

    public KafkaListenerExample(KafkaTemplate<String, String> kafkaTemplate, MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.createOrderCounter = Counter.builder("kafka.messages.createOrder")
            .description("Total messages received in createOrder topic")
            .register(meterRegistry);
        this.retryCounter = Counter.builder("kafka.messages.retry")
            .description("Total messages sent to retry topic")
            .register(meterRegistry);
        this.deadLetterCounter = Counter.builder("kafka.messages.deadletter")
            .description("Total messages sent to dead letter queue")
            .register(meterRegistry);
        this.successCounter = Counter.builder("kafka.messages.success")
            .description("Total messages processed successfully")
            .register(meterRegistry);
    }

    @KafkaListener(topics = "createOrder", groupId = "test_group_id")
    public void listenCreateOrder(String message) {
        createOrderCounter.increment();
        try {
            sendApiRequest(message);
            successCounter.increment();
        } catch (ApiCallException e) {
            int retryCount = 0;
            sendRetryTopic(message, retryCount);
        }
    }

    @KafkaListener(topics = "createOrderRetry", groupId = "test_group_id")
    public void listenCreateOrderRetry(String message, @Header(name = "retryCount", required = false) Integer retryCount) {
        if (retryCount == null) retryCount = 0;
        try {
            sendApiRequest(message);
            successCounter.increment();
        } catch (ApiCallException e) {
            retryCount++;
            if (retryCount >= MAX_RETRY_COUNT) {
                sendDlTopic(message);
                return;
            }
            sendRetryTopic(message, retryCount);
        }
    }

    @KafkaListener(topics = "createOrderDeadLetter", groupId = "test_group_id")
    public void listenCreateOrderDL(String message) {
        log.warn("New event received in DLQ: {}", message);
        insertDb(message);
    }

    private void sendApiRequest(String message) throws ApiCallException {
        // Simulate API call, throw exception for demo
        if (message.contains("fail")) {
            throw new ApiCallException("API call failed");
        }
        log.info("API call succeeded for message: {}", message);
    }

    private void sendRetryTopic(String message, int retryCount) {
        ProducerRecord<String, String> record = new ProducerRecord<>(
            "createOrderRetry", null, message
        );
        record.headers().add(new RecordHeader("retryCount", Integer.toString(retryCount).getBytes()));
        kafkaTemplate.send(record);
        retryCounter.increment();
        log.info("Sent to retry topic: {} with retryCount: {}", message, retryCount);
    }

    private void sendDlTopic(String message) {
        kafkaTemplate.send("createOrderDeadLetter", message);
        deadLetterCounter.increment();
        log.info("Sent to DLQ: {}", message);
    }

    private void insertDb(String message) {
        // Simulate DB insert
        log.info("Inserted into DB: {}", message);
    }

    // Custom exception for API call failures
    private static class ApiCallException extends Exception {
        public ApiCallException(String msg) {
            super(msg);
        }
    }
}
