package com.github.saphyra.apphub.integration.structure.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OneParamRequest<T> {
    private T value;
}
