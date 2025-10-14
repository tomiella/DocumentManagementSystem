package at.bif.swen.rest.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
