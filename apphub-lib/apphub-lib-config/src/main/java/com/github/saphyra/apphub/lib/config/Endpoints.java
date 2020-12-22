package com.github.saphyra.apphub.lib.config;

public class Endpoints {
    //PAGES
    public static final String INDEX_PAGE = "/web";
    public static final String ACCOUNT_PAGE = "/web/user/account";
    public static final String MODULES_PAGE = "/web/modules";
    public static final String ADMIN_PANEL_INDEX_PAGE = "/web/admin-panel";
    public static final String ADMIN_PANEL_ROLE_MANAGEMENT_PAGE = "/web/admin-panel/role-management";
    public static final String ERROR_PAGE = "/web/error";
    public static final String NOTEBOOK_PAGE = "/web/notebook";
    public static final String SKYXPLORE_MAIN_MENU_PAGE = "/web/skyxplore";
    public static final String SKYXPLORE_CHARACTER_PAGE = "/web/skyxplore/character";
    public static final String SKYXPLORE_LOBBY_PAGE = "/web/skyxplore/lobby";
    public static final String SKYXPLORE_GAME_PAGE = "/web/skyxplore/game";

    //PLATFORM
    public static final String HEALTH = "/platform/health";
    public static final String TRANSLATE_ERROR_CODE = "/internal/localization/error-code";

    //EVENTS
    public static final String DELETE_EXPIRED_ACCESS_TOKENS_EVENT = "/event/delete-expired-access-tokens";
    public static final String REFRESH_ACCESS_TOKEN_EXPIRATION_EVENT = "/event/refresh-access-token-expiration";
    public static final String DELETE_ACCOUNT_EVENT = "/event/delete-account";
    public static final String PAGE_VISITED_EVENT = "/event/page-visited";
    public static final String SKYXPLORE_LOBBY_CLEANUP_EVENT = "/event/skyxplore/lobby/cleanup";
    public static final String MESSAGE_SENDER_PING_REQUEST_EVENT = "/event/message-sender/ping-request";
    public static final String MESSAGE_SENDER_CONNECTION_CLEANUP_EVENT = "/event/message-sender/connection-cleanup";

    //WEB-SOCKET-MESSAGING
    public static final String SEND_MESSAGE = "/internal/message/{group}";

    //CONNECTION SUBSCRIPTIONS
    public static final String CONNECTION_SKYXPLORE_MAIN_MENU = "/api/message-sender/skyxplore/main-menu";
    public static final String CONNECTION_SKYXPLORE_LOBBY = "/api/message-sender/skyxplore/lobby";
    public static final String CONNECTION_SKYXPLORE_GAME = "/api/message-sender/skyxplore/game";

    //LOGIN
    public static final String LOGIN = "/api/user/authentication/login";
    public static final String LOGOUT = "/api/user/authentication/logout";
    public static final String CHECK_SESSION = "/api/user/authentication/session";

    //ACCOUNT
    public static final String REGISTER = "/api/user";
    public static final String CHANGE_LANGUAGE = "/api/user/account/language";
    public static final String CHANGE_EMAIL = "/api/user/account/email";
    public static final String CHANGE_USERNAME = "/api/user/account/username";
    public static final String CHANGE_PASSWORD = "/api/user/account/password";
    public static final String DELETE_ACCOUNT = "/api/user/account";
    public static final String GET_LANGUAGES = "/api/user/data/languages";

    //USER-DATA
    public static final String INTERNAL_GET_ACCESS_TOKEN_BY_ID = "/internal/user/authentication/{accessTokenId}";
    public static final String INTERNAL_GET_USER_LANGUAGE = "/internal/user/{userId}/data/language";
    public static final String INTERNAL_GET_LAST_VISITED_PAGE = "/internal/user/authentication/last-visited-page/{userId}";

    //ADMIN_PANEL
    public static final String GET_USER_ROLES = "/api/user/data/roles";
    public static final String ADD_ROLE = "/api/user/data/roles";
    public static final String REMOVE_ROLE = "/api/user/data/roles";

    public static final String REGISTER_PROCESSOR = "/platform/event-gateway";
    public static final String HEARTBEAT = "/platform/event-gateway/{serviceName}";
    public static final String SEND_EVENT = "/internal/event-gateway";


    //MODULES
    public static final String GET_MODULES_OF_USER = "/api/modules";
    public static final String SET_FAVORITE = "/api/modules/{module}/favorite";

