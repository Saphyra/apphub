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

        const request = new Request(Mapping.getEndpoint("GET_NOTEBOOK_TABLE", {listItemId: listItemId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(tableData){
                mapTableData(tableData);
                document.getElementById("view-table-title").innerHTML = tableData.title;
                displayTable();
            };
        dao.sendRequestAsync(request);

        document.getElementById("view-table-title").contentEditable = false;
        switchTab("main-page", "view-table");
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
                inputField.value = tableHead.content;
                inputField.disabled = !editingEnabled;
                inputField.classList.add("column-title");
                inputField.classList.add("view-table-column-title");
        node.appendChild(inputField);

        const buttonWrapper = document.createElement("SPAN");
            buttonWrapper.classList.add("view-table-operations-button-wrapper");
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

            const contentNode = document.createElement("DIV");
                contentNode.classList.add("table-column-content");
                contentNode.classList.add("view-table-input-field");
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
                            buttonWrapper.style.visibility = visibility();
                            buttonWrapper.classList.add("view-table-operations-button-wrapper");
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
        $(".view-table-operations-button-wrapper").css("visibility", "visible");
        switchTab("view-table-button-wrapper", "view-table-editing-operations-button-wrapper");
    }

    function discardChanges(){
        viewTable(openedTableId);
    }

    function addColumn(){
        const columnHead = createTableHead({tableHeadId: null, content: Localization.getAdditionalContent("column-name-title")});
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
        const title = document.getElementById("view-table-title").innerHTML;

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
                return new Stream(row.columns)
                    .map(function(column){
                        return {
                            key: column.inputField.id,
                            value: column.inputField.innerHTML
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

        const request = new Request(Mapping.getEndpoint("EDIT_NOTEBOOK_TABLE", {listItemId: openedTableId}), body);
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("table-saved"));
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

    function convertToChecklistTable(){
        const request = new Request(Mapping.getEndpoint("CONVERT_NOTEBOOK_TABLE_TO_CHECKLIST_TABLE", {listItemId: openedTableId}));
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("table-conversion-successful"));
                checklistTableViewController.viewChecklistTable(openedTableId);
                eventProcessor.processEvent(new Event(events.LIST_ITEM_SAVED));
            }
        dao.sendRequestAsync(request);
    }
})();