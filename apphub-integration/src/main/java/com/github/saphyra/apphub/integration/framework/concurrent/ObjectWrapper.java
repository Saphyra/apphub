package com.github.saphyra.apphub.integration.framework.concurrent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ObjectWrapper <T>{
    private T value;
}
