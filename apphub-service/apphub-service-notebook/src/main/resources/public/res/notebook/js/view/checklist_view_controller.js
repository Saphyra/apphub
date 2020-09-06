(function ChecklistViewController(){
    let openedChecklistId = null;

    window.checklistViewController = new function(){
        this.viewChecklist = viewChecklist;
        this.enableEditing = enableEditing;
        this.discardChanges = discardChanges;
        this.addItem = addItem;
    }

    function viewChecklist(listItemId){
        openedChecklistId = listItemId;
        const request = new Request(Mapping.getEndpoint("GET_CHECKLIST_ITEM", {listItemId: listItemId}));
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
        switchTab("view-checklist-button-wrapper", "view-checklist-edit-button-wrapper");
    }

    function createChecklistItem(itemData, editingEnabled){
        editingEnabled = editingEnabled || false;

        const nodeRow = document.createElement("DIV");
            nodeRow.classList.add("view-checklist-item");
            $(nodeRow).attr("checklist-item-id", itemData.checklistItemId);

            const checkedCell = document.createElement("DIV");
                checkedCell.classList.add("view-checklist-item-checked-cell");

                const checkedBox = document.createElement("INPUT");
                    checkedBox.type = "checkbox";
                    checkedBox.checked = itemData.checked;
            checkedCell.appendChild(checkedBox);
        nodeRow.appendChild(checkedCell);

            const contentCell = document.createElement("DIV");
                contentCell.classList.add("view-checklist-item-content-cell");
                contentCell.innerHTML = itemData.content;
                contentCell.contentEditable = editingEnabled;
                setContentDecoration(contentCell, itemData.checked);
        nodeRow.appendChild(contentCell);

            const operationsCell = document.createElement("DIV");
                operationsCell.classList.add("view-checklist-item-operations-cell");

                const moveUpButton = document.createElement("BUTTON");
                    moveUpButton.classList.add("view-checklist-item-edit-button");
                    moveUpButton.disabled = !editingEnabled;
                    moveUpButton.innerHTML = "^";
                    moveUpButton.onclick = function(){
                        const sibling = nodeRow.previousSibling;
                        if(sibling){
                            document.getElementById("view-checklist-content").insertBefore(nodeRow, sibling);
                        }
                    }
            operationsCell.appendChild(moveUpButton);

                const moveDownButton = document.createElement("BUTTON");
                    moveDownButton.classList.add("view-checklist-item-edit-button");
                    moveDownButton.disabled = !editingEnabled;
                    moveDownButton.innerHTML = "V";
                    moveDownButton.onclick = function(){
                        const sibling = nodeRow.nextSibling;
                        if(sibling){
                            document.getElementById("view-checklist-content").insertBefore(nodeRow, sibling.nextSibling);
                        }
                    }
            operationsCell.appendChild(moveDownButton);

                const removeButton = document.createElement("BUTTON");
                    removeButton.classList.add("view-checklist-item-edit-button");
                    removeButton.disabled = !editingEnabled;
                    removeButton.innerHTML = "X";
                    removeButton.onclick = function(){
                        document.getElementById("view-checklist-content").removeChild(nodeRow);
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
        const request = new Request(Mapping.getEndpoint("UPDATE_CHECKLIST_ITEM_STATUS", {checklistItemId: checklistItemId}), {value: checked});
            request.processValidResponse = function(){
            }
        dao.sendRequestAsync(request);
    }
    
    function enableEditing(){
        document.getElementById("view-checklist-title").contentEditable = true;
        $(".view-checklist-item-edit-button").prop("disabled", false);
        $(".view-checklist-item-content-cell").attr("contenteditable", true);
        switchTab("view-checklist-button-wrapper", "view-checklist-editing-operations-button-wrapper");
    }

    function discardChanges(){
        viewChecklist(openedChecklistId);
    }

    function addItem(){
        const itemData = {
            checklistItemId: null,
            content: "",
            checked: false,
        }

        document.getElementById("view-checklist-content").appendChild(createChecklistItem(itemData, true));
    }
})();