(function PlanetController(){
    let openedPlanetId;
    let currentPlanetName;

    pageLoader.addLoader(function(){addRightClickMove(ids.planetSurfaceContainer, ids.planetMiddleBar, false)}, "Planet add rightClickMove");
    pageLoader.addLoader(planetRenaming, "Planet renaming");

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
            }else{
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