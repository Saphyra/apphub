import MapStream from "../collection/MapStream";
import Stream from "../collection/Stream";
import Constants from "../Constants";
import NotificationKey from "../notification/NotificationKey";
import { getBrowserLanguage, hasValue, setCookie } from "../Utils";
import getDefaultErrorHandler from "./DefaultErrorHandler";
import ErrorHandler from "./ErrorHandler";
import RequestMethod from "./RequestMethod";
import Response from "./Response";
import ResponseStatus from "./ResponseStatus";

const REFRESH_URL = "/api/authorization/token/refresh";

export default class Request {
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

    send(setDisplaySpinner = () => { }, shouldRefreshTokens = true) {
        setDisplaySpinner(true);

        const xhr = new XMLHttpRequest();
        xhr.open(this.requestMethod, this.url, true);

        new MapStream(this.headers)
            .filter((name, value) => hasValue(value))
            .forEach((name, value) => xhr.setRequestHeader(name, value));

        xhr.setRequestHeader("Cache-Control", "no-cache");
        xhr.setRequestHeader(Constants.HEADER_BROWSER_LANGUAGE, getBrowserLanguage());
        xhr.setRequestHeader("accept", "application/json");

        const request = this;

        return new Promise((resolve, reject) => {
            xhr.onload = () => {
                const response = new Response(xhr.status, xhr.responseText);
                setDisplaySpinner(false);
                if (response.status === ResponseStatus.OK) {
                    const parsedBody = this.responseConverter(response);
                    resolve(parsedBody);
                } else if (response.status === ResponseStatus.UNAUTHORIZED && shouldRefreshTokens) {
                    if (this.url.endsWith(REFRESH_URL)) {
                        reject();
                    } else {
                        return refreshTokens()
                            .then(() => this.send(setDisplaySpinner))
                            .then(resolve);
                    }
                } else {
                    this.handleError(response);
                    reject();
                }
            };

            xhr.onerror = () => {
                setDisplaySpinner(false);
                this.handleError(new Response(xhr.status, xhr.responseText), request);
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

async function refreshTokens() {
    return new Request(RequestMethod.POST, REFRESH_URL)
        .addErrorHandler(new ErrorHandler(
            (response) => response.status == ResponseStatus.UNAUTHORIZED,
            () => {
                sessionStorage.errorCode = NotificationKey.NO_VALID_SESSION;
                window.location.href = "/web?redirect=/" + (window.location.pathname + window.location.search).substr(1);
            }
        ))
        .send()
        .then(response => {
            setCookie("access-token", response.accessToken.jwt, response.accessToken.expiration, response.accessToken.path);
            setCookie("refresh-token", response.refreshToken.jwt, response.refreshToken.expiration, response.refreshToken.path);
        });
}