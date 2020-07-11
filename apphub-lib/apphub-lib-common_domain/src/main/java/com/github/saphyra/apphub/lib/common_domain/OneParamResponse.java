package com.github.saphyra.apphub.lib.common_domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OneParamResponse<T> {
    private T value;
}
