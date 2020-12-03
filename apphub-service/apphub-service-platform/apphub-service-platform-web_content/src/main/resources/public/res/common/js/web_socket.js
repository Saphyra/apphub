function WebSocketConnection(ep){
    const endpoint = ep;
    const host = window.location.host;
    const handlers = [];

    let connection;

    this.addHandler = function(handler){
        handlers.push(handler);
        return this;
    }

    this.connect = function(){
        if(connection){
            throwException("IllegalState", "Connection already established.");
        }

        connection = new WebSocket("ws://" + host + endpoint.getUrl());

        connection.onmessage = handleMessage;
    }

    function handleMessage(event){
        const payload = JSON.parse(event.data);
        console.log(payload); //TODO

        const eventName = payload.eventName;
        const eventData = payload.payload;

        new Stream(handlers)
            .filter(function(handler){return handler.canHandle(eventName)})
            .peek(function(handler){handler.handle(eventData, eventName)})
            .findFirst()
            .ifNotPresent(function(){logService.logToConsole("No WebSocketHandler found for eventName " + eventName)});
    }
}

function WebSocketEventHandler(ch, h){
    this.canHandle = ch;
    this.handle = h;
}