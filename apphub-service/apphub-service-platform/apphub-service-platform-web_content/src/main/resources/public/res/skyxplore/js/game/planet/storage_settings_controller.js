(function StorageSettingsController(){
    const PAGE_NAME = "PLANET_STORAGE_SETTINGS";

    const availableResourcesSyncEngine = new SyncEngineBuilder()
        .withContainerId(ids.storageSettingsResourceInput)
        .withGetKeyMethod(availableResource => {return availableResource})
        .withCreateNodeMethod(createAvailableResourceOption)
        .withSortMethod((a, b) => {return dataCaches.itemDataNames.get(a).localeCompare(dataCaches.itemDataNames.get(b))})
        .withIdPrefix("available-resource")
        .build();

    const storageSettingsSyncEngine = new SyncEngineBuilder()
        .withContainerId(ids.currentStorageSettingsContainer)
        .withGetKeyMethod(storageSetting => {return storageSetting.storageSettingId})
        .withCreateNodeMethod(createStorageSetting)
        .withSortMethod((a, b) => {return dataCaches.itemDataNames.get(a.dataId).localeCompare(dataCaches.itemDataNames.get(b.dataId))})
        .withIdPrefix("storage-setting")
        .build();

    pageLoader.addLoader(setUpEventListeners, "StorageSettings set up event listeners");
    pageLoader.addLoader(()=>{document.getElementById(ids.closeStorageSettingsButton).onclick = function(){planetController.openPlanetWindow()}}, "Close storage settings");

    window.storageSettingsController = new function(){
        this.viewStorageSettings = viewStorageSettings;
        this.createStorageSettings = createStorageSettings;
    }

    function viewStorageSettings(planetId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_GET_STORAGE_SETTINGS", {planetId: planetId}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(storageSettings){
                displayStorageSettings(storageSettings);
                switchTab("main-tab", ids.storageSettings);
            }
        dao.sendRequestAsync(request);
    }

    function displayStorageSettings(storageSettings){
        availableResourcesSyncEngine.addAll(storageSettings.availableResources);
        storageSettingsSyncEngine.addAll(storageSettings.currentSettings);
        resetPanels();
    }
    
    function resetPanels(){
        const display = storageSettingsSyncEngine.size() > 0 ? "none" : "block";
            document.getElementById(ids.noStorageSettings).style.display = display;
        setPriority();
        $(".create-storage-setting-input").prop("disabled", availableResourcesSyncEngine.size() == 0);
    }

    function createAvailableResourceOption(dataId){
        const option = document.createElement("OPTION");
        option.value = dataId;
        option.innerHTML = dataCaches.itemDataNames.get(dataId);
        return option;
    }

    function createStorageSetting(storageSetting){
        const dataId = storageSetting.dataId;
        const storageType = dataCaches.itemData.get(dataId).storageType;

        const node = document.createElement("DIV");
            node.classList.add("storage-setting");

            const title = document.createElement("H3");
                title.innerHTML = dataCaches.itemDataNames.get(storageSetting.dataId);
        node.appendChild(title);

            const inputContainer = document.createElement("DIV");
                const amountLabel = document.createElement("LABEL");
                    const amountTitle = document.createElement("SPAN");
                        amountTitle.innerHTML = localization.getAdditionalContent("storage-setting-amount") + ": ";
                amountLabel.appendChild(amountTitle);
                    const amountInput = document.createElement("INPUT");
                        amountInput.type = "number";
                        amountInput.step = 1;
                        amountInput.min = 1;
                        amountInput.value = storageSetting.targetAmount;
                amountLabel.appendChild(amountInput);
            inputContainer.appendChild(amountLabel);

                const priorityLabel = document.createElement("LABEL");
                    const priorityTitle = document.createElement("SPAN");
                        priorityTitle.innerHTML = localization.getAdditionalContent("storage-setting-priority") + ": ";
                priorityLabel.appendChild(priorityTitle);
                    const priorityInput = document.createElement("INPUT");
                        priorityInput.type = "range";
                        priorityInput.min = 1;
                        priorityInput.max = 10;
                        priorityInput.step = 1;
                        priorityInput.value = storageSetting.priority;
                priorityLabel.appendChild(priorityInput);
                    const priorityValue = document.createElement("SPAN");
                        priorityValue.innerHTML = storageSetting.priority;
                priorityLabel.appendChild(priorityValue);
            inputContainer.appendChild(priorityLabel);
        node.appendChild(inputContainer);

            const buttonContainer = document.createElement("DIV");
                const saveButton = document.createElement("BUTTON");
                    saveButton.innerHTML = localization.getAdditionalContent("save-storage-setting");
            buttonContainer.appendChild(saveButton);

                const deleteButton = document.createElement("BUTTON");
                    deleteButton.innerHTML = localization.getAdditionalContent("delete-storage-setting");
            buttonContainer.appendChild(deleteButton);

                const resetButton = document.createElement("BUTTON");
                    resetButton.innerText = localization.getAdditionalContent("reset");
                    resetButton.onclick = function(){
                        storageSettingsSyncEngine.renderNode(storageSetting.storageSettingId);
                    }
            buttonContainer.appendChild(resetButton);
        node.appendChild(buttonContainer);

        priorityInput.onchange = function(){
            priorityValue.innerHTML = priorityInput.value;
        }

        saveButton.onclick = function(){updateStorageSetting(dataId, storageSetting.storageSettingId, amountInput.value, priorityInput.value)};
        deleteButton.onclick = function(){deleteStorageSetting(storageSetting.storageSettingId, storageSetting.dataId)};

        return node;
    }

    function updateStorageSetting(dataId, storageSettingId, targetAmount, priority){
        if(targetAmount < 1){
            notificationService.showError(localization.getAdditionalContent("storage-setting-amount-too-low"));
            return;
        }

        const payload = {
            dataId: dataId,
            storageSettingId: storageSettingId,
            targetAmount: targetAmount,
            priority: priority
        }

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_EDIT_STORAGE_SETTING", {planetId: planetController.getOpenedPlanetId()}), payload);
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(storageSetting){
                notificationService.showSuccess(localization.getAdditionalContent("storage-setting-saved"));
                storageSettingsSyncEngine.add(storageSetting);
                resetPanels();
            }
        dao.sendRequestAsync(request);
    }

    function deleteStorageSetting(storageSettingId, dataId){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("delete-storage-setting-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("delete-storage-setting-confirmation-dialog-detail", {resource: dataCaches.itemDataNames.get(dataId)}))
            .withConfirmButton(localization.getAdditionalContent("delete-storage-setting-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("delete-storage-setting-decline-button"))

        confirmationService.openDialog(
            "delete-storage-setting-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_DELETE_STORAGE_SETTING", {planetId: planetController.getOpenedPlanetId(), storageSettingId: storageSettingId}));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(localization.getAdditionalContent("storage-setting-deleted"));
                        storageSettingsSyncEngine.remove(storageSettingId);
                        availableResourcesSyncEngine.add(dataId);
                        resetPanels();
                    }
                dao.sendRequestAsync(request);
            }
        );
    }

    function createStorageSettings(){
        const dataId = document.getElementById(ids.storageSettingsResourceInput).value;
        const amount = document.getElementById(ids.storageSettingsAmountInput).value;
        const priority = document.getElementById(ids.storageSettingsPriorityInput).value;

        const payload = {
            dataId: dataId,
            targetAmount: amount,
            priority: priority
        }

        if(amount < 0){
            notificationService.showError(localization.getAdditionalContent("storage-setting-amount-too-low"));
            return;
        }

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_CREATE_STORAGE_SETTING", {planetId: planetController.getOpenedPlanetId()}), payload);
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(storageSetting){
                notificationService.showSuccess(localization.getAdditionalContent("storage-setting-created"));
                storageSettingsSyncEngine.add(storageSetting);
                availableResourcesSyncEngine.remove(storageSetting.dataId);
                resetPanels();
            }
        dao.sendRequestAsync(request);
    }

    function setPriority(){
        document.getElementById(ids.storageSettingsPriorityInput).value = 5;
        document.getElementById(ids.storageSettingsPriorityValue).innerHTML = 5;
    }

    function setUpEventListeners(){
        const priorityInput = document.getElementById(ids.storageSettingsPriorityInput);
            priorityInput.onchange = function(){
                document.getElementById(ids.storageSettingsPriorityValue).innerHTML = priorityInput.value;
            }
        const amountInput = document.getElementById(ids.storageSettingsAmountInput);
            amountInput.onchange = function(){
                if(amountInput.value < 0){
                    amountInput.value = 0;
                }
            }
    }
})();