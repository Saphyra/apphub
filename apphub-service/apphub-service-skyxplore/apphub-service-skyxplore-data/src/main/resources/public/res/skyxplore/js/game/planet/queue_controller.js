(function QueueController(){
    const syncEngine = new SyncEngineBuilder()
        .withContainerId(ids.queue)
        .withGetKeyMethod(function(queueItem){return queueItem.itemId})
        .withCreateNodeMethod(createQueueItem)
        .withSortMethod((a, b) => {return a.totalPriority - b.totalPriority})
        .withIdPrefix("queue-item")
        .build();

    let openedPlanetId = null;

    pageLoader.addLoader(addHandlers, "QueueController WS event handlers");

    window.queueController = new function(){
        this.loadQueue = loadQueue;
    }

    function loadQueue(planetId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_GET_QUEUE", {planetId: planetId}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(queue){
                openedPlanetId = planetId;
                syncEngine.clear();
                syncEngine.addAll(queue);
            }
        dao.sendRequestAsync(request);
    }

    function createQueueItem(item){
        const node = document.createElement("DIV");
            node.classList.add('queue-item');
        switch(item.type){
            case "CONSTRUCTION":
                fillForConstruction(openedPlanetId, node, item);
            break;
            case "TERRAFORMATION":
                fillForTerraformation(openedPlanetId, node, item);
            break;
            default:
                throwException("IllegalArgument", "Not implemented type: " + item.type);
            break;
        }

        return node;

        function fillForConstruction(planetId, node, item){
            const title = document.createElement("DIV");
                title.classList.add("queue-item-title");
                title.innerText = dataCaches.itemDataNames.get(item.data.dataId) + " lvl " + item.data.currentLevel + " => " + (item.data.currentLevel + 1);
            node.appendChild(title);

            node.appendChild(createProgressBarWrapper(item));
            node.appendChild(createPrioritySlider(planetId, item));
            node.appendChild(createCancelButton(planetId, item));
        }

        function fillForTerraformation(planetId, node, item){
            const title = document.createElement("DIV");
                title.classList.add("queue-item-title");
                title.innerText = dataCaches.surfaceTypeLocalization.get(item.data.currentSurfaceType) + " => " + dataCaches.surfaceTypeLocalization.get(item.data.targetSurfaceType);
            node.appendChild(title);

            node.appendChild(createProgressBarWrapper(item));
            node.appendChild(createPrioritySlider(planetId, item));
            node.appendChild(createCancelButton(planetId, item));
        }

        function createProgressBarWrapper(item){
            const progressBarWrapper = document.createElement("DIV");
                progressBarWrapper.classList.add("queue-item-progress");
                const status = Math.floor(item.currentWorkPoints / item.requiredWorkPoints * 100);
                progressBarWrapper.appendChild(createProgressBar(status, status + "%"));
            return progressBarWrapper;
        }

        function createPrioritySlider(planetId, item){
            const prioritySliderWrapper = document.createElement("DIV");
                prioritySliderWrapper.classList.add("queue-item-priority");

                const prioritySliderLabel = document.createElement("SPAN");
                    prioritySliderLabel.innerText = Localization.getAdditionalContent("priority") + ": ";
            prioritySliderWrapper.appendChild(prioritySliderLabel);

                const prioritySliderInput = document.createElement("INPUT");
                    prioritySliderInput.type = "range";
                    prioritySliderInput.min = 1;
                    prioritySliderInput.max = 10;
                    prioritySliderInput.step = 1;
                    prioritySliderInput.value = item.ownPriority;
            prioritySliderWrapper.appendChild(prioritySliderInput);

                const prioritySliderValue = document.createElement("SPAN");
                    prioritySliderValue.innerText = item.ownPriority;
            prioritySliderWrapper.appendChild(prioritySliderValue);

                prioritySliderInput.onchange = function(){
                    prioritySliderValue.innerText = prioritySliderInput.value;
                    updatePriority(planetId, item.type, item.itemId, prioritySliderInput.value);
                }
            return prioritySliderWrapper;
        }

        function createCancelButton(planetId, item){
            const cancelButton = document.createElement("BUTTON");
                cancelButton.classList.add("cancel-queue-item-button");
                cancelButton.innerText = Localization.getAdditionalContent("cancel-production");
                cancelButton.onclick = function(){
                    cancelItem(planetId, item.type, item.itemId);
                }
            return cancelButton;
        }
    }

    function updatePriority(planetId, type, itemId, priority){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_SET_QUEUE_ITEM_PRIORITY", {planetId: planetId, type: type, itemId: itemId}), {value: priority});
        dao.sendRequestAsync(request);
    }

    function cancelItem(planetId, type, itemId){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("cancel-queue-item-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("cancel-queue-item-confirmation-dialog-detail"))
            .withConfirmButton(Localization.getAdditionalContent("cancel-queue-item-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("cancel-queue-item-cancel-button"));

        confirmationService.openDialog(
            "cancel-queue-item-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_CANCEL_QUEUE_ITEM", {planetId: planetId, type: type, itemId: itemId}));
                    request.processValidResponse = function(){
                    }
                dao.sendRequestAsync(request);
            }
        );
    }

    function addHandlers(){
        wsConnection.addHandler(new WebSocketEventHandler(
            function(eventName){return webSocketEvents.PLANET_QUEUE_ITEM_MODIFIED == eventName},
            queueItem => {syncEngine.add(queueItem)}
        ));

        wsConnection.addHandler(new WebSocketEventHandler(
            function(eventName){return webSocketEvents.PLANET_QUEUE_ITEM_DELETED == eventName},
            itemId => {syncEngine.remove(itemId)}
        ));
    }
})();