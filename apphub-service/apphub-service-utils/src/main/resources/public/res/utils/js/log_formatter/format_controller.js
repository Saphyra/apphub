(function FormatController(){
    let logRecords = [];
    let rules = {};
    let visibility = {};
    let orderBy = null;

    const operations = {
        IS: "IS",
        IS_NOT: "IS_NOT",
        EXISTS: "EXISTS",
        DOES_NOT_EXIST: "DOES_NOT_EXIST"
    }

    window.formatController = new function(){
        this.format = format;
        this.addRule = addRule;
        this.orderOriginal = orderOriginal;
    }

    $(document).ready(init);

    function format(){
        const input = document.getElementById(ids.logInput).value;

        const data = parseInput(input);

        logRecords = data;

        const parameters = getParameters(data);

            addOptionsToFilter(parameters);
            processVisibility(parameters);
            addOrderButtons(parameters);

        displayLogRecords();
        enableInputs();

        function parseInput(input){
            try{
                return JSON.parse(input);
            }catch(e){
                notificationService.showError(Localization.getAdditionalContent("json-processing-failed"));
                throw e;
            }
        }

        function getParameters(data){
            return new Stream(data)
                .flatMap(function(record){return new Stream(Object.keys(record))})
                .distinct()
                .sorted(function(a, b){return a.localeCompare(b)})
                .toList();
        }

        function addOptionsToFilter(parameters){
            const filterParameterSelect = document.getElementById(ids.filterParameterSelect);
                filterParameterSelect.innerHTML = "";

            new Stream(parameters)
                .map(createOption)
                .forEach(function(option){filterParameterSelect.appendChild(option)});

            function createOption(parameter){
                const option = document.createElement("OPTION");
                    option.value = parameter;
                    option.innerText = parameter;
                return option;
            }
        }

        function processVisibility(parameters){
            const visibilityContainer = document.getElementById(ids.visibilityContainer);
                visibilityContainer.innerHTML = "";

                const request = new Request(Mapping.getEndpoint("UTILS_LOG_FORMATTER_GET_VISIBILITY"), parameters);
                    request.convertResponse = function(response){
                        return new Stream(JSON.parse(response.body))
                            .toMap(
                                function(v){return v.parameter},
                                function(v){return v}
                            );
                    }
                    request.processValidResponse = function(visibilities){
                        new Stream(parameters)
                            .peek(function(parameter){visibility[parameter] = visibilities[parameter].visibility})
                            .map(function(parameter){return createVisibilityButton(visibilities[parameter])})
                            .forEach(function(node){visibilityContainer.appendChild(node)});
                    }
                dao.sendRequest(request);

            function createVisibilityButton(v){
                const node = document.createElement("LABEL");
                    node.classList.add("visibility-button");
                    node.classList.add("button");

                    const checkbox = document.createElement("INPUT");
                        checkbox.type = "checkbox";
                        checkbox.checked = v.visibility;
                        checkbox.onchange = function(){
                            visibility[v.parameter] = checkbox.checked;
                            displayLogRecords();
                            updateVisibility(v.id, checkbox.checked);
                        }
                node.appendChild(checkbox);

                    const label = document.createElement("SPAN");
                        label.innerText = v.parameter;
                node.appendChild(label);
                return node;
            }
        }

        function addOrderButtons(parameters){
            const buttonContainer = document.getElementById(ids.orderButtons);
                buttonContainer.innerHTML = "";

                new Stream(parameters)
                    .map(createOrderButton)
                    .forEach(function(node){buttonContainer.appendChild(node)});

            function createOrderButton(parameter){
                const button = document.createElement("BUTTON");
                    button.innerText = parameter;
                    button.onclick = function(){
                        orderBy = parameter;
                        displayLogRecords();
                    }
                return button;
            }
        }

        function enableInputs(){
            document.getElementById(ids.filterParameterSelect).disabled = false;
            document.getElementById(ids.filterSearchOperation).disabled = false;
            document.getElementById(ids.filterInput).disabled = false;
            document.getElementById(ids.addFilterRuleButton).disabled = false;
        }
    }

    function displayLogRecords(){
        const resultContainer = document.getElementById(ids.resultContainer);

        const processedRecords = processLogRecords();

        const formatted = JSON.stringify(processedRecords, null, 2)
            .replaceAll("\\n", "<BR>")
            .replaceAll("\\r", "")
            .replaceAll("\\t", "    ");
            resultContainer.innerText = formatted;

        function processLogRecords(){
            const data = JSON.parse(JSON.stringify(logRecords));

            return new Stream(data)
                .filter(function(record){return shouldDisplay(record)})
                .sorted(function(a, b){
                    if(orderBy ==  null){
                        return 0;
                    }

                    return String(a[orderBy]).localeCompare(b[orderBy]);
                })
                .reverse(document.getElementById(ids.orderType).value == "desc")
                .map(convertRecord)
                .toList();

            function shouldDisplay(record){
                return new MapStream(rules)
                    .toListStream()
                    .allMatch(function(rule){return rule.shouldDisplay(record)});
            }

            function convertRecord(record){
                return new Stream(Object.keys(record))
                    .sorted(function(a, b){return a.localeCompare(b)})
                    .filter(function(parameter){return visibility[parameter]})
                    .toMap(
                        function(parameter){return parameter},
                        function(parameter){return record[parameter]}
                    )
            }
        }
    }

    function addRule(){
        const parameter = document.getElementById(ids.filterParameterSelect).value;
        const operation = document.getElementById(ids.filterSearchOperation).value;
        const filterTextInput = document.getElementById(ids.filterInput);
        const filterText = filterTextInput.value;

        if(parameter.length == 0){
            return;
        }

        if((operation == operations.IS || operation == operations.IS_NOT) && filterText.length == 0){
            notificationService.showError(Localization.getAdditionalContent("empty-filter-text"));
            return;
        }

        const rule = new Rule(parameter, operation, filterText);
        rules[rule.getId()] = rule;

        document.getElementById(ids.filterRuleContainer).appendChild(rule.getNode());
        filterTextInput.value = "";

        displayLogRecords();
    }

    function removeRule(id){
        const rule = rules[id];
        delete rules[id];

        document.getElementById(ids.filterRuleContainer).removeChild(rule.getNode());

        displayLogRecords();
    }

    function orderOriginal(){
        orderBy = null;
        displayLogRecords();
    }

    function updateVisibility(id, visible){
        const request = new Request(Mapping.getEndpoint("UTILS_LOG_FORMATTER_SET_VISIBILITY"), {id: id, visible: visible});
            request.processVisibility = function(){
            }
        dao.sendRequestAsync(request);
    }

    function init(){
        const filterSearchOperation = document.getElementById(ids.filterSearchOperation);
        filterSearchOperation.onchange = function(){
            const operation = filterSearchOperation.value;
            document.getElementById(ids.filterInput).disabled = operation == operations.EXISTS || operation == operations.DOES_NOT_EXIST
        }

        document.getElementById(ids.orderType).onchange = displayLogRecords;
    }

    function Rule(p, o, t){
        const id = generateRandomId();
        const parameter = p;
        const operation = o;
        const filterText = t.toLowerCase();
        const node = createNode();

        function createNode(){
            const node = document.createElement("DIV");
                node.classList.add("filter-rule");

                node.innerText = parameter + " " + operation;
                if(operation == operations.IS || operation == operations.IS_NOT){
                    node.innerText += " " + filterText;
                }

            const removeButton = document.createElement("BUTTON");
                removeButton.classList.add("filter-rule-remove-button")
                removeButton.innerText = "X";
                removeButton.onclick = function(){
                    removeRule(id);
                }
            node.appendChild(removeButton);

            return node;
        }

        this.shouldDisplay = function(record){
            switch(operation){
                case operations.IS:
                    if(record[parameter] == undefined){
                        return false;
                    }

                    return record[parameter].toLowerCase().indexOf(filterText) > -1;
                break;
                case operations.IS_NOT:
                    if(record[parameter] == undefined){
                        return true;
                    }

                    return record[parameter].toLowerCase().indexOf(filterText) < 0;
                break;
                case operations.EXISTS:
                    return record[parameter] != undefined;
                break;
                case operations.DOES_NOT_EXIST:
                    return record[parameter] == undefined;
                break;
                default:
                    throwException("IllegalState", "Unknown operation: " + operation);
            }
        }

        this.getId = function(){
            return id;
        }

        this.getNode = function(){
            return node;
        }
    }
})();