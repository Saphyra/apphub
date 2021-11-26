window.mapConstants = {
    X_OFFSET: 90,
    Y_OFFSET: 70,
    STAR_SIZE: 20,
    STAR_NAME_OFFSET: 30
};

(function MapController(){
    pageLoader.addLoader(function(){addRightClickMove(ids.mapSvgContainer, ids.mapWrapper, false)}, "Map add rightClickMove");

    pageLoader.addLoader(function(){mapController.zoomController  = new ZoomController(ids.mapSvgContainer, 1, 0.125, 0.125, 3)}, "Add Map Zoom controller");

    window.mapController = new function(){
        this.showMap = function(){
            universeController.loadUniverse();
            switchTab("main-tab", "map");
        }

        this.zoomIn = function(){
            this.zoomController.zoomIn();
        }

        this.zoomOut = function(){
            this.zoomController.zoomOut();
        }
    }
})();