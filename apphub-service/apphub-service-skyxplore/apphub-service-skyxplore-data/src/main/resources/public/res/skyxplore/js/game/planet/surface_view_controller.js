(function SurfaceViewController() {
    window.surfaceViewController = new function(){
        this.loadSurface = loadSurface;
    }

    function loadSurface(planetId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_PLANET_SURFACE", {planetId: planetId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(surfaces){
                displaySurfaces(planetId, surfaces);
            }
        dao.sendRequestAsync(request);
    }

    function displaySurfaces(planetId, surfaces){
        const surfaceContainer = document.getElementById(ids.planetSurfaceContainer);
            surfaceContainer.innerHTML = "";

        const coordinateMapping = createCoordinateMapping(surfaces);

        for(let rowIndex in coordinateMapping){
            const rowNode = document.createElement("DIV");
                rowNode.classList.add("surface-table-row");

                const row = coordinateMapping[rowIndex];

                for(let columnIndex in row){
                    const surface = row[columnIndex];

                    const surfaceNode = document.createElement("SPAN");
                        surfaceNode.classList.add("surface-table-cell");
                        surfaceNode.classList.add("surface-type-" + surface.surfaceType.toLowerCase());
                        surfaceNode.id = surface.surfaceId;

                        if(surface.building){
                            surfaceNode.appendChild(createBuilding(planetId, surface.building));
                        }else{
                            surfaceNode.appendChild(createEmptySurface(surface.surfaceId, surface.surfaceType));
                        }
                    rowNode.appendChild(surfaceNode);
                }
            surfaceContainer.appendChild(rowNode);
        }

        function createCoordinateMapping(surfaces){
            const result = {};

            for(let index in surfaces){
                const surface = surfaces[index];
                const coordinate = surface.coordinate;
                if(result[coordinate.x] == undefined){
                    result[coordinate.x] = {};
                }

                result[coordinate.x][coordinate.y] = surface;
            }

            return result;
        }

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
                                    constructionController.cancelConstruction(planetId, buildingId, dataId);
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
                        constructionController.upgradeBuilding(planetId, building.buildingId);
                    }
                return upgradeButton;
            }
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

                    const terraformButton = document.createElement("button");
                        terraformButton.classList.add("empty-surface-terraform-button");
                        terraformButton.innerHTML = "T" //TODO proper terraform background icon
                        terraformButton.onclick = function(){
                            terraformingController.openTerraformWindow(planetController.getOpenedPlanetId(), surfaceType, surfaceId);
                        }
                footer.appendChild(terraformButton);
            content.appendChild(footer);

            return content;
        }
    }


})();