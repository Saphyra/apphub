window.mapConstants = {
    X_OFFSET: 90,
    Y_OFFSET: 70,
    STAR_SIZE: 20,
    STAR_NAME_OFFSET: 30
};

(function MapController(){
    pageLoader.addLoader(function(){addRightClickMove(ids.mapSvgContainer, ids.mapContainer, false)}, "Map add rightClickMove");

    window.mapController = new function(){
        this.displayMap = function(){

        }
    }
})();