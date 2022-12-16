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
        const surfaceNode = domBuilder.create("DIV")

        return surfaceNode.id(surface.surfaceId)
            .addClass("surface-table-cell")
            .addClass("surface-type-" + surface.surfaceType.toLowerCase())
            .appendChild(getContent(surface, surfaceNode))
            .getNode();

        function getContent(surface, surfaceNode){
            if(surface.building){
                return createBuilding(currentPlanetId, surface.surfaceType, surface.building, surfaceNode);
            }else if(surface.terraformation){
                return createTerraformation(currentPlanetId, surface.surfaceId, surface.terraformation);
            }else{
                return createEmptySurface(surface.surfaceId, surface.surfaceType);
            }

            function createBuilding(planetId, surfaceType, building, surfaceNode){
                const titleBuilder = domBuilder.titleBuilder()
                    .append(dataCaches.itemDataNames.get(building.dataId))
                    .append(" - ")
                    .append(localization.getAdditionalContent("level"))
                    .append(": ")
                    .append(building.level);
                    addEffect(surfaceType, building, titleBuilder);


                surfaceNode.title(titleBuilder.build());

                const content = document.createElement("DIV");
                    content.classList.add("building-" + building.dataId);
                    content.classList.add("surface-content");

                    const levelCell = document.createElement("DIV");
                        levelCell.innerHTML = localization.getAdditionalContent("level") + ": " + building.level;
                        levelCell.classList.add("surface-header");
                content.appendChild(levelCell);

                    if(building.construction){
                        content.appendChild(createConstructionFooter(planetId, building.buildingId, building.construction, building.dataId));
                    }else if(building.level < dataCaches.itemData.get(building.dataId).maxLevel){
                        const footer = document.createElement("DIV");
                            footer.classList.add("surface-footer");
                            footer.appendChild(createUpgradeBuildingFooter(planetId, surfaceType, building));
                        content.appendChild(footer);
                    }

                return content;

                function addEffect(surfaceType, building, titleBuilder){
                    titleBuilder.newLine();

                    const buildingData = dataCaches.itemData.get(building.dataId);

                    switch(buildingData.buildingType){
                        case "miscellaneous":
                            addMiscTitle(building, buildingData, titleBuilder);
                        break;
                        case "storage":
                            titleBuilder.appendLine(localization.getAdditionalContent("building-effect-title-storage"))
                                .appendLine((building.level * buildingData.capacity) + " x " + dataCaches.storageTypeLocalization.get(buildingData.stores), 2);
                        break;
                        case "production":
                            new Stream(Object.keys(buildingData.gives))
                                .filter((itemId) => {return buildingData.gives[itemId].placed.indexOf(surfaceType) > -1})
                                .forEach((itemId) => {
                                    const productionData = buildingData.gives[itemId];
                                    titleBuilder.appendLine((building.level * productionData.constructionRequirements.parallelWorkers) + " x " + productionData.amount + " " + dataCaches.itemDataNames.get(itemId));
                                    new MapStream(productionData.constructionRequirements.requiredResources)
                                        .toListStream((itemId, amount) => {return amount + " x " + dataCaches.itemDataNames.get(itemId)})
                                        .sorted((a, b) => {return a.localeCompare(b)})
                                        .forEach((line) => titleBuilder.appendLine(line, 2));
                                });
                        break;
                    }
                }

                function addMiscTitle(building, buildingData, titleBuilder){
                    switch(buildingData.id){
                        case "community_center":
                            titleBuilder.appendLine(building.level + " x " + buildingData.data.seats + " " + localization.getAdditionalContent("seats"));
                            new Stream(["moraleRechargeMultiplier", "energyUsage"])
                                .forEach((property) => titleBuilder.appendLine(localization.getAdditionalContent(property) + ": " + buildingData.data[property], 2));
                            break;
                        case "hospital":
                            titleBuilder.appendLine(building.level + " x " + buildingData.data.beds + " " + localization.getAdditionalContent("beds"));

                            titleBuilder.appendLine(localization.getAdditionalContent("heal"));
                                new Stream(["energyUsage", "regenerationPerSecond"])
                                    .forEach((property) => titleBuilder.appendLine(buildingData.data.heal[property] + ": " + localization.getAdditionalContent(property), 2));

                            titleBuilder.appendLine(localization.getAdditionalContent("birth"))
                                titleBuilder.appendLine(localization.getAdditionalContent("maternityLeave") + ": " + buildingData.data.birth.maternityLeaveSeconds, 2);
                                titleBuilder.appendLine(localization.getAdditionalContent("required-work-points") + ": " + buildingData.data.birth.constructionRequirements.requiredWorkPoints, 2);
                                new MapStream(buildingData.data.birth.constructionRequirements.requiredResources)
                                    .sorted((a, b) => {return dataCaches.itemDataNames.get(a.getKey()).localeCompare(dataCaches.itemDataNames.get(b.getKey()))})
                                    .forEach((dataId, amount) => titleBuilder.appendLine(amount + " x " + dataCaches.itemDataNames.get(dataId), 2));
                            break;
                        case "restaurant":
                            titleBuilder.appendLine(building.level + " x " + buildingData.data.seats + " " + localization.getAdditionalContent("seats"));
                            new Stream(["satietyRechargeMultiplier", "energyUsage"])
                                .forEach((property) => titleBuilder.appendLine(localization.getAdditionalContent(property) + ": " + buildingData.data[property], 2));
                            break;
                        case "school":
                            titleBuilder.appendLine(building.level + " x " + buildingData.data.desks + " " + localization.getAdditionalContent("desks"));
                            new Stream(["experiencePerSecond", "energyUsage"])
                                .forEach((property) => titleBuilder.appendLine(localization.getAdditionalContent(property) + ": " + buildingData.data[property], 2));
                        break;
                        default:
                            throwException("IllegalArgument", "Unhandled misc building: " + buildingData.id);
                    }
                }

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

                function createUpgradeBuildingFooter(planetId, surfaceType, building){
                    const upgradeButton = document.createElement("button");
                        upgradeButton.classList.add("upgrade-building-button");
                        upgradeButton.innerHTML = localization.getAdditionalContent("upgrade");
                        upgradeButton.onclick = function(){
                            constructionController.openUpgradeBuildingWindow(planetId, surfaceType, building.buildingId, building.dataId, building.level)
                        }
                    return upgradeButton;
                }
            }

            function createTerraformation(planetId, surfaceId, terraformation){
                const content = document.createElement("DIV");
                    content.classList.add("surface-content");

                    const header = document.createElement("DIV");
                        header.innerHTML = dataCaches.surfaceTypeLocalization.get(terraformation.data);
                        header.classList.add("surface-header");
                content.appendChild(header);

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

                        const modifyButton = document.createElement("button");
                            modifyButton.classList.add("empty-surface-modify-button");
                            modifyButton.onclick = function(){
                                modifySurfaceController.openModifySurfaceWindow(planetController.getOpenedPlanetId(), surfaceType, surfaceId);
                            }
                    footer.appendChild(modifyButton);

                content.appendChild(footer);
                return content;
            }
        }
    }

    function addHandlers(){
        wsConnection.addHandler(new WebSocketEventHandler(
            function(eventName){return webSocketEvents.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED == eventName},
            surface => {syncEngine.add(surface)}
        ));
    }
})();