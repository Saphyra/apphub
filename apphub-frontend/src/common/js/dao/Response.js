import MapStream from "../collection/MapStream";
import { ResponseStatus } from "./dao";

const Response = class {
    constructor(status, body) {
        this.status = status;
        this.statusKey = this.getStatusKey(status);
        this.body = body;
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

export default Response;