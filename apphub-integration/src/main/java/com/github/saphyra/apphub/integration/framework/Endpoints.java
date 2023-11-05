package com.github.saphyra.apphub.integration.framework;

public class Endpoints {
    //PAGES
    public static final String INDEX_PAGE = "/web";
    public static final String ACCOUNT_PAGE = "/web/user/account";
    public static final String MODULES_PAGE = "/web/modules";
    public static final String CALENDAR_PAGE = "/web/calendar";
    public static final String ADMIN_PANEL_ROLE_MANAGEMENT_PAGE = "/web/admin-panel/role-management";
    public static final String ADMIN_PANEL_DISABLED_ROLE_MANAGEMENT_PAGE = "/web/admin-panel/disabled-role-management";
    public static final String ADMIN_PANEL_MEMORY_MONITORING_PAGE = "/web/admin-panel/memory-monitoring";
    public static final String ADMIN_PANEL_MIGRATION_TASKS_PAGE = "/web/admin-panel/migration-tasks";
    public static final String ADMIN_PANEL_BAN_PAGE = "/web/admin-panel/ban";
    public static final String ERROR_PAGE = "/web/error";
    public static final String NOTEBOOK_PAGE = "/web/notebook";
    public static final String SKYXPLORE_MAIN_MENU_PAGE = "/web/skyxplore";
    public static final String SKYXPLORE_CHARACTER_PAGE = "/web/skyxplore/character";
    public static final String SKYXPLORE_LOBBY_PAGE = "/web/skyxplore/lobby";
    public static final String SKYXPLORE_GAME_PAGE = "/web/skyxplore/game";
    public static final String COMMUNITY_PAGE = "/web/community";
    public static final String NOTEBOOK_NEW_PAGE = "/web/notebook/new/{parent}";
    public static final String NOTEBOOK_NEW_LIST_ITEM_PAGE = "/web/notebook/new/{listItemType}/"; //{parent}
    public static final String NOTEBOOK_EDIT_LIST_ITEM_PAGE = "/web/notebook/edit";

    //Training
    public static final String TRAINING_HTML_PAGE = "/web/training/html/001_introduction";
    public static final String TRAINING_CSS_PAGE = "/web/training/css/001_introduction";
    public static final String TRAINING_BASICS_OF_PROGRAMMING_PAGE = "/web/training/basics_of_programming/001_introduction";
    public static final String TRAINING_JAVASCRIPT_PAGE = "/web/training/javascript/001_introduction";

    //CONNECTION SUBSCRIPTIONS
    public static final String WS_CONNECTION_ADMIN_PANEL_MEMORY_MONITORING = "/api/ws/admin-panel/monitoring/memory";
    public static final String WS_CONNECTION_SKYXPLORE_MAIN_MENU = "/api/ws/skyxplore-data/main-menu";
    public static final String WS_CONNECTION_SKYXPLORE_LOBBY = "/api/ws/skyxplore-lobby/lobby";
    public static final String WS_CONNECTION_SKYXPLORE_LOBBY_INVITATION = "/api/ws/skyxplore-lobby/invitation";
    public static final String WS_CONNECTION_SKYXPLORE_GAME = "/api/ws/skyxplore-game/game";

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

    //SETTINGS
    public static final String GET_USER_SETTINGS = "/api/user/settings/{category}";
    public static final String SET_USER_SETTINGS = "/api/user/settings";

    //BAN USER
    public static final String ACCOUNT_BAN_USER = "/api/user/ban";
    public static final String ACCOUNT_REMOVE_BAN = "/api/user/ban/{banId}";
    public static final String ACCOUNT_GET_BANS = "/api/user/ban/{userId}";
    public static final String ACCOUNT_MARK_FOR_DELETION = "/api/user/{userId}/mark-for-deletion";
    public static final String ACCOUNT_UNMARK_FOR_DELETION = "/api/user/{userId}/mark-for-deletion";

    public static final String USER_DATA_GET_USER_ROLES = "/api/user/data/roles";
    public static final String USER_DATA_ADD_ROLE = "/api/user/data/roles";
    public static final String USER_DATA_REMOVE_ROLE = "/api/user/data/roles";
    public static final String USER_DATA_DISABLE_ROLE = "/api/user/data/roles/{role}";
    public static final String USER_DATA_GET_DISABLED_ROLES = "/api/user/data/roles/disabled";

