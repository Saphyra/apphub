function categoryNodeFactory(itemDetails){
    const node = document.createElement("DIV");
        node.classList.add("list-item-details-item");
        node.classList.add("button");
        node.classList.add("category");

        node.onclick = function(){
            categoryContentController.loadCategoryContent(itemDetails.id);
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
                    deleteButton.innerHTML = Localization.getAdditionalContent("delete-button");
                    deleteButton.onclick = function(e){
                        e.stopPropagation();
                        deleteCategory(itemDetails.id);
                    }
            buttonListWrapper.appendChild(deleteButton);
        optionsContainer.appendChild(buttonListWrapper);
    node.appendChild(optionsContainer);
    return node;

    function deleteCategory(categoryId){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("category-deletion-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("category-deletion-confirmation-dialog-detail"))
            .withConfirmButton(Localization.getAdditionalContent("category-deletion-confirmation-dialog-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("category-deletion-confirmation-dialog-decline-button"));

        confirmationService.openDialog(
            "deletion-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("DELETE_NOTEBOOK_LIST_ITEM", {listItemId: categoryId}))
                    request.processValidResponse = function(){
                        notificationService.showSuccess(Localization.getAdditionalContent("category-deleted"));
                        eventProcessor.processEvent(new Event(events.CATEGORY_DELETED, categoryId));
                    }
                dao.sendRequestAsync(request);
            }
        )
    }
}