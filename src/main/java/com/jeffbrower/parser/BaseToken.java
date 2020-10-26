package com.jeffbrower.parser;

public final class BaseToken {
    public final Object data;
    public final int end;

    public BaseToken(final Object data, final int end) {
        this.data = data;
        this.end = end;
    }
}
