scriptLoader.loadScript("/res/common/js/confirmation_service.js");

(function StorageSettingsController(){
    let availableStorage;
    let currentSettings;

    window.storageSettingsController = new function(){
        this.viewStorageSettings = viewStorageSettings;
        this.createStorageSettings = createStorageSettings;
    }

    $(document).ready(init);

    function viewStorageSettings(planetId){
        document.getElementById(ids.closeStorageSettingsButton).onclick = function(){
            planetController.viewPlanet(planetId);
        }

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_GET_STORAGE_SETTINGS", {planetId: planetId}));
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(storageSettings){
                displayStorageSettings(storageSettings);
            }
        dao.sendRequestAsync(request);

        switchTab("main-tab", ids.storageSettings);
    }

    function displayStorageSettings(storageSettings){
        availableStorage = storageSettings.availableStorage;
        currentSettings = new Stream(storageSettings.currentSettings)
            .toMap(function(setting){return setting.dataId}, function(setting){return setting.targetAmount});

        setUpCreatePanel(storageSettings);
        displayCurrentSettings(storageSettings);

        function setUpCreatePanel(storageSettings){
            const resourceSelectMenu = document.getElementById(ids.storageSettingsResourceInput);
                resourceSelectMenu.innerHTML = "";

                new Stream(storageSettings.availableResources)
                    .sorted(function(a, b){return itemDataNameLocalization.get(a).localeCompare(itemDataNameLocalization.get(b))})
                    .map(createOption)
                    .forEach(function(node){resourceSelectMenu.appendChild(node)})

            updateMaxResourceAmount();
            setBachSize();
            setPriority();
            $(".create-storage-setting-input").prop("disabled", storageSettings.availableResources.length == 0);

            function createOption(dataId){
                const option = document.createElement("OPTION");
                option.value = dataId;
                option.innerHTML = itemDataNameLocalization.get(dataId);
                return option;
            }
        }

        function displayCurrentSettings(storageSettings){
            const display = Object.keys(storageSettings.currentSettings).length ? "none" : "block";
                document.getElementById(ids.noStorageSettings).style.display = display;

            const container = document.getElementById(ids.currentStorageSettingsContainer);
                container.innerHTML = "";

                new Stream(storageSettings.currentSettings)
                    .sorted(function(a, b){return itemDataNameLocalization.get(a.dataId).localeCompare(itemDataNameLocalization.get(b.dataId))})
                    .map(createStorageSetting)
                    .forEach(function(node){container.appendChild(node)});

            function createStorageSetting(storageSetting){
                const dataId = storageSetting.dataId;
                const storageType = itemData.get(dataId).storageType;
                const maxAmount = availableStorage[storageType] + storageSetting.targetAmount;

                const node = document.createElement("DIV");
                    node.classList.add("storage-setting");

                    const title = document.createElement("H3");
                        title.innerHTML = itemDataNameLocalization.get(storageSetting.dataId);
                node.appendChild(title);

                    const inputContainer = document.createElement("DIV");
                        const amountLabel = document.createElement("LABEL");
                            const amountTitle = document.createElement("SPAN");
                                amountTitle.innerHTML = Localization.getAdditionalContent("storage-setting-amount") + ": ";
                        amountLabel.appendChild(amountTitle);
                            const amountInput = document.createElement("INPUT");
                                amountInput.type = "number";
                                amountInput.max = maxAmount;
                                amountInput.step = 1;
                                amountInput.value = storageSetting.targetAmount;
                        amountLabel.appendChild(amountInput);
                    inputContainer.appendChild(amountLabel);

                        const batchSizeLabel = document.createElement("LABEL");
                            const batchSizeTitle = document.createElement("SPAN");
                                batchSizeTitle.innerHTML = Localization.getAdditionalContent("storage-setting-batch-size") + ": ";
                        batchSizeLabel.appendChild(batchSizeTitle);
                            const batchSizeInput = document.createElement("INPUT");
                                batchSizeInput.type = "number";
                                batchSizeInput.min = 1;
                                batchSizeInput.step = 1;
                                batchSizeInput.value = storageSetting.batchSize;
                        batchSizeLabel.appendChild(batchSizeInput);
                    inputContainer.appendChild(batchSizeLabel);

                        const priorityLabel = document.createElement("LABEL");
                            const priorityTitle = document.createElement("SPAN");
                                priorityTitle.innerHTML = Localization.getAdditionalContent("storage-setting-priority") + ": ";
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
                            saveButton.innerHTML = Localization.getAdditionalContent("save-storage-setting");
                    buttonContainer.appendChild(saveButton);

                        const deleteButton = document.createElement("BUTTON");
                            deleteButton.innerHTML = Localization.getAdditionalContent("delete-storage-setting");
                    buttonContainer.appendChild(deleteButton);
                node.appendChild(buttonContainer);

                amountInput.onchange = function(){
                    if(amountInput.value > maxAmount){
                        amountInput.value = maxAmount;
                    }
                }

                batchSizeInput.onchange = function(){
                    if(batchSizeInput.value < 1){
                        batchSizeInput.value = 1;
                    }
                }

                priorityInput.onchange = function(){
                    priorityValue.innerHTML = priorityInput.value;
                }

                saveButton.onclick = function(){updateStorageSetting(storageSetting.storageSettingId, amountInput.value, batchSizeInput.value, priorityInput.value)};
                deleteButton.onclick = function(){deleteStorageSetting(storageSetting.storageSettingId, storageSetting.dataId)};

                return node;
            }
        }
    }

    function updateStorageSetting(storageSettingId, targetAmount, batchSize, priority){
        if(targetAmount < 1){
            notificationService.showError(Localization.getAdditionalContent("storage-setting-amount-too-low"));
            return;
        }

        const payload = {
            storageSettingId: storageSettingId,
            targetAmount: targetAmount,
            batchSize: batchSize,
            priority: priority
        }

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_EDIT_STORAGE_SETTING", {planetId: planetController.getOpenedPlanetId(), storageSettingId: storageSettingId}), payload);
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("storage-setting-saved"));
                viewStorageSettings(planetController.getOpenedPlanetId());
            }
        dao.sendRequestAsync(request);
    }

    function deleteStorageSetting(storageSettingId, dataId){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("delete-storage-setting-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("delete-storage-setting-confirmation-dialog-detail", {resource: itemDataNameLocalization.get(dataId)}))
            .withConfirmButton(Localization.getAdditionalContent("delete-storage-setting-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("delete-storage-setting-decline-button"))


        confirmationService.openDialog(
            "delete-storage-setting-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_DELETE_STORAGE_SETTING", {planetId: planetController.getOpenedPlanetId(), storageSettingId: storageSettingId}));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(Localization.getAdditionalContent("storage-setting-deleted"));
                        viewStorageSettings(planetController.getOpenedPlanetId());
                    }
                dao.sendRequestAsync(request);
            }
        );
    }

    function createStorageSettings(){
        const dataId = document.getElementById(ids.storageSettingsResourceInput).value;
        const amount = document.getElementById(ids.storageSettingsAmountInput).value;
        const batchSize = document.getElementById(ids.storageSettingsBatchSizeInput).value;
        const priority = document.getElementById(ids.storageSettingsPriorityInput).value;

        const payload = {
            dataId: dataId,
            targetAmount: amount,
            batchSize: batchSize,
            priority: priority
        }

        if(amount < 1){
            notificationService.showError(Localization.getAdditionalContent("storage-setting-amount-too-low"));
            return;
        }

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_PLANET_CREATE_STORAGE_SETTING", {planetId: planetController.getOpenedPlanetId()}), payload);
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("storage-setting-created"));
                viewStorageSettings(planetController.getOpenedPlanetId());
            }
        dao.sendRequestAsync(request);
    }

    function updateMaxResourceAmount(){
        const amountInput = document.getElementById(ids.storageSettingsAmountInput);

        const dataId = document.getElementById(ids.storageSettingsResourceInput).value;
        if(dataId == ""){
            amountInput.value = 0;
            return;
        }
        const storageType = itemData.get(dataId).storageType;
        const maxAmount = availableStorage[storageType];


            amountInput.max = maxAmount;
            amountInput.value = maxAmount == 0 ? 0 : 1;

        const disabled = maxAmount == 0;
        document.getElementById(ids.storageSettingsAmountInput).disabled = disabled;
        document.getElementById(ids.storageSettingsCreateButton).disabled = disabled;
    }

    function setBachSize(){
        document.getElementById(ids.storageSettingsBatchSizeInput).value = 10;
    }

    function setPriority(){
        document.getElementById(ids.storageSettingsPriorityInput).value = 5;
        document.getElementById(ids.storageSettingsPriorityValue).innerHTML = 5;
    }

    function init(){
        const priorityInput = document.getElementById(ids.storageSettingsPriorityInput);
            priorityInput.onchange = function(){
                document.getElementById(ids.storageSettingsPriorityValue).innerHTML = priorityInput.value;
            }
        document.getElementById(ids.storageSettingsResourceInput).onchange = function(){
            updateMaxResourceAmount();
        }

        const amountInput = document.getElementById(ids.storageSettingsAmountInput);
            amountInput.onchange = function(){
                if(amountInput.value > amountInput.max){
                    amountInput.value = amountInput.max;
                }
            }

        const batchSizeInput = document.getElementById(ids.storageSettingsBatchSizeInput);
            batchSizeInput.onchange = function(){
                if(batchSizeInput.value < 1){
                    batchSizeInput.value = 1;
                }
            }
    }
})();