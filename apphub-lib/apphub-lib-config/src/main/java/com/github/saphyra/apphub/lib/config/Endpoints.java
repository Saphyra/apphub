package com.github.saphyra.apphub.lib.config;

public class Endpoints {
    //PAGES
    public static final String INDEX_PAGE = "/web";
    public static final String ACCOUNT_PAGE = "/web/user/account";
    public static final String MODULES_PAGE = "/web/modules";
    public static final String ADMIN_PANEL_ROLE_MANAGEMENT_PAGE = "/web/admin-panel/role-management";
    public static final String ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE = "/web/admin-panel/disabled-role-management";
    public static final String ADMIN_PANEL_ERROR_REPORT_PAGE = "/web/admin-panel/error-report";
    public static final String ADMIN_PANEL_BAN_PAGE = "/web/admin-panel/ban";
    public static final String ADMIN_PANEL_ROLES_FOR_ALL_PAGE = "/web/admin-panel/roles-for-all";
    public static final String ERROR_PAGE = "/web/error";
    public static final String NOTEBOOK_PAGE = "/web/notebook";
    public static final String SKYXPLORE_MAIN_MENU_PAGE = "/web/skyxplore";
    public static final String SKYXPLORE_CHARACTER_PAGE = "/web/skyxplore/character";
    public static final String SKYXPLORE_LOBBY_PAGE = "/web/skyxplore/lobby";
    public static final String SKYXPLORE_GAME_PAGE = "/web/skyxplore/game";
    public static final String UTILS_JSON_FORMATTER_PAGE = "/web/utils/json-formatter";
    public static final String UTILS_LOG_FORMATTER_PAGE = "/web/utils/log-formatter";
    public static final String UTILS_BASE64_ENCODER_PAGE = "/web/utils/base64";

    //Training
    public static final String TRAINING_BOOK_PAGE = "/web/training/{book}/{chapter}";
    public static final String TRAINING_SAMPLE_PAGE = "/web/training/{book}/sample/{sample}";
    public static final String TRAINING_SAMPLE_ENDPOINT = "/web/training/sample";

    //PLATFORM
    public static final String HEALTH = "/platform/health";
    public static final String TRANSLATE_ERROR_CODE = "/internal/localization/error-code";
    public static final String TRANSLATE_KEY = "/internal/localization/key";

    public static final String REGISTER_PROCESSOR = "/platform/event-gateway";
    public static final String HEARTBEAT = "/platform/event-gateway/{serviceName}";
    public static final String SEND_EVENT = "/internal/event-gateway";

    //EVENTS
    public static final String EVENT_DELETE_EXPIRED_ACCESS_TOKENS = "/event/delete-expired-access-tokens";
    public static final String EVENT_REFRESH_ACCESS_TOKEN_EXPIRATION = "/event/refresh-access-token-expiration";
    public static final String EVENT_DELETE_ACCOUNT = "/event/delete-account";
    public static final String EVENT_SKYXPLORE_LOBBY_CLEANUP = "/event/skyxplore/lobby/cleanup";
    public static final String EVENT_SKYXPLORE_GAME_CLEANUP = "/event/skyxplore/game/cleanup";
    public static final String EVENT_MESSAGE_SENDER_PING_REQUEST = "/event/message-sender/ping-request";
    public static final String EVENT_MESSAGE_SENDER_CONNECTION_CLEANUP = "/event/message-sender/connection-cleanup";
    public static final String EVENT_TRIGGER_ACCOUNT_DELETION = "/event/user-data/trigger-account-deletion";
    public static final String EVENT_TRIGGER_REVOKE_EXPIRED_BANS = "/event/user-data/trigger-remove-expired-bans";

    //WEB-SOCKET-MESSAGING
    public static final String WEB_SOCKET_SEND_MESSAGE = "/internal/message/{group}";

    //CONNECTION SUBSCRIPTIONS
    public static final String WS_CONNECTION_SKYXPLORE_MAIN_MENU = "/api/message-sender/skyxplore/main-menu";
    public static final String WS_CONNECTION_SKYXPLORE_LOBBY = "/api/message-sender/skyxplore/lobby";
    public static final String WS_CONNECTION_SKYXPLORE_GAME = "/api/message-sender/skyxplore/game";

    //ERROR REPORTING
    public static final String ADMIN_PANEL_INTERNAL_REPORT_ERROR = "/internal/admin-panel/report-error";

