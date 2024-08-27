package com.github.saphyra.apphub.integration.framework;


import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class UrlFactory {
    public static String create(int serverPort, String uri) {
        return create(serverPort, uri, Map.of(), Map.of());
    }

    public static String create(int serverPort, String uri, String pathVariableKey, Object pathVariableValue) {
        return create(serverPort, uri, Map.of(pathVariableKey, pathVariableValue), Map.of());
    }

    public static String create(int serverPort, String uri, Map<String, Object> pathVariables) {
        return create(serverPort, uri, pathVariables, Map.of());
    }

    public static String createWithRedirect(int serverPort, String uri, String redirectUrl) {
        return create(serverPort, uri, Map.of(), Map.of("redirect", redirectUrl));
    }

    public static String create(int serverPort, String url, Map<String, ?> pathVariables, Map<String, ?> queryParams) {
        String uri = String.format("http://localhost:%s%s", serverPort, injectPathVariables(url, pathVariables));

        return addQueryParams(uri, queryParams);
    }

    private static String addQueryParams(String uri, Map<String, ?> queryParams) {
        if (queryParams.isEmpty()) {
            return uri;
        }

        uri += "?";

        uri += queryParams.entrySet()
            .stream()
            .filter(entry -> !isNull(entry.getValue()))
            .map(entry -> String.join("=", entry.getKey(), entry.getValue().toString()))
            .collect(Collectors.joining("&"));

        return uri;
    }

    private static String injectPathVariables(String url, Map<String, ?> pathVariables) {
        String urlBase = url;
        for (Map.Entry<String, ?> entry : pathVariables.entrySet()) {
            String key = String.format("{%s}", entry.getKey());
            urlBase = urlBase.replace(key, entry.getValue().toString());
        }
        return urlBase;
    }
}