    //MIGRATION
    public static final String ADMIN_PANEL_MIGRATION_GET_TASKS = "/api/admin-panel/migration";
    public static final String ADMIN_PANEL_MIGRATION_TRIGGER_TASK = "/api/admin-panel/migration/{event}";
    public static final String ADMIN_PANEL_MIGRATION_DELETE_TASK = "/api/admin-panel/migration/{event}";

    //MODULES
    public static final String MODULES_GET_MODULES_OF_USER = "/api/modules";
    public static final String MODULES_SET_FAVORITE = "/api/modules/{module}/favorite";

    //NOTEBOOK
    public static final String NOTEBOOK_GET_CATEGORY_TREE = "/api/notebook/category/tree";
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
    public static final String NOTEBOOK_UPDATE_CHECKLIST_ITEM_CONTENT = "/api/notebook/checklist/item/{checklistItemId}/content";
    public static final String NOTEBOOK_CREATE_TABLE = "/api/notebook/table";
    public static final String NOTEBOOK_EDIT_TABLE = "/api/notebook/table/{listItemId}";
    public static final String NOTEBOOK_GET_TABLE = "/api/notebook/table/{listItemId}";
    public static final String NOTEBOOK_CLONE_LIST_ITEM = "/api/notebook/{listItemId}/clone";
    public static final String NOTEBOOK_UPDATE_CHECKLIST_TABLE_ROW_STATUS = "/api/notebook/checklist-table/{rowId}/status";
    public static final String NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST = "/api/notebook/checklist/{listItemId}/checked";
    public static final String NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST_TABLE = "/api/notebook/checklist-table/{listItemId}/checked";
    public static final String NOTEBOOK_ORDER_CHECKLIST_ITEMS = "/api/notebook/checklist/{listItemId}/order";
    public static final String NOTEBOOK_SEARCH = "/api/notebook/item/search";
    public static final String NOTEBOOK_ARCHIVE_LIST_ITEM = "/api/notebook/item/{listItemId}/archive";
    public static final String NOTEBOOK_CREATE_ONLY_TITLE = "/api/notebook/only-title";
    public static final String NOTEBOOK_DELETE_CHECKLIST_ITEM = "/api/notebook/checklist/item/{checklistItemId}";

    //UTILS
    public static final String UTILS_LOG_FORMATTER_GET_VISIBILITY = "/api/utils/log-formatter/visibility";
    public static final String UTILS_LOG_FORMATTER_SET_VISIBILITY = "/api/utils/log-formatter/visibility";

    //SKYXPLORE-DATA
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
    public static final String SKYXPLORE_GET_ITEM_DATA = "/api/skyxplore/data/data/{dataId}";

    //SKYXPLORE-LOBBY
    public static final String SKYXPLORE_LOBBY_IS_IN_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_EXIT_FROM_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_INVITE_TO_LOBBY = "/api/skyxplore/lobby/invite/{friendId}";
    public static final String SKYXPLORE_LOBBY_ACCEPT_INVITATION = "/api/skyxplore/lobby/join/{invitorId}";
    public static final String SKYXPLORE_LOBBY_GET_MEMBERS = "/api/skyxplore/lobby/members";
    public static final String SKYXPLORE_CREATE_LOBBY = "/api/skyxplore/lobby";
    public static final String SKYXPLORE_LOBBY_GET_GAME_SETTINGS = "/api/skyxplore/lobby/settings";
    public static final String SKYXPLORE_START_GAME = "/api/skyxplore/lobby/start";
    public static final String SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS = "/api/skyxplore/lobby/friends/active";
    public static final String SKYXPLORE_LOBBY_LOAD_GAME = "/api/skyxplore/lobby/load-game/{gameId}";
    public static final String SKYXPLORE_LOBBY_CREATE_OR_MODIFY_AI = "/api/skyxplore/lobby/ai";
    public static final String SKYXPLORE_LOBBY_GET_AIS = "/api/skyxplore/lobby/ai";
    public static final String SKYXPLORE_LOBBY_REMOVE_AI = "/api/skyxplore/lobby/ai/{userId}";
    public static final String SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_PLAYER = "/api/skyxplore/lobby/alliance/player/{userId}";
    public static final String SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_AI = "/api/skyxplore/lobby/alliance/ai/{userId}";
    public static final String SKYXPLORE_LOBBY_EDIT_SETTINGS = "/api/skyxplore/lobby/settings";

