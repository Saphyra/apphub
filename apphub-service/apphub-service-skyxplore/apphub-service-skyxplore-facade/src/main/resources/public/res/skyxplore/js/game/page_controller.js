scriptLoader.loadScript("/res/common/js/web_socket.js");

(function PageController(){
    window.ids = {
    }

    window.webSocketEvents = {
    }

    const wsConnection = new WebSocketConnection(Mapping.getEndpoint("CONNECTION_SKYXPLORE_GAME"));

    window.pageController = new function(){
        this.webSocketConnection = wsConnection;
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "skyxplore", fileName: "game"}));
        wsConnection
            .connect();
    });
})();