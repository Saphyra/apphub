package com.github.saphyra.apphub.integration.common.framework;

public class Endpoints {

    public static final String LOGIN = "/api/user/authentication/login";
    public static final String LOGOUT = "/api/user/authentication/logout";

    public static final String REGISTER = "/api/user";

    public static final String GET_MODULES_OF_USER = "/api/modules";
    public static final String SET_FAVORITE = "/api/modules/{module}/favorite";

    public static final String ACCOUNT_PAGE = "/web/user/account";
    public static final String MODULES_PAGE = "/web/modules";
    public static final String WEB_ROOT = "/web";
    public static final String ROOT = "/";
    public static final String ADMIN_PANEL_PAGE = "/web/admin-panel";
    public static final String ERROR_PAGE = "/web/error";
    public static final String NOTEBOOK_PAGE = "/web/notebook";

    public static final String CHANGE_LANGUAGE = "/api/user/account/language";
    public static final String GET_LANGUAGES = "/api/user/data/languages";
    public static final String CHANGE_EMAIL = "/api/user/account/email";

    public static final String CHANGE_USERNAME = "/api/user/account/username";
    public static final String CHANGE_PASSWORD = "/api/user/account/password";
    public static final String DELETE_ACCOUNT = "/api/user/account";

    public static final String GET_ROLES = "/api/user/data/roles";
    public static final String ADD_ROLE = "/api/user/data/roles";
    public static final String REMOVE_ROLE = "/api/user/data/roles";

    public static final String GET_NOTEBOOK_CATEGORY_TREE = "/api/notebook/category";
    public static final String CREATE_NOTEBOOK_CATEGORY = "/api/notebook/category";
    public static final String GET_CHILDREN_OF_NOTEBOOK_CATEGORY = "/api/notebook/category/children";
    public static final String DELETE_NOTEBOOK_LIST_ITEM = "/api/notebook/item/{listItemId}";
    public static final String CREATE_NOTEBOOK_TEXT = "/api/notebook/text";
    public static final String GET_NOTEBOOK_TEXT = "/api/notebook/text/{listItemId}";
    public static final String EDIT_NOTEBOOK_TEXT = "/api/notebook/text/{listItemId}";
    public static final String CREATE_NOTEBOOK_LINK = "/api/notebook/link";
    public static final String CREATE_NOTEBOOK_CHECKLIST_ITEM = "/api/notebook/checklist";
    public static final String EDIT_NOTEBOOK_LIST_ITEM = "/api/notebook/item/{listItemId}";
    public static final String GET_NOTEBOOK_CHECKLIST = "/api/notebook/checklist/{listItemId}";
    public static final String EDIT_NOTEBOOK_CHECKLIST_ITEM = "/api/notebook/checklist/{listItemId}";
    public static final String UPDATE_CHECKLIST_ITEM_STATUS = "/api/notebook/checklist/item/{checklistItemId}/status";
    public static final String CREATE_NOTEBOOK_TABLE = "/api/notebook/table";
    public static final String GET_NOTEBOOK_TABLE = "/api/notebook/table/{listItemId}";
    public static final String EDIT_NOTEBOOK_TABLE = "/api/notebook/table/{listItemId}";
}