    public static final String ADMIN_PANEL_GET_ERROR_REPORTS = "/api/admin-panel/error-report";
    public static final String ADMIN_PANEL_GET_ERROR_REPORT = "/api/admin-panel/error-report/{id}";
    public static final String ADMIN_PANEL_DELETE_ERROR_REPORTS = "/api/admin-panel/error-report";
    public static final String ADMIN_PANEL_MARK_ERROR_REPORTS = "/api/admin-panel/error-report/mark/{status}";
    public static final String ADMIN_PANEL_DELETE_READ_ERROR_REPORTS = "/api/admin-panel/error-report/read";

    //LOGIN
    public static final String LOGIN = "/api/user/authentication/login";
    public static final String LOGOUT = "/api/user/authentication/logout";
    public static final String CHECK_SESSION = "/api/user/authentication/session";
    public static final String EXTEND_SESSION = "/api/user/authentication/session";

    //ACCOUNT
    public static final String ACCOUNT_REGISTER = "/api/user";
    public static final String ACCOUNT_CHANGE_LANGUAGE = "/api/user/account/language";
    public static final String ACCOUNT_CHANGE_EMAIL = "/api/user/account/email";
    public static final String ACCOUNT_CHANGE_USERNAME = "/api/user/account/username";
    public static final String ACCOUNT_CHANGE_PASSWORD = "/api/user/account/password";
    public static final String ACCOUNT_DELETE_ACCOUNT = "/api/user/account";
    public static final String ACCOUNT_GET_LANGUAGES = "/api/user/data/languages";
    public static final String USER_DATA_SEARCH_ACCOUNT = "/api/user/accounts";

    //BAN USER
    public static final String ACCOUNT_BAN_USER = "/api/user/ban";
    public static final String ACCOUNT_REMOVE_BAN = "/api/user/ban/{banId}";
    public static final String ACCOUNT_GET_BANS = "/api/user/ban/{userId}";

    //USER-DATA
    public static final String USER_DATA_INTERNAL_GET_ACCESS_TOKEN_BY_ID = "/internal/user/authentication/{accessTokenId}";
    public static final String USER_DATA_INTERNAL_GET_USER_LANGUAGE = "/internal/user/{userId}/data/language";
    public static final String USER_DATA_INTERNAL_USER_GET_USERNAME = "/internal/user/{userId}/data/name";

    public static final String USER_DATA_GET_USER_ROLES = "/api/user/data/roles";
    public static final String USER_DATA_ADD_ROLE = "/api/user/data/roles";
    public static final String USER_DATA_REMOVE_ROLE = "/api/user/data/roles";
    public static final String USER_DATA_DISABLE_ROLE = "/api/user/data/roles/{role}";
    public static final String USER_DATA_ENABLE_ROLE = "/api/user/data/roles/{role}";
    public static final String USER_DATA_GET_DISABLED_ROLES = "/api/user/data/roles/disabled";
    public static final String USER_DATA_ADD_ROLE_TO_ALL = "/api/user/data/roles/all/{role}";
    public static final String USER_DATA_REMOVE_ROLE_FROM_ALL = "/api/user/data/roles/all/{role}";
    public static final String USER_DATA_ROLES_FOR_ALL_RESTRICTED = "/api/user/data/roles/restricted";

    //MODULES
    public static final String MODULES_GET_MODULES_OF_USER = "/api/modules";
    public static final String MODULES_SET_FAVORITE = "/api/modules/{module}/favorite";

    //NOTEBOOK
    public static final String NOTEBOOK_GET_CATEGORY_TREE = "/api/notebook/category";
    public static final String NOTEBOOK_CREATE_CATEGORY = "/api/notebook/category";
    public static final String NOTEBOOK_CREATE_TEXT = "/api/notebook/text";
    public static final String NOTEBOOK_GET_CHILDREN_OF_CATEGORY = "/api/notebook/category/children";
    public static final String NOTEBOOK_DELETE_LIST_ITEM = "/api/notebook/item/{listItemId}";
    public static final String NOTEBOOK_EDIT_LIST_ITEM = "/api/notebook/item/{listItemId}";
    public static final String NOTEBOOK_PIN_LIST_ITEM = "/api/notebook/item/{listItemId}/pin";
    public static final String NOTEBOOK_GET_PINNED_ITEMS = "/api/notebook/item/pinned";
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
    public static final String NOTEBOOK_SEARCH = "/api/notebook/item/search";

