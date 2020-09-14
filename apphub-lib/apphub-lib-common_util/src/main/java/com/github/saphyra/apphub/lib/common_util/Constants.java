package com.github.saphyra.apphub.lib.common_util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public final String ACCESS_TOKEN_COOKIE = "access-token";
    public final String ACCESS_TOKEN_HEADER = "apphub-access-token";

    public final String LOCALE_HEADER = "language";
    public final String LOCALE_COOKIE = "language";
    public final String BROWSER_LANGUAGE_HEADER = "BrowserLanguage";

    public final String API_URI_PATTERN = "/api/**";
    public final String INDEX_PAGE_URI = "/web";
    public final String RESOURCE_PATH_PATTERN = "/res/**";
    public final String SEND_EVENT_REQUEST_METADATA_KEY_BLOCKING_REQUEST = "blocking_request";
    public final String REQUEST_TYPE_HEADER = "Request-Type";
    public final String REQUEST_TYPE_VALUE = "rest";
    public final String AUTHORIZATION_HEADER = "auth";
}
