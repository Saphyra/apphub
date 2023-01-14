function fileNodeFactory(parent, itemDetails, displayOpenParentCategoryButton){
    const node = document.createElement("DIV");
        node.classList.add("list-item-details-item");
        node.classList.add("button");
        node.classList.add("file");

        node.onclick = function(){
            //TODO open a page with the details of the file (file name, size, etc)
            window.open(Mapping.getEndpoint("STORAGE_DOWNLOAD_FILE", {storedFileId: itemDetails.value}).getUrl())
        }

        node.ondragstart = function(e){
            e.dataTransfer.setData("listItemId", itemDetails.id);
        }
        node.draggable = true;

        const title = document.createElement("SPAN");
            title.innerText = itemDetails.title;
    node.appendChild(title);

    node.appendChild(actionButtonFactory.create(
        parent,
        itemDetails,
        node,
        function(){deleteImage(itemDetails.id, itemDetails.title)},
        displayOpenParentCategoryButton
    ));
    return node;

    function deleteImage(listItemId, title){
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
                        pinController.loadPinnedItems();
                        contentController.removeListItem(listItemId);
                    }
                dao.sendRequestAsync(request);
            }
        )
    }
}