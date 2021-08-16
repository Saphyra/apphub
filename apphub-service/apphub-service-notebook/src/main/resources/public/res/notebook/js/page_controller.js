(function PageController(){
    scriptLoader.loadScript("/res/notebook/js/category_list_controller.js");
    scriptLoader.loadScript("/res/notebook/js/creation/category_creation_controller.js");
    scriptLoader.loadScript("/res/notebook/js/creation/text_creation_controller.js");
    scriptLoader.loadScript("/res/notebook/js/creation/link_creation_controller.js");
    scriptLoader.loadScript("/res/notebook/js/creation/checklist_creation_controller.js");
    scriptLoader.loadScript("/res/notebook/js/creation/table_creation_controller.js");
    scriptLoader.loadScript("/res/notebook/js/creation/checklist_table_creation_controller.js");
    scriptLoader.loadScript("/res/notebook/js/content/content_controller.js");
    scriptLoader.loadScript("/res/notebook/js/content/list_item_clone_service.js");
    scriptLoader.loadScript("/res/notebook/js/list_item_edition_service.js");
    scriptLoader.loadScript("/res/notebook/js/pin_controller.js");
    scriptLoader.loadScript("/res/notebook/js/search_controller.js");

    events.OPEN_CREATE_CATEGORY_DIALOG = "OPEN_CREATE_CATEGORY_DIALOG";
    events.OPEN_CREATE_TEXT_DIALOG = "OPEN_CREATE_TEXT_DIALOG";
    events.OPEN_CREATE_LINK_DIALOG = "OPEN_CREATE_LINK_DIALOG";
    events.CATEGORY_DELETED = "CATEGORY_DELETED";
    events.ITEM_DELETED = "ITEM_DELETED";
    events.SAVE_CATEGORY = "save-category";
    events.CATEGORY_SAVED = "CATEGORY_SAVED";
    events.LIST_ITEM_SAVED = "LIST_ITEM_SAVED";
    events.OPEN_CREATE_CHECKLIST_DIALOG = "OPEN_CREATE_CHECKLIST_DIALOG";
    events.OPEN_CREATE_TABLE_DIALOG = "OPEN_CREATE_TABLE_DIALOG";
    events.OPEN_CREATE_CHECKLIST_TABLE_DIALOG = "OPEN_CREATE_CHECKLIST_TABLE_DIALOG";

    window.ids = {
        pinnedItems: "pinned-items",
        searchInput: "search-container-title"
    }

    $(document).ready(function(){
        eventProcessor.processEvent(new Event(events.LOAD_LOCALIZATION, {module: "notebook", fileName: "notebook"}));
    });

    window.pageController = new function(){
        this.openCreateCategoryDialog = function(){
            document.getElementById("new-category-title").value = "";
            switchTab("main-page", "create-category");
            switchTab("button-wrapper", "create-category-buttons");
        }

        this.openCreateTextDialog = function(){
            switchTab("main-page", "create-text");
            switchTab("button-wrapper", "create-text-buttons");
        }

        this.openCreateLinkDialog = function(){
            switchTab("main-page", "create-link");
            switchTab("button-wrapper", "create-link-buttons");
        }

        this.openCreateChecklistDialog = function(){
            switchTab("main-page", "create-checklist");
            switchTab("button-wrapper", "create-checklist-buttons");
        }

        this.openCreateTableDialog = function(){
            switchTab("main-page", "create-table");
            switchTab("button-wrapper", "create-table-buttons");
        }

        this.openCreateChecklistTableDialog = function(){
            switchTab("main-page", "create-checklist-table");
            switchTab("button-wrapper", "create-checklist-table-buttons");
        }

        this.openMainPage = function(){
            switchTab("main-page", "category-content");
            switchTab("button-wrapper", "category-content-buttons");
        }
    }

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){
            return eventType == events.CATEGORY_SAVED
        },
        pageController.openMainPage
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.OPEN_CREATE_CATEGORY_DIALOG},
        pageController.openCreateCategoryDialog
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.OPEN_CREATE_TEXT_DIALOG},
        pageController.openCreateTextDialog
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.OPEN_CREATE_LINK_DIALOG},
        pageController.openCreateLinkDialog
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.OPEN_CREATE_CHECKLIST_DIALOG},
        pageController.openCreateChecklistDialog
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.OPEN_CREATE_TABLE_DIALOG},
        pageController.openCreateTableDialog
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.OPEN_CREATE_CHECKLIST_TABLE_DIALOG},
        pageController.openCreateChecklistTableDialog
    ));
})();