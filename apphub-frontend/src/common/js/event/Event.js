const Event = class {
    constructor(eventName, payload) {
        this.eventName = eventName;
        this.payload = payload;
    }
}

export default Event;