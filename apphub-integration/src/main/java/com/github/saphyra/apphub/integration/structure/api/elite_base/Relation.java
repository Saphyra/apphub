package com.github.saphyra.apphub.integration.structure.api.elite_base;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public enum Relation {
    ANY_MATCH((required, current) -> required.stream().anyMatch(current::contains)),
    ALL_MATCH((required, current) -> new HashSet<>(current).containsAll(required)),
    NONE_MATCH((required, current) -> required.stream().noneMatch(current::contains)),
    EMPTY((required, current) -> current.isEmpty()),
    ANY((required, current) -> true),
    ;

    private final BiFunction<List<String>, List<String>,Boolean> function;

    public boolean apply(List<String> required, List<String> current) {
        return function.apply(required, current);
    }
}
