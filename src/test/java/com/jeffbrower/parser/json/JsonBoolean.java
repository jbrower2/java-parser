package com.jeffbrower.parser.json;

public final class JsonBoolean extends JsonValue {
    public static final JsonBoolean FALSE = new JsonBoolean(false);
    public static final JsonBoolean TRUE = new JsonBoolean(true);

    public final boolean value;

    private JsonBoolean(final boolean value) {
        this.value = value;
    }

    @Override
    public StringBuilder appendTo(final StringBuilder b, final String indent, final String tab) {
        return b.append(value);
    }
}
