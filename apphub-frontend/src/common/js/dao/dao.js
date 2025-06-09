import "../collection/MapStream";
import MapStream from "../collection/MapStream";
import Constants from "../Constants";
import Stream from "../collection/Stream";
import getDefaultErrorHandler from "./DefaultErrorHandler";
import Response from "./Response";
import { getBrowserLanguage, hasValue } from "../Utils";

export const Endpoint = class {
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

export const RequestMethod = {
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

    send(setDisplaySpinner = () => { }) {
        setDisplaySpinner(true);

        const xhr = new XMLHttpRequest();
        xhr.open(this.requestMethod, this.url, true);

        new MapStream(this.headers)
            .filter((name, value) => hasValue(value))
            .forEach((name, value) => xhr.setRequestHeader(name, value));

        xhr.setRequestHeader("Cache-Control", "no-cache");
        xhr.setRequestHeader(Constants.HEADER_BROWSER_LANGUAGE, getBrowserLanguage());
        xhr.setRequestHeader("accept", "application/json");

        return new Promise((resolve, reject) => {
            xhr.onload = () => {
                const response = new Response(xhr.status, xhr.responseText);
                setDisplaySpinner(false);
                if (response.status === ResponseStatus.OK) {
                    const parsedBody = this.responseConverter(response);
                    resolve(parsedBody);
                } else {
                    this.handleError(response);
                    reject();
                }
            };

            xhr.onerror = () => {
                setDisplaySpinner(false);
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