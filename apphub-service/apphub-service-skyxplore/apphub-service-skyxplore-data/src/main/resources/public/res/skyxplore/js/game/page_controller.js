const SESSION_EXTENSION_ENABLED = true;

const ids = {
    //Platform
    pauseGameButton: "pause-game-button",
    resumeGameButton: "resume-game-button",

    //Map
    mapSvgContainer: "map-svg-container",
    mapContainer: "map-container",
    mapWrapper: "map-wrapper",

    //Chat
    chatButton: "chat-button",
    chatContainer: "chat",
    chatMessageInput: "send-message-input",
    generalChatButton: "general-chat-button",
    allianceChatButton: "alliance-chat-button",
    createChatRoomTitleInput: "create-chat-room-title-input",
    createChatRoomNoPlayers: "create-chat-room-no-players",
    createChatRoomPlayerList: "create-chat-room-players",
    createChatRoomContainer: "create-chat-room-container",
    chatRooms: "chat-rooms",
    chatMessageContainers: "chat-message-containers",

    //SolarSystem
    solarSystemSvgContainer: "solar-system-svg-container",
    solarSystemContainer: "solar-system-container",
    solarSystemWrapper: "solar-system-wrapper",
    solarSystem: "solar-system",
    solarSystemName: "solar-system-name",
    closePlanetButton: "close-planet-button",

    //Planet
    planet: "planet",
    planetName: "planet-name",
    planetSurfaceContainer: "planet-surface-container",
    planetSurfaceWrapper: "planet-surface-wrapper",
    planetMiddleBar: "planet-middle-bar",
    toggleEnergyDetailsButton: "toggle-energy-details-button",
    toggleLiquidDetailsButton: "toggle-liquid-details-button",
    toggleBulkDetailsButton: "toggle-bulk-details-button",
    planetStorageEnergyDetailsContainer: "planet-storage-energy-details-container",
    planetStorageEnergyDetailsContainerWrapper: "planet-storage-energy-details-container-wrapper",
    planetStorageLiquidDetailsContainer: "planet-storage-liquid-details-container",
    planetStorageLiquidDetailsContainerWrapper: "planet-storage-liquid-details-container-wrapper",
    planetStorageBulkDetailsContainer: "planet-storage-bulk-details-container",
    planetStorageBulkDetailsContainerWrapper: "planet-storage-bulk-details-container-wrapper",
    planetOpenPopulationOverviewButton: "planet-open-population-overview-button",
    planetPopulationOverviewProgressBarBackground: "planet-population-overview-progress-bar-background",
    planetPopulationOverviewActual: "planet-population-overview-actual",
    planetPopulationOverviewCapacity: "planet-population-overview-capacity",
    planetBuildingsContainer: "planet-buildings-container",
    planetBuildingsTotalUsedSlots: "planet-buildings-total-used-slots",
    planetBuildingsTotalSlots: "planet-buildings-total-slots",
    planetBuildingsTotalLevel: "planet-buildings-total-level",

    //StorageSettings
    closeStorageSettingsButton: "close-storage-settings-button",
    storageSettingsPriorityInput: "storage-settings-priority-input",
    storageSettingsPriorityValue: "storage-settings-priority-value",
    storageSettings: "storage-settings",
    storageSettingsResourceInput: "storage-settings-resource-input",
    storageSettingsBatchSizeInput: "storage-settings-batch-size-input",
    storageSettingsAmountInput: "storage-settings-amount-input",
    storageSettingsCreateButton: "create-storage-settings-button",
    noStorageSettings: "no-storage-settings",
    currentStorageSettingsContainer: "storage-settings-list",

    //Population overview
    closePopulationOverviewButton: "close-population-overview-button",
    populationOverview: "population-overview",
    populationOverviewSkillSelectionToggleButton: "population-overview-skill-selection-toggle-button",
    populationOverviewOrderToggleButton: "population-overview-order-toggle-button",
    populationOverviewSkillSelectionContainer: "population-overview-skill-selection-container",
    populationOverviewOrderContainer: "population-overview-order-container",
    populationOverviewSkillList: "population-overview-skill-list",
    populationOverviewOrderSkillListInput: "population-overview-order-skill-list-input",
    populationOverviewOrderStatListInput: "population-overview-order-stat-list-input",
    populationOverviewShowAllSkills: "population-overview-show-all-skills",
    populationOverviewHideAllSkills: "population-overview-hide-all-skills",
    populationOverviewCitizenList: "population-overview-citizen-list",

    //Construction
    construction: "construction",
    closeConstructionButton: "close-construction-button",
    availableBuildings: "available-buildings",

    //Terraforming
    terraformingPossibilities: "terraforming-possibilities",
    closeTerraformationButton: "close-terraformation-button",
    terraformation: "terraformation",

    //Queue
    queue: "planet-right-bar-content",
}

