scriptLoader.loadScript("/res/common/js/web_socket.js");
scriptLoader.loadScript("/res/skyxplore/js/game/map_controller.js");

(function PageController(){
    window.ids = {
        mapSvgContainer: "map-svg-container"
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