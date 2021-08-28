(function PlanetController(){
    let openedPlanetId;

    pageLoader.addLoader(function(){addRightClickMove(ids.planetSurfaceContainer, ids.planetMiddleBar, false)}, "Planet add rightClickMove");

    window.planetController = new function(){
        this.viewPlanet = viewPlanet;
        this.openStorageSettings = openStorageSettings;
        this.getOpenedPlanetId = function(){
            return openedPlanetId;
        }
    }

    function viewPlanet(planetId){
        openedPlanetId = planetId;
        surfaceViewController.loadSurface(planetId);
        planetStorageController.loadStorage(planetId);
        planetPopulationController.loadPopulation(planetId);
        planetBuildingController.loadBuildings(planetId);
        planetPriorityController.loadPriorities(planetId);
        loadPlanetOverview(planetId);

        document.getElementById(ids.closePlanetButton).onclick = function(){
            solarSystemController.viewSolarSystem(solarSystemController.getOpenedSolarSystemId());
        };
        switchTab("main-tab", ids.planet);
    }

    function loadPlanetOverview(planetId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_PLANET_OVERVIEW", {planetId: planetId}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(planetOverview){
                document.getElementById(ids.planetName).innerText = planetOverview.planetName;
            }
        dao.sendRequestAsync(request);
    }

    function openStorageSettings(){
        storageSettingsController.viewStorageSettings(openedPlanetId);
    }
})();