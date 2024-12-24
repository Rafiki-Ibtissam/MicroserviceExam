package com.example.notificationservice.services;


import com.example.notificationservice.models.Notification;
import com.example.notificationservice.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public void saveNotification(String orderId, String userEmail) {
        Notification notification = Notification.builder()
                .orderId(orderId)
                .userEmail(userEmail)
                .message("Your order with ID " + orderId + " has been successfully created.")
                .sent(false)
                .build();
        repository.save(notification);
    }
}

