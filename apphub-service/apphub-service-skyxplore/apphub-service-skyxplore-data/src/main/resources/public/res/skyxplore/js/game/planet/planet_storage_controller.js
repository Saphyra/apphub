(function PlanetStorageController(){
    idMasks = {
        planetStorageCapacity: new IdMask("planet-storage-*-capacity"),
        planetStorageReserved: new IdMask("planet-storage-*-reserved-value"),
        planetStorageActual: new IdMask("planet-storage-*-actual-value"),
        planetStorageAllocated: new IdMask("planet-storage-*-allocated-value"),
        planetStorageActualProgressBar: new IdMask("planet-storage-*-actual-progress-bar"),
        planetStorageAllocatedProgressBar: new IdMask("planet-storage-*-allocated-progress-bar"),
        planetStorageReservedProgressBar: new IdMask("planet-storage-*-reserved-progress-bar"),
        planetStorageDetailContainer: new IdMask("planet-storage-*-details-container"),
    }

    pageLoader.addLoader(setUpEventListeners, "PlanetStorage set up event listeners");
    pageLoader.addLoader(function(){$(".planet-storage-reserved-label").text(Localization.getAdditionalContent("planet-storage-reserved-label"))}, "Fill PlanetStorageReservedLabels");
    pageLoader.addLoader(addHandlers, "PlanetStorageController add WS event handlers");

    window.planetStorageController = new function(){
        this.loadStorage = loadStorage;
    }

    function loadStorage(planetId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_PLANET_STORAGE", {planetId: planetId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(storage){
                displayStorage(storage);
            }
        dao.sendRequestAsync(request);
    }

    function displayStorage(storage){
        for(let storageType in storage){
            processStorage(storageType, storage[storageType]);
        }

        function processStorage(storageType, storageDetails){
            setBars(storageType, storageDetails);

            document.getElementById(idMasks.planetStorageCapacity.get(storageType)).innerHTML = storageDetails.capacity;
            document.getElementById(idMasks.planetStorageReserved.get(storageType)).innerHTML = storageDetails.reservedStorageAmount;
            document.getElementById(idMasks.planetStorageActual.get(storageType)).innerHTML = storageDetails.actualResourceAmount;
            document.getElementById(idMasks.planetStorageAllocated.get(storageType)).innerHTML = storageDetails.allocatedResourceAmount;

            fillDetailedResourceList(storageType, storageDetails.resourceDetails);

            function setBars(storageType, storageDetails){
                const actualWidth = (storageDetails.actualResourceAmount - storageDetails.allocatedResourceAmount) / storageDetails.capacity * 100;
                const allocatedWidth = storageDetails.allocatedResourceAmount / storageDetails.capacity * 100;
                const reservedWidth = storageDetails.reservedStorageAmount / storageDetails.capacity * 100;

                document.getElementById(idMasks.planetStorageActualProgressBar.get(storageType)).style.width = actualWidth + "%";

                const allocatedProgressBar = document.getElementById(idMasks.planetStorageAllocatedProgressBar.get(storageType));
                    allocatedProgressBar.style.left = actualWidth + "%";
                    allocatedProgressBar.style.width = allocatedWidth + "%";

                const reservedProgressBar = document.getElementById(idMasks.planetStorageReservedProgressBar.get(storageType));
                    reservedProgressBar.style.left = (actualWidth + allocatedWidth) + "%";
                    reservedProgressBar.style.width = reservedWidth + "%";
            }

            function fillDetailedResourceList(storageType, resourceDetails){
                const container = document.getElementById(idMasks.planetStorageDetailContainer.get(storageType));
                    container.innerHTML = "";

                    new Stream(resourceDetails)
                        .sorted(function(a, b){dataCaches.itemDataNames.get(a.dataId).localeCompare(dataCaches.itemDataNames.get(b.dataId))})
                        .map(createResourceDetailsNode)
                        .forEach(function(node){container.appendChild(node)});

                function createResourceDetailsNode(resourceDetail){
                    const node = document.createElement("DIV");
                        node.classList.add("planet-storage-storage-details-item");

                        const resourceName = document.createElement("DIV");
                            resourceName.innerHTML = dataCaches.itemDataNames.get(resourceDetail.dataId);
                    node.appendChild(resourceName);

                        const detailsContainer = document.createElement("UL");
                            const storedAmount = document.createElement("LI");
                                storedAmount.innerHTML = Localization.getAdditionalContent("stored-amount-label") + " (" + resourceDetail.allocatedResourceAmount + ") / ";
                                const storedAmountValue = document.createElement("SPAN");
                                    storedAmountValue.innerHTML = resourceDetail.actualAmount;
                            storedAmount.appendChild(storedAmountValue);
                        detailsContainer.appendChild(storedAmount);

                            const reservedAmount = document.createElement("LI");
                                const reservedAmountLabel = document.createElement("SPAN");
                                    reservedAmountLabel.innerHTML = Localization.getAdditionalContent("reserved-amount-label") + ": ";
                            reservedAmount.appendChild(reservedAmountLabel);
                                const reservedAmountValue = document.createElement("SPAN");
                                    reservedAmountValue.innerHTML = resourceDetail.reservedStorageAmount;
                            reservedAmount.appendChild(reservedAmountValue);
                        detailsContainer.appendChild(reservedAmount);
                    node.appendChild(detailsContainer);
                    return node;
                }
            }
        }
    }

    function setUpEventListeners(){
        setUpEnergy();
        setUpLiquid();
        setUpBulk();

        function setUpEnergy(){
            const container = document.getElementById(ids.planetStorageEnergyDetailsContainerWrapper);
            const element = document.getElementById(ids.planetStorageEnergyDetailsContainer);

            const sw = new Switch(
                () => roll.rollInVertical(element, container),
                () => roll.rollOutVertical(element)
            );

            document.getElementById(ids.toggleEnergyDetailsButton).onclick = function(){
                sw.apply();
            }
        }

        function setUpLiquid(){
            const container = document.getElementById(ids.planetStorageLiquidDetailsContainerWrapper);
            const element = document.getElementById(ids.planetStorageLiquidDetailsContainer);

            const sw = new Switch(
                () => roll.rollInVertical(element, container),
                () => roll.rollOutVertical(element)
            );

            document.getElementById(ids.toggleLiquidDetailsButton).onclick = function(){
                sw.apply();
            }
        }

        function setUpBulk(){
            const container = document.getElementById(ids.planetStorageBulkDetailsContainerWrapper);
            const element = document.getElementById(ids.planetStorageBulkDetailsContainer);

            const sw = new Switch(
                () => roll.rollInVertical(element, container),
                () => roll.rollOutVertical(element)
            );

            document.getElementById(ids.toggleBulkDetailsButton).onclick = function(){
                sw.apply();
            }
        }
    }

    function addHandlers(){
        wsConnection.addHandler(new WebSocketEventHandler(
            function(eventName){return webSocketEvents.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED == eventName},
            displayStorage
        ));
    }
})();