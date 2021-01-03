(function PlanetPriorityController(){
    const priorityTypes = ["construction", "manufacturing", "extraction"];

    const idMasks = {
        planetPrioritiesConstructionInput: new IdMask("planet-priorities-*-input"),
        planetPrioritiesConstructionValue: new IdMask("planet-priorities-*-value"),
    }

    window.planetPriorityController = new function(){
        this.loadPriorities = loadPriorities;
    }
    
    $(document).ready(init);

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
            document.getElementById(idMasks.planetPrioritiesConstructionInput.get(priorityType)).value = priority;
            document.getElementById(idMasks.planetPrioritiesConstructionValue.get(priorityType)).innerHTML = priority;
        }
    }

    function updatePriority(priorityType, priority){
        const planetId = planetController.getOpenedPlanetId();
        //TODO
        logService.showSuccess("Updating priority of type " + priorityType + " to " + priority + " on planet " + planetId);
    }

    function init(){
        new Stream(priorityTypes)
            .forEach(function(priorityType){
                const input = document.getElementById(idMasks.planetPrioritiesConstructionInput.get(priorityType));
                    input.onchange = function(){
                        document.getElementById(idMasks.planetPrioritiesConstructionValue.get(priorityType)).innerHTML = input.value;
                        updatePriority(priorityType, input.value);
                    }
            });
    }
})();