package com.github.saphyra.apphub.integration.framework;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BiWrapper<E1, E2> {
    private final E1 entity1;
    private final E2 entity2;
}
