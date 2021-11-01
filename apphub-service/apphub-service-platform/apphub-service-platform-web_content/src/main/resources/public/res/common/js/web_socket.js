function WebSocketConnection(ep){
    const endpoint = ep;
    const host = window.location.host;
    const handlers = [new PingWebSocketHandler(this), new RedirectWebSocketHandler(this)];

    let reconnectionTryCount = 1;

    let connection = null;

    this.addHandler = function(handler){
        console.log("Adding eventHandler", handler.canHandle);
        handlers.push(handler);
        return this;
    }

    this.addHandlers = function(h){
        console.log(h)
        new Stream(h)
            .peek(function(handler){console.log("Adding eventHandler", handler.canHandle)})
            .forEach(function(handler){handlers.push(handler)});
        return this;
    }

    this.connect = connect

    function connect(){
        if(connection){
            throwException("IllegalState", "Connection already established.");
        }

        const url = "ws://" + host + endpoint.getUrl();
        console.log("Connecting to WebSocket endpoint " + url);
        connection = new WebSocket(url);

        connection.onmessage = handleMessage;
        connection.onerror = function(err){
            console.log("WebSocket encountered error: ", err.message, "Closing connection");
        };
        connection.onclose = reconnect;
    }

    this.close = function(){
        if(!connection){
            throwException("IllegalState", "Connection is not established.");
        }
        connection.close();
    }

    this.sendEvent = function(event){
        if(!connection){
            throwException("IllegalState", "Connection is not established");
        }

        connection.send(event.assemble())
    }

    function handleMessage(event){
        reconnectionTryCount = 1;
        const payload = JSON.parse(event.data);

        const eventName = payload.eventName;
        const eventData = payload.payload;

        new Stream(handlers)
            .filter(function(handler){return handler.canHandle(eventName)})
            .peek(function(handler){handler.handle(eventData, eventName)})
            .findFirst()
            .ifNotPresent(function(){logService.logToConsole("No WebSocketHandler found for eventName " + eventName)});
    }

    function reconnect(){
        console.log("Connection lost. Reconnecting... Retry count: " + reconnectionTryCount);
        connection = null;
        setTimeout(connect, reconnectionTryCount * 1000);
        reconnectionTryCount++;
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

    function RedirectWebSocketHandler(c){
        const wsConnection = c;

        this.canHandle = function(eventName){
            return eventName == "redirect";
        }

        this.handle = function(redirectUrl){
            window.location.href = event.eventName;
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