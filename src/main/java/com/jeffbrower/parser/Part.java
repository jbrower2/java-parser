package com.jeffbrower.parser;

import java.util.List;
import java.util.Optional;

abstract class Part<T extends Enum<T>, R extends Enum<R>, F> {
    abstract Optional<PartialResult<?>> parse(Parser<T, R, F> parser, List<Token<T>> tokens, int i);
}
