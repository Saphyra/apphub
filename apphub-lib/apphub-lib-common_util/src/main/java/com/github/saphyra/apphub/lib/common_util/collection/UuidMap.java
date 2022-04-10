package com.github.saphyra.apphub.lib.common_util.collection;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
public class UuidMap extends HashMap<UUID, UUID> {
    public UuidMap(Map<UUID, UUID> map) {
        super(map);
    }
}
