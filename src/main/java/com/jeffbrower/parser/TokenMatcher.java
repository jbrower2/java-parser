package com.jeffbrower.parser;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface TokenMatcher {
    static TokenMatcher literal(final String literal) {
        final int length = literal.length();
        if (length == 0) {
            throw new IllegalArgumentException("Cannot use an empty string as a token");
        }
        return (input, start) -> {
            final int end = start + length;
            if (end > input.length()) {
                return Optional.empty();
            }
            for (int i = 0, j = start; i < length; i++, j++) {
                if (literal.charAt(i) != input.charAt(j)) {
                    return Optional.empty();
                }
            }
            return Optional.of(new BaseToken(null, end));
        };
    }

    static TokenMatcher chars(final char[] chars) {
        if (chars.length == 0) {
            throw new IllegalArgumentException("Must specify at least 1 character");
        }
        return (input, start) -> {
            final StringBuilder b = new StringBuilder();
            final int length = input.length();
            int i = start;
            while (i < length) {
                final char c = input.charAt(i);
                boolean found = false;
                for (final char ch : chars) {
                    if (c == ch) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    break;
                }
                i++;
                b.append(c);
            }
            return i > start ? Optional.of(new BaseToken(b.toString(), i)) : Optional.empty();
        };
    }

    static TokenMatcher regex(final Pattern regex) {
        return (input, start) -> {
            final Matcher m = regex.matcher(input);
            m.region(start, input.length());
            return m.lookingAt() && m.end() > start ? Optional.of(new BaseToken(m.toMatchResult(), m.end())) : Optional.empty();
        };
    }

    Optional<BaseToken> match(CharSequence input, int start);
}
