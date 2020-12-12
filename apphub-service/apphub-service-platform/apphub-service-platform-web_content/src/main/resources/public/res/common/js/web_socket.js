function WebSocketConnection(ep){
    const endpoint = ep;
    const host = window.location.host;
    const handlers = [new PingWebSocketHandler(this)];

    let connection = null;

    this.addHandler = function(handler){
        console.log("Adding eventHandler", handler.canHandle);
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

    this.close = function(){
        connection.close();
    }

    this.sendEvent = function(event){
        if(!connection){
            throwException("IllegalState", "Connection is not established");
        }

        connection.send(event.assemble())
    }

    function handleMessage(event){
        const payload = JSON.parse(event.data);

        const eventName = payload.eventName;
        const eventData = payload.payload;

        new Stream(handlers)
            .filter(function(handler){return handler.canHandle(eventName)})
            .peek(function(handler){handler.handle(eventData, eventName)})
            .findFirst()
            .ifNotPresent(function(){logService.logToConsole("No WebSocketHandler found for eventName " + eventName)});
    }

    function PingWebSocketHandler(c){
        const wsConnection = c;

        this.canHandle = function(eventName){
            return eventName == "ping";
        }

        this.handle = function(){
            wsConnection.sendEvent(new WebSocketEvent("ping"));
        }
    }
}

function WebSocketEventHandler(ch, h){
    this.canHandle = ch;
    this.handle = h;
}

function WebSocketEvent(e, p){
    const event = e;
    const payload = p == undefined ? null : p;

    this.assemble = function(){
        return JSON.stringify({eventName: event, payload: payload});
    }
}