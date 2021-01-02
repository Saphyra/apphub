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
                const node = document.createElement("DIV");
                    node.innerHTML = itemDataNameLocalization.get(storageSetting.dataId);
                return node;
            }
        }
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
        const dataId = document.getElementById(ids.storageSettingsResourceInput).value;
        const storageType = itemData.get(dataId).storageType;
        const maxAmount = availableStorage[storageType];

        const amountInput = document.getElementById(ids.storageSettingsAmountInput)
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