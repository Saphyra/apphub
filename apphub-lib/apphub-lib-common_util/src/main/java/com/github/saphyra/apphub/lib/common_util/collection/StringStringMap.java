package com.github.saphyra.apphub.lib.common_util.collection;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class StringStringMap extends HashMap<String, String> {
    public StringStringMap(Map<String, String> map) {
        putAll(map);
    }
}
