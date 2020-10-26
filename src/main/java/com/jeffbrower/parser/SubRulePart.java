package com.jeffbrower.parser;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

final class SubRulePart<T extends Enum<T>, R extends Enum<R>, F> extends Part<T, R, F> {
    private final R rule;

    SubRulePart(final R rule) {
        this.rule = rule;
    }

    @Override
    Optional<PartialResult<?>> parse(final Parser<T, R, F> parser, final List<Token<T>> tokens, final int i) {
        return parser.rules.get(rule).parse(parser, tokens, i).map(Function.identity());
    }
}
