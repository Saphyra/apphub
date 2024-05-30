package com.github.saphyra.apphub.ci.utils.concurrent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ObjectWrapper <T>{
    private T value;
}
