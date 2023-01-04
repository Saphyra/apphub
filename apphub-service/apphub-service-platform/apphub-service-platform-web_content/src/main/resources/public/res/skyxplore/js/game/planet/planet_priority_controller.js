(function PlanetPriorityController(){
    const priorityTypes = ["construction", "industry", "well_being", "education"];

    const idMasks = {
        planetPrioritiesInput: new IdMask("planet-priorities-*-input"),
        planetPrioritiesValue: new IdMask("planet-priorities-*-value"),
    }

    pageLoader.addLoader(setUpPriorityInputs, "PlanetPriority set up priority inputs");

    window.planetPriorityController = new function(){
        this.loadPriorities = loadPriorities;
    }
    
    function loadPriorities(planetId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_GET_PRIORITIES", {planetId: planetId}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(priorities){
                displayPriorities(priorities);
            }
        dao.sendRequestAsync(request);
    }

    function displayPriorities(priorities){
        new MapStream(priorities)
            .forEach(setPriorities);

        function setPriorities(priorityType, priority){
            document.getElementById(idMasks.planetPrioritiesInput.get(priorityType)).value = priority;
            document.getElementById(idMasks.planetPrioritiesValue.get(priorityType)).innerHTML = priority;
        }
    }

    function updatePriority(priorityType, priority){
        const planetId = planetController.getOpenedPlanetId();
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_UPDATE_PRIORITY", {planetId: planetId, priorityType: priorityType}), {value: priority});
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("planet-priority-updated"));
            }
        dao.sendRequestAsync(request);
    }

    function setUpPriorityInputs(){
        new Stream(priorityTypes)
            .forEach(function(priorityType){
                const input = document.getElementById(idMasks.planetPrioritiesInput.get(priorityType));
                    input.onchange = function(){
                        document.getElementById(idMasks.planetPrioritiesValue.get(priorityType)).innerHTML = input.value;
                        updatePriority(priorityType, input.value);
                    }
            });
    }
})();