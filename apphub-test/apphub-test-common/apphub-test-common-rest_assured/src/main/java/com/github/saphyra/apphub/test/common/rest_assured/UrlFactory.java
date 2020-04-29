package com.github.saphyra.apphub.test.common.rest_assured;

import java.util.Map;

public class UrlFactory {
    public static String create(int serverPort, String url) {
        return String.format("http://localhost:%s%s", serverPort, url);
    }

    public static String create(int serverPort, String uri, Map<String, Object> uriParams) {
        String urlBase = create(serverPort, uri);
        for (Map.Entry<String, Object> entry : uriParams.entrySet()) {
            String key = String.format("{%s}", entry.getKey());
            urlBase = urlBase.replace(key, entry.getValue().toString());
        }
        return urlBase;
    }
}
