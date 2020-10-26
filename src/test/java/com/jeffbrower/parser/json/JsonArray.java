package com.jeffbrower.parser.json;

public final class JsonArray extends JsonValue {
    public final JsonValue[] values;

    public JsonArray(final JsonValue[] values) {
        this.values = values;
    }

    @Override
    public StringBuilder appendTo(final StringBuilder b, final String indent, final String tab) {
        if (values.length == 0) {
            return b.append("[]");
        }
        final boolean pretty = tab != null;
        b.append('[');
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                b.append(',');
            }
            if (pretty) {
                b.append(System.lineSeparator()).append(indent).append(tab);
            }
            values[i].appendTo(b, pretty ? indent + tab : null, tab);
        }
        if (pretty) {
            b.append(System.lineSeparator()).append(indent);
        }
        return b.append(']');
    }
}
