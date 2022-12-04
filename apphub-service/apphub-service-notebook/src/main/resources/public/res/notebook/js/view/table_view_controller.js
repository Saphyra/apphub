(function TableViewController(){
    let editingEnabled = false;
    let openedTableId = null;
    let columnNames = null;
    let rows = null;

    window.tableViewController = new function(){
        this.viewTable = viewTable;
        this.enableEditing = enableEditing;
        this.discardChanges = discardChanges;
        this.addColumn = addColumn;
        this.addRow = addRow;
        this.saveChanges = saveChanges;
        this.convertToChecklistTable = convertToChecklistTable;
    }

    function viewTable(listItemId){
        openedTableId = listItemId;
        editingEnabled = false;
        columnNames = [];
        rows = [];

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_GET_TABLE", {listItemId: listItemId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(tableData){
                mapTableData(tableData);
                document.getElementById("view-table-title").innerText = tableData.title;
                displayTable();
            };
        dao.sendRequestAsync(request);

        document.getElementById("view-table-title").contentEditable = false;
        switchTab("main-page", "view-table");
        switchTab("button-wrapper", "view-table-buttons");
        switchTab("view-table-button-wrapper", "view-table-edit-button-wrapper");

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
                    return createRow(orderedColumnList);
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
                inputField.classList.add("view-table-column-title");
        node.appendChild(inputField);

        const buttonWrapper = document.createElement("SPAN");
            buttonWrapper.classList.add("view-table-operations-button-wrapper");
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

    function createRow(columnList){
        const rowNode = document.createElement("TR");
            rowNode.id = generateRandomId();

            const columns = new Stream(columnList)
                .map(function(column){return createColumn(column)})
                .toList();

        return {
            rowNode: rowNode,
            columns: columns
        }
    }

    function createColumn(columnData){
        const cell = document.createElement("TD");
            cell.id = generateRandomId();
            cell.classList.add("table-column");
            cell.classList.add("selectable");

            const contentNode = document.createElement("DIV");
                contentNode.classList.add("table-column-content");
                contentNode.classList.add("view-table-input-field");
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

    function displayTable(){
        displayColumnNames();
        displayRows();
    }

    function displayColumnNames(){
        const row = document.getElementById("view-table-head-row");
            row.innerHTML = "";

            row.appendChild(document.createElement("TH"));

            new Stream(columnNames)
                .forEach(function(column){row.appendChild(column.columnNode)});
    }

    function displayRows(){
        const contentNode = document.getElementById("view-table-content");
            contentNode.innerHTML = "";

        new Stream(rows)
            .forEach(function(row){
                const rowNode = row.rowNode;
                    rowNode.innerHTML = "";

                    const buttonCell = document.createElement("TD");
                        const buttonWrapper = document.createElement("DIV");
                            buttonWrapper.classList.add("view-table-operations-button-wrapper");
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
                                    removeRow(rowNode.id);
                                    if(!editingEnabled){
                                        saveChanges();
                                    }
                                }
                        buttonWrapper.appendChild(deleteRowButton);
                    buttonCell.appendChild(buttonWrapper);
                    rowNode.appendChild(buttonCell);

                    new Stream(row.columns)
                        .forEach(function(column){rowNode.appendChild(column.columnNode)});

                contentNode.appendChild(rowNode);
            });
    }

    function enableEditing(){
        editingEnabled = true;
        document.getElementById("view-table-title").contentEditable = true;
        $(".view-table-input-field").attr("contenteditable", true);
        $(".view-table-column-title").attr("disabled", false);
        $(".view-table-item-edit-button").prop("disabled", false);
        switchTab("view-table-button-wrapper", "view-table-editing-operations-button-wrapper");
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
                viewTable(openedTableId);
            }
        )
    }

    function addColumn(){
        const columnHead = createTableHead({tableHeadId: null, content: null});
        columnNames.push(columnHead);

        new Stream(rows)
            .forEach(function(row){row.columns.push(createColumn({tableJoinId: null, content: ""}))});
        displayTable();
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
        displayTable();
    }

    function saveChanges(){
        const title = document.getElementById("view-table-title").innerText;

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
                return new Stream(row.columns)
                    .map(function(column){
                        return {
                            key: column.inputField.id,
                            value: column.inputField.innerText
                        };
                    })
                    .toList();
            })
            .toList();

        const body = {
            title: title,
            columnNames: columnNameList,
            columns: rowList
        };

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_EDIT_TABLE", {listItemId: openedTableId}), body);
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("table-saved"));
                viewTable(openedTableId);
                eventProcessor.processEvent(new Event(events.LIST_ITEM_SAVED));
            }
        dao.sendRequestAsync(request);
    }

    function removeColumn(columnId){
        const columnIndex = search(columnNames, function(column){return column.columnNode.id === columnId});

        columnNames.splice(columnIndex, 1);

        new Stream(rows)
            .map(function(row){return row.columns})
            .forEach(function(columns){columns.splice(columnIndex, 1)});

        displayTable();
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

        displayTable();
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

        displayTable();
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

    function convertToChecklistTable(){
        const request = new Request(Mapping.getEndpoint("NOTEBOOK_CONVERT_TABLE_TO_CHECKLIST_TABLE", {listItemId: openedTableId}));
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("table-conversion-successful"));
                checklistTableViewController.viewChecklistTable(openedTableId);
                eventProcessor.processEvent(new Event(events.LIST_ITEM_SAVED));
            }
        dao.sendRequestAsync(request);
    }
})();