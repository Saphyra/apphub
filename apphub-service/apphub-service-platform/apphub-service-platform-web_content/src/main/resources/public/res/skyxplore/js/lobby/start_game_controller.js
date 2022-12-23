(function StartGameController(){
    if(window.userId != window.host){
        pageLoader.addLoader(function(){document.getElementById(ids.startGameButton).style.display = "none"}, "Hide gameCreationButton");
    }

    if(window.gameCreationStarted){
        pageLoader.addLoader(gameCreationStarted, "GameCreation started");
    }

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
                .withTitle(localization.getAdditionalContent("start-game-confirmation-dialog-title"))
                .withDetail(localization.getAdditionalContent("start-game-confirmation-dialog-detail"))
                .withConfirmButton(localization.getAdditionalContent("start-game-confirmation-dialog-confirm-button"))
                .withDeclineButton(localization.getAdditionalContent("start-game-confirmation-dialog-cancel-button"));

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
        notificationService.showSuccess(localization.getAdditionalContent("game-started"));
        spinner.open();
    }
})();