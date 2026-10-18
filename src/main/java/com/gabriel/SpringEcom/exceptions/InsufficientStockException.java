package com.gabriel.SpringEcom.exceptions;

public class InsufficientStockException extends DomainException {
    private final String productName;
    private final int currentStock;
    private final int requested;

    public InsufficientStockException(String productName, int currentStock, int requested) {
        super("Estoque insuficiente para o produto '%s'. Estoque atual: %d, quantidade solicitada: %d."
                .formatted(productName, currentStock, requested));
        this.productName = productName;
        this.currentStock = currentStock;
        this.requested = requested;
    }

    public String getProductName() { return productName; }
    public int getCurrentStock() { return currentStock; }
    public int getRequested() { return requested; }
}