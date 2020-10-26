package com.jeffbrower.parser;

import java.util.List;
import java.util.Optional;

final class TokenPart<T extends Enum<T>, R extends Enum<R>, F> extends Part<T, R, F> {
    private final T token;

    TokenPart(final T token) {
        this.token = token;
    }

    @Override
    Optional<PartialResult<?>> parse(final Parser<T, R, F> parser, final List<Token<T>> tokens, final int i) {
        if (i >= tokens.size()) {
            return Optional.empty();
        }
        final Token<T> t = tokens.get(i);
        return token == t.key ? Optional.of(new PartialResult<>(t.data, i + 1)) : Optional.empty();
    }
}
