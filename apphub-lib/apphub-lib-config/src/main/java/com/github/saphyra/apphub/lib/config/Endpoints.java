package com.github.saphyra.apphub.lib.config;

public class Endpoints {
    public static final String HEALTH = "/platform/health";

    public static final String LOGIN = "/api/user/authentication/login";
    public static final String LOGOUT = "/api/user/authentication/logout";
    public static final String CHECK_SESSION = "/api/user/authentication/session";

    public static final String REGISTER = "/api/user/data";
    public static final String CHANGE_LANGUAGE = "/api/user/account/language";
    public static final String CHANGE_EMAIL = "/api/user/account/email";
    public static final String CHANGE_USERNAME = "/api/user/account/username";
    public static final String CHANGE_PASSWORD = "/api/user/account/password";
    public static final String DELETE_ACCOUNT = "/api/user/account";
    public static final String GET_LANGUAGES = "/api/user/data/languages";

    public static final String GET_USER_ROLES = "/api/user/data/roles";
    public static final String ADD_ROLE =  "/api/user/data/roles";
    public static final String REMOVE_ROLE =  "/api/user/data/roles";

    public static final String INTERNAL_GET_ACCESS_TOKEN_BY_ID = "/internal/user/authentication/{accessTokenId}";
    public static final String INTERNAL_GET_USER_LANGUAGE = "/internal/user/{userId}/data/language";

    public static final String REGISTER_PROCESSOR = "/platform/event-gateway";
    public static final String HEARTBEAT = "/platform/event-gateway/{serviceName}";
    public static final String SEND_EVENT = "/internal/event-gateway";

    public static final String DELETE_EXPIRED_ACCESS_TOKENS_EVENT = "/event/delete-expired-access-tokens";
    public static final String REFRESH_ACCESS_TOKEN_EXPIRATION_EVENT = "/event/refresh-access-token-expiration";
    public static final String DELETE_ACCOUNT_EVENT = "/event/delete-account";

    public static final String TRANSLATE_ERROR_CODE = "/internal/localization/error-code";

    public static final String GET_MODULES_OF_USER = "/api/modules";
    public static final String SET_FAVORITE = "/api/modules/{module}/favorite";

    public static final String INDEX_PAGE = "/web";
    public static final String ACCOUNT_PAGE = "/web/user/account";
    public static final String MODULES_PAGE = "/web/modules";
    public static final String ADMIN_PANEL_INDEX_PAGE = "/web/admin-panel";
    public static final String ADMIN_PANEL_ROLE_MANAGEMENT_PAGE = "/web/admin-panel/role-management";
    public static final String ERROR_PAGE = "/web/error";
}
