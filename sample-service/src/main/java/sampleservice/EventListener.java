package sampleservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class EventListener {

    @Value("${kafka.topic.out}")
    private String outTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    HelloService service;

    @KafkaListener(topics = "${kafka.topic.in}")
    @SendTo
    public String receiveMessageFromOut(String message) {
        return service.hello(message);
    }
}
