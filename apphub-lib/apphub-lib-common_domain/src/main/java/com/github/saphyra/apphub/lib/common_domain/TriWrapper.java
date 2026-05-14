package com.github.saphyra.apphub.lib.common_domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TriWrapper<E1, E2, E3> {
    private E1 entity1;
    private E2 entity2;
    private E3 entity3;
}
