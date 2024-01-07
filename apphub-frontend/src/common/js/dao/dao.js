import "../collection/MapStream";
import MapStream from "../collection/MapStream";
import Constants from "../Constants";
import Utils from "../Utils";
import Stream from "../collection/Stream";
import getDefaultErrorHandler from "./DefaultErrorHandler";
import Response from "./Response";

const Endpoint = class {
    constructor(requestMethod, url) {
        this.requestMethod = requestMethod;
        this.url = url;
    }

    createRequest(body, pathVariables = {}, queryParams = {}, rawBody = false) {
        const request = new Request(
            this.requestMethod,
            this.assembleUrl(pathVariables, queryParams),
            body,
            rawBody
        );
        if (this.requestMethod !== RequestMethod.GET) {
            request.header("Content-Type", "application/json");
        }

        return request;
    }

    assembleUrl(pathVariables, queryParams) {
        pathVariables = pathVariables || {};
        queryParams = queryParams || {};

        const pathVariablesFilled = fillPathVariables(this.url, pathVariables);
        const queryParamsFilled = fillQueryParams(pathVariablesFilled, queryParams);

        return queryParamsFilled;

        function fillPathVariables(url, pathVariables) {
            let result = url;

            new MapStream(pathVariables)
                .forEach((placeholder, value) => {
                    const key = "{" + placeholder + "}";
                    result = result.replace(key, value);
                });

            return result;
        }

        function fillQueryParams(url, queryParams) {
            if (Object.keys(queryParams).length === 0) {
                return url;
            }

            const queryString = new MapStream(queryParams)
                .toList((key, value) => key + "=" + value)
                .join("&");

            return url + "?" + queryString;
        }
    }
}

const RequestMethod = {
    POST: "POST",
    GET: "GET",
    PUT: "PUT",
    DELETE: "DELETE"
}

const Request = class {
    constructor(requestMethod, url, body, rawBody = false) {
        this.requestMethod = requestMethod;
        this.url = url;
        this.body = rawBody ? body : this.processBody(body);
        this.responseConverter = (response) => {
            if (response.body !== null && response.body.length >= 2) {
                return JSON.parse(response.body);
            } else {
                return null;
            }
        }
        this.errorHandlers = [];
        this.headers = {};
    }

    header(name, value) {
        this.headers[name] = value;

        return this;
    }

    processBody(body) {
        if (body === null || body === undefined) {
            return null;
        }

        if (typeof body === "object") {
            return JSON.stringify(body);
        }
    }

    addErrorHandler = (errorHandler) => {
        this.errorHandlers.push(errorHandler);
        return this;
    }

    send() {
        const xhr = new XMLHttpRequest();
        xhr.open(this.requestMethod, this.url, true);

        new MapStream(this.headers)
            .filter((name, value) => Utils.hasValue(value))
            .forEach((name, value) => xhr.setRequestHeader(name, value));

        xhr.setRequestHeader("Cache-Control", "no-cache");
        xhr.setRequestHeader(Constants.HEADER_BROWSER_LANGUAGE, Utils.getBrowserLanguage());
        xhr.setRequestHeader(Constants.HEADER_REQUEST_TYPE_NAME, Constants.HEADER_REQUEST_TYPE_VALUE);

        return new Promise((resolve, reject) => {
            xhr.onload = () => {
                const response = new Response(xhr.status, xhr.responseText);
                if (response.status === ResponseStatus.OK) {
                    const parsedBody = this.responseConverter(response);
                    resolve(parsedBody);
                } else {
                    this.handleError(response);
                    reject();
                }
            };

            xhr.onerror = () => {
                this.handleError(new Response(xhr.status, xhr.responseText));
                reject();
            }

            xhr.send(this.body);
        });
    }

    handleError(response) {
        new Stream(this.errorHandlers)
            .filter((errorHandler) => errorHandler.canHandle(response))
            .findFirst()
            .orElse(getDefaultErrorHandler())
            .handle(response);
    }
}

