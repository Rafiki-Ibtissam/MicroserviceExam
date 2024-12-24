package com.example.notificationservice.consumers;


import com.example.notificationservice.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class NotificationConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationService notificationService;

    public NotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "orders", groupId = "notifications-group")
    public void consumeOrderEvent(String message) {
        LOGGER.info("Consumed message: {}", message);

        // Assuming the message is in JSON format { "orderId": "123", "userEmail": "test@example.com" }
        String[] parts = message.replace("{", "").replace("}", "").split(",");
        String orderId = parts[0].split(":")[1].replace("\"", "").trim();
        String userEmail = parts[1].split(":")[1].replace("\"", "").trim();

        notificationService.saveNotification(orderId, userEmail);
        LOGGER.info("Notification saved for order ID: {}", orderId);
    }
}



