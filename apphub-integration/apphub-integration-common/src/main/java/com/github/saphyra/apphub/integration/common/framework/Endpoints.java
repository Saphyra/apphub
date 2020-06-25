package com.github.saphyra.apphub.integration.common.framework;

public class Endpoints {

    public static final String LOGIN = "/api/user/authentication/login";
    public static final String LOGOUT = "/api/user/authentication/logout";

    public static final String REGISTER = "/api/user/data";

    public static final String GET_MODULES_OF_USER = "/api/modules";
    public static final String SET_FAVORITE = "/api/modules/{module}/favorite";

    public static final String ACCOUNT_PAGE = "/web/user/account";
    public static final String MODULES_PAGE = "/web/modules";
    public static final String WEB_ROOT = "/web";
    public static final String ROOT = "/";
    public static final String ADMIN_PANEL_PAGE = "/web/admin-panel";
    public static final String ERROR_PAGE = "/web/error";

    public static final String CHANGE_LANGUAGE = "/api/user/account/language";
    public static final String GET_LANGUAGES = "/api/user/data/languages";
    public static final String CHANGE_EMAIL = "/api/user/account/email";

    public static final String CHANGE_USERNAME = "/api/user/account/username";
    public static final String CHANGE_PASSWORD = "/api/user/account/password";
    public static final String DELETE_ACCOUNT = "/api/user/account";

    public static final String GET_ROLES = "/api/user/data/roles";
    public static final String ADD_ROLE = "/api/user/data/roles";
    public static final String REMOVE_ROLE = "/api/user/data/roles";

}
