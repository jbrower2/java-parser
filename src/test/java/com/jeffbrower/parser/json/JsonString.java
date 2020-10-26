package com.jeffbrower.parser.json;

public final class JsonString extends JsonValue {
    public final String value;

    public JsonString(final String value) {
        this.value = value;
    }

    @Override
    public StringBuilder appendTo(final StringBuilder b, final String indent, final String tab) {
        return appendJsonString(b, value);
    }
}
