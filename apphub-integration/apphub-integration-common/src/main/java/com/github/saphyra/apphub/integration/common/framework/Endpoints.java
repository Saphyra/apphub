package com.github.saphyra.apphub.integration.common.framework;

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
    public static final String UTILS_JSON_FORMATTER_PAGE = "/web/utils/json-formatter";
    public static final String UTILS_LOG_FORMATTER_PAGE = "/web/utils/log-formatter";

    //PLATFORM
    public static final String HEALTH = "/platform/health";
    public static final String TRANSLATE_ERROR_CODE = "/internal/localization/error-code";

    //EVENTS
    public static final String EVENT_DELETE_EXPIRED_ACCESS_TOKENS = "/event/delete-expired-access-tokens";
    public static final String EVENT_REFRESH_ACCESS_TOKEN_EXPIRATION = "/event/refresh-access-token-expiration";
    public static final String EVENT_DELETE_ACCOUNT = "/event/delete-account";
    public static final String EVENT_PAGE_VISITED = "/event/page-visited";
    public static final String EVENT_SKYXPLORE_LOBBY_CLEANUP = "/event/skyxplore/lobby/cleanup";
    public static final String EVENT_MESSAGE_SENDER_PING_REQUEST = "/event/message-sender/ping-request";
    public static final String EVENT_MESSAGE_SENDER_CONNECTION_CLEANUP = "/event/message-sender/connection-cleanup";

    //WEB-SOCKET-MESSAGING
    public static final String WEB_SOCKET_SEND_MESSAGE = "/internal/message/{group}";

    //CONNECTION SUBSCRIPTIONS
    public static final String WS_CONNECTION_SKYXPLORE_MAIN_MENU = "/api/message-sender/skyxplore/main-menu";
    public static final String WS_CONNECTION_SKYXPLORE_LOBBY = "/api/message-sender/skyxplore/lobby";
    public static final String WS_CONNECTION_SKYXPLORE_GAME = "/api/message-sender/skyxplore/game";

    //LOGIN
    public static final String LOGIN = "/api/user/authentication/login";
    public static final String LOGOUT = "/api/user/authentication/logout";
    public static final String CHECK_SESSION = "/api/user/authentication/session";

    //ACCOUNT
    public static final String ACCOUNT_REGISTER = "/api/user";
    public static final String ACCOUNT_CHANGE_LANGUAGE = "/api/user/account/language";
    public static final String ACCOUNT_CHANGE_EMAIL = "/api/user/account/email";
    public static final String ACCOUNT_CHANGE_USERNAME = "/api/user/account/username";
    public static final String ACCOUNT_CHANGE_PASSWORD = "/api/user/account/password";
    public static final String ACCOUNT_DELETE_ACCOUNT = "/api/user/account";
    public static final String ACCOUNT_GET_LANGUAGES = "/api/user/data/languages";

    //USER-DATA
    public static final String USER_DATA_INTERNAL_GET_ACCESS_TOKEN_BY_ID = "/internal/user/authentication/{accessTokenId}";
    public static final String USER_DATA_INTERNAL_GET_USER_LANGUAGE = "/internal/user/{userId}/data/language";
    public static final String USER_DATAINTERNAL_GET_LAST_VISITED_PAGE = "/internal/user/authentication/last-visited-page/{userId}";
    public static final String USER_DATAINTERNAL_USER_GET_USERNAME = "/internal/user/{userId}/data/name";
    public static final String USER_DATA_GET_USER_ROLES = "/api/user/data/roles";
    public static final String USER_DATA_ADD_ROLE = "/api/user/data/roles";
    public static final String USER_DATA_REMOVE_ROLE = "/api/user/data/roles";
    public static final String USER_DATA_DISABLE_ROLE = "/api/user/data/roles/{role}";
    public static final String USER_DATA_ENABLE_ROLE = "/api/user/data/roles/{role}";
    public static final String USER_DATA_GET_DISABLED_ROLES = "/api/user/data/roles/disabled";

    public static final String REGISTER_PROCESSOR = "/platform/event-gateway";
    public static final String HEARTBEAT = "/platform/event-gateway/{serviceName}";
    public static final String SEND_EVENT = "/internal/event-gateway";

    //MODULES
    public static final String MODULES_GET_MODULES_OF_USER = "/api/modules";
    public static final String MODULES_SET_FAVORITE = "/api/modules/{module}/favorite";

    //NOTEBOOK
    public static final String NOTEBOOK_GET_CATEGORY_TREE = "/api/notebook/category";
    public static final String NOTEBOOK_CREATE_CATEGORY = "/api/notebook/category";
    public static final String NOTEBOOK_CREATE_TEXT = "/api/notebook/text";
    public static final String NOTEBOOK_GET_CHILDREN_OF_CATEGORY = "/api/notebook/category/children";
    public static final String NOTEBOOK_CATEGORY_CONTENT_VIEW = "/web/notebook/content/category";
    public static final String NOTEBOOK_DELETE_LIST_ITEM = "/api/notebook/item/{listItemId}";
    public static final String NOTEBOOK_EDIT_LIST_ITEM = "/api/notebook/item/{listItemId}";
    public static final String NOTEBOOK_GET_TEXT = "/api/notebook/text/{listItemId}";
    public static final String NOTEBOOK_EDIT_TEXT = "/api/notebook/text/{listItemId}";
    public static final String NOTEBOOK_CREATE_LINK = "/api/notebook/link";
    public static final String NOTEBOOK_CREATE_CHECKLIST_ITEM = "/api/notebook/checklist";
    public static final String NOTEBOOK_EDIT_CHECKLIST_ITEM = "/api/notebook/checklist/{listItemId}";
    public static final String NOTEBOOK_GET_CHECKLIST_ITEM = "/api/notebook/checklist/{listItemId}";
    public static final String NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS = "/api/notebook/checklist/item/{checklistItemId}/status";
    public static final String NOTEBOOK_CREATE_TABLE = "/api/notebook/table";
    public static final String NOTEBOOK_EDIT_TABLE = "/api/notebook/table/{listItemId}";
    public static final String NOTEBOOK_GET_TABLE = "/api/notebook/table/{listItemId}";
    public static final String NOTEBOOK_CLONE_LIST_ITEM = "/api/notebook/{listItemId}/clone";
    public static final String NOTEBOOK_CREATE_CHECKLIST_TABLE = "/api/notebook/checklist-table";
    public static final String NOTEBOOK_EDIT_CHECKLIST_TABLE = "/api/notebook/checklist-table/{listItemId}";
    public static final String NOTEBOOK_GET_CHECKLIST_TABLE = "/api/notebook/checklist-table/{listItemId}";
    public static final String NOTEBOOK_UPDATE_CHECKLIST_TABLE_ROW_STATUS = "/api/notebook/checklist-table/{listItemId}/{rowIndex}";
    public static final String NOTEBOOK_CONVERT_TABLE_TO_CHECKLIST_TABLE = "/api/notebook/table/{listItemId}/convert-to-checklist-table";
    public static final String NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST = "/api/notebook/checklist/{listItemId}/checked";
    public static final String NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST_TABLE = "/api/notebook/checklist-table/{listItemId}/checked";
    public static final String NOTEBOOK_ORDER_CHECKLIST_ITEMS = "/api/notebook/checklist/{listItemId}/order";

    //UTILS
    public static final String UTILS_LOG_FORMATTER_GET_VISIBILITY = "/api/utils/log-formatter/visibility";
    public static final String UTILS_LOG_FORMATTER_SET_VISIBILITY = "/api/utils/log-formatter/visibility";

    //SKYXPLORE-DATA
    public static final String SKYXPLORE_INTERNAL_IS_CHARACTER_EXISTS = "/internal/skyxplore/character/exists";
    public static final String SKYXPLORE_INTERNAL_GET_CHARACTER_BY_USER_ID = "/allowed-internal/skyxplore/character/{userId}";
    public static final String SKYXPLORE_INTERNAL_SAVE_GAME_DATA = "/allowed-internal/skyxplore/game/data";
    public static final String SKYXPLORE_CREATE_OR_UPDATE_CHARACTER = "/api/skyxplore/character";
    public static final String SKYXPLORE_SEARCH_FOR_FRIENDS = "/api/skyxplore/friend/candidate";
    public static final String SKYXPLORE_ADD_FRIEND = "/api/skyxplore/friend/request";
    public static final String SKYXPLORE_GET_SENT_FRIEND_REQUEST = "/api/skyxplore/friend/request/sent";
    public static final String SKYXPLORE_GET_INCOMING_FRIEND_REQUEST = "/api/skyxplore/friend/request/incoming";
    public static final String SKYXPLORE_CANCEL_FRIEND_REQUEST = "/api/skyxplore/friend/request/{friendRequestId}";
    public static final String SKYXPLORE_ACCEPT_FRIEND_REQUEST = "/api/skyxplore/friend/request/{friendRequestId}";
    public static final String SKYXPLORE_GET_FRIENDS = "/api/skyxplore/friend";
    public static final String SKYXPLORE_REMOVE_FRIEND = "/api/skyxplore/friend/{friendshipId}";
    public static final String SKYXPLORE_GET_CHARACTER = "/api/skyxplore/character";
    public static final String SKYXPLORE_GET_ITEM_DATA = "/api/skyxplore/data/{dataId}";

    //SKYXPLORE-LOBBY
    public static final String SKYXPLORE_INTERNAL_USER_JOINED_TO_LOBBY = "/internal/lobby/{userId}";
    public static final String SKYXPLORE_INTERNAL_USER_LEFT_LOBBY = "/internal/lobby/{userId}";
    public static final String SKYXPLORE_INTERNAL_LOBBY_PROCESS_WEB_SOCKET_EVENTS = "/web-socket-event/skyxplore/lobby/{userId}";
    public static final String SKYXPLORE_INTERNAL_LOBBY_VIEW_FOR_PAGE = "/internal/skyxplore/lobby/page";
    public static final String SKYXPLORE_INTERNAL_LOBBY_PLAYER_ONLINE = "/web-socket-event/skyxplore/lobby/online/{userId}";
    public static final String SKYXPLORE_INTERNAL_LOBBY_PLAYER_OFFLINE = "/web-socket-event/skyxplore/lobby/online/{userId}";

    public static final String SKYXPLORE_EXIT_FROM_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_INVITE_TO_LOBBY = "/api/skyxplore/lobby/invite/{friendId}";
    public static final String SKYXPLORE_LOBBY_ACCEPT_INVITATION = "/api/skyxplore/lobby/join/{invitorId}";
    public static final String SKYXPLORE_LOBBY_GET_MEMBERS = "/api/skyxplore/lobby/members";
    public static final String SKYXPLORE_CREATE_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_LOBBY_GET_GAME_SETTINGS = "/api/skyxplore/lobby/settings";
    public static final String SKYXPLORE_START_GAME = "/api/skyxplore/lobby/start";
    public static final String SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS = "/api/skyxplore/friends/active";

    //SKYXPLORE-GAME
    public static final String SKYXPLORE_INTERNAL_GAME_PROCESS_WEB_SOCKET_EVENTS = "/web-socket-event/skyxplore/game/{userId}";

    public static final String SKYXPLORE_INTERNAL_CREATE_GAME = "/internal/skyxplore/game";
    public static final String SKYXPLORE_INTERNAL_USER_JOINED_TO_GAME = "/internal/skyxplore/game/{userId}";
    public static final String SKYXPLORE_INTERNAL_USER_LEFT_GAME = "/internal/skyxplore/game/{userId}";
    public static final String SKYXPLORE_INTERNAL_IS_USER_IN_GAME = "/internal/skyxplore/game";

    public static final String SKYXPLORE_GAME_MAP = "/api/skyxplore/game/universe";
    public static final String SKYXPLORE_GAME_GET_PLAYERS = "/api/skyxplore/game/player";
    public static final String SKYXPLORE_GAME_CREATE_CHAT_ROOM = "/api/skyxplore/game/chat/room";
    public static final String SKYXPLORE_GAME_LEAVE_CHAT_ROOM = "/api/skyxplore/game/chat/room/{roomId}";
    public static final String SKYXPLORE_GET_SOLAR_SYSTEM = "/api/skyxplore/game/solar-system/{solarSystemId}";
    public static final String SKYXPLORE_PLANET_GET_SURFACE = "/api/skyxplore/game/planet/{planetId}/surface";
    public static final String SKYXPLORE_PLANET_GET_STORAGE = "/api/skyxplore/game/planet/{planetId}/storage";
    public static final String SKYXPLORE_PLANET_GET_POPULATION_OVERVIEW = "/api/skyxplore/game/planet/{planetId}/population";
    public static final String SKYXPLORE_PLANET_GET_BUILDING_OVERVIEW = "/api/skyxplore/game/planet/{planetId}/building";
    public static final String SKYXPLORE_PLANET_GET_PRIORITIES = "/api/skyxplore/game/planet/{planetId}/priority";
    public static final String SKYXPLORE_PLANET_GET_STORAGE_SETTINGS = "/api/skyxplore/game/planet/{planetId}/storage-settings";
    public static final String SKYXPLORE_PLANET_CREATE_STORAGE_SETTING = "/api/skyxplore/game/planet/{planetId}/storage-settings";
    public static final String SKYXPLORE_PLANET_DELETE_STORAGE_SETTING = "/api/skyxplore/game/planet/{planetId}/storage-settings/{storageSettingId}";
    public static final String SKYXPLORE_PLANET_EDIT_STORAGE_SETTING = "/api/skyxplore/game/planet/{planetId}/storage-settings";
    public static final String SKYXPLORE_PLANET_GET_POPULATION = "/api/skyxplore/game/planet/{planetId}/citizen";
    public static final String SKYXPLORE_PLANET_RENAME_CITIZEN = "/api/skyxplore/game/planet/{planetId}/citizen/{citizenId}/rename";
    public static final String SKYXPLORE_PLANET_UPDATE_PRIORITY = "/api/skyxplore/game/planet/{planetId}/priority/{priorityType}";
}
