(function PlanetController(){
    const PAGE_NAME = "PLANET";

    let openedPlanetId;
    let currentPlanetName;

    pageLoader.addLoader(function(){addRightClickMove(ids.planetSurfaceContainer, ids.planetSurfaceWrapper, false)}, "Planet add rightClickMove");
    pageLoader.addLoader(planetRenaming, "Planet renaming");
    pageLoader.addLoader(function(){planetController.zoomController  = new ZoomController(ids.planetSurfaceContainer, 1, 0.125, 0.125, 3)}, "Add Planet Zoom controller");

    window.planetController = new function(){
        this.viewPlanet = viewPlanet;
        this.openPlanetWindow = openPlanetWindow;
        this.openStorageSettings = openStorageSettings;
        this.getOpenedPlanetId = function(){
            return openedPlanetId;
        }
        this.zoomIn = function(){
            this.zoomController.zoomIn();
        }

        this.zoomOut = function(){
            this.zoomController.zoomOut();
        }
    }

    function viewPlanet(planetId){
        openedPlanetId = planetId;
        surfaceViewController.loadSurface(planetId);
        planetStorageController.loadStorage(planetId);
        planetPopulationController.loadPopulation(planetId);
        planetBuildingController.loadBuildings(planetId);
        planetPriorityController.loadPriorities(planetId);
        queueController.loadQueue(planetId);
        loadPlanetOverview(planetId);

        document.getElementById(ids.closePlanetButton).onclick = function(){
            solarSystemController.viewSolarSystem(solarSystemController.getOpenedSolarSystemId());
        };
        openPlanetWindow();
    }

    function openPlanetWindow(){
        switchTab("main-tab", ids.planet);
        wsConnection.sendEvent(new WebSocketEvent(webSocketEvents.PAGE_OPENED, {pageType: PAGE_NAME, pageId: openedPlanetId}));
    }

    function loadPlanetOverview(planetId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_PLANET_OVERVIEW", {planetId: planetId}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(planetOverview){
                currentPlanetName = planetOverview.planetName;
                document.getElementById(ids.planetName).innerText = planetOverview.planetName;
            }
        dao.sendRequestAsync(request);
    }

    function openStorageSettings(){
        storageSettingsController.viewStorageSettings(openedPlanetId);
    }

    function planetRenaming(){
        const planetNameField = document.getElementById(ids.planetName);

        $(planetNameField).on("keyup keypress", function(e){
            if(e.which == 13){
                e.preventDefault();
            }
        })

        planetNameField.onclick = function(){
            planetNameField.contentEditable = true;
            planetNameField.focus();
        }

        planetNameField.addEventListener("focusin", function(){selectElementText(planetNameField)});

        planetNameField.addEventListener("focusout", function(){
            const newName = planetNameField.innerHTML;

            if(newName == currentPlanetName){
                planetNameField.contentEditable = false;
                return;
            }

            if(isBlank(newName)){
                planetNameField.innerText = currentPlanetName;
                planetNameField.contentEditable = false;
            } else if(newName.length > 30){
                planetNameField.innerText = currentPlanetName;
                planetNameField.contentEditable = false;
                notificationService.showError(Localization.getAdditionalContent("new-planet-name-too-long"));
            } else{
                const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_RENAME", {planetId: openedPlanetId}), {value: newName});
                    request.processValidResponse = function(){
                        currentPlanetName = newName;
                        planetNameField.contentEditable = false;
                    }
                    request.processInvalidResponse = function(){
                        planetNameField.innerText = currentPlanetName;
                        planetNameField.contentEditable = false;
                    }
                dao.sendRequestAsync(request);
            }
        });
    }
})();