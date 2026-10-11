package com.github.saphyra.apphub.api.platform.main_gateway.model;

public class MainGatewayEndpoints {
    //Internal
    public static final String MAIN_GATEWAY_INTERNAL_INVALIDATE_ACCESS_TOKEN = "/internal/main-gateway/access-tokens/{accessTokenId}";
    public static final String MAIN_GATEWAY_INVALIDATE_REFRESH_TOKENS = "/internal/main-gateway/refresh-tokens";

    public static final String MAIN_GATEWAY_GET_WEBSOCKET_PROTOCOL = "/api/ws/protocol";
    public static final String MAIN_GATEWAY_CHECK_SESSION = "/api/session";

    //Util
    public static final String MAIN_GATEWAY_UTIL_INVALIDATE_ACCESS_TOKEN = "/main-gateway/access-token";
    public static final String MAIN_GATEWAY_UTIL_GET_OWN_USER_ID = "/user/id";
}
