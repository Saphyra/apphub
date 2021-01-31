(function CharacterLoadingService(){
    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.LOCALIZATION_LOADED},
        loadCharacter,
        true
    ).setName("CharacterLoader"));

    //TODO Page gets the proposed characterName from the model. Add the type=CREATE/EDIT to the model, and eliminate this file. The endpoint call also can be removed.
    function loadCharacter(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_CHARACTER"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(character){
                document.getElementById(ids.characterTabTitle).innerHTML = Localization.getAdditionalContent("edit-character-title");
                document.getElementById(ids.characterNameInput).value = character.name;
            }
            request.getErrorHandler()
                .addErrorHandler(new ErrorHandler(
                    function(request, response){return response.status == ResponseStatus.NOT_FOUND},
                    function(request, response){
                        document.getElementById(ids.characterTabTitle).innerHTML = Localization.getAdditionalContent("create-character-title");
                    }
                ));
        dao.sendRequestAsync(request);
    }
})();