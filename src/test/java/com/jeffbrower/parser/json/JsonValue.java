package com.jeffbrower.parser.json;

public abstract class JsonValue {
    static final String DEFAULT_TAB = "  ";

    static StringBuilder appendJsonString(final StringBuilder b, final CharSequence s) {
        b.append('"');
        s.codePoints().forEach(c -> {
            if (c == '\b') {
                b.append("\\b");
            }
            else if (c == '\f') {
                b.append("\\f");
            }
            else if (c == '\n') {
                b.append("\\n");
            }
            else if (c == '\r') {
                b.append("\\r");
            }
            else if (c == '\t') {
                b.append("\\t");
            }
            else if (c < ' ') {
                b.append(String.format("\\u%04x", c));
            }
            else {
                if (c == '"' || c == '\\') {
                    b.append('\\');
                }
                b.appendCodePoint(c);
            }
        });
        return b.append('"');
    }

    public abstract StringBuilder appendTo(StringBuilder b, String indent, String tab);

    @Override
    public final String toString() {
        return toString(DEFAULT_TAB);
    }

    public final String toString(final String tab) {
        return appendTo(new StringBuilder(), tab == null ? null : "", tab).toString();
    }
}
