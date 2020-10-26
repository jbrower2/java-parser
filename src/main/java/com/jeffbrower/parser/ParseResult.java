package com.jeffbrower.parser;

public final class ParseResult {
    private final Object[] results;

    public ParseResult(final Object[] results) {
        this.results = results;
    }

    public <T> T get(final int i, final Class<T> clazz) {
        final Object value = results[i];
        if (value == null) {
            throw new NullPointerException();
        }
        return clazz.cast(value);
    }
}
