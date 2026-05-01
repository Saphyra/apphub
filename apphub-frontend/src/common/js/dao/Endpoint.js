import MapStream from "../collection/MapStream";
import { hasValue } from "../Utils";
import Request from "./Request";
import RequestMethod from "./RequestMethod";

export default class Endpoint {
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
                .filter((key, value) => hasValue(value))
                .toList((key, value) => key + "=" + value)
                .join("&");

            return url + "?" + queryString;
        }
    }

    toPageUrl() {
        const result = this.url.replace(/\{(\w+)\}/g, ":$1");
        return result;
    }
}