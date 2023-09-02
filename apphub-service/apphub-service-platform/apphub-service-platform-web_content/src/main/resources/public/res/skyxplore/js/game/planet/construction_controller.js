(function ConstructionController(){
    const MODE_AVAILABLE = "available";
    const MODE_UPGRADE = "upgrade";

    let openedPlanetId;
    let openedBuildingId;

    window.constructionController = new function(){
        this.fillAvailableBuildings = fillAvailableBuildings;
        this.cancelConstruction = cancelConstruction;
        this.cancelDeconstruction = cancelDeconstruction;
        this.openUpgradeBuildingWindow = openUpgradeBuildingWindow;
        this.upgradeBuilding = upgradeBuilding
    }

    function fillAvailableBuildings(planetId, surfaceType, surfaceId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_DATA_AVAILABLE_BUILDINGS", {surfaceType: surfaceType}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(availableBuildings){
                const container = document.getElementById(ids.availableBuildings);
                    container.innerHTML = "";

                    new Stream(availableBuildings)
                        .sorted(function(a, b){dataCaches.itemDataNames.get(a).localeCompare(dataCaches.itemDataNames.get(b))})
                        .map(function(dataId){return createAvailableBuilding(planetId, surfaceId, surfaceType, dataId)})
                        .forEach(function(node){container.appendChild(node)});
            }
        dao.sendRequestAsync(request);

        function createAvailableBuilding(planetId, surfaceId, surfaceType, dataId){
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

                container.appendChild(createEffect(surfaceType, itemData, 1, MODE_AVAILABLE).getNode());
                container.appendChild(createConstructionRequirements(itemData.constructionRequirements["1"], MODE_AVAILABLE));

                const buildButton = document.createElement("BUTTON");
                    buildButton.classList.add("construct-new-building-button");
                    buildButton.innerText = localization.getAdditionalContent("construct-new-building");
                    buildButton.onclick = function(){
                        constructNewBuilding(planetId, surfaceId, dataId);
                    }
            container.appendChild(buildButton);
            return container;
        }
    }

    function constructNewBuilding(planetId, surfaceId, dataId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_BUILDING_CONSTRUCT_NEW", {planetId: planetId, surfaceId: surfaceId}), {value: dataId});
            request.processValidResponse = function(){
                planetController.openPlanetWindow();
            }
        dao.sendRequestAsync(request);
    }

    function openUpgradeBuildingWindow(planetId, surfaceType, buildingId, buildingDataId, currentLevel){
        openedPlanetId = planetId;
        openedBuildingId = buildingId;

        const newLevel = currentLevel + 1;
        const itemData = dataCaches.itemData.get(buildingDataId);

        document.getElementById(ids.closeUpgradeBuildingButton).onclick = function(){
            planetController.openPlanetWindow();
        };
        
        document.getElementById(ids.upgradeBuildingDetailsTitleName).innerText = dataCaches.itemDataNames.get(buildingDataId);
        document.getElementById(ids.upgradeBuildingDetailsTitleCurrentLevel).innerText = currentLevel;
        document.getElementById(ids.upgradeBuildingDetailsTitleNextLevel).innerText = currentLevel + 1;
        
        document.getElementById(ids.upgradeBuildingDetailsDescription).innerText = dataCaches.itemDataDescriptions.get(buildingDataId);

        const constructionRequirementsContainer = document.getElementById(ids.upgradeBuildingDetailsConstructionCost);
            constructionRequirementsContainer.innerHTML = "";
            constructionRequirementsContainer.appendChild(createConstructionRequirements(itemData.constructionRequirements[newLevel], MODE_UPGRADE));

        const effectContainer = document.getElementById(ids.upgradeBuildingDetailsEffect);
            effectContainer.innerHTML = "";
            effectContainer.appendChild(createEffect(surfaceType, itemData, newLevel, MODE_UPGRADE).getNode());

        switchTab("main-tab", ids.upgradeBuilding);
    }

    function upgradeBuilding(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_BUILDING_UPGRADE", {planetId: openedPlanetId, buildingId: openedBuildingId}));
            request.processValidResponse = function(){
                planetController.openPlanetWindow();
            }
        dao.sendRequestAsync(request);
    }
    
    function createConstructionRequirements(constructionRequirements, mode){
        const container = document.createElement("TABLE");
            container.classList.add(mode + "-building-construction-requirements");
            container.classList.add("formatted-table");

            const thead = document.createElement("THEAD");
                const headRow = document.createElement("TR");
                    const headCell = document.createElement("TH");
                        headCell.colSpan = 2;
                        headCell.innerText = localization.getAdditionalContent("construction-requirements");
                headRow.appendChild(headCell);
            thead.appendChild(headRow);
        container.appendChild(thead);

            const tbody = document.createElement("TBODY");
                new MapStream(constructionRequirements.requiredResources)
                    .sorted(function(a, b){return dataCaches.itemDataNames.get(a.getKey()).localeCompare(dataCaches.itemDataNames.get(b.getKey()))})
                    .toListStream((resourceDataId, amount) => {return createRow(dataCaches.itemDataNames.get(resourceDataId), amount)})
                    .forEach(function(node){tbody.appendChild(node)});

                tbody.appendChild(createRow(localization.getAdditionalContent("required-work-points"), constructionRequirements.requiredWorkPoints));
        container.appendChild(tbody);

        return container;

        function createRow(name, amount){
            const row = document.createElement("TR");
                const resourceCell = document.createElement("TD");
                    resourceCell.innerText = name;
            row.appendChild(resourceCell);

                const amountCell = document.createElement("TD");
                    amountCell.innerText = amount;
            row.appendChild(amountCell);
            return row;
        }
    }

    function createEffect(surfaceType, itemData, level, mode){
        return domBuilder.create("DIV")
            .addClass(mode + "-building-effect")
            .appendChild(() => {
                switch(itemData.buildingType){
                    case "miscellaneous":
                        return createMiscBuildingEffect(itemData, level, mode);
                    break;
                    case "storage":
                        return createStorageBuildingEffect(itemData, level, mode);
                    break;
                    case "production":
                        return createProductionBuildingEffect(surfaceType, itemData, level, mode);
                    break;
                }
            });

        function createMiscBuildingEffect(itemData, level, mode){
            return domBuilder.create("TABLE")
                .addClass("formatted-table")
                .addClass(mode + "-building-effect-misc")
                .appendChild(domBuilder.create("TBODY")
                    .appendChildren(createForBuilding(itemData, level))
                );

            function createForBuilding(itemData, level){
                switch(itemData.id){
                    case "community_center":
                        return new Stream(["seats", "moraleRechargeMultiplier", "energyUsage"])
                            .map((id) => createRow(localization.getAdditionalContent(id), itemData.data[id]));
                    case "hospital":
                        const result = [];

                        result.push(createRow(localization.getAdditionalContent("beds"), itemData.data.beds));

                        const healRow = domBuilder.create("TR")
                            .appendChild(domBuilder.create("TD", localization.getAdditionalContent("heal")))
                            .appendChild(domBuilder.create("TD")
                                .appendChild(domBuilder.create("DIV", localization.getAdditionalContent("energyUsage") + ": " + itemData.data.heal.energyUsage))
                                .appendChild(domBuilder.create("DIV", localization.getAdditionalContent("regenerationPerSecond") + ": " + itemData.data.heal.regenerationPerSecond))
                            );
                        result.push(healRow);

                        const birthRow = domBuilder.create("TR")
                            .appendChild(domBuilder.create("TD", localization.getAdditionalContent("birth")))
                            .appendChild(domBuilder.create("TD")
                                .appendChild(domBuilder.create("DIV", localization.getAdditionalContent("maternityLeave") + ": " + itemData.data.birth.maternityLeaveSeconds))
                                .appendChild(domBuilder.create("DIV", localization.getAdditionalContent("required-work-points") + ": " + itemData.data.birth.constructionRequirements.requiredWorkPoints))
                                .appendChildren(new MapStream(itemData.data.birth.constructionRequirements.requiredResources)
                                    .sorted((a, b) => {return dataCaches.itemDataNames.get(a.getKey()).localeCompare(dataCaches.itemDataNames.get(b.getKey()))})
                                    .toListStream((dataId, amount) => {return domBuilder.create("DIV", amount + " x " + dataCaches.itemDataNames.get(dataId))})
                                )
                            );
                        result.push(birthRow);

                        return result;
                    case "restaurant":
                        return new Stream(["seats", "satietyRechargeMultiplier", "energyUsage"])
                            .map((id) => createRow(localization.getAdditionalContent(id), itemData.data[id]));
                    case "school":
                        return new Stream(["desks", "experiencePerSecond", "energyUsage"])
                            .map((id) => createRow(localization.getAdditionalContent(id), itemData.data[id]));
                    default:
                        throwException("IllegalArgument", "Unhandled misc building " + itemData.dataId);
                }

                function createRow(label, value){
                    return domBuilder.create("TR")
                        .appendChild(domBuilder.create("TD", label))
                        .appendChild(domBuilder.create("TD", value))
                }
            }
        }

        function createStorageBuildingEffect(itemData, level, mode){
            return domBuilder.create("TABLE")
                .addClass("formatted-table")
                .addClass(mode + "-building-effect-storage")
                .appendChild(domBuilder.create("THEAD")
                    .appendChild(domBuilder.create("TR")
                        .appendChild(domBuilder.create("TH")
                            .attr("colSpan", 2)
                            .innerText(localization.getAdditionalContent("building-effect-title-storage"))
                        )
                    )
                )
                .appendChild(domBuilder.create("TBODY")
                    .appendChild(domBuilder.create("TR")
                        .appendChild(domBuilder.create("TD")
                            .innerText(dataCaches.storageTypeLocalization.get(itemData.stores)))
                        .appendChild(domBuilder.create("TD")
                            .innerText(level * itemData.capacity))
                    )
                )
        }

        function createProductionBuildingEffect(surfaceType, itemData, level, mode){
            const table = document.createElement("TABLE");
                table.classList.add("formatted-table");
                table.classList.add(mode + "-building-effect-production");

                const tableHead = document.createElement("THEAD");
                    const titleRow = document.createElement("TR");
                        const titleCell = document.createElement("TH");
                            titleCell.innerText = localization.getAdditionalContent("building-effect-title-production");
                            titleCell.colSpan = 2;
                    titleRow.appendChild(titleCell);
                tableHead.appendChild(titleRow);
            table.appendChild(tableHead);

                    const headerRow = document.createElement("TR");
                        const producedItemHeader = document.createElement("TH");
                            producedItemHeader.innerText = localization.getAdditionalContent("building-effect-production-header-produced-item");
                    headerRow.appendChild(producedItemHeader);

                        const productionRequirementsHeader = document.createElement("TH");
                            productionRequirementsHeader.innerText = localization.getAdditionalContent("building-effect-production-header-production-requirements");
                    headerRow.appendChild(productionRequirementsHeader);
                tableHead.appendChild(headerRow);
            table.appendChild(tableHead);

                const tableBody = document.createElement("TBODY");
                    fetchProductions(surfaceType, itemData, level)
                        .forEach((node) => tableBody.appendChild(node));
            table.appendChild(tableBody);

            return table;

            function fetchProductions(surfaceType, itemData, level){
                return new Stream(Object.keys(itemData.gives))
                    .filter((itemId) => {return itemData.gives[itemId].placed.indexOf(surfaceType) > -1})
                    .map((itemId) => {return createProductionRequirementRow(itemId, itemData.gives[itemId], level)});

                function createProductionRequirementRow(itemId, gives, level){
                    const row = document.createElement("TR");
                        const producedItemCell = document.createElement("TD");
                            producedItemCell.innerText = gives.amount + " x " + dataCaches.itemDataNames.get(itemId);
                    row.appendChild(producedItemCell);

                        const constructionRequirementsCell = document.createElement("TD");
                            new MapStream(gives.constructionRequirements.requiredResources)
                                .sorted((a, b) => {return dataCaches.itemDataNames.get(a.getKey()).localeCompare(dataCaches.itemDataNames.get(b.getKey()))})
                                .toListStream((requiredItemId, amount) => {return createElementWithText("DIV", dataCaches.itemDataNames.get(requiredItemId) + ": " + amount)})
                                .forEach((node) => constructionRequirementsCell.appendChild(node));

                        constructionRequirementsCell.appendChild(createElementWithText("DIV", localization.getAdditionalContent("required-work-points") + ": " + gives.constructionRequirements.requiredWorkPoints));

                    row.appendChild(constructionRequirementsCell);
                    return row;
                }
            }
        }
    }

    function cancelConstruction(planetId, buildingId, dataId){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("cancel-construction-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("cancel-construction-confirmation-dialog-detail", {buildingName: dataCaches.itemDataNames.get(dataId)}))
            .withConfirmButton(localization.getAdditionalContent("cancel-construction-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("cancel-construction-cancel-button"));

        confirmationService.openDialog(
            "cancel-construction-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("SKYXPLORE_BUILDING_CANCEL_CONSTRUCTION", {planetId: planetId, buildingId: buildingId}));
                dao.sendRequestAsync(request);
            }
        );
    }

    function cancelDeconstruction(planetId, buildingId, dataId){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("cancel-deconstruction-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("cancel-deconstruction-confirmation-dialog-detail", {buildingName: dataCaches.itemDataNames.get(dataId)}))
            .withConfirmButton(localization.getAdditionalContent("cancel-deconstruction-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("cancel-deconstruction-cancel-button"));

        return new Promise((resolve, reject) => {
            confirmationService.openDialog(
                "cancel-deconstruction-confirmation-dialog",
                confirmationDialogLocalization,
                function(){
                    const request = new Request(Mapping.getEndpoint("SKYXPLORE_BUILDING_CANCEL_DECONSTRUCTION", {planetId: planetId, buildingId: buildingId}));
                        request.convertResponse = jsonConverter;
                        request.processValidResponse = function(surface){
                            resolve(surface);
                        }
                    dao.sendRequestAsync(request);
                }
            );
        });
    }
})();