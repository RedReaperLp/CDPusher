package com.github.redreaperlp.cdpusher.util;

public class Value<T, Z> {
    private final T value;
    private boolean isErrored = false;
    private final int status;
    private Z error;

    public Value(T value) {
        this(200, value);
    }

    public Value(int status, T value) {
        this.status = status;
        this.value = value;
    }

    public Value(int status, T value, Z error) {
        this.status = status;
        this.value = value;
        this.error = error;
        isErrored = true;
    }

    public boolean isErrored() {
        if (error == null) {
            return false;
        }
        return isErrored;
    }

    public int getStatus() {
        return status;
    }

    public T getValue() {
        return value;
    }

    public Z getError() {
        return error;
    }
}
