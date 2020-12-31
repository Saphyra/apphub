scriptLoader.loadScript("/res/common/js/animation/move_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/surface_view_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/planet_storage_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/planet_population_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/planet_building_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/planet_priority_controller.js");

(function PlanetController(){
    let openedPlanetId;

    window.planetController = new function(){
        this.viewPlanet = viewPlanet;
        this.getOpenedPlanetId = function(){
            return openedPlanetId;
        }
    }

    $(document).ready(init);

    function viewPlanet(planetId, closeCallBack){
        openedPlanetId = planetId;
        surfaceViewController.loadSurface(planetId);
        planetStorageController.loadStorage(planetId);
        planetPopulationController.loadPopulation(planetId);
        planetBuildingController.loadBuildings(planetId);
        planetPriorityController.loadPriorities(planetId);

        document.getElementById(ids.closePlanetButton).onclick = closeCallBack;
        switchTab("main-tab", ids.planet);
    }

    function init(){
        addRightClickMove(ids.planetSurfaceContainer, ids.planetMiddleBar, false);
    }
})();