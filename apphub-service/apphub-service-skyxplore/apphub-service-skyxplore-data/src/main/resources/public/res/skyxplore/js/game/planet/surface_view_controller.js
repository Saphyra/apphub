(function SurfaceViewController() {
    const syncEngine = new SyncEngineBuilder()
        .withContainerId(ids.planetSurfaceContainer)
        .withGetKeyMethod(surface => {return surface.surfaceId})
        .withCreateNodeMethod(createSurface)
        .withSortMethod(sortSurfaces)
        .withIdPrefix("surface")
        .build();

    let currentPlanetId = null;

    pageLoader.addLoader(addHandlers, "SurfaceViewController add WS event handlers");

    window.surfaceViewController = new function(){
        this.loadSurface = loadSurface;
        this.updateSurface = function(surface){
            syncEngine.add(surface);
        }
    }

    function loadSurface(planetId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_PLANET_SURFACE", {planetId: planetId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(surfaces){
                currentPlanetId = planetId;
                syncEngine.clear();
                syncEngine.addAll(surfaces);
                document.getElementById(ids.planetSurfaceContainer).style.gridTemplateColumns = "repeat(" + Math.sqrt(surfaces.length) + ", var(--surface-table-cell-size))";
            }
        dao.sendRequestAsync(request);
    }

    function sortSurfaces(a, b){
        if(a.coordinate.x == b.coordinate.x){
            return a.coordinate.y - b.coordinate.y;
        }

        return a.coordinate.x - b.coordinate.x;
    }

    function createSurface(surface){
        const surfaceNode = document.createElement("DIV");
            surfaceNode.classList.add("surface-table-cell");
            surfaceNode.classList.add("surface-type-" + surface.surfaceType.toLowerCase());
            surfaceNode.id = surface.surfaceId;

            if(surface.building){
                surfaceNode.appendChild(createBuilding(currentPlanetId, surface.building));
            }else if(surface.terraformation){
                surfaceNode.appendChild(createTerraformation(currentPlanetId, surface.surfaceId, surface.terraformation));
            }else{
                surfaceNode.appendChild(createEmptySurface(surface.surfaceId, surface.surfaceType));
            }
        return surfaceNode;

        function createBuilding(planetId, building){
            const content = document.createElement("DIV");
                content.classList.add("building-" + building.dataId);
                content.classList.add("surface-content");

                const levelCell = document.createElement("DIV");
                    levelCell.innerHTML = Localization.getAdditionalContent("level") + ": " + building.level;
                    levelCell.classList.add("surface-header");
            content.appendChild(levelCell);

                if(building.construction){
                    content.appendChild(createConstructionFooter(planetId, building.buildingId, building.construction, building.dataId));
                }else if(building.level < dataCaches.itemData.get(building.dataId).maxLevel){
                    const footer = document.createElement("DIV");
                        footer.classList.add("surface-footer");
                        footer.appendChild(createUpgradeBuildingFooter(planetId, building));
                    content.appendChild(footer);
                }

            return content;

            function createConstructionFooter(planetId, buildingId, construction, dataId){
                const footer = document.createElement("DIV");
                    footer.classList.add("surface-footer");

                    const progressBar = document.createElement("DIV");
                        progressBar.classList.add("progress-bar-container");

                        const progressBarBackground = document.createElement("DIV");
                            progressBarBackground.classList.add("progress-bar-background");
                            progressBarBackground.style.width = (construction.currentWorkPoints / construction.requiredWorkPoints * 100) + "%";
                    progressBar.appendChild(progressBarBackground);

                        const progressBarContent = document.createElement("DIV");
                            progressBarContent.classList.add("progress-bar-text");

                            const cancelConstructionButton = document.createElement("BUTTON");
                                cancelConstructionButton.classList.add("cancel-construction-button");
                                cancelConstructionButton.innerText = "-";
                                cancelConstructionButton.onclick = function(){
                                    constructionController.cancelConstruction(planetId, buildingId, dataId)
                                        .then((surface)=>{syncEngine.add(surface)});
                                }
                        progressBarContent.appendChild(cancelConstructionButton);
                    progressBar.appendChild(progressBarContent);
                footer.appendChild(progressBar);
                return footer;
            }

            function createUpgradeBuildingFooter(planetId, building){
                const upgradeButton = document.createElement("button");
                    upgradeButton.classList.add("upgrade-building-button");
                    upgradeButton.innerHTML = Localization.getAdditionalContent("upgrade");
                    upgradeButton.onclick = function(){
                        constructionController.upgradeBuilding(planetId, building.buildingId)
                            .then((surface)=>{syncEngine.add(surface)});
                    }
                return upgradeButton;
            }
        }

        function createTerraformation(planetId, surfaceId, terraformation){
            const content = document.createElement("DIV");
                content.classList.add("empty-surface-content");
                content.classList.add("surface-content");

                const footer = document.createElement("DIV");
                    footer.classList.add("surface-footer");

                    const progressBar = document.createElement("DIV");
                        progressBar.classList.add("progress-bar-container");

                        const progressBarBackground = document.createElement("DIV");
                            progressBarBackground.classList.add("progress-bar-background");
                            progressBarBackground.style.width = (terraformation.currentWorkPoints / terraformation.requiredWorkPoints * 100) + "%";
                    progressBar.appendChild(progressBarBackground);

                        const progressBarContent = document.createElement("DIV");
                            progressBarContent.classList.add("progress-bar-text");

                            const cancelTerraformationButton = document.createElement("BUTTON");
                                cancelTerraformationButton.classList.add("cancel-terraformation-button");
                                cancelTerraformationButton.innerText = "-";
                                cancelTerraformationButton.onclick = function(){
                                    terraformationController.cancelTerraformation(planetId, surfaceId)
                                        .then((surface)=>{syncEngine.add(surface)});
                                }
                        progressBarContent.appendChild(cancelTerraformationButton);
                    progressBar.appendChild(progressBarContent);
                footer.appendChild(progressBar);
            content.appendChild(footer);
            return content;
        }

        function createEmptySurface(surfaceId, surfaceType){
            const content = document.createElement("DIV");
                content.classList.add("empty-surface-content");
                content.classList.add("surface-content");

                const footer = document.createElement("DIV");
                    footer.classList.add("surface-footer");

                    const buildButton = document.createElement("button");
                        buildButton.classList.add("empty-surface-construct-new-building-button");
                        buildButton.innerHTML = "+";
                        buildButton.onclick = function(){
                            constructionController.openConstructNewBuildingWindow(planetController.getOpenedPlanetId(), surfaceType, surfaceId);
                        }
                footer.appendChild(buildButton);

                if(dataCaches.terraformingPossibilities.get(surfaceType).length > 0){
                    const terraformButton = document.createElement("button");
                        terraformButton.classList.add("empty-surface-terraform-button");
                        terraformButton.innerHTML = "T" //TODO proper terraform background icon
                        terraformButton.onclick = function(){
                            terraformationController.openTerraformWindow(planetController.getOpenedPlanetId(), surfaceId, surfaceType);
                        }
                    footer.appendChild(terraformButton);
                }

            content.appendChild(footer);
            return content;
        }
    }

    function addHandlers(){
        wsConnection.addHandler(new WebSocketEventHandler(
            function(eventName){return webSocketEvents.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED == eventName},
            surface => {syncEngine.add(surface)}
        ));
    }
})();