package com.github.saphyra.apphub.integration.framework.endpoints;

public class UserEndpoints {
    //Login Session
    public static final String LOGIN = "/api/user/authentication/login";
    public static final String LOGOUT = "/api/user/authentication/logout";
    public static final String CHECK_SESSION = "/api/user/authentication/session";

    //Account
    public static final String ACCOUNT_PAGE = "/web/user/account";
    public static final String ACCOUNT_REGISTER = "/api/user";
    public static final String ACCOUNT_CHANGE_EMAIL = "/api/user/account/email";
    public static final String ACCOUNT_CHANGE_USERNAME = "/api/user/account/username";
    public static final String ACCOUNT_CHANGE_PASSWORD = "/api/user/account/password";
    public static final String ACCOUNT_DELETE_ACCOUNT = "/api/user/account";

    //Ban
    public static final String ACCOUNT_BAN_USER = "/api/user/ban";
    public static final String ACCOUNT_REVOKE_BAN = "/api/user/ban/{banId}";
    public static final String ACCOUNT_GET_BANS = "/api/user/ban/{userId}";
    public static final String ACCOUNT_MARK_FOR_DELETION = "/api/user/ban/{userId}/mark-for-deletion";
    public static final String ACCOUNT_UNMARK_FOR_DELETION = "/api/user/ban/{userId}/mark-for-deletion";
    public static final String ACCOUNT_BAN_SEARCH = "/api/user/ban/search";

    //SETTINGS
    public static final String GET_USER_SETTINGS = "/api/user/settings/{category}";
    public static final String SET_USER_SETTINGS = "/api/user/settings";

    //Role
    public static final String USER_DATA_GET_USER_ROLES = "/api/user/data/roles";
    public static final String USER_DATA_ADD_ROLE = "/api/user/data/roles";
    public static final String USER_DATA_REMOVE_ROLE = "/api/user/data/roles";
    public static final String USER_DATA_DISABLE_ROLE = "/api/user/data/roles/{role}";
    public static final String USER_DATA_GET_DISABLED_ROLES = "/api/user/data/roles/disabled";
    public static final String USER_DATA_ADD_ROLE_TO_ALL = "/api/user/data/roles/all/{role}";
    public static final String USER_DATA_REMOVE_ROLE_FROM_ALL = "/api/user/data/roles/all/{role}";
    public static final String USER_DATA_ROLES_FOR_ALL_RESTRICTED = "/api/user/data/roles/restricted";
}
