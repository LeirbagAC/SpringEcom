package com.gabriel.SpringEcom.exceptions;

public class NotFoundException extends DomainException {
    public NotFoundException(String resource, Object key) {
        super("%s '%s' não foi encontrado.".formatted(resource, key));
    }
}