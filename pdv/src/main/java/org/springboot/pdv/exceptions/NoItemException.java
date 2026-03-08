package org.springboot.pdv.exceptions;

public class NoItemException extends RuntimeException {

    public NoItemException(String message) {
        super(message);
    }
}