    //UTILS
    public static final String UTILS_LOG_FORMATTER_GET_VISIBILITY = "/api/utils/log-formatter/visibility";
    public static final String UTILS_LOG_FORMATTER_SET_VISIBILITY = "/api/utils/log-formatter/visibility";

    //SKYXPLORE-DATA
    public static final String SKYXPLORE_INTERNAL_GET_CHARACTER_BY_USER_ID = "/allowed-internal/skyxplore/data/character/{userId}";
    public static final String SKYXPLORE_INTERNAL_SAVE_GAME_DATA = "/allowed-internal/skyxplore/data/game/data";
    public static final String SKYXPLORE_INTERNAL_LOAD_GAME_ITEM = "/internal/skyxplore/data/game/data/item/{id}/{type}";
    public static final String SKYXPLORE_INTERNAL_DELETE_GAME_ITEM = "/internal/skyxplore/data/game-item/{id}/{type}";
    public static final String SKYXPLORE_INTERNAL_LOAD_GAME_ITEM_CHILDREN = "/internal/skyxplore/data/game-item/children/{parent}/{type}";

    public static final String SKYXPLORE_CREATE_OR_UPDATE_CHARACTER = "/api/skyxplore/data/character";
    public static final String SKYXPLORE_GET_GAMES = "/api/skyxplore/data/saved-game";
    public static final String SKYXPLORE_DELETE_GAME = "/api/skyxplore/data/saved-game/{gameId}";
    public static final String SKYXPLORE_SEARCH_FOR_FRIENDS = "/api/skyxplore/data/friend/candidate";
    public static final String SKYXPLORE_ADD_FRIEND = "/api/skyxplore/data/friend/request";
    public static final String SKYXPLORE_GET_SENT_FRIEND_REQUEST = "/api/skyxplore/data/friend/request/sent";
    public static final String SKYXPLORE_GET_INCOMING_FRIEND_REQUEST = "/api/skyxplore/data/friend/request/incoming";
    public static final String SKYXPLORE_CANCEL_FRIEND_REQUEST = "/api/skyxplore/data/friend/request/{friendRequestId}";
    public static final String SKYXPLORE_ACCEPT_FRIEND_REQUEST = "/api/skyxplore/data/friend/request/{friendRequestId}";
    public static final String SKYXPLORE_GET_FRIENDS = "/api/skyxplore/data/friend";
    public static final String SKYXPLORE_REMOVE_FRIEND = "/api/skyxplore/data/friend/{friendshipId}";
    public static final String SKYXPLORE_GET_CHARACTER = "/api/skyxplore/data/character";
    public static final String SKYXPLORE_GET_ITEM_DATA = "/api/skyxplore/data/data/{dataId}";
    public static final String SKYXPLORE_DATA_AVAILABLE_BUILDINGS = "/api/skyxplore/data/data/{surfaceType}/buildings";

    //SKYXPLORE-LOBBY
    public static final String SKYXPLORE_INTERNAL_LOBBY_PROCESS_WEB_SOCKET_EVENTS = "/web-socket-event/skyxplore/lobby/{userId}";
    public static final String SKYXPLORE_INTERNAL_LOBBY_PLAYER_ONLINE = "/web-socket-event/skyxplore/lobby/online/{userId}";
    public static final String SKYXPLORE_INTERNAL_LOBBY_PLAYER_OFFLINE = "/web-socket-event/skyxplore/lobby/online/{userId}";

    public static final String SKYXPLORE_INTERNAL_USER_JOINED_TO_LOBBY = "/allowed-internal/skyxplore/lobby/{userId}";
    public static final String SKYXPLORE_INTERNAL_USER_LEFT_LOBBY = "/allowed-internal/skyxplore/lobby/{userId}";
    public static final String SKYXPLORE_INTERNAL_LOBBY_VIEW_FOR_PAGE = "/internal/skyxplore/lobby/page";


