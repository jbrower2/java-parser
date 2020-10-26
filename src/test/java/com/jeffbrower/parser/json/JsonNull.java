package com.jeffbrower.parser.json;

public final class JsonNull extends JsonValue {
    public static final JsonNull INSTANCE = new JsonNull();

    private JsonNull() {
    }

    @Override
    public StringBuilder appendTo(final StringBuilder b, final String indent, final String tab) {
        return b.append("null");
    }
}
