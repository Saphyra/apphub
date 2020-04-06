package com.github.saphyra.apphub.lib.endpoint;

public class Endpoint {
    public static final String LOGIN = "/api/user/authentication/login";
    public static final String REGISTER = "/api/user/data";

    public static final String INTERNAL_FIND_USER_BY_EMAIL = "/internal/user/data/{email}";
    public static final String INTERNAL_GET_ACCESS_TOKEN_BY_ID = "/internal/user/authentication/{accessTokenId}";

    public static final String REGISTER_PROCESSOR = "/internal/platform/event-gateway";
    public static final String HEARTBEAT = "/internal/platform/event-gateway/{serviceName}";
    public static final String SEND_EVENT = "/internal/platform/event-gateway";

    public static final String DELETE_EXPIRED_ACCESS_TOKENS_EVENT = "/event/delete-expired-access-tokens";
    public static final String REFRESH_ACCESS_TOKEN_EXPIRATION_EVENT = "/event/refresh-access-token-expiration";
}
