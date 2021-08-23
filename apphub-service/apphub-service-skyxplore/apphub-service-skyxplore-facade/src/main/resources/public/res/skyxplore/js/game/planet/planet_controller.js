scriptLoader.loadScript("/res/common/js/animation/move_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/surface_view_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/planet_storage_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/planet_population_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/planet_building_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/planet_priority_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/storage_settings_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/population_overview_controller.js");

(function PlanetController(){
    let openedPlanetId;

    window.planetController = new function(){
        this.viewPlanet = viewPlanet;
        this.openStorageSettings = openStorageSettings;
        this.getOpenedPlanetId = function(){
            return openedPlanetId;
        }
    }

    $(document).ready(init);

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

    function init(){
        addRightClickMove(ids.planetSurfaceContainer, ids.planetMiddleBar, false);
    }
})();