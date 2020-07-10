package com.github.saphyra.apphub.test.common.rest_assured;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlFactory {
    public static String create(int serverPort, String url) {
        return String.format("http://localhost:%s%s", serverPort, url);
    }

    public static String create(int serverPort, String uri, String key, Object value) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(key, value);
        return create(serverPort, uri, paramMap);
    }

    public static String create(int serverPort, String uri, Map<String, ?> uriParams) {
        String urlBase = create(serverPort, uri);
        for (Map.Entry<String, ?> entry : uriParams.entrySet()) {
            String key = String.format("{%s}", entry.getKey());
            urlBase = urlBase.replace(key, entry.getValue().toString());
        }
        return urlBase;
    }

    public static String create(int serverPort, String uri, Map<String, ?> uriParams, Map<String, ? extends Object> queryParams) {
        String base = create(serverPort, uri, uriParams);
        return base + "?" + queryParams.entrySet()
            .stream()
            .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining("&"));
    }
}
