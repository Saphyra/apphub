package com.github.saphyra.apphub.lib.common_domain;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public final String ACCESS_TOKEN_COOKIE = "access-token";
    public final String ACCESS_TOKEN_HEADER = "apphub-access-token";
    public final String ROLE_ADMIN = "ADMIN";

    public final String LOCALE_HEADER = "language";
    public final String LOCALE_COOKIE = "language";
    public final String BROWSER_LANGUAGE_HEADER = "BrowserLanguage";

    public final String RESOURCE_PATH_PATTERN = "/res/**";
    public final String WEB_PATH_PATTERN = "/web/**";
    public final String SEND_EVENT_REQUEST_METADATA_KEY_BLOCKING_REQUEST = "blocking_request";
    public final String AUTHORIZATION_HEADER = "auth";

    public static final String EMPTY_STRING = "";
}
