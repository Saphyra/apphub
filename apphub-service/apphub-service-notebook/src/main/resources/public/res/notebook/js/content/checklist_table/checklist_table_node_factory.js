scriptLoader.loadScript("/res/notebook/js/view/checklist_table_view_controller.js");

function checklistTableNodeFactory(parent, itemDetails){
    const node = document.createElement("DIV");
        node.classList.add("list-item-details-item");
        node.classList.add("button");
        node.classList.add("checklist-table");

        node.onclick = function(){
            checklistTableViewController.viewChecklistTable(itemDetails.id);
        }

        const title = document.createElement("SPAN");
            title.innerHTML = itemDetails.title;
    node.appendChild(title);

    node.appendChild(actionButtonFactory.create(
        parent,
        itemDetails,
        function(){deleteChecklistTable(itemDetails.id, itemDetails.title)}
    ));
    return node;

    function deleteChecklistTable(listItemId, title){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("deletion-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("deletion-confirmation-dialog-detail", {listItemTitle: title}))
            .withConfirmButton(Localization.getAdditionalContent("deletion-confirmation-dialog-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("deletion-confirmation-dialog-decline-button"));

        confirmationService.openDialog(
            "deletion-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("NOTEBOOK_DELETE_LIST_ITEM", {listItemId: listItemId}))
                    request.processValidResponse = function(){
                        notificationService.showSuccess(Localization.getAdditionalContent("item-deleted"));
                        eventProcessor.processEvent(new Event(events.ITEM_DELETED, listItemId));
                    }
                dao.sendRequestAsync(request);
            }
        )
    }
}