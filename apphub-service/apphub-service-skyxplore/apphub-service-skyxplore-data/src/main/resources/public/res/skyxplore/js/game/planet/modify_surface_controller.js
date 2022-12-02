(function ModifySurfaceController(){
    window.modifySurfaceController = new function(){
        this.openModifySurfaceWindow = openModifySurfaceWindow;
    }

    function openModifySurfaceWindow(planetId, surfaceType, surfaceId){
        constructionController.fillAvailableBuildings(planetId, surfaceType, surfaceId);
        terraformationController.fillTerraformWindow(planetId, surfaceId, surfaceType);

        switchTab("main-tab", ids.modifySurface);

        document.getElementById(ids.closeModifySurfaceButton).onclick = function(){
            planetController.openPlanetWindow();
        };
    }
})();