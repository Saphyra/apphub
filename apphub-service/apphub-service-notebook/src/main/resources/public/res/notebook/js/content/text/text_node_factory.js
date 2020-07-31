scriptLoader.loadScript("/res/notebook/js/view/text_view_controller.js");

function textNodeFactory(parent, itemDetails){
    const node = document.createElement("DIV");
        node.classList.add("list-item-details-item");
        node.classList.add("button");
        node.classList.add("text");

        node.onclick = function(){
            textViewController.viewText(itemDetails.id);
        }

        const title = document.createElement("SPAN");
            title.innerHTML = itemDetails.title;
    node.appendChild(title);

        const optionsContainer = document.createElement("DIV");
            optionsContainer.classList.add("list-item-options-container");

            const optionsButton = document.createElement("BUTTON");
                optionsButton.classList.add("list-item-option-button");
                optionsButton.classList.add("list-item-options-button");
                optionsButton.innerHTML = Localization.getAdditionalContent("list-item-options-button");
                optionsButton.onclick = function(e){
                    e.stopPropagation();
                }
        optionsContainer.appendChild(optionsButton)

            const buttonListWrapper = document.createElement("DIV");
                buttonListWrapper.classList.add("list-item-options-button-list-wrapper");

                const deleteButton = document.createElement("BUTTON");
                    deleteButton.classList.add("list-item-option-button");
                    deleteButton.classList.add("delete-button");
                    deleteButton.innerHTML = Localization.getAdditionalContent("delete-button");
                    deleteButton.onclick = function(e){
                        e.stopPropagation();
                        deleteText(itemDetails.id);
                    }
            buttonListWrapper.appendChild(deleteButton);
                const editButton = document.createElement("BUTTON");
                    editButton.classList.add("list-item-option-button");
                    editButton.classList.add("edit-button");
                    editButton.innerHTML = Localization.getAdditionalContent("edit-button");
                    editButton.onclick = function(e){
                        e.stopPropagation();
                        listItemEditionService.openEditListItemWindow(parent, itemDetails);
                    }
            buttonListWrapper.appendChild(editButton);
        optionsContainer.appendChild(buttonListWrapper);
    node.appendChild(optionsContainer);
    return node;

    function deleteText(listItemId){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("deletion-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("deletion-confirmation-dialog-detail"))
            .withConfirmButton(Localization.getAdditionalContent("deletion-confirmation-dialog-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("deletion-confirmation-dialog-decline-button"));

        confirmationService.openDialog(
            "deletion-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("DELETE_NOTEBOOK_LIST_ITEM", {listItemId: listItemId}))
                    request.processValidResponse = function(){
                        notificationService.showSuccess(Localization.getAdditionalContent("item-deleted"));
                        eventProcessor.processEvent(new Event(events.ITEM_DELETED, listItemId));
                    }
                dao.sendRequestAsync(request);
            }
        )
    }
}