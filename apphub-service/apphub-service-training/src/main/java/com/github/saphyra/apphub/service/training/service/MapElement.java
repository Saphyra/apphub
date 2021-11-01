package com.github.saphyra.apphub.service.training.service;

import lombok.Data;

@Data
public class MapElement<T> {
    private String key;
    private T value;
}