export const ResponseStatus = {
    OK: 200,
    BAD_REQUEST: 400,
    UNAUTHORIZED: 401,
    FORBIDDEN: 403,
    NOT_FOUND: 404,
    METHOD_NOT_ALLOWED: 405,
    CONFLICT: 409,
    GONE: 410,
    PRECONDITION_FAILED: 412,
    LOCKED: 423,
    TOO_MANY_REQUESTS: 429,
    INTERNAL_SERVER_ERROR: 500,
    NOT_IMPLEMENTED: 501,
    GATEWAY_TIMEOUT: 504,
    CONNECTION_REFUSED: 0,
}

const Endpoints = {
    //Platform
    CHECK_SESSION: new Endpoint(RequestMethod.GET, "/api/user/authentication/session"),
    GET_OWN_USER_ID: new Endpoint(RequestMethod.GET, "/user/id"),

    //User
    USER_DATA_GET_USERNAME: new Endpoint(RequestMethod.GET, "/api/user/data/name"),

    //Index
    ACCOUNT_REGISTER: new Endpoint(RequestMethod.POST, "/api/user"),
    LOGIN: new Endpoint(RequestMethod.POST, "/api/user/authentication/login"),
    LOGOUT: new Endpoint(RequestMethod.POST, "/api/user/authentication/logout"),

    //Modules
    MODULES_GET: new Endpoint(RequestMethod.GET, "/api/modules"),
    MODULES_SET_FAVORITE: new Endpoint(RequestMethod.POST, "/api/modules/{module}/favorite"),

    //SkyXplore platform
    SKYXPLORE_PLATFORM_HAS_CHARACTER: new Endpoint(RequestMethod.GET, "/api/skyxplore/data/character/exists"),
    SKYXPLORE_GET_CHARACTER_NAME: new Endpoint(RequestMethod.GET, "/api/skyxplore/data/character/name"),
    SKYXPLORE_IS_USER_IN_GAME: new Endpoint(RequestMethod.GET, "/api/skyxplore/game"),

    //SkyXplore Data
    SKYXPLORE_CREATE_OR_UPDATE_CHARACTER: new Endpoint(RequestMethod.POST, "/api/skyxplore/data/character"),
    SKYXPLORE_GET_GAMES: new Endpoint(RequestMethod.GET, "/api/skyxplore/data/saved-game"),
    SKYXPLORE_SEARCH_FOR_FRIENDS: new Endpoint(RequestMethod.POST, "/api/skyxplore/data/friend/candidate"),
    SKYXPLORE_ADD_FRIEND: new Endpoint(RequestMethod.PUT, "/api/skyxplore/data/friend/request"),
    SKYXPLORE_GET_INCOMING_FRIEND_REQUEST: new Endpoint(RequestMethod.GET, "/api/skyxplore/data/friend/request/incoming"),
    SKYXPLORE_GET_SENT_FRIEND_REQUEST: new Endpoint(RequestMethod.GET, "/api/skyxplore/data/friend/request/sent"),
    SKYXPLORE_CANCEL_FRIEND_REQUEST: new Endpoint(RequestMethod.DELETE, "/api/skyxplore/data/friend/request/{friendRequestId}"),
    SKYXPLORE_ACCEPT_FRIEND_REQUEST: new Endpoint(RequestMethod.POST, "/api/skyxplore/data/friend/request/{friendRequestId}"),
    SKYXPLORE_GET_FRIENDS: new Endpoint(RequestMethod.GET, "/api/skyxplore/data/friend"),
    SKYXPLORE_REMOVE_FRIEND: new Endpoint(RequestMethod.DELETE, "/api/skyxplore/data/friend/{friendshipId}"),

    //SkyXplore Lobby
    SKYXPLORE_CREATE_LOBBY: new Endpoint(RequestMethod.PUT, "/api/skyxplore/lobby"),
    SKYXPLORE_LOBBY_IS_IN_LOBBY: new Endpoint(RequestMethod.GET, "/api/skyxplore/lobby"),
    SKYXPLORE_LOBBY_VIEW_FOR_PAGE: new Endpoint(RequestMethod.GET, "/api/skyxplore/lobby/page"),
    SKYXPLORE_LOBBY_GET_ALLIANCES: new Endpoint(RequestMethod.GET, "/api/skyxplore/lobby/alliances"),
    SKYXPLORE_LOBBY_GET_PLAYERS: new Endpoint(RequestMethod.GET, "/api/skyxplore/lobby/players"),
    SKYXPLORE_LOBBY_EXIT: new Endpoint(RequestMethod.DELETE, "/api/skyxplore/lobby"),
    SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_PLAYER: new Endpoint(RequestMethod.POST, "/api/skyxplore/lobby/alliance/player/{userId}"),
    SKYXPLORE_LOBBY_CHANGE_ALLIANCE_OF_AI: new Endpoint(RequestMethod.POST, "/api/skyxplore/lobby/alliance/ai/{userId}"),
    SKYXPLORE_LOBBY_GET_ACTIVE_FRIENDS: new Endpoint(RequestMethod.GET, "/api/skyxplore/lobby/friends/active"),
    SKYXPLORE_INVITE_TO_LOBBY: new Endpoint(RequestMethod.POST, "/api/skyxplore/lobby/invite/{friendId}"),
    SKYXPLORE_LOBBY_ACCEPT_INVITATION: new Endpoint(RequestMethod.POST, "/api/skyxplore/lobby/join/{invitorId}"),
    SKYXPLORE_LOBBY_GET_AIS: new Endpoint(RequestMethod.GET, "/api/skyxplore/lobby/ai"),
    SKYXPLORE_LOBBY_CREATE_OR_MODIFY_AI: new Endpoint(RequestMethod.PUT, "/api/skyxplore/lobby/ai"),
    SKYXPLORE_LOBBY_REMOVE_AI: new Endpoint(RequestMethod.DELETE, "/api/skyxplore/lobby/ai/{userId}"),
    SKYXPLORE_LOBBY_GET_SETTINGS: new Endpoint(RequestMethod.GET, "/api/skyxplore/lobby/settings"),
    SKYXPLORE_LOBBY_EDIT_SETTINGS: new Endpoint(RequestMethod.POST, "/api/skyxplore/lobby/settings"),
    SKYXPLORE_LOBBY_START_GAME: new Endpoint(RequestMethod.POST, "/api/skyxplore/lobby/start"),
    SKYXPLORE_LOBBY_LOAD_GAME: new Endpoint(RequestMethod.POST, "/api/skyxplore/lobby/load-game/{gameId}"),
    SKYXPLORE_DELETE_GAME: new Endpoint(RequestMethod.DELETE, "/api/skyxplore/data/saved-game/{gameId}"),
    SKYXPLORE_BUILDING_CONSTRUCT_NEW: new Endpoint(RequestMethod.PUT, "/api/skyxplore/game/building/{planetId}/{surfaceId}"),
    SKYXPLORE_PLANET_SET_QUEUE_ITEM_PRIORITY: new Endpoint(RequestMethod.POST, "/api/skyxplore/game/{planetId}/{type}/{itemId}/priority"),
    SKYXPLORE_PLANET_CANCEL_QUEUE_ITEM: new Endpoint(RequestMethod.DELETE, "/api/skyxplore/game/{planetId}/{type}/{itemId}"),
    SKYXPLORE_GAME_TERRAFORM_SURFACE: new Endpoint(RequestMethod.POST, "/api/skyxplore/game/surface/{planetId}/{surfaceId}/terraform"),
    SKYXPLORE_GAME_CANCEL_TERRAFORMATION: new Endpoint(RequestMethod.DELETE, "/api/skyxplore/game/surface/{planetId}/{surfaceId}/terraform"),

    //SkyXplore Game Data
    SKYXPLORE_GET_ITEM_DATA: new Endpoint(RequestMethod.GET, "/api/skyxplore/data/data/{dataId}"),
    SKYXPLORE_DATA_TERRAFORMING_POSSIBILITIES: new Endpoint(RequestMethod.GET, "/api/skyxplore/data/{surfaceType}/terraforming-possibilities"),
    SKYXPLORE_DATA_AVAILABLE_BUILDINGS: new Endpoint(RequestMethod.GET, "/api/skyxplore/data/data/{surfaceType}/buildings"),
    SKYXPLORE_DATA_CITIZEN_STATS_AND_SKILLS: new Endpoint(RequestMethod.GET, "/api/skyxplore/data/citizen/stats-and-skills"),
    SKYXPLORE_DATA_RESOURCES: new Endpoint(RequestMethod.GET, "/api/skyxplore/data/resources"),

    //SkyXplore Game
    SKYXPLORE_GAME_PAUSE: new Endpoint(RequestMethod.POST, "/api/skyxplore/game/pause"),
    SKYXPLORE_EXIT_GAME: new Endpoint(RequestMethod.DELETE, "/api/skyxplore/game"),
    SKYXPLORE_GAME_GET_CHAT_ROOMS: new Endpoint(RequestMethod.GET, "/api/skyxplore/game/chat/room"),
    SKYXPLORE_GAME_GET_PLAYERS: new Endpoint(RequestMethod.GET, "/api/skyxplore/game/player"),
    SKYXPLORE_GAME_CREATE_CHAT_ROOM: new Endpoint(RequestMethod.PUT, "/api/skyxplore/game/chat/room"),
    SKYXPLORE_GAME_LEAVE_CHAT_ROOM: new Endpoint(RequestMethod.DELETE, "/api/skyxplore/game/chat/room/{roomId}"),
    SKYXPLORE_GAME_MAP: new Endpoint(RequestMethod.GET, "/api/skyxplore/game/universe"),
    SKYXPLORE_GET_SOLAR_SYSTEM: new Endpoint(RequestMethod.GET, "/api/skyxplore/game/solar-system/{solarSystemId}"),
    SKYXPLORE_SOLAR_SYSTEM_RENAME: new Endpoint(RequestMethod.POST, "/api/skyxplore/game/solar-system/{solarSystemId}/name"),
    SKYXPLORE_PLANET_RENAME: new Endpoint(RequestMethod.POST, "/api/skyxplore/game/planet/{planetId}/name"),
    SKYXPLORE_PLANET_GET_OVERVIEW: new Endpoint(RequestMethod.GET, "/api/skyxplore/game/planet/{planetId}/overview"),
    SKYXPLORE_PLANET_UPDATE_PRIORITY: new Endpoint(RequestMethod.POST, "/api/skyxplore/game/planet/{planetId}/priority/{priorityType}"),
    SKYXPLORE_BUILDING_DECONSTRUCT: new Endpoint(RequestMethod.POST, "/api/skyxplore/game/building/{planetId}/{buildingId}/deconstruct"),
    SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION: new Endpoint(RequestMethod.DELETE, "/api/skyxplore/game/building/{planetId}/{buildingId}/deconstruct"),
    SKYXPLORE_BUILDING_UPGRADE: new Endpoint(RequestMethod.POST, "/api/skyxplore/game/building/{planetId}/{buildingId}"),
    SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION: new Endpoint(RequestMethod.DELETE, "/api/skyxplore/game/building/{planetId}/{buildingId}"),
    SKYXPLORE_PLANET_GET_POPULATION: new Endpoint(RequestMethod.GET, "/api/skyxplore/game/planet/{planetId}/citizen"),
    SKYXPLORE_PLANET_RENAME_CITIZEN: new Endpoint(RequestMethod.POST, "/api/skyxplore/game/citizen/{citizenId}/rename"),
    SKYXPLORE_PLANET_GET_STORAGE_SETTINGS: new Endpoint(RequestMethod.GET, "/api/skyxplore/game/planet/{planetId}/storage-settings"),
    SKYXPLORE_PLANET_CREATE_STORAGE_SETTING: new Endpoint(RequestMethod.PUT, "/api/skyxplore/game/planet/{planetId}/storage-settings"),
    SKYXPLORE_PLANET_EDIT_STORAGE_SETTING: new Endpoint(RequestMethod.POST, "/api/skyxplore/game/storage-settings"),
    SKYXPLORE_PLANET_DELETE_STORAGE_SETTING: new Endpoint(RequestMethod.DELETE, "/api/skyxplore/game/storage-settings/{storageSettingId}"),

    //Notebook
    NOTEBOOK_GET_CATEGORY_TREE: new Endpoint(RequestMethod.GET, "/api/notebook/category/tree"),
    NOTEBOOK_GET_PINNED_ITEMS: new Endpoint(RequestMethod.GET, "/api/notebook/item/pinned"),
    NOTEBOOK_GET_CHILDREN_OF_CATEGORY: new Endpoint(RequestMethod.GET, "/api/notebook/category/children"),
    NOTEBOOK_CREATE_CATEGORY: new Endpoint(RequestMethod.PUT, "/api/notebook/category"),
    NOTEBOOK_CREATE_TEXT: new Endpoint(RequestMethod.PUT, "/api/notebook/text"),
    NOTEBOOK_GET_TEXT: new Endpoint(RequestMethod.GET, "/api/notebook/text/{listItemId}"),
    NOTEBOOK_EDIT_TEXT: new Endpoint(RequestMethod.POST, "/api/notebook/text/{listItemId}"),
    NOTEBOOK_DELETE_LIST_ITEM: new Endpoint(RequestMethod.DELETE, "/api/notebook/item/{listItemId}"),
    NOTEBOOK_ARCHIVE_ITEM: new Endpoint(RequestMethod.POST, "/api/notebook/item/{listItemId}/archive"),
    NOTEBOOK_PIN_LIST_ITEM: new Endpoint(RequestMethod.POST, "/api/notebook/item/{listItemId}/pin"),
    NOTEBOOK_CLONE_LIST_ITEM: new Endpoint(RequestMethod.POST, "/api/notebook/{listItemId}/clone"),
    NOTEBOOK_GET_LIST_ITEM: new Endpoint(RequestMethod.GET, "/api/notebook/list-item/{listItemId}"),
    NOTEBOOK_EDIT_LIST_ITEM: new Endpoint(RequestMethod.POST, "/api/notebook/item/{listItemId}"),
    NOTEBOOK_CREATE_LINK: new Endpoint(RequestMethod.PUT, "/api/notebook/link"),
    NOTEBOOK_CREATE_ONLY_TITLE: new Endpoint(RequestMethod.PUT, "/api/notebook/only-title"),
    NOTEBOOK_CREATE_CHECKLIST: new Endpoint(RequestMethod.PUT, "/api/notebook/checklist"),
    NOTEBOOK_GET_CHECKLIST: new Endpoint(RequestMethod.GET, "/api/notebook/checklist/{listItemId}"),
    NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS: new Endpoint(RequestMethod.POST, "/api/notebook/checklist/item/{checklistItemId}/status"),
    NOTEBOOK_UPDATE_CHECKLIST_ITEM_CONTENT: new Endpoint(RequestMethod.POST, "/api/notebook/checklist/item/{checklistItemId}/content"),
    NOTEBOOK_DELETE_CHECKLIST_ITEM: new Endpoint(RequestMethod.DELETE, "/api/notebook/checklist/item/{checklistItemId}"),
    NOTEBOOK_EDIT_CHECKLIST: new Endpoint(RequestMethod.POST, "/api/notebook/checklist/{listItemId}"),
    NOTEBOOK_CHECKLIST_DELETE_CHECKED: new Endpoint(RequestMethod.DELETE, "/api/notebook/checklist/{listItemId}/checked"),
    NOTEBOOK_ORDER_CHECKLIST_ITEMS: new Endpoint(RequestMethod.POST, "/api/notebook/checklist/{listItemId}/order"),
    NOTEBOOK_ADD_CHECKLIST_ITEM: new Endpoint(RequestMethod.PUT, "/api/notebook/checklist/{listItemId}/item"),
    NOTEBOOK_CREATE_TABLE: new Endpoint(RequestMethod.PUT, "/api/notebook/table"),
    NOTEBOOK_GET_TABLE: new Endpoint(RequestMethod.GET, "/api/notebook/table/{listItemId}"),
    NOTEBOOK_EDIT_TABLE: new Endpoint(RequestMethod.POST, "/api/notebook/table/{listItemId}"),
    NOTEBOOK_CREATE_CHECKLIST_TABLE: new Endpoint(RequestMethod.PUT, "/api/notebook/checklist-table"),
    NOTEBOOK_GET_CHECKLIST_TABLE: new Endpoint(RequestMethod.GET, "/api/notebook/checklist-table/{listItemId}"),
    NOTEBOOK_TABLE_SET_ROW_STATUS: new Endpoint(RequestMethod.POST, "/api/notebook/table/row/{rowId}/status"),
    NOTEBOOK_TABLE_SET_CHECKBOX_COLUMN_STATUS: new Endpoint(RequestMethod.POST, "/api/notebook/table/column/{columnId}/status"),
    NOTEBOOK_EDIT_CHECKLIST_TABLE: new Endpoint(RequestMethod.POST, "/api/notebook/checklist-table/{listItemId}"),
    NBOTEBOOK_CREATE_IMAGE: new Endpoint(RequestMethod.PUT, "/api/notebook/image"),
    NOTEBOOK_GET_LIST_ITEM: new Endpoint(RequestMethod.GET, "/api/notebook/list-item/{listItemId}"),
    NBOTEBOOK_CREATE_FILE: new Endpoint(RequestMethod.PUT, "/api/notebook/file"),
    NOTEBOOK_SEARCH: new Endpoint(RequestMethod.POST, "/api/notebook/item/search"),
    NOTEBOOK_MOVE_LIST_ITEM: new Endpoint(RequestMethod.POST, "/api/notebook/{listItemId}/move"),
    NOTEBOOK_CREATE_CUSTOM_TABLE: new Endpoint(RequestMethod.PUT, "/api/notebook/custom-table"),
    NOTEBOOK_GET_CUSTOM_TABLE: new Endpoint(RequestMethod.GET, "/api/notebook/custom-table/{listItemId}"),
    NOTEBOOK_TABLE_DELETE_CHECKED: new Endpoint(RequestMethod.DELETE, "/api/notebook/table/{listItemId}/checked"),

    //Storage
    STORAGE_UPLOAD_FILE: new Endpoint(RequestMethod.PUT, "/api/storage/{storedFileId}"),
    STORAGE_DOWNLOAD_FILE: new Endpoint(RequestMethod.GET, "/api/storage/{storedFileId}"),
    STORAGE_GET_METADATA: new Endpoint(RequestMethod.GET, "/api/storage/{storedFileId}/metadata"),

    //User Settings
    GET_USER_SETTINGS: new Endpoint(RequestMethod.GET, "/api/user/settings/{category}"),
    SET_USER_SETTINGS: new Endpoint(RequestMethod.POST, "/api/user/settings"),

    //Admin Panel
    //Migration Tasks
    ADMIN_PANEL_MIGRATION_GET_TASKS: new Endpoint(RequestMethod.GET, "/api/admin-panel/migration"),
    ADMIN_PANEL_MIGRATION_DELETE_TASK: new Endpoint(RequestMethod.DELETE, "/api/admin-panel/migration/{event}"),
    ADMIN_PANEL_MIGRATION_TRIGGER_TASK: new Endpoint(RequestMethod.POST, "/api/admin-panel/migration/{event}"),
}

export default Endpoints;