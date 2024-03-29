package com.github.saphyra.apphub.integration.structure.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Range<T> {
    private T min;
    private T max;
}
