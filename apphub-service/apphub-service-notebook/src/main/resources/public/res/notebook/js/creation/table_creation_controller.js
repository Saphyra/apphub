(function TableCreationController(){
    let currentCategoryId = null;
    let columnNames = null;
    let rows = null;

    window.tableCreationController = new function(){
        this.save = save;
        this.newColumn = newColumn;
        this.newRow = newRow;
        this.openCreateTableDialog = function(){
            document.getElementById("create-table-selected-category-title").innerText = localization.getAdditionalContent("root-title");
            document.getElementById("new-table-title").value = "";
            loadChildrenOfCategory(categoryContentController.getCurrentCategoryId());
            columnNames = [];
            rows = [];
            newColumn();
            newRow();
            switchTab("main-page", "create-table");
            switchTab("button-wrapper", "create-table-buttons");
        }
    }

    function loadChildrenOfCategory(categoryId){
        currentCategoryId = categoryId;

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_GET_CHILDREN_OF_CATEGORY", null, {categoryId: categoryId, type: "CATEGORY"}));
            request.convertResponse = function(response){
                return JSON.parse(response.body)
            }
            request.processValidResponse = function(categoryResponse){
                const title = categoryResponse.title || localization.getAdditionalContent("root-title");
                displayChildrenOfCategory(categoryId, categoryResponse.parent, categoryResponse.children, title);
            }
        dao.sendRequestAsync(request);
    }

    function displayChildrenOfCategory(categoryId, parent, categories, title){
        document.getElementById("create-table-selected-category-title").innerText = title;

        const parentButton = document.getElementById("create-table-parent-selection-parent-button");
            if(categoryId == null){
                parentButton.classList.add("disabled");
                parentButton.onclick = null;
            }else{
                parentButton.classList.remove("disabled");
                parentButton.onclick = function(){
                    loadChildrenOfCategory(parent);
                }
            }

        const container = document.getElementById("create-table-parent-selection-category-list");
            container.innerHTML = "";

            if(!categories.length){
                const noContentText = document.createElement("DIV");
                    noContentText.classList.add("no-content");
                    noContentText.innerText = localization.getAdditionalContent("category-empty");
                container.appendChild(noContentText);
            }

            new Stream(categories)
                .sorted(function(a, b){return a.title.localeCompare(b.title)})
                .map(function(category){return createCategoryNode(category)})
                .forEach(function(node){container.appendChild(node)});

        function createCategoryNode(category){
            const node = document.createElement("DIV");
                node.classList.add("category");
                node.classList.add("button");
                node.classList.add("create-item-category");

                node.innerText = category.title;

                node.onclick = function(){
                    loadChildrenOfCategory(category.id);
                }

            return node;
        }
    }

    function save(){
        const title = document.getElementById("new-table-title").value;

        if(!title.length){
            notificationService.showError(localization.getAdditionalContent("new-item-title-empty"));
            return;
        }

        const columnNameValues = new Stream(columnNames)
            .map(function(column){return column.inputField})
            .map(function(inputField){return inputField.value})
            .toList();

        if(new Stream(columnNameValues).anyMatch(function(columnName){return columnName.length == 0})){
            notificationService.showError(localization.getAdditionalContent("empty-column-name"));
            return;
        }

        const rowValues = new Stream(rows)
            .map(function(row){return row.columns})
            .map(function(columns){return extractValues(columns)})
            .toList();

        const body = {
            title: title,
            parent: currentCategoryId,
            columnNames: columnNameValues,
            columns: rowValues
        }

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_CREATE_TABLE"), body);
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("table-saved"));
                eventProcessor.processEvent(new Event(events.LIST_ITEM_SAVED));
                pageController.openMainPage();
            }
        dao.sendRequestAsync(request);

        function extractValues(columnNodes){
            return new Stream(columnNodes)
                .map(function(columnNode){return columnNode.inputField})
                .map(function(inputField){return inputField.innerText})
                .toList();
        }
    }

    function newColumn(){
        const columnHead = createTableHead();
        columnNames.push(columnHead);

        new Stream(rows)
            .forEach(function(row){row.columns.push(createColumn(""))})
        displayData();

        function createTableHead(){
            const node = document.createElement("TH");
                node.id = generateRandomId();
                const inputField = document.createElement("INPUT");
                    inputField.classList.add("column-title");
                    inputField.type = "text";
                    inputField.placeholder = localization.getAdditionalContent("column-name-title");
            node.appendChild(inputField);

            const buttonWrapper = document.createElement("SPAN");
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
    }

    function newRow(){
        const columns = new Stream(columnNames)
            .map(function(){return createColumn("")})
            .toList();

        const rowNode = document.createElement("TR");
            rowNode.id = generateRandomId();

        const record = {
            rowNode: rowNode,
            columns: columns
        }
        rows.push(record);
        displayData();
    }

    function displayData(){
        displayColumnNames();
        displayRows();
    }

    function displayColumnNames(){
        const row = document.getElementById("new-table-head-row");
            row.innerHTML = "";

            row.appendChild(document.createElement("TH"));

            new Stream(columnNames)
                .forEach(function(column){row.appendChild(column.columnNode)});
    }

    function displayRows(){
        const contentNode = document.getElementById("new-table-content");
            contentNode.innerHTML = "";

        new Stream(rows)
            .forEach(function(row){
                const rowNode = row.rowNode;
                    rowNode.innerHTML = "";

                    const buttonCell = document.createElement("TD");
                        const moveUpButton = document.createElement("BUTTON");
                            moveUpButton.innerText = "^";
                            moveUpButton.onclick = function(){
                                moveRowUp(rowNode.id);
                            }
                    buttonCell.appendChild(moveUpButton);
                        const moveDownButton = document.createElement("BUTTON");
                            moveDownButton.innerText = "v";
                            moveDownButton.onclick = function(){
                                moveRowDown(rowNode.id);
                            }
                    buttonCell.appendChild(moveDownButton);
                        const deleteRowButton = document.createElement("BUTTON");
                            deleteRowButton.innerText = "X";
                            deleteRowButton.onclick = function(){
                                removeRow(rowNode.id);
                            }
                    buttonCell.appendChild(deleteRowButton);
                    rowNode.appendChild(buttonCell);

                    new Stream(row.columns)
                        .forEach(function(column){rowNode.appendChild(column.columnNode)});

                contentNode.appendChild(rowNode);
            });
    }

    function createColumn(content){
        const cell = document.createElement("TD");
            cell.id = generateRandomId();
            cell.classList.add("table-column");

            const contentNode = document.createElement("DIV");
                contentNode.classList.add("table-column-content")
                contentNode.contentEditable = true;
        cell.appendChild(contentNode);

        return {
            columnNode: cell,
            inputField: contentNode
        };
    }

    function removeColumn(columnId){
        const columnIndex = search(columnNames, function(column){return column.columnNode.id === columnId});

        columnNames.splice(columnIndex, 1);
        
        new Stream(rows)
            .map(function(row){return row.columns})
            .forEach(function(columns){columns.splice(columnIndex, 1)});

        displayData();
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

        displayData();
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

        displayData();
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
})();