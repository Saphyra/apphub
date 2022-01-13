(function PlanetBuildingController(){
    window.planetBuildingController = new function(){
        this.loadBuildings = loadBuildings;
    }

    function loadBuildings(planetId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_GET_BUILDING_OVERVIEW", {planetId: planetId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(buildingOverviews){
                displayBuildingOverviews(buildingOverviews);
            }
        dao.sendRequestAsync(request);
    }

    function displayBuildingOverviews(buildingOverviews){
        const container = document.getElementById(ids.planetBuildingsContainer);
            container.innerHTML = "";

        new MapStream(buildingOverviews)
            .sorted(function(e1, e2){return dataCaches.surfaceTypeLocalization.get(e1.getKey()).localeCompare(dataCaches.surfaceTypeLocalization.get(e2.getKey()))})
            .map(createSurfaceBuildingOverview)
            .toListStream()
            .forEach(function(node){container.appendChild(node)});

        const totalUsedSlots = new MapStream(buildingOverviews)
            .toListStream()
            .flatMap(function(buildingOverview){return new Stream(buildingOverview.buildingDetails)})
            .count();
        const totalSlots = new MapStream(buildingOverviews)
            .toListStream()
            .map(function(buildingOverview){return buildingOverview.slots})
            .sum();
        const totalLevels = new MapStream(buildingOverviews)
            .toListStream()
            .flatMap(function(buildingOverview){return new Stream(buildingOverview.buildingDetails)})
            .map(function(buildingDetails){return buildingDetails.levelSum})
            .sum();

        document.getElementById(ids.planetBuildingsTotalUsedSlots).innerHTML = totalUsedSlots;
        document.getElementById(ids.planetBuildingsTotalSlots).innerHTML = totalSlots;
        document.getElementById(ids.planetBuildingsTotalLevel).innerHTML = totalLevels;

        function createSurfaceBuildingOverview(surfaceType, buildingOverview){
            const container = document.createElement("DIV");
                container.classList.add("planet-bar-list-item");

                const labels = document.createElement("DIV");
                    const surfaceTypeLabel = document.createElement("SPAN");
                        surfaceTypeLabel.innerHTML = dataCaches.surfaceTypeLocalization.get(surfaceType);
                labels.appendChild(surfaceTypeLabel);

                    const separatorLabel = document.createElement("SPAN");
                        separatorLabel.innerHTML = ": ";
                labels.appendChild(separatorLabel);

                    const usedSlotsLabel = document.createElement("SPAN");
                        usedSlotsLabel.innerHTML = buildingOverview.usedSlots;
                labels.appendChild(usedSlotsLabel);

                    const separatorLabel2 = document.createElement("SPAN");
                        separatorLabel2.innerHTML = " / ";
                labels.appendChild(separatorLabel2);

                    const totalSlotsLabel = document.createElement("SPAN");
                        totalSlotsLabel.innerHTML = buildingOverview.slots;
                labels.appendChild(totalSlotsLabel);

                    const expandButton = document.createElement("BUTTON");
                        expandButton.classList.add("window-close-button");
                        expandButton.innerHTML = "+";
                labels.appendChild(expandButton);
            container.appendChild(labels);

                const detailsTable = document.createElement("TABLE");
                    detailsTable.style.display = "none";

                    const headRow = document.createElement("TR");
                        const buildingNameHead = document.createElement("TH");
                            buildingNameHead.innerHTML = Localization.getAdditionalContent("planet-building-detail-name");
                    headRow.appendChild(buildingNameHead);

                        const levelSumHead = document.createElement("TH");
                            levelSumHead.innerHTML = Localization.getAdditionalContent("planet-building-detail-level-sum");
                    headRow.appendChild(levelSumHead);

                        const usedSlotsHead = document.createElement("TH");
                            usedSlotsHead.innerHTML = Localization.getAdditionalContent("planet-building-detail-used-slots");
                    headRow.appendChild(usedSlotsHead);
                detailsTable.appendChild(headRow);

                new Stream(buildingOverview.buildingDetails)
                    .sorted(function(a, b){return dataCaches.itemDataNames.get(a.dataId).localeCompare(dataCaches.itemDataNames.get(b.dataId))})
                    .map(createRow)
                    .forEach(function(node){detailsTable.appendChild(node)});

            container.appendChild(detailsTable);

            const methodSwitch = new Switch(
                function(){roll.rollInVertical(detailsTable, container)},
                function(){roll.rollOutVertical(detailsTable)}
            );

            expandButton.onclick = function(){
                methodSwitch.apply();
            }

            return container;

            function createRow(buildingDetails){
                const row = document.createElement("TR");
                    const buildingName = document.createElement("TD");
                        buildingName.innerHTML = dataCaches.itemDataNames.get(buildingDetails.dataId);
                row.appendChild(buildingName);

                    const levelSum = document.createElement("TD");
                        levelSum.innerHTML = buildingDetails.levelSum;
                row.appendChild(levelSum);

                    const usedSlots = document.createElement("TD");
                        usedSlots.innerHTML = buildingDetails.usedSlots;
                row.appendChild(usedSlots);
                return row;
            }
        }
    }
})();