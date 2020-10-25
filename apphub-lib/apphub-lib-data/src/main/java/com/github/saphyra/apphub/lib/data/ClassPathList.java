package com.github.saphyra.apphub.lib.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

public class ClassPathList<T> extends ArrayList<T> {
    public ClassPathList(ObjectMapperWrapper objectMapper, String fileLocation) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileLocation);
        TypeReference<Collection<T>> ref = new TypeReference<Collection<T>>() {
        };
        addAll(objectMapper.readValue(inputStream, ref));
    }
}
