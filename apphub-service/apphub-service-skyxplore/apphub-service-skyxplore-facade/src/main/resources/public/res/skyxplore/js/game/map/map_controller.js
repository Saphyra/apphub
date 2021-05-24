scriptLoader.loadScript("/res/common/js/animation/move_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/map/universe_controller.js");

window.mapConstants = {
    X_OFFSET: 90,
    Y_OFFSET: 70,
    STAR_SIZE: 20,
    STAR_NAME_OFFSET: 30
};

(function MapController(){
    window.mapController = new function(){
        this.displayMap = function(){

        }
    }

    $(document).ready(init);

    function init(){
        universeController.loadUniverse();
        addRightClickMove(ids.mapSvgContainer, ids.mapContainer, false);
    }
})();