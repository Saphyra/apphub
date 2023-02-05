(function CustomTableCreationController(){
    const selectableColumnTypes = [
        "EMPTY",
        "NUMBER",
        "TEXT",
        "IMAGE",
        "FILE",
        "CHECKBOX",
        "COLOR",
        "DATE",
        "TIME",
        "DATE_TIME",
        "MONTH",
        "RANGE",
        "LINK",
    ]

    const ColumnType = toEnum(selectableColumnTypes.concat(["NAVIGATION", "CHECKED"]));
    const columnTypeLocalization = localization.loadCustomLocalization("notebook", "column_type");

    let currentCategoryId = null;
    let tableHeads;
    let tableRows;

    window.customTableCreationController = new function(){
        this.openCreateCustomTableDialog = openCreateCustomTableDialog;
        this.newColumn = newColumn;
        this.newRow = newRow;
        this.save = save;
    }

    function openCreateCustomTableDialog(){
        document.getElementById("create-custom-table-selected-category-title").innerText = localization.getAdditionalContent("root-title");
        document.getElementById("new-custom-table-title").value = "";
        loadChildrenOfCategory(categoryContentController.getCurrentCategoryId());

        tableHeads = createSyncEngine(ids.newCustomTableHeadRow, "table-head", createTableHeadNode, (a, b) => {return a.columnIndex - b.columnIndex});
            tableHeads.add(new TableHead(ColumnType.NAVIGATION, -2));
            tableHeads.add(new TableHead(ColumnType.CHECKED, -1));

        tableRows = createSyncEngine(ids.newCustomTableContent, "table-row", createTableRowNode, (r1, r2) => {return r1.rowIndex - r2.rowIndex}, (node, item) => {return node});

        newColumn();
        newRow();

        switchTab("main-page", "create-custom-table");
        switchTab("button-wrapper", "create-custom-table-buttons");
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
        
        function displayChildrenOfCategory(categoryId, parent, categories, title){
            document.getElementById("create-custom-table-selected-category-title").innerText = title;
    
            const parentButton = document.getElementById("create-custom-table-parent-selection-parent-button");
                if(categoryId == null){
                    parentButton.classList.add("disabled");
                    parentButton.onclick = null;
                }else{
                    parentButton.classList.remove("disabled");
                    parentButton.onclick = function(){
                        loadChildrenOfCategory(parent);
                    }
                }
    
            const container = document.getElementById("create-custom-table-parent-selection-category-list");
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
    }

    function newColumn(){
        tableHeads.add(new TableHead(ColumnType.EMPTY, tableHeads.size() - 2));
        tableRows.applyToAll((tableRow) => {
            tableRow.columns.add(new TableColumn(ColumnType.EMPTY, tableRow, tableRow.columns.size() - 2));
        });
    }

    function newRow(){
        const row = new TableRow(tableRows.size());
            tableRows.add(row);

        new Sequence(tableHeads.size() - 2)
            .map((columnIndex) => new TableColumn(getColumnType(columnIndex), row, columnIndex))
            .forEach((column) => row.columns.add(column));

        function getColumnType(columnIndex){
            const cache = tableRows.getCache();
            if(Object.keys(cache).length < 2){
                return ColumnType.EMPTY;
            }

            const row = findRow(cache);

            const columnsCache = row.columns.getCache();
            const columns = Object.values(columnsCache);
            const column = columns[columnIndex];

            return column.getColumnType();

            function findRow(cache){
                const sorted = new MapStream(cache)
                    .toListStream()
                    .sorted((a, b) => {return b.rowIndex - a.rowIndex})
                    .toList();

                return sorted[1];
            }
        }
    }

    function save(){
        const title = document.getElementById("new-custom-table-title").value;

        if(!title.length){
            notificationService.showError(localization.getAdditionalContent("new-item-title-empty"));
            return;
        }

        const columnNameValues = new MapStream(tableHeads.getCache())
            .toListStream()
            .filter((tableHead) => {return tableHead.columnType == ColumnType.EMPTY})
            .sorted((a, b) => {return a.columnIndex - b.columnIndex})
            .map(function(column){return column.inputField})
            .map(function(inputField){return inputField.value})
            .toList();

        if(new Stream(columnNameValues).anyMatch(function(columnName){return columnName.length == 0})){
            notificationService.showError(localization.getAdditionalContent("empty-column-name"));
            return;
        }

        const payload = {
            title: title,
            parent: currentCategoryId,
            columnNames: columnNameValues,
            rows: fetchRows()
        }

        console.log(payload); //TODO send request

        function fetchRows(){
            return new MapStream(tableRows.getCache())
                .toListStream()
                .sorted((a, b) => {return a.rowIndex - b.rowIndex})
                .map((tableRow) => {return createRowRequest(tableRow)})
                .toList();

            function createRowRequest(tableRow){
                return {
                    checked: isChecked(tableRow),
                    rowIndex: tableRow.rowIndex,
                    columns: fetchColumns(tableRow)
                }

                function isChecked(tableRow){
                    return new MapStream(tableRow.columns.getCache())
                        .toListStream()
                        .filter((column) => {return column.getColumnType() == ColumnType.CHECKED})
                        .findFirst()
                        .orElseThrow("IllegalArgument", "Row has no checked column")
                        .getData()
                        .getNode()
                        .checked;
                }

                function fetchColumns(tableRow){
                    return new MapStream(tableRow.columns.getCache())
                        .toListStream()
                        .skip(2)
                        .map((column) => {return column.assembleRequest()})
                        .toList();
                }
            }
        }
    }

    function createTableHeadNode(tableHead){
        return domBuilder.create("TH")
            .appendChildren(() => {
                switch(tableHead.columnType){
                    case ColumnType.NAVIGATION:
                        return null;
                    case ColumnType.CHECKED:
                        return domBuilder.create("SPAN", localization.getAdditionalContent("table-head-checked-column"));
                    default:
                        return [
                            tableHead.inputField,
                            domBuilder.create("SPAN")
                                .addClass("nowrap")
                                .appendChildren([
                                    domBuilder.create("BUTTON")
                                        .innerText("<")
                                        .onclick(() => moveColumnLeft(tableHead)),
                                    domBuilder.create("BUTTON")
                                        .innerText(">")
                                        .onclick(() => moveColumnRight(tableHead)),
                                    domBuilder.create("BUTTON")
                                        .innerText("X")
                                        .onclick(() => removeColumn(tableHead)),
                                ])
                        ];
                }
            })
            .getNode();
    }

    function createTableRowNode(tableRow){
        return domBuilder.create("TR")
            .getNode();
    }

    function createTableColumnNode(tableColumn){
        return domBuilder.create("TD")
            .attr("column-type", tableColumn.getColumnType())
            .addClassIf(() => {return tableColumn.getColumnType() != ColumnType.CHECKED && tableColumn.getColumnType() != ColumnType.NAVIGATION}, "table-content-column")
            .appendChild(domBuilder.create("DIV")
                .addClassIf(() => {return tableColumn.getColumnType() != ColumnType.CHECKED && tableColumn.getColumnType() != ColumnType.NAVIGATION}, "table-column-content-wrapper")
                .appendChildren(tableColumn.assembleColumnContent()))
            .getNode();
    }

    function createSyncEngine(parent, idPrefix, createNodeMethod, sortMethod, updateNodeMethod){
        return new SyncEngineBuilder()
            .withContainerId(parent)
            .withGetKeyMethod((obj) => {return obj.id})
            .withCreateNodeMethod(createNodeMethod)
            .withIdPrefix(idPrefix)
            .withUpdateNodeMethod(updateNodeMethod)
            .withSortMethod(sortMethod)
            .build();
    }

    function moveColumnLeft(tableHead){
        if(tableHead.columnIndex > 0){
            const columnIndex = tableHead.columnIndex;
            const newIndex = columnIndex - 1;

            findTableHeadByColumnIndex(newIndex)
                .columnIndex = columnIndex;

            tableHead.columnIndex = newIndex;

            tableHeads.render();

            tableRows.applyToAll(tableRow => {
                const columns = tableRow.columns;

                const oldColumn = findColumnByColumnIndex(columns, columnIndex);
                const newColumn = findColumnByColumnIndex(columns, newIndex);

                oldColumn.setColumnIndex(newIndex);
                newColumn.setColumnIndex(columnIndex);

                columns.render();
            });
        }
    }

    function moveColumnRight(tableHead){
        if(tableHead.columnIndex < tableHeads.size() - 3){
            const columnIndex = tableHead.columnIndex;
            const newIndex = columnIndex + 1;

            findTableHeadByColumnIndex(newIndex)
                .columnIndex = columnIndex;

            tableHead.columnIndex = newIndex;

            tableHeads.render();

            tableRows.applyToAll(tableRow => {
                const columns = tableRow.columns;

                const oldColumn = findColumnByColumnIndex(columns, columnIndex);
                const newColumn = findColumnByColumnIndex(columns, newIndex);

                oldColumn.setColumnIndex(newIndex);
                newColumn.setColumnIndex(columnIndex);

                columns.render();
            });
        }
    }

    function moveRowDown(tableColumn){
        const tableRow = tableColumn.getRow();
        const rowIndex = tableRow.rowIndex;
        const newIndex = rowIndex + 1;

        if(rowIndex < tableRows.size() - 1){
            new MapStream(tableRows.getCache())
                .toListStream()
                .filter((tableRow) => {return tableRow.rowIndex == newIndex})
                .findFirst()
                .get()
                .rowIndex = rowIndex;

            tableRow.rowIndex = newIndex;

            tableRows.render();
        }
    }

    function moveRowUp(tableColumn){
        const tableRow = tableColumn.getRow();
        const rowIndex = tableRow.rowIndex;
        const newIndex = rowIndex - 1;

        if(rowIndex > 0){
            new MapStream(tableRows.getCache())
                .toListStream()
                .filter((tableRow) => {return tableRow.rowIndex == newIndex})
                .findFirst()
                .get()
                .rowIndex = rowIndex;

            tableRow.rowIndex = newIndex;

            tableRows.render();
        }
    }

    function removeColumn(tableHead){
        const columnIndex = tableHeads.indexOf(tableHead);

        tableHeads.remove(tableHead.id);

        tableRows.applyToAll((tableRow) => {
            const columns = tableRow.columns;

            const keyToRemove = columns.keys()[columnIndex];

            columns.remove(keyToRemove);
        });
    }

    function removeRow(tableRow){
        tableRows.remove(tableRow.id);
    }

    function findTableHeadByColumnIndex(columnIndex){
        return new MapStream(tableHeads.getCache())
            .toListStream()
            .filter((tableHead) => {return tableHead.columnIndex == columnIndex})
            .findFirst()
            .orElseThrow("IllegalArgument", "TableHead not found with columnIndex " + columnIndex);
    }

    function findColumnByColumnIndex(se, columnIndex){
        return new MapStream(se.getCache())
            .toListStream()
            .filter((column) => {return column.getColumnIndex() == columnIndex})
            .findFirst()
            .orElseThrow("IllegalArgument", "TableColumn not found with columnIndex " + columnIndex, se.getCache());
    }

    function TableHead(columnType, ci){
        this.columnType = columnType || throwException("IllegalArgument", "columType must not be null");
        this.id = generateRandomId();
        this.columnIndex = hasValue(ci) ? ci : throwException("IllegalArgument", "columnIndex must not be null");
        this.inputField = domBuilder.create("INPUT")
            .type("text")
            .attr("placeholder", localization.getAdditionalContent("column-name-title"))
            .addClass("column-title")
            .getNode();
    }

    function TableRow(rowIndex){
        this.id = generateRandomId();
        this.rowIndex = (hasValue(rowIndex) ? rowIndex : throwException("IllegalArgument", "rowIndex must not be null"));
        this.columns = createSyncEngine("table-row-" + this.id, "table-column", createTableColumnNode, (a, b) => {return a.getColumnIndex() - b.getColumnIndex()})
            .add(new TableColumn(ColumnType.NAVIGATION, this, -2), true)
            .add(new TableColumn(ColumnType.CHECKED, this, -1), true);
    }

    function TableColumn(ct, r, ci){
        console.log("Creating column with columnType " + ct + " and columnIndex " + ci);
        const id = generateRandomId();
        this.id = id;
        let columnType = ct || throwException("IllegalArgument", "columType must not be null");
        const row = r || throwException("IllegalArgument", "row must not be null");
        let data = createData(columnType);
        let columnIndex = hasValue(ci) ? ci : throwException("IllegalArgument", "columnIndex must not be null");

        this.getColumnType = () => {return columnType};
        this.getRow = () => {return row};
        this.assembleColumnContent = assembleColumnContent;
        this.getData = function(){return data};
        this.getColumnIndex = function(){return columnIndex};
        this.setColumnIndex = function(ci){columnIndex = ci};
        this.assembleRequest = assembleRequest;

        function createData(columnType){
            switch (columnType){
                case ColumnType.CHECKED:
                case ColumnType.CHECKBOX:
                    return domBuilder.create("INPUT")
                        .type("checkbox");
                case ColumnType.EMPTY:
                    return domBuilder.create("BUTTON")
                        .innerText("+")
                        .onclick(() => selectColumnType(columnType));
                case ColumnType.NAVIGATION:
                    return null;
                case ColumnType.NUMBER:
                    const numberInput = domBuilder.create("INPUT")
                        .type("number")
                        .value(0)
                        .attr("step", 1);

                    const stepInput = domBuilder.create("INPUT")
                        .type("number")
                        .value(1)
                        .attr("min", 1)
                        .attr("step", "any")
                        .onchange((node) => {return () => numberInput.attr("step", node.value)});

                    return {
                        numberInput: numberInput.getNode(),
                        stepInput: stepInput.getNode()
                    }
                case ColumnType.RANGE:
                    const rangeInput = domBuilder.create("INPUT")
                        .type("range")
                        .attr("min", 1)
                        .attr("max", 10)
                        .attr("step", 1)
                        .value(5);

                    const minInput = domBuilder.create("INPUT")
                        .type("number")
                        .value(1)
                        .attr("step", "any")
                        .onchange((node) => {return () => rangeInput.attr("min", node.value)});

                    const maxInput = domBuilder.create("INPUT")
                        .type("number")
                        .value(10)
                        .attr("step", "any")
                        .onchange((node) => {return () => rangeInput.attr("max", node.value)});

                    const stepInput2 = domBuilder.create("INPUT")
                        .type("number")
                        .value(1)
                        .attr("min", 1)
                        .attr("step", "any")
                        .onchange((node) => {return () => rangeInput.attr("step", node.value)});

                    return {
                        rangeInput: rangeInput,
                        minInput: minInput,
                        maxInput: maxInput,
                        stepInput: stepInput2
                    };
                case ColumnType.TEXT:
                    return domBuilder.create("TEXTAREA")
                        .style("resize", "both");
                case ColumnType.IMAGE:
                    return domBuilder.create("INPUT")
                        .type("file")
                        .attr("accept", "image/png, image/gif, image/jpeg, image/jpg, image/bmp");
                case ColumnType.FILE:
                    return domBuilder.create("INPUT")
                        .type("file");
                case ColumnType.COLOR:
                    return domBuilder.create("INPUT")
                        .type("color");
                case ColumnType.DATE:
                    return domBuilder.create("INPUT")
                        .type("date");
                case ColumnType.DATE_TIME:
                    return domBuilder.create("INPUT")
                        .type("datetime-local");
                case ColumnType.TIME:
                    return domBuilder.create("INPUT")
                        .type("time");
                case ColumnType.MONTH:
                    return domBuilder.create("INPUT")
                        .type("month");
                case ColumnType.LINK:
                    return domBuilder.create("INPUT")
                        .type("text");
                default:
                    throwException("IllegalArgument", "Unhandled columnType " + columnType);
            }
        }

        function assembleColumnContent(){
            switch(columnType){
                case ColumnType.NAVIGATION:
                    return domBuilder.create("DIV")
                        .addClass("nowrap")
                        .appendChildren([
                            domBuilder.create("BUTTON")
                                .innerText("^")
                                .onclick(() => moveRowUp(this)),
                            domBuilder.create("BUTTON")
                                .innerText("v")
                                .onclick(() => moveRowDown(this)),
                            domBuilder.create("BUTTON")
                                .innerText("X")
                                .onclick(() => removeRow(row)),
                        ]);
                case ColumnType.EMPTY:
                case ColumnType.CHECKED:
                    return data;
                case ColumnType.TEXT:
                case ColumnType.CHECKBOX:
                case ColumnType.DATE:
                case ColumnType.DATE_TIME:
                case ColumnType.TIME:
                case ColumnType.MONTH:
                case ColumnType.LINK:
                    return [
                        data,
                        createModifyButton()
                    ];
                case ColumnType.NUMBER:
                    return [
                        domBuilder.create("LABEL")
                            .appendChild(domBuilder.create("SPAN", localization.getAdditionalContent("value")))
                            .appendChild(data.numberInput),
                        domBuilder.create("LABEL")
                            .appendChild(domBuilder.create("SPAN", localization.getAdditionalContent("step")))
                            .appendChild(data.stepInput),
                        createModifyButton()
                    ];
                case ColumnType.IMAGE:
                    const image = domBuilder.create("IMG")
                        .addClass("custom-table-image-preview");

                    data.onchange((node) => {
                        return () => {
                             const input = data.getNode();
                             if(input.files && input.files[0]){
                                 const reader = new FileReader();

                                 reader.onload = (e) => {image.getNode().src = e.target.result};

                                 reader.readAsDataURL(input.files[0]);
                             }
                         }
                    });

                    return [
                        data,
                        image,
                        createModifyButton()
                    ];
                case ColumnType.FILE:
                    const fileName = domBuilder.create("DIV");

                    data.onchange((node) => {
                        return () => fileName.innerText(data.getNode().files[0].name);
                    });

                    return [
                        data,
                        fileName,
                        createModifyButton()
                    ];
                case ColumnType.COLOR:
                    const value = domBuilder.create("DIV", data.getNode().value);

                    data.onchange((node) => {
                        return () => value.innerText(data.getNode().value);
                    });

                    return [
                        data,
                        value,
                        createModifyButton()
                    ];
                case ColumnType.RANGE:
                    const rangeLabel = domBuilder.create("SPAN", data.rangeInput.getNode().value);
                    data.rangeInput.onchange((node) => {
                        return () => rangeLabel.innerText(node.value);
                    });

                    const minLabel = domBuilder.create("SPAN", localization.getAdditionalContent("minimum"));
                        data.minInput.onchange((node) => {
                            return () => {
                                if(Number(node.value) > Number(data.maxInput.getValue())){
                                    node.value = data.maxInput.getValue();
                                }

                                if(Number(node.value) > Number(data.rangeInput.getValue())){
                                    rangeLabel.innerText(node.value);
                                }

                                data.rangeInput.attr("min", node.value);
                            };
                        });

                    const maxLabel = domBuilder.create("SPAN", localization.getAdditionalContent("maximum"));
                        data.maxInput.onchange((node) => {
                            return () => {
                                if(Number(node.value) < Number(data.minInput.getValue())){
                                    node.value = data.minInput.getValue();
                                }

                                if(Number(node.value) < Number(data.rangeInput.getNode().value)){
                                    rangeLabel.innerText(node.value);
                                }

                                data.rangeInput.attr("max", node.value);
                            };
                        });

                    const stepLabel = domBuilder.create("SPAN", localization.getAdditionalContent("step"));
                        data.stepInput.onchange((node) => {
                            return () => data.rangeInput.attr("step", node.value);
                        });

                    return [
                        domBuilder.create("LABEL")
                            .appendChildren([
                                data.rangeInput,
                                rangeLabel
                            ]),
                        domBuilder.create("LABEL")
                            .appendChildren([
                                minLabel,
                                data.minInput
                            ]),
                        domBuilder.create("LABEL")
                            .appendChildren([
                                maxLabel,
                                data.maxInput
                            ]),
                        domBuilder.create("LABEL")
                            .appendChildren([
                                stepLabel,
                                data.stepInput
                            ])
                    ]
                default:
                    return domBuilder.create("SPAN", tableColumn.getColumnType());
            }

            function createModifyButton(){
                return domBuilder.create("BUTTON")
                    .addClass("modify-column-type-button")
                    .innerText("X") //TODO proper button
                    .onclick(() => selectColumnType(columnType));
            }
        }

        function selectColumnType(currentColumnType){
            const radioButtons = getRadioButtons();

            const confirmationDialogLocalization = new ConfirmationDialogLocalization()
                .withTitle(localization.getAdditionalContent("select-column-type-title"))
                .withDetail(assembleOptions(radioButtons))
                .withConfirmButton(localization.getAdditionalContent("select-column-type-button"))
                .withDeclineButton(localization.getAdditionalContent("select-column-type-cancel-button"));

            confirmationService.openDialog(
                "select-column-type",
                confirmationDialogLocalization,
                () => {
                    columnType = new Stream(radioButtons)
                        .filter((node) => {return node.checked})
                        .findFirst()
                        .map(node => {return node.value})
                        .get();
                    data = createData(columnType);

                    row.columns.renderNode(id);
                }
            )

            function getRadioButtons(){
                return new Stream(selectableColumnTypes)
                    .map((columnType) => {
                        return domBuilder.create("INPUT")
                           .type("radio")
                           .value(columnType)
                           .attr("name", "column-type")
                           .attr("checked", columnType == currentColumnType)
                           .getNode()
                    })
                    .toList();
            }

            function assembleOptions(radioButtons){
                return domBuilder.create("SPAN")
                    .appendChildren(new Stream(radioButtons)
                        .map((radioButton) => {
                            return domBuilder.create("LABEL")
                                .addClass("column-type-label")
                                .appendChild(radioButton)
                                .appendChild(domBuilder.create("SPAN", columnTypeLocalization.get(radioButton.value)))
                        }))
                    .getNode();
            }
        }

        function assembleRequest(){
            return {
                type: columnType,
                columnIndex: columnIndex,
                value: assembleValue()
            }

            function assembleValue(){
                switch(columnType){
                    case ColumnType.EMPTY:
                        return null;
                    case ColumnType.CHECKBOX:
                        return data.getNode().checked;
                    case ColumnType.NUMBER:
                        return {
                            number: data.numberInput.getValue(),
                            step: data.stepInput.getValue()
                        }
                    case ColumnType.RANGE:
                        return {
                            min: data.minInput.getValue(),
                            max: data.maxInput.getValue(),
                            step: data.stepInput.getValue(),
                            value: data.rangeInput.getValue()
                        }
                    case ColumnType.TEXT:
                    case ColumnType.COLOR:
                    case ColumnType.DATE:
                    case ColumnType.DATE_TIME:
                    case ColumnType.TIME:
                    case ColumnType.MONTH:
                    case ColumnType.LINK:
                        return data.getValue();
                    case ColumnType.IMAGE:
                    case ColumnType.FILE:
                        const input = data.getNode();

                        if(!input.files || !input.files[0]){
                            notificationService.showError(localization.getAdditionalContent("no-file-selected"));
                            throwException("IllegalArgument", "File not selected");
                        }

                        const file = input.files[0];

                        if(file.size > FILE_SIZE_LIMIT){
                            notificationService.showError(localization.getAdditionalContent("file-too-big"));
                            throwException("IllegalArgument", "File too large");
                        }

                        return {
                            fileName: file.name,
                            size: file.size
                        }
                    default:
                        throwException("IllegalState", "Could not assemble value for columnType " + columnType);
                }
            }
        }
    }
})();