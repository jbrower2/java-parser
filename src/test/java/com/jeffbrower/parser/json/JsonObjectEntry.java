package com.jeffbrower.parser.json;

public final class JsonObjectEntry {
    public final String key;
    public final JsonValue value;

    public JsonObjectEntry(final String key, final JsonValue value) {
        this.key = key;
        this.value = value;
    }

    public StringBuilder appendTo(final StringBuilder b, final String indent, final String tab) {
        JsonValue.appendJsonString(b, key).append(':');
        if (tab != null) {
            b.append(' ');
        }
        return value.appendTo(b, indent, tab);
    }

    @Override
    public String toString() {
        return toString(JsonValue.DEFAULT_TAB);
    }

    public String toString(final String tab) {
        return appendTo(new StringBuilder(), tab == null ? null : "", tab).toString();
    }
}
