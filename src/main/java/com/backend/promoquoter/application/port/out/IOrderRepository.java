package com.backend.promoquoter.application.port.out;

public interface IOrderRepository {
    void saveOrder(String orderId, String orderData);
    String getOrder(String orderId);
}
