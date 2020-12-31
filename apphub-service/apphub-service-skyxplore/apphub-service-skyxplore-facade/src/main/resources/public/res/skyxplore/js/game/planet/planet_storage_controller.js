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

    window.planetStorageController = new function(){
        this.loadStorage = loadStorage;
    }

    eventProcessor.registerProcessor(
        new EventProcessor(
            function(eventType){return eventType == events.LOCALIZATION_LOADED},
            function(){
                $(".planet-storage-reserved-label").text(Localization.getAdditionalContent("planet-storage-reserved-label"));
            },
            true
        ).setName("planet-storage-reserved-label-filler")
    );

    $(document).ready(init);

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
                        .sorted(function(a, b){itemDataNameLocalization.get(a.dataId).localeCompare(itemDataNameLocalization.get(b.dataId))})
                        .map(createResourceDetailsNode)
                        .forEach(function(node){container.appendChild(node)});

                function createResourceDetailsNode(resourceDetail){
                    const node = document.createElement("DIV");
                        node.classList.add("planet-storage-storage-details-item");

                        const resourceName = document.createElement("DIV");
                            resourceName.innerHTML = itemDataNameLocalization.get(resourceDetail.dataId);
                    node.appendChild(resourceName);

                        const detailsContainer = document.createElement("UL");
                            const storedAmount = document.createElement("LI");
                                storedAmount.innerHTML = Localization.getAdditionalContent("stored-amount-label") + " (" + resourceDetail.allocatedResourceAmount + ")";
                                const storedAmountLabel = document.createElement("SPAN");
                                    storedAmountLabel.innerHTML = Localization.getAdditionalContent("stored-amount-label") + ": ";
                            storedAmount.appendChild(storedAmountLabel);
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
                    return node;
                }
            }
        }
    }

    function init(){
        document.getElementById(ids.toggleEnergyDetailsButton).onclick = function(){
            $("#" + ids.planetStorageEnergyDetailsContainer).toggle(); //TODO consider using fadeInVertical
        }
        document.getElementById(ids.toggleLiquidDetailsButton).onclick = function(){
            $("#" + ids.planetStorageLiquidDetailsContainer).toggle(); //TODO consider using fadeInVertical
        }
        document.getElementById(ids.toggleBulkDetailsButton).onclick = function(){
            $("#" + ids.planetStorageBulkDetailsContainer).toggle(); //TODO consider using fadeInVertical
        }
    }

    function IdMask(m){
        const mask = m;

        this.get = function(replacement){
            return mask.replace("*", replacement);
        }
    }
})();