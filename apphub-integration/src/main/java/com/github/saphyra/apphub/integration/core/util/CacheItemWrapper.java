package com.github.saphyra.apphub.integration.core.util;

import lombok.Data;

@Data
public class CacheItemWrapper<T>{
    private final T item;
}
