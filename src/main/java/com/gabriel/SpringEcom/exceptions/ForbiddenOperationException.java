package com.gabriel.SpringEcom.exceptions;

public class ForbiddenOperationException extends DomainException {
    public ForbiddenOperationException(String message) {
        super(message);
    }

    public static ForbiddenOperationException notOwner(String resource) {
        return new ForbiddenOperationException(
                "Você não tem permissão para modificar este(a) %s.".formatted(resource));
    }
}