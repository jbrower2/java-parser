package com.jeffbrower.parser.json;

import java.math.BigDecimal;

public final class JsonNumber extends JsonValue {
    public final BigDecimal value;

    public JsonNumber(final BigDecimal value) {
        this.value = value;
    }

    @Override
    public StringBuilder appendTo(final StringBuilder b, final String indent, final String tab) {
        return b.append(value);
    }
}