const webSocketEvents = {
    CHAT_SEND_MESSAGE: "skyxplore-game-chat-send-message",
    USER_JOINED: "skyxplore-game-user-joined",
    USER_LEFT: "skyxplore-game-user-left",
    CHAT_ROOM_CREATED: "skyxplore-game-chat-room-created",
    PAGE_OPENED: "skyxplore-game-page-opened",
    PLANET_QUEUE_ITEM_MODIFIED: "skyxplore-game-planet-queue-item-modified",
    PLANET_QUEUE_ITEM_DELETED: "skyxplore-game-planet-queue-item-deleted",
    SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED: "skyxplore-game-planet-surface-modified",
    SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED: "skyxplore-game-planet-storage-modified",
    SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED: "skyxplore-game-planet-citizen-modified",
    SKYXPLORE_GAME_PAUSED: "skyxplore-game-paused",
}

scriptLoader.loadScript("/res/common/js/web_socket.js");
scriptLoader.loadScript("/res/common/js/cache.js");
scriptLoader.loadScript("/res/common/js/localization/custom_localization.js");
scriptLoader.loadScript("/res/common/js/confirmation_service.js");
scriptLoader.loadScript("/res/common/js/animation/move_controller.js");
scriptLoader.loadScript("/res/common/js/animation/zoom_controller.js");
scriptLoader.loadScript("/res/common/js/sync_engine.js");
scriptLoader.loadScript("/res/skyxplore/js/game/caches.js");
scriptLoader.loadScript("/res/skyxplore/js/game/chat_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/map/map_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/solar_system/solar_system_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/planet_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/map/universe_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/surface_view_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/planet_storage_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/planet_population_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/planet_building_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/planet_priority_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/storage_settings_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/population_overview_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/construction_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/terraformation_controller.js");
scriptLoader.loadScript("/res/skyxplore/js/game/planet/queue_controller.js");

(function PageController(){
    window.wsConnection = new WebSocketConnection(Mapping.getEndpoint("WS_CONNECTION_SKYXPLORE_GAME"))
        .addHandler(new WebSocketEventHandler(
            (eventName)=>{return eventName == webSocketEvents.SKYXPLORE_GAME_PAUSED},
            setPausedStatus
        ));

    window.pageController = new function(){
        this.exitGame = exitGame;
        this.pauseGame = function(paused){
            const request = new Request(Mapping.getEndpoint("SKYXPLORE_GAME_PAUSE"), {value: paused});
                request.processValidResponse = function(){};
            dao.sendRequestAsync(request);
        }
    }

    function exitGame(){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("exit-game-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("exit-game-confirmation-dialog-detail"))
            .withConfirmButton(Localization.getAdditionalContent("exit-game-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("exit-game-decline-button"))

        confirmationService.openDialog(
            "exit-game-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("SKYXPLORE_EXIT_GAME"));
                    request.processValidResponse = function(){
                        window.location.href = Mapping.SKYXPLORE_PAGE;
                    }
                dao.sendRequestAsync(request);
            }
        );
    }

    function setPausedStatus(pausedStatus){
        const id = pausedStatus ? ids.resumeGameButton : ids.pauseGameButton;
        switchTab("pause-button", id)
    }

    $(document).ready(function(){
        eventProcessor.registerProcessor(new EventProcessor(
            (eventType) => {return eventType == events.PAGE_LOADERS_COMPLETED},
            () => {
                wsConnection.connect();
                wsConnection.waitForConnection(()=>{mapController.showMap()});
            },
            true,
            "Connect to WebSocket"
        ));

        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "skyxplore", fileName: "game"}));
        //document.addEventListener('contextmenu', event => event.preventDefault()); //TODO restore when development is finished
    });
})();

function IdMask(m){
    const mask = m;

    this.get = function(replacement){
        const result = mask.replace("*", replacement);
        return result;
    }
}

function createProgressBar(progress, content){
    const progressBarContainer = document.createElement("DIV");
            progressBarContainer.classList.add("progress-bar-container");

            const progressBarBackground = document.createElement("DIV");
                progressBarBackground.classList.add("progress-bar-background");
                progressBarBackground.style.width = progress + "%";
        progressBarContainer.appendChild(progressBarBackground);

            const progressBarText = document.createElement("DIV");
                progressBarText.classList.add("progress-bar-text");
                progressBarText.innerHTML = content;
        progressBarContainer.appendChild(progressBarText);
    return progressBarContainer;
}