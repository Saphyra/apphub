(function TextViewController(){
    let openedTextId = null;

    window.textViewController = new function(){
        this.viewText = viewText;
        this.enableEditing = enableEditing;
        this.saveChanges = saveChanges;
        this.discardChanges = discardChanges;
    }

    function viewText(textId){
        openedTextId = textId;
        const request = new Request(Mapping.getEndpoint("GET_NOTEBOOK_TEXT", {listItemId: textId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = displayText;
        dao.sendRequestAsync(request);
    }

    function displayText(textData){
        const titleNode = document.getElementById("view-text-title");
        titleNode.innerHTML = textData.title;
        titleNode.contentEditable = false;

        const contentArea = document.getElementById("view-text-content");
        contentArea.value = textData.content;
        contentArea.readOnly = true;
        switchTab("main-page", "view-text");
        switchTab("view-text-button-wrapper", "view-text-edit-button-wrapper");
    }

    function enableEditing(){
        document.getElementById("view-text-content").readOnly = false;
        document.getElementById("view-text-title").contentEditable = true;
        switchTab("view-text-button-wrapper", "view-text-editing-operations-button-wrapper");
    }

    function saveChanges(){
        const title = document.getElementById("view-text-title").innerHTML;
        const content = document.getElementById("view-text-content").value;

        if(!title.length){
            notificationService.showError(Localization.getAdditionalContent("new-item-title-empty"));
            return;
        }

        const request = new Request(Mapping.getEndpoint("EDIT_NOTEBOOK_TEXT", {listItemId: openedTextId}), {title: title, content: content});
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("text-saved"));
                viewText(openedTextId);
                eventProcessor.processEvent(new Event(events.LIST_ITEM_SAVED));
            }
        dao.sendRequestAsync(request);
    }

    function discardChanges(){
        viewText(openedTextId);
    }
})();