package com.github.saphyra.apphub.integration.framework;

import lombok.Data;

import java.util.Map;

@Data
public class ParsedUri {
    private final String uri;
    private final Map<String, String> queryParams;
}
