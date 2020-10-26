package com.jeffbrower.parser;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

public class Parser<T extends Enum<T>, R extends Enum<R>, F> {
    private final Lexer<T> lexer;
    final EnumMap<R, Rule<T, R, F, ?>> rules;
    private final Rule<T, R, F, F> root;

    public static <T extends Enum<T>, R extends Enum<R>, F> Builder<T, R, F> builder(final Lexer<T> lexer, final Class<R> ruleClass,
                final Class<F> resultClass) {
        return new Builder<>(lexer, ruleClass, resultClass);
    }

    private Parser(final Lexer<T> lexer, final EnumMap<R, Rule<T, R, F, ?>> rules, final Rule<T, R, F, F> root) {
        this.lexer = lexer;
        this.rules = rules;
        this.root = root;
    }

    public F parse(final CharSequence input) {
        final List<Token<T>> tokens = lexer.lex(input);
        final Optional<PartialResult<F>> opt = root.parse(this, tokens, 0);
        final PartialResult<F> result = opt.orElseThrow(() -> new IllegalArgumentException("Failed to parse"));
        if (result.end < tokens.size()) {
            throw new IllegalArgumentException("Did not consume all input");
        }
        return result.value;
    }

    public static final class Builder<T extends Enum<T>, R extends Enum<R>, F> {
        private final Lexer<T> lexer;
        final EnumMap<R, Rule<T, R, F, ?>> rules;
        private final Class<F> resultClass;
        Rule<T, R, F, F> root;

        private Builder(final Lexer<T> lexer, final Class<R> ruleClass, final Class<F> resultClass) {
            this.lexer = lexer;
            rules = new EnumMap<>(ruleClass);
            this.resultClass = resultClass;
        }

        public RuleBuilder<T, R, F, F> root(final R rule) {
            if (root != null) {
                throw new IllegalArgumentException("Root rule already specified");
            }
            return new RuleBuilder<>(true, this, rule, resultClass);
        }

        public <X> RuleBuilder<T, R, F, X> rule(final R rule, final Class<X> resultClass) {
            return new RuleBuilder<>(false, this, rule, resultClass);
        }

        public Parser<T, R, F> build() {
            if (root == null) {
                throw new IllegalArgumentException("Root rule not specified");
            }
            return new Parser<>(lexer, rules, root);
        }
    }
}
