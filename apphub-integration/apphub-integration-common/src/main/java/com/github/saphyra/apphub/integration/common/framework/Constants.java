package com.github.saphyra.apphub.integration.common.framework;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String ACCESS_TOKEN_COOKIE = "access-token";
    public static final String ACCESS_TOKEN_HEADER = "apphub-access-token";

    public static final String LOCALE_HEADER = "language";
    public static final String LOCALE_COOKIE = "language";
    public static final String BROWSER_LANGUAGE_HEADER = "BrowserLanguage";

    public static final String API_URI_PATTERN = "/api/**";
    public static final String INDEX_PAGE_URI = "/web";
}
