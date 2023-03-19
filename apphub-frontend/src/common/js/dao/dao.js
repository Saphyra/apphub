import "../collection/MapStream";
import MapStream from "../collection/MapStream";
import Constants from "../Constants";
import Utils from "../Utils";
import ErrorHandler from "./ErrorHandler";
import NotificationService from "../notification/NotificationService";
import Stream from "../collection/Stream";

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
}

const defaultErrorHandler = () => {
    return new ErrorHandler(
        () => true,
        (response) => {
            if (isErrorResponse(response.body)) {
                const errorResponse = JSON.parse(response.body);

                switch (errorResponse.errorCode) {
                    case "SESSION_EXPIRED":
                        sessionStorage.errorMessage = "session-expired";
                        //TODO
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

const ResponseStatus = {
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
    ACCOUNT_REGISTER: new Endpoint(RequestMethod.POST, "/api/user"),
    LOGIN: new Endpoint(RequestMethod.POST, "/api/user/authentication/login"),
}

export default Endpoints;