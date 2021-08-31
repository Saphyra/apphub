(function ContentController(){
    const nodeFactories = {
        CATEGORY: categoryNodeFactory,
        TEXT: textNodeFactory,
        LINK: linkNodeFactory,
        CHECKLIST: checklistNodeFactory,
        TABLE: tableNodeFactory,
        CHECKLIST_TABLE: checklistTableNodeFactory
    }

    window.contentController = new function(){
        this.createListItemId = createListItemId;
        this.nodeFactories = nodeFactories;
    }

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){
            return eventType == events.CATEGORY_DELETED
                || eventType == events.ITEM_DELETED
        },
        function(event){
            document.getElementById("category-content-list").removeChild(document.getElementById(createListItemId(event.getPayload())));
        },
    ));

    function createListItemId(listItemId){
        return "list-item-" + listItemId;
    }
})();