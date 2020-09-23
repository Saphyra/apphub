(function TableCreationController(){
    let currentCategoryId = null;
    let columnNames = [];
    let rows = [];

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.OPEN_CREATE_TABLE_DIALOG},
        init
    ));

    window.tableCreationController = new function(){
        this.save = save;
        this.newColumn = newColumn;
        this.newRow = newRow;
    }

    function init(){
        document.getElementById("create-table-selected-category-title").innerHTML = Localization.getAdditionalContent("root-title");
        document.getElementById("new-table-title").value = "";
        loadChildrenOfCategory(null);
        columnNames = [];
        rows = [];
        newColumn();
        newRow();
    }

    function loadChildrenOfCategory(categoryId){
        currentCategoryId = categoryId;

        const request = new Request(Mapping.getEndpoint("GET_CHILDREN_OF_NOTEBOOK_CATEGORY", null, {categoryId: categoryId, type: "CATEGORY"}));
            request.convertResponse = function(response){
                return JSON.parse(response.body)
            }
            request.processValidResponse = function(categoryResponse){
                const title = categoryResponse.title || Localization.getAdditionalContent("root-title");
                displayChildrenOfCategory(categoryId, categoryResponse.parent, categoryResponse.children, title);
            }
        dao.sendRequestAsync(request);
    }

    function displayChildrenOfCategory(categoryId, parent, categories, title){
        document.getElementById("create-table-selected-category-title").innerHTML = title;

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
                    noContentText.innerHTML = Localization.getAdditionalContent("category-empty");
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

                node.innerHTML = category.title;

                node.onclick = function(){
                    loadChildrenOfCategory(category.id);
                }

            return node;
        }
    }

    function save(){
        //TODO implement
    }

    function newColumn(){
        const tableHeadElement = createTableHead();
        columnNames.push(tableHeadElement);
        document.getElementById("table-head-row").appendChild(tableHeadElement);

        new Stream(rows)
            .forEach(function(row){row.push(createColumn(""))})
        displayRows();
       

        function createTableHead(){
            const node = document.createElement("TH");
                const inputField = document.createElement("INPUT");
                    inputField.classList.add("column-title");
                    inputField.type = "text";
                    inputField.placeholder = Localization.getAdditionalContent("column-name-title");
            node.appendChild(inputField);

            const buttonWrapper = document.createElement("SPAN");
                buttonWrapper.classList.add("table-head-button-wrapper");

                const moveLeftButton = document.createElement("BUTTON");
                    moveLeftButton.innerHTML = "<";
                    moveLeftButton.onclick = function(){
                        //TODO implement
                    }
            buttonWrapper.appendChild(moveLeftButton);
                const moveRightButton = document.createElement("BUTTON");
                    moveRightButton.innerHTML = ">";
                    moveRightButton.onclick = function(){
                        //TODO implement
                    }
            buttonWrapper.appendChild(moveRightButton);
                const deleteColumnButton = document.createElement("BUTTON");
                    deleteColumnButton.innerHTML = "X";
                    deleteColumnButton.onclick = function(){
                        //TODO implement
                    }
            buttonWrapper.appendChild(deleteColumnButton);

            node.appendChild(buttonWrapper);
            return node;
        }
    }

    function newRow(){
        const row = [];

        new Stream(columnNames)
            .forEach(function(){row.push(createColumn(""))});

        rows.push(row);
        displayRows();
    }

    function displayRows(){
        const contentNode = document.getElementById("new-table-content");
            contentNode.innerHTML = "";

        new Stream(rows)
            .forEach(function(row){
                const rowNode = document.createElement("TR");

                    const buttonCell = document.createElement("TD");
                        const moveUpButton = document.createElement("BUTTON");
                            moveUpButton.innerHTML = "^";
                            moveUpButton.onclick = function(){
                                //TODO implement
                            }
                    buttonCell.appendChild(moveUpButton);
                        const moveDownButton = document.createElement("BUTTON");
                            moveDownButton.innerHTML = "v";
                            moveDownButton.onclick = function(){
                                //TODO implement
                            }
                    buttonCell.appendChild(moveDownButton);
                        const deleteRowButton = document.createElement("BUTTON");
                            deleteRowButton.innerHTML = "X";
                            deleteRowButton.onclick = function(){
                                //TODO implement
                            }
                    buttonCell.appendChild(deleteRowButton);
                    rowNode.appendChild(buttonCell);

                    new Stream(row)
                        .forEach(function(cellNode){rowNode.appendChild(cellNode)});

                contentNode.appendChild(rowNode);
            });
    }

    function createColumn(content){
        const cell = document.createElement("TD");
            cell.classList.add("table-column");

            const contentNode = document.createElement("DIV");
                contentNode.classList.add("table-column-content")
                contentNode.contentEditable = true;
        cell.appendChild(contentNode);

        return cell;
    }
})();