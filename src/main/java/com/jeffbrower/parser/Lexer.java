package com.jeffbrower.parser;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public final class Lexer<T extends Enum<T>> {
    public static <T extends Enum<T>> Builder<T> builder(final Class<T> tokenClass) {
        return new Builder<>(tokenClass);
    }

    private final EnumMap<T, Lexer.TokenType> tokens;

    private Lexer(final EnumMap<T, Lexer.TokenType> tokens) {
        this.tokens = tokens;
    }

    public List<Token<T>> lex(final CharSequence input) {
        final ArrayList<Token<T>> list = new ArrayList<>();
        final int length = input.length();
        int i = 0;
        outer: while (i < length) {
            for (final Map.Entry<T, Lexer.TokenType> e : tokens.entrySet()) {
                final Lexer.TokenType t = e.getValue();
                final Optional<BaseToken> res = t.parser.match(input, i);
                if (res.isPresent()) {
                    final BaseToken base = res.get();
                    i = base.end;
                    if (!t.skip) {
                        list.add(new Token<>(e.getKey(), base.data));
                    }
                    continue outer;
                }
            }
            throw new IllegalArgumentException("Unexpected input at character " + i);
        }
        list.trimToSize();
        return list;
    }

    public static final class Builder<T extends Enum<T>> {
        private final EnumMap<T, Lexer.TokenType> tokens;

        private Builder(final Class<T> tokenClass) {
            tokens = new EnumMap<>(tokenClass);
        }

        public Lexer.Builder<T> literal(final T token, final String literal) {
            return put(token, new TokenType(false, TokenMatcher.literal(literal)));
        }

        public Lexer.Builder<T> literal(final T token, final String literal, final Supplier<?> s) {
            final TokenMatcher base = TokenMatcher.literal(literal);
            return put(token, new TokenType(false, (input, start) -> base.match(input, start).map(r -> new BaseToken(s.get(), r.end))));
        }

        public Lexer.Builder<T> skipLiteral(final T token, final String literal) {
            return put(token, new TokenType(true, TokenMatcher.literal(literal)));
        }

        public Lexer.Builder<T> chars(final T token, final char... chars) {
            return put(token, new TokenType(false, TokenMatcher.chars(chars)));
        }

        public Lexer.Builder<T> skipChars(final T token, final char... chars) {
            return put(token, new TokenType(true, TokenMatcher.chars(chars)));
        }

        public Lexer.Builder<T> regex(final T token, final Pattern regex) {
            return put(token, new TokenType(false, TokenMatcher.regex(regex)));
        }

        public Lexer.Builder<T> regex(final T token, final Pattern regex, final Function<MatchResult, ?> f) {
            final TokenMatcher base = TokenMatcher.regex(regex);
            return put(token, new TokenType(false, (input, start) -> base.match(input, start).map(r -> new BaseToken(f.apply((MatchResult) r.data), r.end))));
        }

        public Lexer.Builder<T> skipRegex(final T token, final Pattern regex) {
            return put(token, new TokenType(true, TokenMatcher.regex(regex)));
        }

        public Lexer.Builder<T> custom(final T token, final TokenMatcher parser) {
            return put(token, new TokenType(false, parser));
        }

        public Lexer.Builder<T> customSkip(final T token, final TokenMatcher parser) {
            return put(token, new TokenType(true, parser));
        }

        private Lexer.Builder<T> put(final T token, final Lexer.TokenType t) {
            if (tokens.putIfAbsent(token, t) != null) {
                throw new IllegalArgumentException("Duplicate definitions for " + token);
            }
            return this;
        }

        public Lexer<T> build() {
            return new Lexer<>(tokens);
        }
    }

    private static final class TokenType {
        public final boolean skip;
        public final TokenMatcher parser;

        public TokenType(final boolean skip, final TokenMatcher parser) {
            this.skip = skip;
            this.parser = parser;
        }
    }
}
