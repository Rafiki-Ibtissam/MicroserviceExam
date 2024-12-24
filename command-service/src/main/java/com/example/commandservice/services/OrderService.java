package com.example.commandservice.services;

import com.example.commandservice.models.Order;
import com.example.commandservice.proxy.ProductClient;
import com.example.commandservice.repositories.OrderRepository;
import com.example.commandservice.models.Product;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository repository;
    private final ProductClient productClient;

    public OrderService(OrderRepository repository, ProductClient productClient) {
        this.repository = repository;
        this.productClient = productClient;
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackGetProduct")
    public Product getProduct(Long productId) {
        logger.info("Fetching product details for ID: {}", productId);
        return productClient.getProductById(productId);
    }

    public Product fallbackGetProduct(Long productId, Throwable throwable) {
        logger.error("Error fetching product with ID: {}. Fallback triggered: {}", productId, throwable.getMessage());
        Product fallbackProduct = new Product();
        fallbackProduct.setId(productId);
        fallbackProduct.setName("Unknown Product");
        fallbackProduct.setDescription("Product service is currently unavailable");
        fallbackProduct.setPrice(0.0);
        fallbackProduct.setStock(0);
        return fallbackProduct;
    }

    public Order createOrder(String productId, String userEmail, int quantity) {
        // Validate input
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // Fetch product details
        Product product = getProduct(Long.parseLong(productId));

        // Ensure sufficient stock
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }

        // Create and save the order
        Order order = new Order();
        order.setProductId(Long.parseLong(productId));
        order.setUserEmail(userEmail);
        order.setQuantity(quantity);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(product.getPrice() * quantity);

        logger.info("Creating order for Product ID: {}, Quantity: {}, Total Price: {}", productId, quantity, order.getTotalPrice());

        return repository.save(order);
    }

    public Order getOrderById(Long id) {
        logger.info("Fetching order with ID: {}", id);
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
