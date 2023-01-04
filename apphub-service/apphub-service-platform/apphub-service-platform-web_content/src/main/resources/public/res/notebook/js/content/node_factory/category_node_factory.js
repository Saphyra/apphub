function categoryNodeFactory(parent, itemDetails, displayOpenParentCategoryButton){
    const node = document.createElement("DIV");
        node.classList.add("list-item-details-item");
        node.classList.add("button");
        node.classList.add("category");
        node.ondragover = function(e){
            e.preventDefault();
        }

        node.ondrop = function(e){
            if(e.dataTransfer.getData("listItemId") == itemDetails.id){
                return;
            }

            e.preventDefault();
            const listItemId = e.dataTransfer.getData("listItemId");
            const parent = itemDetails.id;

            listItemEditionService.moveListItem(listItemId, parent);
        }

        node.ondragstart = function(e){
            e.dataTransfer.setData("listItemId", itemDetails.id);
        }
        node.draggable = true;

        node.onclick = function(){
            categoryContentController.loadCategoryContent(itemDetails.id, false);
        }

        const title = document.createElement("SPAN");
            title.innerText = itemDetails.title;
    node.appendChild(title);


    node.appendChild(actionButtonFactory.create(
        parent,
        itemDetails,
        node,
        function(){deleteCategory(itemDetails.id, itemDetails.title)},
        displayOpenParentCategoryButton
    ));
    return node;

    function deleteCategory(categoryId, title){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("deletion-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("category-deletion-confirmation-dialog-detail", {listItemTitle: title}))
            .withConfirmButton(localization.getAdditionalContent("category-deletion-confirmation-dialog-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("deletion-confirmation-dialog-decline-button"));

        confirmationService.openDialog(
            "deletion-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("NOTEBOOK_DELETE_LIST_ITEM", {listItemId: categoryId}))
                    request.processValidResponse = function(){
                        notificationService.showSuccess(localization.getAdditionalContent("category-deleted"));
                        categoryTreeController.removeCategory(categoryId);
                        contentController.removeListItem(categoryId);
                        pinController.loadPinnedItems();
                    }
                dao.sendRequestAsync(request);
            }
        )
    }
}