    //NOTEBOOK
    public static final String GET_NOTEBOOK_CATEGORY_TREE = "/api/notebook/category";
    public static final String CREATE_NOTEBOOK_CATEGORY = "/api/notebook/category";
    public static final String CREATE_NOTEBOOK_TEXT = "/api/notebook/text";
    public static final String GET_CHILDREN_OF_NOTEBOOK_CATEGORY = "/api/notebook/category/children";
    public static final String NOTEBOOK_CATEGORY_CONTENT_VIEW = "/web/notebook/content/category";
    public static final String DELETE_NOTEBOOK_LIST_ITEM = "/api/notebook/item/{listItemId}";
    public static final String EDIT_NOTEBOOK_LIST_ITEM = "/api/notebook/item/{listItemId}";
    public static final String GET_NOTEBOOK_TEXT = "/api/notebook/text/{listItemId}";
    public static final String EDIT_NOTEBOOK_TEXT = "/api/notebook/text/{listItemId}";
    public static final String CREATE_NOTEBOOK_LINK = "/api/notebook/link";
    public static final String CREATE_NOTEBOOK_CHECKLIST_ITEM = "/api/notebook/checklist";
    public static final String EDIT_NOTEBOOK_CHECKLIST_ITEM = "/api/notebook/checklist/{listItemId}";
    public static final String GET_NOTEBOOK_CHECKLIST_ITEM = "/api/notebook/checklist/{listItemId}";
    public static final String UPDATE_CHECKLIST_ITEM_STATUS = "/api/notebook/checklist/item/{checklistItemId}/status";
    public static final String CREATE_NOTEBOOK_TABLE = "/api/notebook/table";
    public static final String EDIT_NOTEBOOK_TABLE = "/api/notebook/table/{listItemId}";
    public static final String GET_NOTEBOOK_TABLE = "/api/notebook/table/{listItemId}";
    public static final String CLONE_NOTEBOOK_LIST_ITEM = "/api/notebook/{listItemId}/clone";
    public static final String CREATE_NOTEBOOK_CHECKLIST_TABLE = "/api/notebook/checklist-table";
    public static final String EDIT_NOTEBOOK_CHECKLIST_TABLE = "/api/notebook/checklist-table/{listItemId}";
    public static final String GET_NOTEBOOK_CHECKLIST_TABLE = "/api/notebook/checklist-table/{listItemId}";
    public static final String UPDATE_CHECKLIST_TABLE_ROW_STATUS = "/api/notebook/checklist-table/{listItemId}/{rowIndex}";
    public static final String CONVERT_NOTEBOOK_TABLE_TO_CHECKLIST_TABLE = "/api/notebook/table/{listItemId}/convert-to-checklist-table";

    //SKYXPLORE-DATA
    public static final String INTERNAL_SKYXPLORE_IS_CHARACTER_EXISTS = "/internal/skyxplore/character/exists";
    public static final String INTERNAL_SKYXPLORE_GET_CHARACTER_BY_USER_ID = "/allowed-internal/skyxplore/character/{userId}";
    public static final String SKYXPLORE_CREATE_OR_UPDATE_CHARACTER = "/api/skyxplore/character";
    public static final String SKYXPLORE_SEARCH_FOR_FRIENDS = "/api/skyxplore/friend/candidate";
    public static final String SKYXPLORE_ADD_FRIEND = "/api/skyxplore/friend/request";
    public static final String SKYXPLORE_GET_SENT_FRIEND_REQUEST = "/api/skyxplore/friend/request/sent";
    public static final String SKYXPLORE_GET_INCOMING_FRIEND_REQUEST = "/api/skyxplore/friend/request/incoming";
    public static final String SKYXPLORE_CANCEL_FRIEND_REQUEST = "/api/skyxplore/friend/request/{friendRequestId}";
    public static final String SKYXPLORE_ACCEPT_FRIEND_REQUEST = "/api/skyxplore/friend/request/{friendRequestId}";
    public static final String SKYXPLORE_GET_FRIENDS = "/api/skyxplore/friend";
    public static final String SKYXPLORE_REMOVE_FRIEND = "/api/skyxplore/friend/{friendshipId}";
    public static final String SKYXPLORE_GET_ACTIVE_FRIENDS = "/api/skyxplore/friend/active";
    public static final String SKYXPLORE_GET_CHARACTER = "/api/skyxplore/character";

    //SKYXPLORE-LOBBY
    public static final String INTERNAL_SKYXPLORE_USER_JOINED_TO_LOBBY = "/internal/lobby/{userId}";
    public static final String INTERNAL_SKYXPLORE_USER_LEFT_LOBBY = "/internal/lobby/{userId}";
    public static final String INTERNAL_SKYXPLORE_LOBBY_PROCESS_WEB_SOCKET_EVENTS = "/web-socket-event/skyxplore/lobby/{userId}";
    public static final String INTERNAL_SKYXPLORE_LOBBY_VIEW_FOR_PAGE = "/internal/skyxplore/lobby/page";

    public static final String SKYXPLORE_EXIT_FROM_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_INVITE_TO_LOBBY = "/api/skyxplore/lobby/invite/{friendId}";
    public static final String SKYXPLORE_LOBBY_ACCEPT_INVITATION = "/api/skyxplore/lobby/join/{invitorId}";
    public static final String SKYXPLORE_LOBBY_GET_MEMBERS = "/api/skyxplore/lobby/members";
    public static final String SKYXPLORE_CREATE_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_LOBBY_GET_GAME_SETTINGS = "/api/skyxplore/lobby/settings";
    public static final String SKYXPLORE_START_GAME = "/api/skyxplore/lobby/start";

    //SKYXPLORE-GAME
    public static final String INTERNAL_SKYXPLORE_GAME_PROCESS_WEB_SOCKET_EVENTS = "/web-socket-event/skyxplore/game/{userId}";

    public static final String INTERNAL_SKYXPLORE_CREATE_GAME = "/internal/skyxplore/game";
    public static final String INTERNAL_SKYXPLORE_USER_JOINED_TO_GAME = "/internal/game/{userId}";
    public static final String INTERNAL_SKYXPLORE_USER_LEFT_GAME = "/internal/game/{userId}";

    public static final String SKYXPLORE_GAME_MAP = "/api/skyxplore/game/universe";
}
