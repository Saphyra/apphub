function linkNodeFactory(parent, itemDetails){
    const node = document.createElement("DIV");
        node.classList.add("list-item-details-item");
        node.classList.add("button");
        node.classList.add("link");

        node.onclick = function(){
            window.open(itemDetails.value);
        }

        const title = document.createElement("SPAN");
            title.innerHTML = itemDetails.title;
            title.title = itemDetails.value;
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
                        deleteLink(itemDetails.id, itemDetails.title);
                    }
            buttonListWrapper.appendChild(deleteButton);

                const cloneButton = document.createElement("BUTTON");
                    cloneButton.classList.add("list-item-option-button");
                    cloneButton.classList.add("clone-button");
                    cloneButton.innerHTML = Localization.getAdditionalContent("clone-button");
                    cloneButton.onclick = function(e){
                        e.stopPropagation();
                        listItemCloneService.clone(itemDetails.id);
                    }
            buttonListWrapper.appendChild(cloneButton);

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

    function deleteLink(listItemId, title){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("deletion-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("deletion-confirmation-dialog-detail", {listItemTitle: title}))
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