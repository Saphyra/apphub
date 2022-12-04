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
        const request = new Request(Mapping.getEndpoint("NOTEBOOK_GET_TEXT", {listItemId: textId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = displayText;
        dao.sendRequestAsync(request);
    }

    function displayText(textData){
        const titleNode = document.getElementById("view-text-title");
            titleNode.innerText = textData.title;
            titleNode.contentEditable = false;

        const contentArea = document.getElementById("view-text-content");
        contentArea.value = textData.content;
        contentArea.readOnly = true;
        switchTab("main-page", "view-text");
        switchTab("button-wrapper", "view-text-buttons");
        switchTab("view-text-button-wrapper", "view-text-edit-button-wrapper");
    }

    function enableEditing(){
        document.getElementById("view-text-content").readOnly = false;
        document.getElementById("view-text-title").contentEditable = true;
        switchTab("view-text-button-wrapper", "view-text-editing-operations-button-wrapper");
    }

    function saveChanges(){
        const title = document.getElementById("view-text-title").innerText;
        const content = document.getElementById("view-text-content").value;

        if(!title.length){
            notificationService.showError(localization.getAdditionalContent("new-item-title-empty"));
            return;
        }

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_EDIT_TEXT", {listItemId: openedTextId}), {title: title, content: content});
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("text-saved"));
                viewText(openedTextId);
                eventProcessor.processEvent(new Event(events.LIST_ITEM_SAVED));
            }
        dao.sendRequestAsync(request);
    }

    function discardChanges(){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("discard-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("discard-confirmation-dialog-detail"))
            .withConfirmButton(localization.getAdditionalContent("discard-confirmation-dialog-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("discard-confirmation-dialog-decline-button"));

        confirmationService.openDialog(
            "discard-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                viewText(openedTextId);
            }
        )
    }
})();