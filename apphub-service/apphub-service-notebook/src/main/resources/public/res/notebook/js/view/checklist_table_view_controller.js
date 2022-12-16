(function ChecklistTableViewController(){
    let editingEnabled = false;
    let openedTableId = null;
    let columnNames = null;
    let rows = null;

    window.checklistTableViewController = new function(){
        this.viewChecklistTable = viewChecklistTable;
        this.enableEditing = enableEditing;
        this.discardChanges = discardChanges;
        this.addColumn = addColumn;
        this.addRow = addRow;
        this.saveChanges = saveChanges;
        this.deleteChecked = deleteChecked;
    }

    function viewChecklistTable(listItemId){
        openedTableId = listItemId;
        editingEnabled = false;
        columnNames = [];
        rows = [];

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_GET_CHECKLIST_TABLE", {listItemId: listItemId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(tableData){
                mapTableData(tableData);
                document.getElementById("view-checklist-table-title").innerText = tableData.title;
                displayChecklistTable();
            };
        dao.sendRequestAsync(request);

        document.getElementById("view-checklist-table-title").contentEditable = false;
        switchTab("main-page", "view-checklist-table");
        switchTab("button-wrapper", "view-checklist-table-buttons");
        switchTab("view-checklist-table-button-wrapper", "view-checklist-table-edit-button-wrapper");

        function mapTableData(tableData){
             new Stream(tableData.tableHeads)
                .sorted(function(a, b){return a.columnIndex - b.columnIndex})
                .map(createTableHead)
                .forEach(function(tableHead){columnNames.push(tableHead)});

            new Stream(tableData.tableColumns)
                .groupBy(function(a){return a.rowIndex})
                .sorted(function(e1, e2){return e1.getKey() - e2.getKey()})
                .toListStream()
                .map(function(columnList){
                    const orderedColumnList = new Stream(columnList)
                        .sorted(function(a, b){return a.columnIndex  - b.columnIndex})
                        .toList();
                    return createRow(orderedColumnList, tableData.rowStatus);
                })
                .forEach(function(row){rows.push(row)});
        }
    }

    function createTableHead(tableHead){
         const node = document.createElement("TH");
            node.classList.add("column-head")
            node.id = generateRandomId();
            const inputField = document.createElement("INPUT");
                if(tableHead.tableHeadId) inputField.id = tableHead.tableHeadId;
                inputField.value = tableHead.content || "";
                inputField.placeholder = localization.getAdditionalContent("column-name-title");
                inputField.disabled = !editingEnabled;
                inputField.classList.add("column-title");
                inputField.classList.add("view-checklist-table-column-title");
        node.appendChild(inputField);

        const buttonWrapper = document.createElement("SPAN");
            buttonWrapper.classList.add("view-checklist-table-operations-button-wrapper");
            buttonWrapper.classList.add("table-head-button-wrapper");

            const moveLeftButton = document.createElement("BUTTON");
                moveLeftButton.innerText = "<";
                moveLeftButton.onclick = function(){
                    moveColumnLeft(node.id);
                }
        buttonWrapper.appendChild(moveLeftButton);
            const moveRightButton = document.createElement("BUTTON");
                moveRightButton.innerText = ">";
                moveRightButton.onclick = function(){
                    moveColumnRight(node.id);
                }
        buttonWrapper.appendChild(moveRightButton);
            const deleteColumnButton = document.createElement("BUTTON");
                deleteColumnButton.innerText = "X";
                deleteColumnButton.onclick = function(){
                    removeColumn(node.id);
                }
        buttonWrapper.appendChild(deleteColumnButton);

        node.appendChild(buttonWrapper);
        return {
            columnNode: node,
            inputField: inputField
        };
    }

    function createRow(columnList, rowStatus){
        const rowNode = document.createElement("TR");
            rowNode.id = generateRandomId();

            const columns = new Stream(columnList)
                .map(function(column){return createColumn(column)})
                .toList();

        const rowIndex = columnList[0].rowIndex
        return {
            checked: rowStatus[rowIndex],
            rowIndex: rowIndex,
            rowNode: rowNode,
            columns: columns
        }
    }

    function createColumn(columnData){
        const cell = document.createElement("TD");
            cell.id = generateRandomId();
            cell.classList.add("table-column");

            const contentNode = document.createElement("DIV");
                contentNode.classList.add("table-column-content");
                contentNode.classList.add("view-checklist-table-input-field");
                contentNode.classList.add("selectable");
                if(columnData.tableJoinId) contentNode.id = columnData.tableJoinId;
                contentNode.contenteditable = editingEnabled;
                contentNode.innerText = columnData.content;
                contentNode.contentEditable = editingEnabled;
        cell.appendChild(contentNode);

        return {
            columnNode: cell,
            inputField: contentNode
        };
    }

    function displayChecklistTable(){
        displayColumnNames();
        displayRows();
    }

    function displayColumnNames(){
        const row = document.getElementById("view-checklist-table-head-row");
            row.innerHTML = "";

            row.appendChild(document.createElement("TH"));
            row.appendChild(createCheckedColumnHead());

            new Stream(columnNames)
                .forEach(function(column){row.appendChild(column.columnNode)});

        function createCheckedColumnHead(){
            const columnHead = document.createElement("TH");
                columnHead.innerText = localization.getAdditionalContent("table-head-checked-column");
            return columnHead;
        }
    }

    function displayRows(){
        const contentNode = document.getElementById("view-checklist-table-content");
            contentNode.innerHTML = "";

        new Stream(rows)
            .forEach(function(row){
                const rowNode = row.rowNode;
                    rowNode.innerHTML = "";

                    setContentDecoration(row.columns, row.checked);

                    const buttonCell = document.createElement("TD");
                        const buttonWrapper = document.createElement("DIV");
                            buttonWrapper.classList.add("view-checklist-table-operations-button-wrapper");
                            const moveUpButton = document.createElement("BUTTON");
                                moveUpButton.innerText = "^";
                                moveUpButton.onclick = function(){
                                    const notModified = moveRowUp(rowNode.id);
                                    if(!editingEnabled && !notModified){
                                        saveChanges();
                                    }
                                }
                        buttonWrapper.appendChild(moveUpButton);
                            const moveDownButton = document.createElement("BUTTON");
                                moveDownButton.innerText = "v";
                                moveDownButton.onclick = function(){
                                    const notModified = moveRowDown(rowNode.id);
                                    if(!editingEnabled && !notModified){
                                        saveChanges();
                                    }
                                }
                        buttonWrapper.appendChild(moveDownButton);
                            const deleteRowButton = document.createElement("BUTTON");
                                deleteRowButton.innerText = "X";
                                deleteRowButton.onclick = function(){
                                    if(!editingEnabled){
                                        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
                                            .withTitle(localization.getAdditionalContent("checklist-table-item-deletion-confirmation-dialog-title"))
                                            .withDetail(localization.getAdditionalContent("checklist-table-item-deletion-confirmation-dialog-detail"))
                                            .withConfirmButton(localization.getAdditionalContent("checklist-table-item-deletion-confirmation-dialog-confirm-button"))
                                            .withDeclineButton(localization.getAdditionalContent("checklist-table-item-deletion-confirmation-dialog-decline-button"));

                                        confirmationService.openDialog(
                                            "checklist-table-item-deletion-confirmation-dialog",
                                            confirmationDialogLocalization,
                                            function(){
                                                removeRow(rowNode.id);
                                                saveChanges();
                                            }
                                        )
                                    }else{
                                        removeRow(rowNode.id);
                                    }
                                }
                        buttonWrapper.appendChild(deleteRowButton);
                    buttonCell.appendChild(buttonWrapper);
                rowNode.appendChild(buttonCell);

                    const checkedCell = document.createElement("TD");
                        checkedCell.classList.add("view-checklist-table-checked-cell");
                        const checkedInput = document.createElement("INPUT");
                            checkedInput.type = "checkbox";
                            checkedInput.checked = row.checked;
                            checkedInput.onchange = function(){
                                row.checked = checkedInput.checked;
                                setContentDecoration(row.columns, row.checked);
                                if(!editingEnabled){
                                    updateStatus(openedTableId, row.rowIndex, row.checked);
                                }
                            }
                    checkedCell.appendChild(checkedInput);
                rowNode.appendChild(checkedCell);

                    new Stream(row.columns)
                        .peek(function(column){
                            column.columnNode.onclick = function(){
                                if(!editingEnabled && !isTextSelected()){
                                    const newValue = !row.checked;
                                    setContentDecoration(row.columns, newValue);
                                    checkedInput.checked = newValue
                                    row.checked = newValue;
                                    updateStatus(openedTableId, row.rowIndex, newValue);
                                }
                            }
                        })
                        .forEach(function(column){rowNode.appendChild(column.columnNode)});

                contentNode.appendChild(rowNode);
            });

        function setContentDecoration(columns, checked){
            new Stream(columns)
                .map(function(column){return column.columnNode})
                .forEach(function(columnNode){
                    columnNode.style.textDecoration = checked ? "line-through" : "initial";
                    columnNode.style.opacity = checked ? 0.5 : 1;
                })
        }

        function updateStatus(listItemId, rowIndex,  checked){
            const request = new Request(Mapping.getEndpoint("NOTEBOOK_UPDATE_CHECKLIST_TABLE_ROW_STATUS", {listItemId: listItemId, rowIndex: rowIndex}), {value: checked});
                request.processValidResponse = function(){
                }
            dao.sendRequestAsync(request);
        }
    }

    function enableEditing(){
        editingEnabled = true;
        document.getElementById("view-checklist-table-title").contentEditable = true;
        $(".view-checklist-table-input-field").attr("contenteditable", true);
        $(".view-checklist-table-column-title").attr("disabled", false);
        $(".view-checklist-table-item-edit-button").prop("disabled", false);
        switchTab("view-checklist-table-button-wrapper", "view-checklist-table-editing-operations-button-wrapper");
    }

    function discardChanges(){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("discard-confirmation-dialog-title"))
            .withDetail(localization.getAdditionalContent("discard-confirmation-dialog-detail"))
            .withConfirmButton(localization.getAdditionalContent("discard-confirmation-dialog-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("discard-confirmation-dialog-decline-button"));

        confirmationService.openDialog(
            "discard-confirmation-dialog",
            confirmationDialogLocalization,
            function(){
                viewChecklistTable(openedTableId);
            }
        )
    }

    function addColumn(){
        const columnHead = createTableHead({tableHeadId: null, content: null});
        columnNames.push(columnHead);

        new Stream(rows)
            .forEach(function(row){row.columns.push(createColumn({tableJoinId: null, content: ""}))});
        displayChecklistTable();
    }

    function addRow(){
        const columns = new Stream(columnNames)
            .map(function(){return createColumn({tableJoinId: null, content: ""})})
            .toList();

        const rowNode = document.createElement("TR");
            rowNode.id = generateRandomId();

        const record = {
            rowNode: rowNode,
            columns: columns
        }
        rows.push(record);
        displayChecklistTable();
    }

    function saveChanges(){
        const title = document.getElementById("view-checklist-table-title").innerText;

        if(!title.length){
            notificationService.showError(localization.getAdditionalContent("new-item-title-empty"));
            return;
        }

        const columnNameList = new Stream(columnNames)
            .map(function(columnName){
                return {
                    key: columnName.inputField.id,
                    value:columnName.inputField.value
                };
            })
            .toList();

        if(new Stream(columnNameList).anyMatch(function(c){return c.value.length == 0})){
            notificationService.showError(localization.getAdditionalContent("empty-column-name"));
            return;
        }

        const rowList = new Stream(rows)
            .map(function(row){
                const columns = new Stream(row.columns)
                    .map(function(column){
                        return {
                            key: column.inputField.id,
                            value: column.inputField.innerText
                        };
                    })
                    .toList();
                return {
                    checked: row.checked,
                    columns: columns
                }
            })
            .toList();

        const body = {
            title: title,
            columnNames: columnNameList,
            rows: rowList
        };

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_EDIT_CHECKLIST_TABLE", {listItemId: openedTableId}), body);
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("checklist-table-saved"));
                viewChecklistTable(openedTableId);
                categoryContentController.reloadCategoryContent();
            }
        dao.sendRequestAsync(request);
    }

    function removeColumn(columnId){
        const columnIndex = search(columnNames, function(column){return column.columnNode.id === columnId});

        columnNames.splice(columnIndex, 1);

        new Stream(rows)
            .map(function(row){return row.columns})
            .forEach(function(columns){columns.splice(columnIndex, 1)});

        displayChecklistTable();
    }

    function moveColumnLeft(columnId){
        const columnIndex = search(columnNames, function(column){return column.columnNode.id === columnId});

        if(columnIndex == 0){
            return;
        }

        const newIndex = columnIndex - 1;

        [columnNames[columnIndex], columnNames[newIndex]] = [columnNames[newIndex], columnNames[columnIndex]];

        new Stream(rows)
            .map(function(row){return row.columns})
            .forEach(function(columns){[columns[columnIndex], columns[newIndex]] = [columns[newIndex], columns[columnIndex]]});

        displayChecklistTable();
    }

    function moveColumnRight(columnId){
        const columnIndex = search(columnNames, function(column){return column.columnNode.id === columnId});

        if(columnIndex == columnNames.length - 1){
            return;
        }

        const newIndex = columnIndex + 1;

        [columnNames[columnIndex], columnNames[newIndex]] = [columnNames[newIndex], columnNames[columnIndex]];

        new Stream(rows)
            .map(function(row){return row.columns})
            .forEach(function(columns){[columns[columnIndex], columns[newIndex]] = [columns[newIndex], columns[columnIndex]]});

        displayChecklistTable();
    }

    function removeRow(rowId){
        const rowIndex = search(rows, function(row){return row.rowNode.id === rowId});

        rows.splice(rowIndex, 1);

        displayRows();
    }

    function moveRowUp(rowId){
        const rowIndex = search(rows, function(row){return row.rowNode.id === rowId});

        if(rowIndex == 0){
            return true;
        }

        const newIndex = rowIndex - 1;

        [rows[rowIndex], rows[newIndex]] = [rows[newIndex], rows[rowIndex]];

        displayRows();
    }

    function moveRowDown(rowId){
        const rowIndex = search(rows, function(row){return row.rowNode.id === rowId});

        if(rowIndex == rows.length - 1){
            return true;
        }

        const newIndex = rowIndex + 1;

        [rows[rowIndex], rows[newIndex]] = [rows[newIndex], rows[rowIndex]];

        displayRows();
    }

    function deleteChecked(){
        const confirmationDialogLocalization = new ConfirmationDialogLocalization()
            .withTitle(localization.getAdditionalContent("delete-checked-items-confirmation-title"))
            .withDetail(localization.getAdditionalContent("delete-checked-items-confirmation-detail"))
            .withConfirmButton(localization.getAdditionalContent("delete-checked-items-confirmation-confirm-button"))
            .withDeclineButton(localization.getAdditionalContent("delete-checked-items-confirmation-cancel-button"));

        confirmationService.openDialog(
            "delete-checked-items-confirmation",
            confirmationDialogLocalization,
            function(){
                const request = new Request(Mapping.getEndpoint("NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST_TABLE", {listItemId: openedTableId}));
                    request.processValidResponse = function(){
                        notificationService.showSuccess(localization.getAdditionalContent("checked-items-deleted"));
                        viewChecklistTable(openedTableId);
                    }
                dao.sendRequestAsync(request);
            }
        )
    }
})();