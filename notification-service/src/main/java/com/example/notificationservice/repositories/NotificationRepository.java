package com.example.notificationservice.repositories;


import com.example.notificationservice.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
