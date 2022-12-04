function textNodeFactory(parent, itemDetails, displayOpenParentCategoryButton){
    const node = document.createElement("DIV");
        node.classList.add("list-item-details-item");
        node.classList.add("button");
        node.classList.add("text");

        node.onclick = function(){
            textViewController.viewText(itemDetails.id);
        }

        const title = document.createElement("SPAN");
            title.innerText = itemDetails.title;
    node.appendChild(title);

    node.appendChild(actionButtonFactory.create(
        parent,
        itemDetails,
        node,
        function(){deleteText(itemDetails.id, itemDetails.title)},
        displayOpenParentCategoryButton
    ));
    return node;

    function deleteText(listItemId, title){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("deletion-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("deletion-confirmation-dialog-detail", {listItemTitle: title}))
            .withConfirmButton(localization.getAdditionalContent("deletion-confirmation-dialog-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("deletion-confirmation-dialog-decline-button"));

        confirmationService.openDialog(
            "deletion-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("NOTEBOOK_DELETE_LIST_ITEM", {listItemId: listItemId}))
                    request.processValidResponse = function(){
                        notificationService.showSuccess(localization.getAdditionalContent("item-deleted"));
                        eventProcessor.processEvent(new Event(events.ITEM_DELETED, listItemId));
                    }
                dao.sendRequestAsync(request);
            }
        )
    }
}