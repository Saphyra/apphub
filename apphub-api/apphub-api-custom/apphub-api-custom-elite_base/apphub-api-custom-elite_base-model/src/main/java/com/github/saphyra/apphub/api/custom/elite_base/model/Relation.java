package com.github.saphyra.apphub.api.custom.elite_base.model;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@RequiredArgsConstructor
public enum Relation {
    ANY_MATCH((required, current) -> required.stream().anyMatch(s -> current.get().contains(s))),
    ALL_MATCH((required, current) -> new HashSet<>(current.get()).containsAll(required)),
    NONE_MATCH((required, current) -> required.stream().noneMatch(s -> current.get().contains(s))),
    EMPTY((_, current) -> current.get().isEmpty()),
    ANY((_, _) -> true),
    ;

    private final BiFunction<List<String>, Supplier<List<String>>, Boolean> function;

    public boolean apply(List<String> required, Supplier<List<String>> current) {
        return function.apply(required, current);
    }
}
