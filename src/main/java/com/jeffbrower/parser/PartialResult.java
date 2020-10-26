package com.jeffbrower.parser;

final class PartialResult<F> {
    public final F value;
    public final int end;

    public PartialResult(final F value, final int end) {
        this.value = value;
        this.end = end;
    }
}
