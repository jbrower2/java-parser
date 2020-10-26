package com.jeffbrower.parser;

public final class Token<T extends Enum<T>> {
    public final T key;
    public final Object data;

    public Token(final T key, final Object data) {
        this.key = key;
        this.data = data;
    }

    public <X> X get(final Class<? extends X> clazz) {
        if (data == null) {
            throw new NullPointerException("Looking for " + clazz.getName() + " but was null, with key " + key);
        }
        return clazz.cast(data);
    }

    public Token<T> withData(final Object data) {
        return new Token<>(key, data);
    }

    @Override
    public String toString() {
        return data == null ? key.toString() : key + "(" + data + ")";
    }
}
