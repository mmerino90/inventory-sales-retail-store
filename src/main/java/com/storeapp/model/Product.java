package com.storeapp.model;

import java.time.LocalDate;

public class Product {
    private int id;
    private String name;
    private String description;
    private double costPrice;
    private double sellingPrice;
    private int quantity;
    private String category;
    private String supplier;
    private LocalDate expiryDate;

    public Product() {}

    public Product(int id, String name, String description, double costPrice, double sellingPrice, 
                   int quantity, String category, String supplier, LocalDate expiryDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.category = category;
        this.supplier = supplier;
        this.expiryDate = expiryDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public double getPrice() {
        return sellingPrice;
    }

    public void setPrice(double price) {
        this.sellingPrice = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return name + " ($" + sellingPrice + ")";
    }
}
