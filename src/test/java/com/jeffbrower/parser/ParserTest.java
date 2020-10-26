package com.jeffbrower.parser;

import com.jeffbrower.parser.json.JsonArray;
import com.jeffbrower.parser.json.JsonBoolean;
import com.jeffbrower.parser.json.JsonNull;
import com.jeffbrower.parser.json.JsonNumber;
import com.jeffbrower.parser.json.JsonObject;
import com.jeffbrower.parser.json.JsonObjectEntry;
import com.jeffbrower.parser.json.JsonString;
import com.jeffbrower.parser.json.JsonValue;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Pattern;

public final class ParserTest {
    public enum JsonToken {
        LCURLY, RCURLY, LSQUARE, RSQUARE, COMMA, COLON, NULL, FALSE, TRUE, NUMBER, STRING, WHITESPACE
    }

    public enum JsonRule {
        OBJECT, OBJECT_ENTRY, ARRAY, VALUE
    }

    private static Optional<BaseToken> parseString(final CharSequence input, final int start) {
        final int length = input.length();
        if (start >= length || input.charAt(start) != '"') {
            return Optional.empty();
        }
        final StringBuilder b = new StringBuilder();
        int i = start;
        while (++i != length) {
            final char c = input.charAt(i);
            if (c == '"') {
                return Optional.of(new BaseToken(b.toString(), i + 1));
            }
            if (c < ' ') {
                throw new IllegalArgumentException("String contained control character: " + c);
            }
            if (c == '\\') {
                if (++i == length) {
                    throw new IllegalArgumentException("Unclosed string literal");
                }
                final char c1 = input.charAt(i);
                switch (c1) {
                    case '"':
                    case '\\':
                    case '/':
                        b.append(c1);
                        break;
                    case 'b':
                        b.append('\b');
                        break;
                    case 'f':
                        b.append('\f');
                        break;
                    case 'n':
                        b.append('\n');
                        break;
                    case 'r':
                        b.append('\r');
                        break;
                    case 't':
                        b.append('\t');
                        break;
                    case 'u':
                        if (i + 4 >= length) {
                            throw new IllegalArgumentException("Unclosed string literal");
                        }
                        final int u1 = Character.digit(input.charAt(++i), 16);
                        final int u2 = Character.digit(input.charAt(++i), 16);
                        final int u3 = Character.digit(input.charAt(++i), 16);
                        final int u4 = Character.digit(input.charAt(++i), 16);
                        if ((u1 | u2 | u3 | u4) == -1) {
                            throw new IllegalArgumentException("Invalid unicode escape: " + input.subSequence(i - 6, i));
                        }
                        b.append((char) (u1 << 24 | u2 << 16 | u3 << 8 | u4));
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid string escape: " + c1);
                }
                continue;
            }
            b.append(c);
        }
        throw new IllegalArgumentException("Unclosed string literal");
    }

    public static void main(final String[] args) {
        final String input = "{\t\r\n \"test\\\"\":[\"hello\",123.456e789,\"world!\"]}";
        // CHECKSTYLE:OFF
        final Lexer<JsonToken> l = Lexer.builder(JsonToken.class)
            .literal(JsonToken.LCURLY, "{")
            .literal(JsonToken.RCURLY, "}")
            .literal(JsonToken.LSQUARE, "[")
            .literal(JsonToken.RSQUARE, "]")
            .literal(JsonToken.COMMA, ",")
            .literal(JsonToken.COLON, ":")
            .literal(JsonToken.NULL, "null")
            .literal(JsonToken.FALSE, "false")
            .literal(JsonToken.TRUE, "true")
            .regex(JsonToken.NUMBER, Pattern.compile("-?(0|[1-9]\\d*)(\\.\\d+)?([eE][+-]?\\d+)?"), mr -> new BigDecimal(mr.group()))
            .custom(JsonToken.STRING, ParserTest::parseString)
            .skipChars(JsonToken.WHITESPACE, ' ', '\n', '\r', '\t')
            .build();
        // CHECKSTYLE:ON
        l.lex(input).forEach(System.out::println);

        // CHECKSTYLE:OFF
        final Parser<JsonToken, JsonRule, JsonObject> p = Parser.builder(l, JsonRule.class, JsonObject.class)
            .root(JsonRule.OBJECT)
                .consume(JsonToken.LCURLY)
                .star(JsonRule.OBJECT_ENTRY, JsonToken.COMMA)
                .consume(JsonToken.RCURLY)
                .up(r -> new JsonObject(r.get(1, JsonObjectEntry[].class)))
            .rule(JsonRule.OBJECT_ENTRY, JsonObjectEntry.class)
                .consume(JsonToken.STRING)
                .consume(JsonToken.COLON)
                .subRule(JsonRule.VALUE)
                .up(r -> new JsonObjectEntry(r.get(0, String.class), r.get(2, JsonValue.class)))
            .rule(JsonRule.ARRAY, JsonArray.class)
                .consume(JsonToken.LSQUARE)
                .star(JsonRule.VALUE, JsonToken.COMMA)
                .consume(JsonToken.RSQUARE)
                .up(r -> new JsonArray(r.get(1, JsonValue[].class)))
            .rule(JsonRule.VALUE, JsonValue.class)
                .consume(JsonToken.NULL)
                .or(r -> JsonNull.INSTANCE)
                .consume(JsonToken.FALSE)
                .or(r -> JsonBoolean.FALSE)
                .consume(JsonToken.TRUE)
                .or(r -> JsonBoolean.TRUE)
                .consume(JsonToken.NUMBER)
                .or(r -> new JsonNumber(r.get(0, BigDecimal.class)))
                .consume(JsonToken.STRING)
                .or(r -> new JsonString(r.get(0, String.class)))
                .subRule(JsonRule.OBJECT)
                .or(r -> r.get(0, JsonObject.class))
                .subRule(JsonRule.ARRAY)
                .up(r -> r.get(0, JsonArray.class))
            .build();
        // CHECKSTYLE:ON
        System.out.println("================");
        System.out.println(p.parse(input));
    }
}
