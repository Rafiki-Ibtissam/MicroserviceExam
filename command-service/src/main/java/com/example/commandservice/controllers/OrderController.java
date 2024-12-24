package com.example.commandservice.controllers;

import com.example.commandservice.models.Order;
import com.example.commandservice.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestParam String productId,
            @RequestParam String userEmail,
            @RequestParam int quantity) {
        try {
            logger.info("Request received to create order for Product ID: {}, Quantity: {}", productId, quantity);
            Order order = service.createOrder(productId, userEmail, quantity);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException ex) {
            logger.error("Error creating order: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception ex) {
            logger.error("Unexpected error: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        try {
            logger.info("Request received to fetch order with ID: {}", id);
            Order order = service.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (RuntimeException ex) {
            logger.error("Error fetching order: {}", ex.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            logger.error("Unexpected error: {}", ex.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
