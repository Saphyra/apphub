package com.github.saphyra.apphub.lib.common_domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.function.Function;

@Data
@Builder
@AllArgsConstructor
public class BiWrapper<E1, E2> {
    private final E1 entity1;
    private final E2 entity2;

    public <T> BiWrapper<E1, T> mapEntity2(Function<E2, T> mapper) {
        return new BiWrapper<>(entity1, mapper.apply(entity2));
    }
}
