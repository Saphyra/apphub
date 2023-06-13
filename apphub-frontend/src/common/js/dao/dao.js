import "../collection/MapStream";
import MapStream from "../collection/MapStream";
import Constants from "../Constants";
import Utils from "../Utils";
import ErrorHandler from "./ErrorHandler";
import NotificationService from "../notification/NotificationService";
import Stream from "../collection/Stream";
import NotificationKey from "../notification/NotificationKey";

const Endpoint = class {
    constructor(requestMethod, url) {
        this.requestMethod = requestMethod;
        this.url = url;
    }

    createRequest(body, pathVariables = {}, queryParams = {}) {
        return new Request(this, body, pathVariables, queryParams);
    }
}

const RequestMethod = {
    POST: "POST",
    GET: "GET",
    PUT: "PUT",
    DELETE: "DELETE"
}

const Request = class {
    constructor(endpoint, body, pathVariables, queryParams) {
        this.requestMethod = endpoint.requestMethod;
        this.url = this.assembleUrl(endpoint.url, pathVariables, queryParams);
        this.body = this.processBody(body);
        this.responseConverter = (response) => {
            if (response.body !== null && response.body.length >= 2) {
                return JSON.parse(response.body);
            } else {
                return null;
            }
        }
        this.errorHandlers = [];
    }

    assembleUrl(url, pathVariables, queryParams) {
        const pathVariablesFilled = fillPathVariables(url, pathVariables);
        const queryParamsFilled = fillQueryParams(pathVariablesFilled, queryParams);

        return queryParamsFilled;

        function fillPathVariables(url, pathVariables) {
            if (!pathVariables) {
                return url;
            }
            let result = url;

            new MapStream(pathVariables)
                .forEach((placeholder, value) => {
                    const key = "{" + placeholder + "}";
                    result = result.replace(key, value);
                });

            return result;
        }

        function fillQueryParams(url, queryParams) {
            if (!queryParams) {
                return url;
            }

            if (Object.keys(queryParams).length === 0) {
                return url;
            }

            const queryString = new MapStream(queryParams)
                .toList((key, value) => key + "=" + value)
                .join("&");

            return url + "?" + queryString;
        }
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

        if (this.requestMethod !== RequestMethod.GET) {
            xhr.setRequestHeader("Content-Type", "application/json");
        }

        xhr.setRequestHeader("Cache-Control", "no-cache");
        xhr.setRequestHeader(Constants.HEADER_BROWSER_LANGUAGE, Utils.getBrowserLanguage());
        xhr.setRequestHeader(Constants.HEADER_REQUEST_TYPE_NAME, Constants.HEADER_REQUEST_TYPE_VALUE);

        return new Promise((resolve, reject) => {
            xhr.onload = () => {
                const response = new Response(xhr);
                if (response.status === ResponseStatus.OK) {
                    const parsedBody = this.responseConverter(response);
                    resolve(parsedBody);
                } else {
                    this.handleError(response);
                    reject();
                }
            };

            xhr.onerror = () => {
                this.handleError(new Response(xhr));
                reject();
            }

            xhr.send(this.body);
        });
    }

    handleError(response) {
        new Stream(this.errorHandlers)
            .filter((errorHandler) => errorHandler.canHandle(response))
            .findFirst()
            .orElse(defaultErrorHandler())
            .handle(response);
    }
}

const Response = class {
    constructor(xhr) {
        this.status = xhr.status;
        this.statusKey = this.getStatusKey(xhr.status);
        this.body = xhr.responseText;
    }

    getStatusKey(status) {
        return new MapStream(ResponseStatus)
            .filter((key, value) => value === status)
            .findAny()
            .orElse("Unknown response status code: " + status);
    }

    toString = function () {
        return this.status + ": " + this.statusKey + " - " + this.body;
    }
}

const defaultErrorHandler = () => {
    return new ErrorHandler(
        () => true,
        (response) => {
            if (isErrorResponse(response.body)) {
                const errorResponse = JSON.parse(response.body);

                switch (errorResponse.errorCode) {
                    case "SESSION_EXPIRED":
                    case "NO_SESSION_AVAILABLE":
                        sessionStorage.errorCode = NotificationKey.NO_VALID_SESSION;
                        window.location.href = Constants.INDEX_PAGE + "?redirect=/" + (window.location.pathname + window.location.search).substr(1);
                        break;
                    default:
                        NotificationService.showErrorCode(errorResponse.errorCode, errorResponse.params);
                }

            } else {
                NotificationService.showError("Error response from BackEnd: " + response.toString());
            }

            function isErrorResponse(responseBody) {
                try {
                    if (responseBody === null || responseBody.length === 0) {
                        console.log("Empty response body");
                        return false;
                    }

                    const errorResponse = JSON.parse(responseBody);

                    return errorResponse.errorCode !== undefined
                        && errorResponse.params !== undefined;
                } catch (e) {
                    console.log(e);
                    return false;
                }
            }
        }
    );
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
    SKYXPLORE_LOBBY_GET_MEMBERS: new Endpoint(RequestMethod.GET, "/api/skyxplore/lobby/members"),
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
    NOTEBOOK_DELETE_CHECKLIST_ITEM: new Endpoint(RequestMethod.DELETE, "/api/notebook/checklist/item/{checklistItemId}"),
    NOTEBOOK_EDIT_CHECKLIST: new Endpoint(RequestMethod.POST, "/api/notebook/checklist/{listItemId}"),
    NOTEBOOK_CHECKLIST_DELETE_CHECKED: new Endpoint(RequestMethod.DELETE, "/api/notebook/checklist/{listItemId}/checked"),
    NOTEBOOK_ORDER_CHECKLIST_ITEMS: new Endpoint(RequestMethod.POST, "/api/notebook/checklist/{listItemId}/order"),
    NOTEBOOK_CREATE_TABLE: new Endpoint(RequestMethod.PUT, "/api/notebook/table"),
    NOTEBOOK_GET_TABLE: new Endpoint(RequestMethod.GET, "/api/notebook/table/{listItemId}"),
    NOTEBOOK_EDIT_TABLE: new Endpoint(RequestMethod.POST, "/api/notebook/table/{listItemId}"),
    NOTEBOOK_CREATE_CHECKLIST_TABLE: new Endpoint(RequestMethod.PUT, "/api/notebook/checklist-table"),
    NOTEBOOK_GET_CHECKLIST_TABLE: new Endpoint(RequestMethod.GET, "/api/notebook/checklist-table/{listItemId}"),
    NOTEBOOK_UPDATE_CHECKLIST_TABLE_ROW_STATUS: new Endpoint(RequestMethod.POST, "/api/notebook/checklist-table/{rowId}/status"),
    NOTEBOOK_EDIT_CHECKLIST_TABLE: new Endpoint(RequestMethod.POST, "/api/notebook/checklist-table/{listItemId}"),
}

export default Endpoints;