    public static final String SKYXPLORE_EXIT_FROM_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_INVITE_TO_LOBBY = "/api/skyxplore/lobby/invite/{friendId}";
    public static final String SKYXPLORE_LOBBY_ACCEPT_INVITATION = "/api/skyxplore/lobby/join/{invitorId}";
    public static final String SKYXPLORE_LOBBY_GET_MEMBERS = "/api/skyxplore/lobby/members";
    public static final String SKYXPLORE_CREATE_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_LOBBY_GET_GAME_SETTINGS = "/api/skyxplore/lobby/settings";
    public static final String SKYXPLORE_START_GAME = "/api/skyxplore/lobby/start";
    public static final String SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS = "/api/skyxplore/lobby/friends/active";
    public static final String SKYXPLORE_LOBBY_LOAD_GAME = "/api/skyxplore/lobby/load-game/{gameId}";

    //SKYXPLORE-GAME
    public static final String SKYXPLORE_INTERNAL_GAME_PROCESS_WEB_SOCKET_EVENTS = "/web-socket-event/skyxplore/game/{userId}";

    public static final String SKYXPLORE_INTERNAL_CREATE_GAME = "/internal/skyxplore/game";
    public static final String SKYXPLORE_INTERNAL_LOAD_GAME = "/internal/skyxplore/game";
    public static final String SKYXPLORE_INTERNAL_USER_JOINED_TO_GAME = "/internal/skyxplore/game/{userId}";
    public static final String SKYXPLORE_INTERNAL_USER_LEFT_GAME = "/internal/skyxplore/game/{userId}";
    public static final String SKYXPLORE_INTERNAL_IS_USER_IN_GAME = "/internal/skyxplore/game";
    public static final String SKYXPLORE_INTERNAL_GET_GAME_FOR_LOBBY_CREATION = "/internal/skyxplore/game/{gameId}/load-preview";

    public static final String SKYXPLORE_GAME_PAUSE = "/api/skyxplore/game/pause";
    public static final String SKYXPLORE_GAME_MAP = "/api/skyxplore/game/universe";
    public static final String SKYXPLORE_GAME_GET_PLAYERS = "/api/skyxplore/game/player";
    public static final String SKYXPLORE_GAME_CREATE_CHAT_ROOM = "/api/skyxplore/game/chat/room";
    public static final String SKYXPLORE_GAME_LEAVE_CHAT_ROOM = "/api/skyxplore/game/chat/room/{roomId}";
    public static final String SKYXPLORE_GET_SOLAR_SYSTEM = "/api/skyxplore/game/solar-system/{solarSystemId}";
    public static final String SKYXPLORE_PLANET_GET_SURFACE = "/api/skyxplore/game/planet/{planetId}/surface";
    public static final String SKYXPLORE_PLANET_GET_STORAGE = "/api/skyxplore/game/planet/{planetId}/storage";
    public static final String SKYXPLORE_PLANET_GET_OVERVIEW = "/api/skyxplore/game/planet/{planetId}/overview";
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
    public static final String SKYXPLORE_PLANET_RENAME = "/api/skyxplore/game/planet/{planetId}/name";
    public static final String SKYXPLORE_SOLAR_SYSTEM_RENAME = "/api/skyxplore/game/solar-system/{solarSystemId}/name";
    public static final String SKYXPLORE_EXIT_GAME = "/api/skyxplore/game";

    public static final String SKYXPLORE_BUILDING_CONSTRUCT_NEW = "/api/skyxplore/game/building/{planetId}/{surfaceId}";
    public static final String SKYXPLORE_BUILDING_UPGRADE = "/api/skyxplore/game/building/{planetId}/{buildingId}";
    public static final String SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION = "/api/skyxplore/game/building/{planetId}/{buildingId}";

    public static final String SKYXPLORE_GAME_TERRAFORM_SURFACE = "/api/skyxplore/game/surface/{planetId}/{surfaceId}/terraform";
    public static final String SKYXPLORE_GAME_CANCEL_TERRAFORMATION = "/api/skyxplore/game/surface/{planetId}/{surfaceId}/terraform";
    public static final String SKYXPLORE_DATA_TERRAFORMING_POSSIBILITIES = "/api/skyxplore/data/{surfaceType}/terraforming-possibilities";

    public static final String SKYXPLORE_PLANET_GET_QUEUE = "/api/skyxplore/game/{planetId}/queue";
    public static final String SKYXPLORE_PLANET_SET_QUEUE_ITEM_PRIORITY = "/api/skyxplore/game/{planetId}/{type}/{itemId}/priority";
    public static final String SKYXPLORE_PLANET_CANCEL_QUEUE_ITEM = "/api/skyxplore/game/{planetId}/{type}/{itemId}";
}