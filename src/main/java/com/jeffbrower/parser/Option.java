package com.jeffbrower.parser;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

final class Option<T extends Enum<T>, R extends Enum<R>, F, X> {
    private final List<Part<T, R, F>> parts;
    private final Function<ParseResult, X> finish;

    Option(final List<Part<T, R, F>> parts, final Function<ParseResult, X> finish) {
        this.parts = parts;
        this.finish = finish;
    }

    Optional<PartialResult<X>> parse(final Parser<T, R, F> parser, final List<Token<T>> tokens, int i) {
        final Object[] results = new Object[parts.size()];
        for (int r = 0; r < results.length; r++) {
            final Optional<PartialResult<?>> opt = parts.get(r).parse(parser, tokens, i).map(Function.identity());
            if (opt.isEmpty()) {
                return Optional.empty();
            }
            final PartialResult<?> result = opt.get();
            i = result.end;
            results[r] = result.value;
        }
        return Optional.of(new PartialResult<>(finish.apply(new ParseResult(results)), i));
    }
}
