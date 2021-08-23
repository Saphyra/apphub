(function PinController(){
    window.pinController = new function(){
        this.pin = function(listItemId){
            setPinStatus(listItemId, true);
        }
        this.unpin = function(listItemId){
            setPinStatus(listItemId, false);
        }
    }

    $(document).ready(init);

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){
            return eventType == events.CATEGORY_DELETED
                || eventType == events.ITEM_DELETED
        },
        function(event){
            loadPinnedItems();
        },
    ));

    function loadPinnedItems(){
        const request = new Request(Mapping.getEndpoint("NOTEBOOK_GET_PINNED_ITEMS"));
            request.convertResponse = jsonConverter;
            request.processValidResponse = displayPinnedItems;
        dao.sendRequestAsync(request);

        function displayPinnedItems(items){
            const container = document.getElementById(ids.pinnedItems);
                container.innerHTML = "";

                new Stream(items)
                    .sorted(function(a, b){return a.title.localeCompare(b.title)})
                    .map(createNode)
                    .forEach(function(node){container.appendChild(node)});

            function createNode(item){
                const node = document.createElement("DIV");
                    node.classList.add("pinned-item");
                    node.classList.add("button");

                    const titleNode = document.createElement("SPAN");
                        titleNode.innerText = item.title;
                node.appendChild(titleNode);

                    switch(item.type){
                        case "CATEGORY":
                            node.classList.add("category");
                            node.onclick = function(){
                                categoryContentController.loadCategoryContent(item.id, true);
                            }
                        break;
                        case "TEXT":
                            node.classList.add("text");
                            node.onclick = function(){
                                textViewController.viewText(item.id);
                            }
                        break;
                        case "LINK":
                            node.classList.add("link");
                            node.onclick = function(){
                                window.open(item.value);
                            }
                        break;
                        case "CHECKLIST":
                            node.classList.add("checklist");
                            node.onclick = function(){
                                checklistViewController.viewChecklist(item.id);
                            }
                        break;
                        case "TABLE":
                            node.classList.add("table");
                            node.onclick = function(){
                                tableViewController.viewTable(item.id);
                            }
                        break;
                        case "CHECKLIST_TABLE":
                            node.classList.add("checklist-table");
                            node.onclick = function(){
                                checklistTableViewController.viewChecklistTable(item.id);
                            }
                        break;
                        default:
                            notificationService.showError("Unknown type: " + item.type);
                    }

                    const unpinButton = document.createElement("BUTTON");
                        unpinButton.innerText = "X";
                        unpinButton.onclick = function(e){
                            e.stopPropagation();
                            setPinStatus(item.id, false);
                        }
                node.appendChild(unpinButton);

                return node;
            }
        }
    }

    function setPinStatus(listItemId, pinned){
        const request = new Request(Mapping.getEndpoint("NOTEBOOK_PIN_LIST_ITEM", {listItemId: listItemId}), {value: pinned});
            request.processValidResponse = function(){
                categoryContentController.reloadCategoryContent();
                loadPinnedItems();
            }
        dao.sendRequestAsync(request);
    }

    function init(){
        loadPinnedItems();
    }
})();