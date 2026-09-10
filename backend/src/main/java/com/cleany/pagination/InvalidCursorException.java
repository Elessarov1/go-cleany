package com.cleany.pagination;

public class InvalidCursorException extends RuntimeException {
    public InvalidCursorException() {
        super("Cursor is invalid");
    }
}
