scriptLoader.loadScript("/res/common/js/confirmation_service.js");
scriptLoader.loadScript("/res/common/js/settings.js");
scriptLoader.loadScript("/res/notebook/js/category_list_controller.js");
scriptLoader.loadScript("/res/notebook/js/creation/category_creation_controller.js");
scriptLoader.loadScript("/res/notebook/js/creation/text_creation_controller.js");
scriptLoader.loadScript("/res/notebook/js/creation/link_creation_controller.js");
scriptLoader.loadScript("/res/notebook/js/creation/checklist_creation_controller.js");
scriptLoader.loadScript("/res/notebook/js/creation/table_creation_controller.js");
scriptLoader.loadScript("/res/notebook/js/creation/checklist_table_creation_controller.js");
scriptLoader.loadScript("/res/notebook/js/creation/only_title_creation_controller.js");
scriptLoader.loadScript("/res/notebook/js/content/list_item_clone_service.js");
scriptLoader.loadScript("/res/notebook/js/list_item_edition_service.js");
scriptLoader.loadScript("/res/notebook/js/pin_controller.js");
scriptLoader.loadScript("/res/notebook/js/search_controller.js");
scriptLoader.loadScript("/res/notebook/js/content/category_content_controller.js");
scriptLoader.loadScript("/res/notebook/js/content/node_factory/category_node_factory.js")
scriptLoader.loadScript("/res/notebook/js/content/node_factory/checklist_node_factory.js")
scriptLoader.loadScript("/res/notebook/js/content/node_factory/table_node_factory.js")
scriptLoader.loadScript("/res/notebook/js/content/node_factory/checklist_table_node_factory.js")
scriptLoader.loadScript("/res/notebook/js/content/node_factory/only_title_node_factory.js")
scriptLoader.loadScript("/res/notebook/js/content/action_button_factory.js")
scriptLoader.loadScript("/res/notebook/js/view/text_view_controller.js");
scriptLoader.loadScript("/res/notebook/js/content/node_factory/link_node_factory.js");
scriptLoader.loadScript("/res/notebook/js/view/table_view_controller.js");
scriptLoader.loadScript("/res/notebook/js/content/node_factory/text_node_factory.js");
scriptLoader.loadScript("/res/notebook/js/view/checklist_table_view_controller.js");
scriptLoader.loadScript("/res/notebook/js/view/checklist_view_controller.js");
scriptLoader.loadScript("/res/notebook/js/content/content_controller.js");
scriptLoader.loadScript("/res/notebook/js/settings_controller.js");

(function PageController(){
    events.CATEGORY_DELETED = "CATEGORY_DELETED";
    events.ITEM_DELETED = "ITEM_DELETED";
    events.CATEGORY_SAVED = "CATEGORY_SAVED";
    events.LIST_ITEM_SAVED = "LIST_ITEM_SAVED";
    events.SETTINGS_LOADED = "SETTINGS_LOADED";
    events.SETTINGS_MODIFIED = "SETTINGS_MODIFIED";
    events.ITEM_ARCHIVED = "ITEM_ARCHIVED";

    window.ids = {
        pinnedItems: "pinned-items",
        searchInput: "search-container-title"
    }

    $(document).ready(function(){
        localization.loadLocalization("notebook", "notebook");
        settings.initialize()
            .then(() => eventProcessor.processEvent(new Event(events.SETTINGS_LOADED)));
    });

    window.pageController = new function(){
        this.openMainPage = function(){
            switchTab("main-page", "category-content");
            switchTab("button-wrapper", "category-content-buttons");
        }
    }

    window.settings = new Settings("notebook");

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){
            return eventType == events.CATEGORY_SAVED
        },
        pageController.openMainPage,
        false,
        "Open main page after category saved"
    ));
})();