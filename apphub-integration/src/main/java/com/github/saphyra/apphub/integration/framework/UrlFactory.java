package com.github.saphyra.apphub.integration.framework;


import com.github.saphyra.apphub.integration.core.TestBase;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class UrlFactory {
    public static String create(String url) {
        return create(TestBase.SERVER_PORT, url);
    }

    public static String create(String url, Map<String, ?> uriParams) {
        return create(TestBase.SERVER_PORT, url, uriParams);
    }

    public static String create(int serverPort, String url) {
        return String.format("http://localhost:%s%s", serverPort, url);
    }

    public static String create(String url, String key, Object value) {
        Map<String, Object> pathVariables = new HashMap<>();
        pathVariables.put(key, value);
        return create(url, pathVariables);
    }

    public static String create(String url, Map<String, ?> uriParams, Map<String, ?> queryParams) {
        String uri = create(url, uriParams);
        if (!queryParams.isEmpty()) {
            uri += "?";
        }
        uri += queryParams.entrySet()
            .stream()
            .filter(entry -> !isNull(entry.getValue()))
            .map(entry -> String.join("=", entry.getKey(), entry.getValue().toString()))
            .collect(Collectors.joining("&"));

        return uri;
    }

    public static String create(int serverPort, String uri, Map<String, ?> uriParams) {
        String urlBase = create(serverPort, uri);
        for (Map.Entry<String, ?> entry : uriParams.entrySet()) {
            String key = String.format("{%s}", entry.getKey());
            urlBase = urlBase.replace(key, entry.getValue().toString());
        }
        return urlBase;
    }
}
