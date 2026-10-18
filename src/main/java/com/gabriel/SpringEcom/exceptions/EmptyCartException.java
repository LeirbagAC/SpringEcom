package com.gabriel.SpringEcom.exceptions;

public class EmptyCartException extends DomainException {
    public EmptyCartException() {
        super("Não é possível finalizar a compra com o carrinho vazio.");
    }
}