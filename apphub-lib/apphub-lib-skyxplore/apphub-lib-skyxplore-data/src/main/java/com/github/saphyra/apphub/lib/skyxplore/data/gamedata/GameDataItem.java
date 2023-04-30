package com.github.saphyra.apphub.lib.skyxplore.data.gamedata;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class GameDataItem {
    private String id;
    private Map<String, Object> data = new HashMap<>();
}