    //SKYXPLORE-GAME
    public static final String SKYXPLORE_GAME_PAUSE = "/api/skyxplore/game/pause";
    public static final String SKYXPLORE_GAME_MAP = "/api/skyxplore/game/universe";
    public static final String SKYXPLORE_GAME_GET_PLAYERS = "/api/skyxplore/game/player";
    public static final String SKYXPLORE_GAME_CREATE_CHAT_ROOM = "/api/skyxplore/game/chat/room";
    public static final String SKYXPLORE_GAME_LEAVE_CHAT_ROOM = "/api/skyxplore/game/chat/room/{roomId}";
    public static final String SKYXPLORE_GET_SOLAR_SYSTEM = "/api/skyxplore/game/solar-system/{solarSystemId}";
    public static final String SKYXPLORE_PLANET_GET_SURFACE = "/api/skyxplore/game/planet/{planetId}/surface";
    public static final String SKYXPLORE_PLANET_GET_STORAGE = "/api/skyxplore/game/planet/{planetId}/storage";
    public static final String SKYXPLORE_PLANET_GET_PRIORITIES = "/api/skyxplore/game/planet/{planetId}/priority";
    public static final String SKYXPLORE_PLANET_GET_STORAGE_SETTINGS = "/api/skyxplore/game/planet/{planetId}/storage-settings";
    public static final String SKYXPLORE_PLANET_CREATE_STORAGE_SETTING = "/api/skyxplore/game/planet/{planetId}/storage-settings";
    public static final String SKYXPLORE_PLANET_DELETE_STORAGE_SETTING = "/api/skyxplore/game/storage-settings/{storageSettingId}";
    public static final String SKYXPLORE_PLANET_EDIT_STORAGE_SETTING = "/api/skyxplore/game/storage-settings";
    public static final String SKYXPLORE_PLANET_GET_POPULATION = "/api/skyxplore/game/planet/{planetId}/citizen";
    public static final String SKYXPLORE_PLANET_RENAME_CITIZEN = "/api/skyxplore/game/citizen/{citizenId}/rename";
    public static final String SKYXPLORE_PLANET_UPDATE_PRIORITY = "/api/skyxplore/game/planet/{planetId}/priority/{priorityType}";
    public static final String SKYXPLORE_PLANET_RENAME = "/api/skyxplore/game/planet/{planetId}/name";
    public static final String SKYXPLORE_SOLAR_SYSTEM_RENAME = "/api/skyxplore/game/solar-system/{solarSystemId}/name";

    public static final String SKYXPLORE_BUILDING_CONSTRUCT_NEW = "/api/skyxplore/game/building/{planetId}/{surfaceId}";
    public static final String SKYXPLORE_BUILDING_UPGRADE = "/api/skyxplore/game/building/{planetId}/{buildingId}";
    public static final String SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION = "/api/skyxplore/game/building/{planetId}/{buildingId}";
    public static final String SKYXPLORE_BUILDING_DECONSTRUCT = "/api/skyxplore/game/building/{planetId}/{buildingId}/deconstruct";
    public static final String SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION = "/api/skyxplore/game/building/{planetId}/{buildingId}/deconstruct";

    public static final String SKYXPLORE_GAME_TERRAFORM_SURFACE = "/api/skyxplore/game/surface/{planetId}/{surfaceId}/terraform";
    public static final String SKYXPLORE_GAME_CANCEL_TERRAFORMATION = "/api/skyxplore/game/surface/{planetId}/{surfaceId}/terraform";

    public static final String SKYXPLORE_PLANET_GET_QUEUE = "/api/skyxplore/game/{planetId}/queue";
    public static final String SKYXPLORE_PLANET_SET_QUEUE_ITEM_PRIORITY = "/api/skyxplore/game/{planetId}/{type}/{itemId}/priority";
    public static final String SKYXPLORE_PLANET_CANCEL_QUEUE_ITEM = "/api/skyxplore/game/{planetId}/{type}/{itemId}";

