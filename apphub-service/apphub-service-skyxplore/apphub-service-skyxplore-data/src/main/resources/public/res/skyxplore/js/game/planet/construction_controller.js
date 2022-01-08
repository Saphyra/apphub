(function ConstructionController(){
    const PAGE_NAME = "PLANET_CONSTRUCTION";

    window.constructionController = new function(){
        this.openConstructNewBuildingWindow = openConstructNewBuildingWindow;
        this.cancelConstruction = cancelConstruction;
        this.upgradeBuilding = upgradeBuilding;
    }

    function openConstructNewBuildingWindow(planetId, surfaceType, surfaceId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_DATA_AVAILABLE_BUILDINGS", {surfaceType: surfaceType}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(availableBuildings){
                const container = document.getElementById(ids.availableBuildings);
                    container.innerHTML = "";

                    new Stream(availableBuildings)
                        .sorted(function(a, b){dataCaches.itemDataNames.get(a).localeCompare(dataCaches.itemDataNames.get(b))})
                        .map(function(dataId){return createAvailableBuilding(planetId, surfaceId, dataId)})
                        .forEach(function(node){container.appendChild(node)});


                document.getElementById(ids.closeConstructionButton).onclick = function(){
                    planetController.viewPlanet(planetId);
                };
                switchTab("main-tab", ids.construction);
                wsConnection.sendEvent(new WebSocketEvent(webSocketEvents.PAGE_OPENED, PAGE_NAME))
            }
        dao.sendRequestAsync(request);

        function createAvailableBuilding(planetId, surfaceId, dataId){
            const itemData = dataCaches.itemData.get(dataId);

            const container = document.createElement("DIV");
                container.classList.add("available-building");
                container.id = dataId;

                const title = document.createElement("DIV");
                    title.classList.add("available-building-title");
                    title.innerText = dataCaches.itemDataNames.get(dataId);
            container.appendChild(title);

                const description = document.createElement("DIV");
                    description.classList.add("available-building-description");
                    description.innerText = dataCaches.itemDataDescriptions.get(dataId);
            container.appendChild(description);

                container.appendChild(createEffect(itemData));
                container.appendChild(createConstructionRequirements(itemData.constructionRequirements));

                const buildButton = document.createElement("BUTTON");
                    buildButton.classList.add("construct-new-building-button");
                    buildButton.innerText = Localization.getAdditionalContent("construct-new-building");
                    buildButton.onclick = function(){
                        constructNewBuilding(planetId, surfaceId, dataId);
                    }
            container.appendChild(buildButton);
            return container;

            function createEffect(itemData){
                const container = document.createElement("DIV");
                    container.classList.add("available-building-effect");

                    switch(itemData.buildingType){
                        case "miscellaneous":
                            //TODO
                        break;
                        case "storage":
                            //TODO
                        break;
                        case "production":
                            //TODO
                        break;
                    }

                return container;
            }

            function createConstructionRequirements(constructionRequirements){
                const container = document.createElement("TABLE");
                    container.classList.add("available-building-construction-requirements");
                    container.classList.add("formatted-table");

                    const thead = document.createElement("THEAD");
                        const headRow = document.createElement("TR");
                            const headCell = document.createElement("TD");
                                headCell.colSpan = 2;
                                headCell.innerText = Localization.getAdditionalContent("construction-requirements");
                        headRow.appendChild(headCell);
                    thead.appendChild(headRow);
                container.appendChild(thead);

                    const tbody = document.createElement("TBODY");
                        new MapStream(constructionRequirements["1"].requiredResources)
                            .peek(function(resourceDataId, amount){console.log(resourceDataId + " - " + amount)})
                            .sorted(function(a, b){return dataCaches.itemDataNames.get(a.getKey()).localeCompare(dataCaches.itemDataNames.get(b.getKey()))})
                            .toListStream(createRow)
                            .forEach(function(node){tbody.appendChild(node)});
                container.appendChild(tbody);

                return container;

                function createRow(resourceDataId, amount){
                    const row = document.createElement("TR");
                        const resourceCell = document.createElement("TD");
                            resourceCell.innerText = dataCaches.itemDataNames.get(resourceDataId);
                    row.appendChild(resourceCell);

                        const amountCell = document.createElement("TD");
                            amountCell.innerText = amount;
                    row.appendChild(amountCell);
                    return row;
                }
            }
        }
    }

    function constructNewBuilding(planetId, surfaceId, dataId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_BUILDING_CONSTRUCT_NEW", {planetId: planetId, surfaceId: surfaceId}), {value: dataId});
            request.processValidResponse = function(){
                planetController.viewPlanet(planetId);
            }
        dao.sendRequestAsync(request);
    }

    function upgradeBuilding(planetId, buildingId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_BUILDING_UPGRADE", {planetId: planetId, buildingId: buildingId}));
            request.processValidResponse = function(){
                planetController.viewPlanet(planetId); //TODO Use WS event
            }
        dao.sendRequestAsync(request);
    }

    function cancelConstruction(planetId, buildingId, dataId){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("cancel-construction-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("cancel-construction-confirmation-dialog-detail", {buildingName: dataCaches.itemDataNames.get(dataId)}))
            .withConfirmButton(Localization.getAdditionalContent("cancel-construction-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("cancel-construction-cancel-button"));

        confirmationService.openDialog(
            "cancel-construction-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION", {planetId: planetId, buildingId: buildingId}));
                    request.processValidResponse = function(){
                        planetController.viewPlanet(planetId); //TODO use WS event
                    }
                dao.sendRequestAsync(request);
            }
        );
    }
})();