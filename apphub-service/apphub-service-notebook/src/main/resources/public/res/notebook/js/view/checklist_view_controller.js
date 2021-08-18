(function ChecklistViewController(){
    let openedChecklistId = null;
    let editingEnabled = false;

    window.checklistViewController = new function(){
        this.viewChecklist = viewChecklist;
        this.enableEditing = enableEditing;
        this.discardChanges = discardChanges;
        this.addItem = addItem;
        this.saveChanges = saveChanges;
        this.deleteChecked = deleteChecked;
        this.orderItems = orderItems;
    }

    function viewChecklist(listItemId){
        openedChecklistId = listItemId;
        const request = new Request(Mapping.getEndpoint("GET_NOTEBOOK_CHECKLIST_ITEM", {listItemId: listItemId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = displayChecklistItem;
        dao.sendRequestAsync(request);
    }

    function displayChecklistItem(checklistItemData){
        const titleNode = document.getElementById("view-checklist-title");
            titleNode.innerHTML = checklistItemData.title;
            titleNode.contentEditable = false;

        const contentTable = document.getElementById("view-checklist-content");
            contentTable.innerHTML = "";

            new Stream(checklistItemData.nodes)
                .sorted(function(a, b){return a.order - b.order})
                .map(createChecklistItem)
                .forEach(function(node){contentTable.appendChild(node)});

        switchTab("main-page", "view-checklist");
        switchTab("button-wrapper", "view-checklist-buttons");
        switchTab("view-checklist-button-wrapper", "view-checklist-edit-button-wrapper");
    }

    function createChecklistItem(itemData){
        const nodeRow = document.createElement("DIV");
            nodeRow.classList.add("view-checklist-item");
            $(nodeRow).attr("checklist-item-id", itemData.checklistItemId);

            const checkedCell = document.createElement("DIV");
                checkedCell.classList.add("view-checklist-item-checked-cell");

                const checkedBox = document.createElement("INPUT");
                    checkedBox.classList.add("view-checklist-item-checked-input");
                    checkedBox.type = "checkbox";
                    checkedBox.checked = itemData.checked;
            checkedCell.appendChild(checkedBox);
        nodeRow.appendChild(checkedCell);

            const contentCell = document.createElement("DIV");
                contentCell.classList.add("view-checklist-item-content");
                contentCell.innerHTML = itemData.content;
                contentCell.contentEditable = editingEnabled;
                contentCell.onclick = function(){
                    if(!editingEnabled){
                        const checked = !checkedBox.checked;
                        setContentDecoration(contentCell, checked);
                        updateStatus(itemData.checklistItemId, checked);
                        checkedBox.checked = checked;
                    }
                }
                setContentDecoration(contentCell, itemData.checked);
        nodeRow.appendChild(contentCell);

            const operationsCell = document.createElement("DIV");
                operationsCell.classList.add("view-checklist-item-operations-cell");

                const moveUpButton = document.createElement("BUTTON");
                    moveUpButton.classList.add("view-checklist-item-edit-button");
                    moveUpButton.innerHTML = "^";
                    moveUpButton.onclick = function(){
                        const sibling = nodeRow.previousSibling;
                        if(sibling){
                            document.getElementById("view-checklist-content").insertBefore(nodeRow, sibling);
                        }else{
                            return;
                        }

                        if(!editingEnabled){
                            saveChanges();
                        }
                    }
            operationsCell.appendChild(moveUpButton);

                const moveDownButton = document.createElement("BUTTON");
                    moveDownButton.classList.add("view-checklist-item-edit-button");
                    moveDownButton.innerHTML = "V";
                    moveDownButton.onclick = function(){
                        const sibling = nodeRow.nextSibling;
                        if(sibling){
                            document.getElementById("view-checklist-content").insertBefore(nodeRow, sibling.nextSibling);
                        }else{
                            return;
                        }

                        if(!editingEnabled){
                            saveChanges();
                        }
                    }
            operationsCell.appendChild(moveDownButton);

                const removeButton = document.createElement("BUTTON");
                    removeButton.classList.add("view-checklist-item-edit-button");
                    removeButton.innerHTML = "X";
                    removeButton.onclick = function(){
                        document.getElementById("view-checklist-content").removeChild(nodeRow);
                        if(!editingEnabled){
                            saveChanges();
                        }
                    }
            operationsCell.appendChild(removeButton);
        nodeRow.appendChild(operationsCell);

        checkedBox.onchange = function(){
            const checked = checkedBox.checked;
            setContentDecoration(contentCell, checked);
            updateStatus(itemData.checklistItemId, checked);
        }
        return nodeRow;

        function setContentDecoration(cell, checked){
            cell.style.textDecoration = checked ? "line-through" : "initial";
            cell.style.opacity = checked ? 0.5 : 1;
        }
    }

    function updateStatus(checklistItemId, checked){
        const request = new Request(Mapping.getEndpoint("UPDATE_NOTEBOOK_CHECKLIST_ITEM_STATUS", {checklistItemId: checklistItemId}), {value: checked});
            request.processValidResponse = function(){
            }
        dao.sendRequestAsync(request);
    }
    
    function enableEditing(){
        document.getElementById("view-checklist-title").contentEditable = true;
        $(".view-checklist-item-content").attr("contenteditable", true);
        switchTab("view-checklist-button-wrapper", "view-checklist-editing-operations-button-wrapper");
        editingEnabled = true;
    }

    function discardChanges(){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("discard-confirmation-dialog-title"))
            .withDetail(Localization.getAdditionalContent("discard-confirmation-dialog-detail"))
            .withConfirmButton(Localization.getAdditionalContent("discard-confirmation-dialog-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("discard-confirmation-dialog-decline-button"));

        confirmationService.openDialog(
            "discard-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                editingEnabled = false;
                viewChecklist(openedChecklistId);
            }
        )
    }

    function addItem(){
        const itemData = {
            checklistItemId: null,
            content: "",
            checked: false,
        }

        document.getElementById("view-checklist-content").appendChild(createChecklistItem(itemData));
    }

    function saveChanges(){
        const title = document.getElementById("view-checklist-title").innerHTML;

        if(!title.length){
            notificationService.showError(Localization.getAdditionalContent("new-item-title-empty"));
            return;
        }

        const items = document.getElementsByClassName("view-checklist-item");
        const nodes = [];

        for(let i = 0; i < items.length; i++){
            const item = items[i];
            const nodeData = {
                checklistItemId: $(item).attr("checklist-item-id"),
                order: i,
                checked: item.querySelector(".view-checklist-item-checked-input").checked,
                content: item.querySelector(".view-checklist-item-content").innerHTML
            }
            nodes.push(nodeData);
        }

        const request = new Request(Mapping.getEndpoint("EDIT_NOTEBOOK_CHECKLIST_ITEM", {listItemId: openedChecklistId}), {title: title, nodes: nodes});
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("checklist-saved"));
                editingEnabled = false;
                viewChecklist(openedChecklistId);
                eventProcessor.processEvent(new Event(events.LIST_ITEM_SAVED));
            }
        dao.sendRequestAsync(request);
    }

    function deleteChecked(){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(Localization.getAdditionalContent("delete-checked-items-confirmation-title"))
            .withDetail(Localization.getAdditionalContent("delete-checked-items-confirmation-detail"))
            .withConfirmButton(Localization.getAdditionalContent("delete-checked-items-confirmation-confirm-button"))
            .withDeclineButton(Localization.getAdditionalContent("delete-checked-items-confirmation-cancel-button"));

        confirmationService.openDialog(
            "delete-checked-items-confirmation",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST", {listItemId: openedChecklistId}));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(Localization.getAdditionalContent("checked-items-deleted"));
                        viewChecklist(openedChecklistId);
                    }
                dao.sendRequestAsync(request);
            }
        )
    }

    function orderItems(){
        const request = new Request(Mapping.getEndpoint("NOTEBOOK_ORDER_CHECKLIST_ITEMS", {listItemId: openedChecklistId}));
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("checklist-items-ordered"));
                viewChecklist(openedChecklistId);
            }
        dao.sendRequestAsync(request);
    }
})();