package com.github.redreaperlp.cdpusher.util;

public class Value<T,Z> {
    private T value;
    private boolean isErrored = false;
    private Z error;

    public Value(T value) {
        this.value = value;
    }

    public Value(T value, Z error) {
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

    public T getValue() {
        return value;
    }

    public Z getError() {
        return error;
    }
}
