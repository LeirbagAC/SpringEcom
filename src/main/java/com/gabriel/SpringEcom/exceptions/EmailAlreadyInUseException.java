package com.gabriel.SpringEcom.exceptions;

public class EmailAlreadyInUseException extends ConflictException {
    public EmailAlreadyInUseException() {
        super("Este e-mail já está em uso por outra conta.");
    }
}
