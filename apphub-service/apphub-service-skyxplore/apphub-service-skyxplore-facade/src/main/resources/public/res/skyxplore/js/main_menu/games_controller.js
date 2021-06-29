(function GamesController(){
    scriptLoader.loadScript("/res/common/js/animation/roll.js");

    let savedGamesDisplayed = false;

    window.gamesController = new function(){
        this.toggleSavedGames = toggleSavedGames;
    }

    function toggleSavedGames(){
        const parent = document.getElementById(ids.games);

        if(savedGamesDisplayed){
            const container = document.getElementById(ids.gamesWrapper);
            savedGamesDisplayed = false;
            roll.rollOutVertical(container, 500)
                .then(() => parent.removeChild(container));
        }else{
            displaySavedGames();
        }
    }

    function displaySavedGames(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_GAMES"));
            request.convertResponse = function(response){
                return new Stream(JSON.parse(response.body))
                    .sorted(function(a, b){return -1 * a.lastPlayed.localeCompare(b.lastPlayed)})
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

                new Stream(games)
                    .map(createItem)
                    .forEach(function(gameNode){gamesWrapper.appendChild(gameNode)});

            return gamesWrapper;

            function createItem(game){
                const gameItem = document.createElement("DIV");
                    gameItem.classList.add("game-item");
                    gameItem.classList.add("button");

                    gameItem.title = Localization.getAdditionalContent("additional-players") + ": " + game.players + "\n" + Localization.getAdditionalContent("last-played") + ": " + game.lastPlayed;

                    gameItem.innerText = game.gameName;

                    const deleteButton = document.createElement("BUTTON");
                        deleteButton.innerText = Localization.getAdditionalContent("delete");
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
        //TODO implement
    }

    function loadGame(gameId){
        //TODO implement
    }
})();