    //COMMUNITY
    public static final String COMMUNITY_BLACKLIST_SEARCH = "/api/community/blacklist/search";
    public static final String COMMUNITY_GET_BLACKLIST = "/api/community/blacklist";
    public static final String COMMUNITY_CREATE_BLACKLIST = "/api/community/blacklist";
    public static final String COMMUNITY_DELETE_BLACKLIST = "/api/community/blacklist/{blacklistId}";
    public static final String COMMUNITY_GET_SENT_FRIEND_REQUESTS = "/api/community/friend-request/sent";
    public static final String COMMUNITY_GET_RECEIVED_FRIEND_REQUESTS = "/api/community/friend-request/received";
    public static final String COMMUNITY_FRIEND_REQUEST_SEARCH = "/api/community/friend-request/search";
    public static final String COMMUNITY_FRIEND_REQUEST_CREATE = "/api/community/friend-request";
    public static final String COMMUNITY_FRIEND_REQUEST_DELETE = "/api/community/friend-request/{friendRequestId}";
    public static final String COMMUNITY_FRIEND_REQUEST_ACCEPT = "/api/community/friend-request/{friendRequestId}";
    public static final String COMMUNITY_GET_FRIENDS = "/api/community/friendship";
    public static final String COMMUNITY_DELETE_FRIENDSHIP = "/api/community/friendship/{friendshipId}";
    public static final String COMMUNITY_GET_GROUPS = "/api/community/group";
    public static final String COMMUNITY_GROUP_CREATE = "/api/community/group";
    public static final String COMMUNITY_GROUP_RENAME = "/api/community/group/{groupId}/name";
    public static final String COMMUNITY_GROUP_GET_MEMBERS = "/api/community/group/{groupId}/member";
    public static final String COMMUNITY_GROUP_SEARCH_MEMBER_CANDIDATES = "/api/community/group/{groupId}/member/search";
    public static final String COMMUNITY_GROUP_CREATE_MEMBER = "/api/community/group/{groupId}/member";
    public static final String COMMUNITY_GROUP_CHANGE_INVITATION_TYPE = "/api/community/group/{groupId}/invitation-type";
    public static final String COMMUNITY_GROUP_DELETE_MEMBER = "/api/community/group/{groupId}/member/{groupMemberId}";
    public static final String COMMUNITY_GROUP_MEMBER_ROLES = "/api/community/group/{groupId}/member/{groupMemberId}";
    public static final String COMMUNITY_GROUP_DELETE = "/api/community/group/{groupId}";
    public static final String COMMUNITY_GROUP_CHANGE_OWNER = "/api/community/group/{groupId}/owner";

    //CALENDAR
    public static final String CALENDAR_GET_CALENDAR = "/api/calendar/calendar";
    public static final String CALENDAR_CREATE_EVENT = "/api/calendar/event";
    public static final String CALENDAR_OCCURRENCE_EDIT = "/api/calendar/occurrence/{occurrenceId}/edit";
    public static final String CALENDAR_OCCURRENCE_DONE = "/api/calendar/occurrence/{occurrenceId}/done";
    public static final String CALENDAR_OCCURRENCE_DEFAULT = "/api/calendar/occurrence/{occurrenceId}/default";
    public static final String CALENDAR_OCCURRENCE_SNOOZED = "/api/calendar/occurrence/{occurrenceId}/snoozed";
    public static final String CALENDAR_EVENT_DELETE = "/api/calendar/event/{eventId}";
    public static final String CALENDAR_SEARCH = "/api/calendar/search";

    //INTEGRATION SERVER
    public static final String INTEGRATION_SERVER_CREATE_TEST_RUN = "/test-run";
    public static final String INTEGRATION_SERVER_REPORT_TEST_CASE = "/test-case/{testRunId}";
    public static final String INTEGRATION_SERVER_GET_AVERAGE_RUN_TIME = "/test-case/run-time/average";
    public static final String INTEGRATION_SERVER_FINISH_TEST_RUN = "/test-run/{testRunId}";
}
