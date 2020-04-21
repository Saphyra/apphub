package com.github.saphyra.apphub.test.common.rest_assured;

public class UrlFactory {
    public static String create(int serverPort, String url) {
        return String.format("http://localhost:%s%s", serverPort, url);
    }
}
