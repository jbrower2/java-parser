package com.jeffbrower.parser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

final class RepPart<T extends Enum<T>, R extends Enum<R>, F> extends Part<T, R, F> {
    @SuppressWarnings("unchecked")
    private static <X> X[] emptyArray(final Class<X> clazz) {
        return (X[]) Array.newInstance(clazz, 0);
    }

    private final R rule;
    private final Optional<T> sep;
    private final int min;
    private final OptionalInt max;

    RepPart(final R rule, final Optional<T> sep, final int min, final OptionalInt max) {
        this.rule = rule;
        this.sep = sep;
        this.min = min;
        this.max = max;
    }

    @Override
    Optional<PartialResult<?>> parse(final Parser<T, R, F> parser, final List<Token<T>> tokens, int i) {
        final List<Object> results = new ArrayList<>();
        final Rule<T, R, F, ?> rule = parser.rules.get(this.rule);
        Optional<PartialResult<?>> opt;
        int j = i;
        while ((opt = rule.parse(parser, tokens, j).map(Function.identity())).isPresent()) {
            final PartialResult<?> result = opt.get();
            results.add(result.value);
            j = i = result.end;
            if (sep.isPresent()) {
                if (j >= tokens.size() || sep.get() != tokens.get(j).key) {
                    break;
                }
                j++;
            }
        }
        if (results.size() < min || max.isPresent() && results.size() > max.getAsInt()) {
            return Optional.empty();
        }
        return Optional.of(new PartialResult<>(results.toArray(emptyArray(rule.resultClass)), i));
    }
}
