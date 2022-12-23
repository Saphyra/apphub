(function CharacterLoadingService(){
    pageLoader.addLoader(loadCharacter, "Load character");

    function loadCharacter(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_CHARACTER"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(character){
                document.getElementById(ids.characterTabTitle).innerHTML = localization.getAdditionalContent("edit-character-title");
                document.getElementById(ids.characterNameInput).value = character.name;
            }
            request.getErrorHandler()
                .addErrorHandler(new ErrorHandler(
                    function(request, response){return response.status == ResponseStatus.NOT_FOUND},
                    function(request, response){
                        document.getElementById(ids.characterTabTitle).innerHTML = localization.getAdditionalContent("create-character-title");
                    }
                ));
        dao.sendRequestAsync(request);
    }
})();