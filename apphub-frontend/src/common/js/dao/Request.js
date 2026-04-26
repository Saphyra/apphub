import MapStream from "../collection/MapStream";
import Stream from "../collection/Stream";
import Constants from "../Constants";
import { getBrowserLanguage, hasValue } from "../Utils";
import getDefaultErrorHandler from "./DefaultErrorHandler";
import Response from "./Response";
import ResponseStatus from "./ResponseStatus";

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