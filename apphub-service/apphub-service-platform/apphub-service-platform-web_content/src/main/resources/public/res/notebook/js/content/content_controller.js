(function ContentController(){
    const nodeFactories = {
        CATEGORY: categoryNodeFactory,
        TEXT: textNodeFactory,
        LINK: linkNodeFactory,
        CHECKLIST: checklistNodeFactory,
        TABLE: tableNodeFactory,
        CHECKLIST_TABLE: checklistTableNodeFactory,
        ONLY_TITLE: onlyTitleNodeFactory
    }

    window.contentController = new function(){
        this.createListItemId = createListItemId;
        this.nodeFactories = nodeFactories;
        this.removeListItem = function(listItemId){
            document.getElementById("category-content-list").removeChild(document.getElementById(createListItemId(listItemId)));
        }
    }

    function createListItemId(listItemId){
        return "list-item-" + listItemId;
    }
})();