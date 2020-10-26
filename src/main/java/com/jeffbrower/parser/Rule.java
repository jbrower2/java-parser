package com.jeffbrower.parser;

import java.util.List;
import java.util.Optional;

final class Rule<T extends Enum<T>, R extends Enum<R>, F, X> {
    final Class<X> resultClass;
    private final List<Option<T, R, F, X>> options;

    Rule(final Class<X> resultClass, final List<Option<T, R, F, X>> options) {
        this.resultClass = resultClass;
        this.options = options;
    }

    Optional<PartialResult<X>> parse(final Parser<T, R, F> parser, final List<Token<T>> tokens, final int i) {
        PartialResult<X> best = null;
        for (final Option<T, R, F, X> option : options) {
            final Optional<PartialResult<X>> opt = option.parse(parser, tokens, i);
            if (opt.isPresent()) {
                final PartialResult<X> result = opt.get();
                if (best == null || best.end > result.end) {
                    best = result;
                }
            }
        }
        return Optional.ofNullable(best);
    }
}
