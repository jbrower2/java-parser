package com.jeffbrower.parser.json;

public final class JsonObject extends JsonValue {
    public final JsonObjectEntry[] entries;

    public JsonObject(final JsonObjectEntry[] entries) {
        this.entries = entries;
    }

    @Override
    public StringBuilder appendTo(final StringBuilder b, final String indent, final String tab) {
        if (entries.length == 0) {
            return b.append("{}");
        }
        final boolean pretty = tab != null;
        b.append('{');
        for (int i = 0; i < entries.length; i++) {
            if (i > 0) {
                b.append(',');
            }
            if (pretty) {
                b.append(System.lineSeparator()).append(indent).append(tab);
            }
            entries[i].appendTo(b, pretty ? indent + tab : null, tab);
        }
        if (pretty) {
            b.append(System.lineSeparator()).append(indent);
        }
        return b.append('}');
    }
}
