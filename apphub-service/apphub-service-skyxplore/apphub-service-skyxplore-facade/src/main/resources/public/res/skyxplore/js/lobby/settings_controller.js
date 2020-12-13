(function SettingsController(){
    window.settingsController = new function(){
        this.createGameSettingsChangedHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return eventName == webSocketEvents.GAME_SETTINGS_CHANGED},
                processGameSettingsChangedEvent
            );
        }
    }

    $(document).ready(init);

    function loadGameSettings(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_LOBBY_GET_GAME_SETTINGS"))
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(settings){
                displayGameSettings(settings);
            }
        dao.sendRequestAsync(request);
    }

    function processGameSettingsChangedEvent(settings){
        displayGameSettings(settings);
    }

    function displayGameSettings(settings){
        document.getElementById(ids.universeSizeInput).value = settings.universeSize;
        document.getElementById(ids.systemSizeInput).value = settings.systemSize;
        document.getElementById(ids.planetSizeInput).value = settings.planetSize;
        document.getElementById(ids.aiPresenceInput).value = settings.aiPresence;
    }

    function updateGameSettings(){
        const payload = {
            universeSize: document.getElementById(ids.universeSizeInput).value,
            systemSize: document.getElementById(ids.systemSizeInput).value,
            planetSize: document.getElementById(ids.planetSizeInput).value,
            aiPresence: document.getElementById(ids.aiPresenceInput).value,
        }

        const event = new WebSocketEvent(webSocketEvents.GAME_SETTINGS_CHANGED, payload);
        pageController.webSocketConnection.sendEvent(event);
    }

    function init(){
        if(window.host != window.userId){
            document.getElementById(ids.universeSizeInput).disabled = "disabled";
            document.getElementById(ids.systemSizeInput).disabled = "disabled";
            document.getElementById(ids.planetSizeInput).disabled = "disabled";
            document.getElementById(ids.aiPresenceInput).disabled = "disabled";
        }else{
            document.getElementById(ids.universeSizeInput).onchange = updateGameSettings;
            document.getElementById(ids.systemSizeInput).onchange = updateGameSettings;
            document.getElementById(ids.planetSizeInput).onchange = updateGameSettings;
            document.getElementById(ids.aiPresenceInput).onchange = updateGameSettings;
        }

        loadGameSettings();
    }
})();