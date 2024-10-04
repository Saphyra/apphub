package com.github.saphyra.apphub.lib.config.common.endpoints;

public class UserEndpoints {
    //Login Session
    public static final String EVENT_DELETE_EXPIRED_ACCESS_TOKENS = "/event/delete-expired-access-tokens";
    public static final String EVENT_REFRESH_ACCESS_TOKEN_EXPIRATION = "/event/refresh-access-token-expiration";
    public static final String LOGIN = "/api/user/authentication/login";
    public static final String LOGOUT = "/api/user/authentication/logout";
    public static final String CHECK_SESSION = "/api/user/authentication/session";
    public static final String EXTEND_SESSION = "/api/user/authentication/session";
    public static final String USER_DATA_INTERNAL_GET_ACCESS_TOKEN_BY_ID = "/internal/user/authentication/{accessTokenId}";
    public static final String EVENT_ACCESS_TOKEN_INVALIDATED = "/event/access-token-invalidated";

    //Account
    public static final String EVENT_TRIGGER_ACCOUNT_DELETION = "/event/user-data/trigger-account-deletion";
    public static final String ACCOUNT_REGISTER = "/api/user";
    public static final String ACCOUNT_CHANGE_LANGUAGE = "/api/user/account/language";
    public static final String ACCOUNT_CHANGE_EMAIL = "/api/user/account/email";
    public static final String ACCOUNT_CHANGE_USERNAME = "/api/user/account/username";
    public static final String ACCOUNT_CHANGE_PASSWORD = "/api/user/account/password";
    public static final String ACCOUNT_DELETE_ACCOUNT = "/api/user/account";
    public static final String USER_DATA_SEARCH_ACCOUNT = "/api/user/accounts";
    public static final String USER_DATA_GET_ACCOUNT = "/api/user/account";
    public static final String USER_DATA_INTERNAL_GET_USER_LANGUAGE = "/internal/user/{userId}/data/language";
    public static final String USER_DATA_INTERNAL_GET_ACCOUNT = "/internal/api/user/data/{userId}";
    public static final String USER_DATA_INTERNAL_USER_EXISTS = "/internal/api/user/data/{userId}/exists";
    public static final String USER_DATA_GET_USERNAME = "/api/user/data/name";

    //Ban
    public static final String EVENT_TRIGGER_REVOKE_EXPIRED_BANS = "/event/user-data/trigger-remove-expired-bans";
    public static final String ACCOUNT_BAN_USER = "/api/user/ban";
    public static final String ACCOUNT_REVOKE_BAN = "/api/user/ban/{banId}";
    public static final String ACCOUNT_GET_BANS = "/api/user/ban/{userId}";
    public static final String ACCOUNT_MARK_FOR_DELETION = "/api/user/ban/{userId}/mark-for-deletion";
    public static final String ACCOUNT_UNMARK_FOR_DELETION = "/api/user/ban/{userId}/mark-for-deletion";
    public static final String ACCOUNT_BAN_SEARCH = "/api/user/ban/search";
    public static final String ACCOUNT_BAN_GET_DETAILS_FOR_ERROR_PAGE = "/api/user/ban/details";

    //SETTINGS
    public static final String GET_USER_SETTINGS = "/api/user/settings/{category}";
    public static final String SET_USER_SETTINGS = "/api/user/settings";

    //Role
    public static final String USER_DATA_GET_USER_ROLES = "/api/user/data/roles";
    public static final String USER_DATA_ADD_ROLE = "/api/user/data/roles";
    public static final String USER_DATA_REMOVE_ROLE = "/api/user/data/roles";
    public static final String USER_DATA_DISABLE_ROLE = "/api/user/data/roles/{role}";
    public static final String USER_DATA_ENABLE_ROLE = "/api/user/data/roles/{role}";
    public static final String USER_DATA_GET_DISABLED_ROLES = "/api/user/data/roles/disabled";
    public static final String USER_DATA_ADD_ROLE_TO_ALL = "/api/user/data/roles/all/{role}";
    public static final String USER_DATA_REMOVE_ROLE_FROM_ALL = "/api/user/data/roles/all/{role}";
    public static final String USER_DATA_ROLES_FOR_ALL_RESTRICTED = "/api/user/data/roles/restricted";
}
