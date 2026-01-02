package com.github.saphyra.apphub.lib.data;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

public class ClassPathList<T> extends ArrayList<T> {
    public ClassPathList(ObjectMapper objectMapper, String fileLocation) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileLocation);
        TypeReference<Collection<T>> ref = new TypeReference<>() {
        };
        addAll(objectMapper.readValue(inputStream, ref));
    }
}
