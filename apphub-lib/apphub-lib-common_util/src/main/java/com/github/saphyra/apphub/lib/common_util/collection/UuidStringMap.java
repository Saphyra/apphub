package com.github.saphyra.apphub.lib.common_util.collection;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
public class UuidStringMap extends HashMap<UUID, String> {
    public UuidStringMap(Map<UUID, String> map) {
        super(map);
    }
}
