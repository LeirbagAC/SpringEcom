package com.gabriel.SpringEcom.exceptions;

public class InactiveProductException extends DomainException {
    public InactiveProductException(Long productId) {
        super("O produto %d não está ativo no catálogo.".formatted(productId));
    }
}