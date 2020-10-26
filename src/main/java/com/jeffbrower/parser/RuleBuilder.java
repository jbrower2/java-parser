package com.jeffbrower.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

public final class RuleBuilder<T extends Enum<T>, R extends Enum<R>, F, X> {
    private final boolean root;
    private final Parser.Builder<T, R, F> parser;
    private final R rule;
    private final Class<X> resultClass;
    private final List<Option<T, R, F, X>> options = new ArrayList<>();
    private List<Part<T, R, F>> parts = new ArrayList<>();

    RuleBuilder(final boolean root, final Parser.Builder<T, R, F> parser, final R rule, final Class<X> resultClass) {
        this.root = root;
        this.parser = parser;
        this.rule = rule;
        this.resultClass = resultClass;
    }

    public RuleBuilder<T, R, F, X> consume(final T token) {
        parts.add(new TokenPart<>(token));
        return this;
    }

    public RuleBuilder<T, R, F, X> subRule(final R subRule) {
        parts.add(new SubRulePart<>(subRule));
        return this;
    }

    public RuleBuilder<T, R, F, X> star(final R rule) {
        return rep(rule, Optional.empty(), 0, OptionalInt.empty());
    }

    public RuleBuilder<T, R, F, X> star(final R rule, final T sep) {
        return rep(rule, Optional.of(sep), 0, OptionalInt.empty());
    }

    public RuleBuilder<T, R, F, X> plus(final R rule) {
        return rep(rule, Optional.empty(), 1, OptionalInt.empty());
    }

    public RuleBuilder<T, R, F, X> plus(final R rule, final T sep) {
        return rep(rule, Optional.of(sep), 1, OptionalInt.empty());
    }

    public RuleBuilder<T, R, F, X> rep(final R rule, final int min) {
        return rep(rule, Optional.empty(), min, OptionalInt.empty());
    }

    public RuleBuilder<T, R, F, X> rep(final R rule, final int min, final int max) {
        return rep(rule, Optional.empty(), min, OptionalInt.of(max));
    }

    public RuleBuilder<T, R, F, X> rep(final R rule, final T sep, final int min) {
        return rep(rule, Optional.of(sep), min, OptionalInt.empty());
    }

    public RuleBuilder<T, R, F, X> rep(final R rule, final T sep, final int min, final int max) {
        return rep(rule, Optional.of(sep), min, OptionalInt.of(max));
    }

    private RuleBuilder<T, R, F, X> rep(final R rule, final Optional<T> sep, final int min, final OptionalInt max) {
        parts.add(new RepPart<>(rule, sep, min, max));
        return this;
    }

    public RuleBuilder<T, R, F, X> or(final Function<ParseResult, X> finish) {
        if (parts.isEmpty()) {
            throw new IllegalArgumentException("No parts specified");
        }
        options.add(new Option<>(parts, finish));
        parts = new ArrayList<>();
        return this;
    }

    @SuppressWarnings("unchecked")
    public Parser.Builder<T, R, F> up(final Function<ParseResult, X> finish) {
        if (parts.isEmpty()) {
            throw new IllegalArgumentException("No parts specified");
        }
        options.add(new Option<>(parts, finish));
        final Rule<T, R, F, X> rule = new Rule<>(resultClass, options);
        if (parser.rules.putIfAbsent(this.rule, rule) != null) {
            throw new IllegalArgumentException("Duplicate definitions for " + rule);
        }
        if (root) {
            parser.root = (Rule<T, R, F, F>) rule;
        }
        return parser;
    }
}
