scriptLoader.loadScript("/res/common/js/web_socket.js");
scriptLoader.loadScript("/res/common/js/cache.js");
scriptLoader.loadScript("/res/common/js/localization/custom_localization.js");
scriptLoader.loadScript("/res/common/js/confirmation_service.js");
scriptLoader.loadScript("/res/common/js/animation/move_controller.js");
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

(function PageController(){
    window.SESSION_EXTENSION_ENABLED = true;

    window.ids = {
        mapSvgContainer: "map-svg-container",
        mapContainer: "map-container",

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
        solarSystem: "solar-system",
        solarSystemName: "solar-system-name",
        closePlanetButton: "close-planet-button",

        //Planet
        planet: "planet",
        planetName: "planet-name",
        planetSurfaceContainer: "planet-surface-container",
        planetMiddleBar: "planet-middle-bar",
        toggleEnergyDetailsButton: "toggle-energy-details-button",
        toggleLiquidDetailsButton: "toggle-liquid-details-button",
        toggleBulkDetailsButton: "toggle-bulk-details-button",
        planetStorageEnergyDetailsContainer: "planet-storage-energy-details-container",
        planetStorageLiquidDetailsContainer: "planet-storage-liquid-details-container",
        planetStorageBulkDetailsContainer: "planet-storage-bulk-details-container",
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
    }

    window.webSocketEvents = {
        CHAT_SEND_MESSAGE: "skyxplore-game-chat-send-message",
        USER_JOINED: "skyxplore-game-user-joined",
        USER_LEFT: "skyxplore-game-user-left",
        CHAT_ROOM_CREATED: "skyxplore-game-chat-room-created",
    }

    window.itemData = new Cache(itemDataLoader);
    window.itemDataNameLocalization = new CustomLocalization("skyxplore", "items");
    window.surfaceTypeLocalization = new CustomLocalization("skyxplore", "surface_type");
    window.skillTypeLocalization = new CustomLocalization("skyxplore", "skill_type");
    window.citizenStatLocalization = new CustomLocalization("skyxplore", "citizen_stat");

    const wsConnection = new WebSocketConnection(Mapping.getEndpoint("WS_CONNECTION_SKYXPLORE_GAME"));

    window.pageController = new function(){
        this.webSocketConnection = wsConnection;

        this.exitGame = exitGame;
    }

    function itemDataLoader(itemId){
        let result;

        const request = new Request(Mapping.getEndpoint("SKYXPLORE_GET_ITEM_DATA", {dataId: itemId}));
            request.processValidResponse = function(response){
                const parsed = JSON.parse(response.body);
                logService.logToConsole("Item loaded with id " + itemId, parsed);
                result = parsed;
            };
        const response = dao.sendRequest(request);

        return result;
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
                window.location.href = Mapping.SKYXPLORE_PAGE;
            }
        );
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "skyxplore", fileName: "game"}));
        wsConnection.addHandler(chatController.createChatSendMessageHandler())
            .addHandler(chatController.createUserJoinedHandler())
            .addHandler(chatController.createUserLeftHandler())
            .addHandler(chatController.createChatRoomCreatedHandler())
            .connect();
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