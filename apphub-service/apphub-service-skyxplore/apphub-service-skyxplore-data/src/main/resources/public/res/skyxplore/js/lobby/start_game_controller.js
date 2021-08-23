scriptLoader.loadScript("/res/common/js/animation/spinner.js");
scriptLoader.loadScript("/res/common/js/confirmation_service.js");

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

        if(lobbyType == "LOAD_GAME" && !membersController.allMembersConnected()){
            const confirmationDialogLocalization = new ConfirmationDialogLocalization()
                .withTitle(Localization.getAdditionalContent("start-game-confirmation-dialog-title"))
                .withDetail(Localization.getAdditionalContent("start-game-confirmation-dialog-detail"))
                .withConfirmButton(Localization.getAdditionalContent("start-game-confirmation-dialog-confirm-button"))
                .withDeclineButton(Localization.getAdditionalContent("start-game-confirmation-dialog-cancel-button"));

            confirmationService.openDialog(
                "start-game-confirmation-dialog",
                confirmationDialogLocalization,
                function(){dao.sendRequestAsync(request);}
            )
        }else{
            dao.sendRequestAsync(request);
        }
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