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
    }

    function viewChecklistTable(listItemId){
        openedTableId = listItemId;
        editingEnabled = false;
        columnNames = [];
        rows = [];

        const request = new Request(Mapping.getEndpoint("GET_NOTEBOOK_CHECKLIST_TABLE", {listItemId: listItemId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(tableData){
                mapTableData(tableData);
                document.getElementById("view-checklist-table-title").innerHTML = tableData.title;
                displayChecklistTable();
            };
        dao.sendRequestAsync(request);

        document.getElementById("view-checklist-table-title").contentEditable = false;
        switchTab("main-page", "view-checklist-table");
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
                inputField.value = tableHead.content;
                inputField.disabled = !editingEnabled;
                inputField.classList.add("column-title");
                inputField.classList.add("view-checklist-table-column-title");
        node.appendChild(inputField);

        const buttonWrapper = document.createElement("SPAN");
            buttonWrapper.classList.add("view-checklist-table-operations-button-wrapper");
            buttonWrapper.style.visibility = visibility();
            buttonWrapper.classList.add("table-head-button-wrapper");

            const moveLeftButton = document.createElement("BUTTON");
                moveLeftButton.innerHTML = "<";
                moveLeftButton.onclick = function(){
                    moveColumnLeft(node.id);
                }
        buttonWrapper.appendChild(moveLeftButton);
            const moveRightButton = document.createElement("BUTTON");
                moveRightButton.innerHTML = ">";
                moveRightButton.onclick = function(){
                    moveColumnRight(node.id);
                }
        buttonWrapper.appendChild(moveRightButton);
            const deleteColumnButton = document.createElement("BUTTON");
                deleteColumnButton.innerHTML = "X";
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
                if(columnData.tableJoinId) contentNode.id = columnData.tableJoinId;
                contentNode.contenteditable = editingEnabled;
                contentNode.innerHTML = columnData.content;
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
                columnHead.innerHTML = Localization.getAdditionalContent("table-head-checked-column");
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
                            buttonWrapper.style.visibility = visibility();
                            buttonWrapper.classList.add("view-checklist-table-operations-button-wrapper");
                            const moveUpButton = document.createElement("BUTTON");
                                moveUpButton.innerHTML = "^";
                                moveUpButton.onclick = function(){
                                    moveRowUp(rowNode.id);
                                }
                        buttonWrapper.appendChild(moveUpButton);
                            const moveDownButton = document.createElement("BUTTON");
                                moveDownButton.innerHTML = "v";
                                moveDownButton.onclick = function(){
                                    moveRowDown(rowNode.id);
                                }
                        buttonWrapper.appendChild(moveDownButton);
                            const deleteRowButton = document.createElement("BUTTON");
                                deleteRowButton.innerHTML = "X";
                                deleteRowButton.onclick = function(){
                                    removeRow(rowNode.id);
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
                                updateStatus(openedTableId, row.rowIndex, row.checked);
                            }
                    checkedCell.appendChild(checkedInput);
                rowNode.appendChild(checkedCell);

                    new Stream(row.columns)
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
            const request = new Request(Mapping.getEndpoint("UPDATE_NOTEBOOK_CHECKLIST_TABLE_ROW_STATUS", {listItemId: listItemId, rowIndex: rowIndex}), {value: checked});
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
        $(".view-checklist-table-operations-button-wrapper").css("visibility", "visible");
        switchTab("view-checklist-table-button-wrapper", "view-checklist-table-editing-operations-button-wrapper");
    }

    function discardChanges(){
        viewChecklistTable(openedTableId);
    }

    function addColumn(){
        const columnHead = createTableHead({tableHeadId: null, content: Localization.getAdditionalContent("column-name-title")});
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
        const title = document.getElementById("view-checklist-table-title").innerHTML;

        if(!title.length){
            notificationService.showError(Localization.getAdditionalContent("new-item-title-empty"));
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
            notificationService.showError(Localization.getAdditionalContent("empty-column-name"));
            return;
        }

        const rowList = new Stream(rows)
            .map(function(row){
                const columns = new Stream(row.columns)
                    .map(function(column){
                        return {
                            key: column.inputField.id,
                            value: column.inputField.innerHTML
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

        const request = new Request(Mapping.getEndpoint("EDIT_NOTEBOOK_CHECKLIST_TABLE", {listItemId: openedTableId}), body);
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("checklist-table-saved"));
                viewChecklistTable(openedTableId);
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
            return;
        }

        const newIndex = rowIndex - 1;

        [rows[rowIndex], rows[newIndex]] = [rows[newIndex], rows[rowIndex]];

        displayRows();
    }

    function moveRowDown(rowId){
        const rowIndex = search(rows, function(row){return row.rowNode.id === rowId});

        if(rowIndex == rows.length - 1){
            return;
        }

        const newIndex = rowIndex + 1;

        [rows[rowIndex], rows[newIndex]] = [rows[newIndex], rows[rowIndex]];

        displayRows();
    }

    function visibility(){
        return editingEnabled ? "visible" : "hidden";
    }
})();