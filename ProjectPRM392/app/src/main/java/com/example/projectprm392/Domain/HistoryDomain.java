package com.example.projectprm392.Domain;

import java.io.Serializable;

public class HistoryDomain implements Serializable {
    private String id;
    private double pricing;
    private String productId;
    private String username;

    // Constructor
    public HistoryDomain(String id, double pricing, String productId, String username) {
        this.id = id;
        this.pricing = pricing;
        this.productId = productId;
        this.username = username;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPricing() {
        return pricing;
    }

    public void setPricing(double pricing) {
        this.pricing = pricing;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}