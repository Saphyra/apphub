scriptLoader.loadScript("/res/common/js/animation/spinner.js");

(function StartGameController(){
    $(document).ready(init);

    window.startGameController = new function(){
        this.createGameCreationInitiatedHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return eventName == webSocketEvents.GAME_CREATION_INITIATED},
                gameCreationStarted
            );
        };

        this.createGameLoadedHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return eventName == webSocketEvents.GAME_LOADED},
                function(){window.location.href = Mapping.SKYXPLORE_GAME_PAGE}
            );
        };

        this.startGame = startGame;
    }

    function startGame(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_START_GAME"));
        dao.sendRequestAsync(request);
    }

    function gameCreationStarted(){
        notificationService.showSuccess(Localization.getAdditionalContent("game-started"));
        spinner.open();
    }

    function init(){
        if(window.userId != window.host){
            document.getElementById(ids.startGameButton).style.display = "none";
        }

        if(window.gameCreationStarted){
            gameCreationStarted();
        }
    }
})();