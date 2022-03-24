function categoryNodeFactory(parent, itemDetails, displayOpenParentCategoryButton){
    const node = document.createElement("DIV");
        node.classList.add("list-item-details-item");
        node.classList.add("button");
        node.classList.add("category");

        node.onclick = function(){
            categoryContentController.loadCategoryContent(itemDetails.id, false);
        }

        const title = document.createElement("SPAN");
            title.innerHTML = itemDetails.title;
    node.appendChild(title);


    node.appendChild(actionButtonFactory.create(
        parent,
        itemDetails,
        function(){deleteCategory(itemDetails.id, itemDetails.title)},
        displayOpenParentCategoryButton
    ));
    return node;

    function deleteCategory(categoryId, title){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("deletion-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("category-deletion-confirmation-dialog-detail", {listItemTitle: title}))
            .withConfirmButton(Localization.getAdditionalContent("category-deletion-confirmation-dialog-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("deletion-confirmation-dialog-decline-button"));

        confirmationService.openDialog(
            "deletion-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("NOTEBOOK_DELETE_LIST_ITEM", {listItemId: categoryId}))
                    request.processValidResponse = function(){
                        notificationService.showSuccess(Localization.getAdditionalContent("category-deleted"));
                        eventProcessor.processEvent(new Event(events.CATEGORY_DELETED, categoryId));
                    }
                dao.sendRequestAsync(request);
            }
        )
    }
}