(function GamesController(){
    let savedGamesDisplayed = false;

    window.gamesController = new function(){
        this.toggleSavedGames = toggleSavedGames;
    }

    function toggleSavedGames(){
        if(savedGamesDisplayed){
            const parent = document.getElementById(ids.games);
            const container = document.getElementById(ids.gamesWrapper);
            savedGamesDisplayed = false;
            roll.rollOutVertical(container, 500)
                .then(() => parent.removeChild(container));
        }else{
            displaySavedGames(parent);
        }
    }

    function displaySavedGames(){
        const parent = document.getElementById(ids.games);
            parent.innerHTML = "";

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_GAMES"));
            request.convertResponse = function(response){
                return new Stream(JSON.parse(response.body))
                    .sorted(function(a, b){return -1 * (a.lastPlayed - b.lastPlayed)})
                    .toList();
            }
            request.processValidResponse = function(games){
                savedGamesDisplayed = true;
                roll.rollInVertical(createGamesWrapper(games), parent, 500);
            }
        dao.sendRequestAsync(request);

        function createGamesWrapper(games){
            const gamesWrapper = document.createElement("DIV");
                gamesWrapper.id = ids.gamesWrapper;

                if(games.length == 0){
                    const noGameNode = document.createElement("DIV");
                        noGameNode.classList.add("game-item");
                        noGameNode.innerText = localization.getAdditionalContent("no-game");
                    gamesWrapper.appendChild(noGameNode);
                }

                new Stream(games)
                    .map(createItem)
                    .forEach(function(gameNode){gamesWrapper.appendChild(gameNode)});

            return gamesWrapper;

            function createItem(game){
                const gameItem = document.createElement("DIV");
                    gameItem.classList.add("game-item");
                    gameItem.classList.add("button");

                    const lastPlayed = new Date(0);
                        lastPlayed.setUTCSeconds(game.lastPlayed);
                    gameItem.title = localization.getAdditionalContent("additional-players") + ": " + game.players + "\n" + localization.getAdditionalContent("last-played") + ": " + formatDate(lastPlayed);

                    gameItem.innerText = game.gameName;

                    const deleteButton = document.createElement("BUTTON");
                        deleteButton.innerText = localization.getAdditionalContent("delete");
                        deleteButton.onclick = function(e){
                            e.stopPropagation();
                            deleteGame(game);
                        }
                gameItem.appendChild(deleteButton);

                gameItem.onclick = function(){
                    loadGame(game.gameId);
                }

                return gameItem;
            }
        }
    }

    function deleteGame(game){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("delete-game-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("delete-game-confirmation-dialog-detail", {gameName: game.gameName}))
            .withConfirmButton(localization.getAdditionalContent("delete-game-confirmation-dialog-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("delete-game-confirmation-dialog-cancel-button"));

        confirmationService.openDialog(
            "delete-game-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("SKYXPLORE_DELETE_GAME", {gameId: game.gameId}));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(localization.getAdditionalContent("game-deleted"));
                        displaySavedGames();
                    }
                dao.sendRequestAsync(request);
            }
        )
    }

    function loadGame(gameId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_LOBBY_LOAD_GAME", {gameId: gameId}));
            request.processValidResponse = function(){
                window.location.href = Mapping.SKYXPLORE_LOBBY_PAGE;
            }
        dao.sendRequestAsync(request);
    }
})();