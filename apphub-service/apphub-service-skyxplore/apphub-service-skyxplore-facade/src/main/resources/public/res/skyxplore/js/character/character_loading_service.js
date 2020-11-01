(function CharacterLoadingService(){
    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.LOCALIZATION_LOADED},
        loadCharacter,
        true
    ).setName("CharacterLoader"));

    function loadCharacter(){
        console.log("Loading character...");
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_CHARACTER"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(character){
                console.log("Character found.");
                document.getElementById(ids.characterTabTitle).innerHTML = Localization.getAdditionalContent("edit-character-title");
                document.getElementById(ids.characterNameInput).value = character.name;
            }
            request.getErrorHandler()
                .addErrorHandler(new ErrorHandler(
                    function(request, response){return response.status == ResponseStatus.NOT_FOUND},
                    function(request, response){
                        console.log("Character not found.");
                        document.getElementById(ids.characterTabTitle).innerHTML = Localization.getAdditionalContent("create-character-title");
                    }
                ));
        dao.sendRequestAsync(request);
    }
})();