(function CategoryViewController(){
    scriptLoader.loadScript("/res/common/js/confirmation_service.js");

    const DELETION_CONFIRMATION_DIALOG_ID = "deletion-confirmation-dialog";

    let currentCategoryId = null;

    const nodeFactories = {
        CATEGORY: categoryNodeFactory
    }

    window.categoryViewController = new function(){
        this.loadContent = loadContent;
    }

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){
            return eventType == events.LOCALIZATION_LOADED
                || eventType == events.CATEGORY_SAVED
        },
        function(){loadContent(currentCategoryId)},
        true
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.CATEGORY_DELETED},
        function(event){
            document.getElementById("category-content-list").removeChild(document.getElementById(createListItemId(event.getPayload())));
        },
    ));

    function loadContent(categoryId){
        const request = new Request(Mapping.getEndpoint("GET_CHILDREN_OF_NOTEBOOK_CATEGORY", null, {categoryId: categoryId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body)
            }
            request.processValidResponse = function(categoryResponse){
                displayCategoryDetails(categoryId, categoryResponse);
            }
        dao.sendRequestAsync(request);
    }

    function displayCategoryDetails(categoryId, categoryDetails){
        const parentButton = document.getElementById("category-content-parent-selection-parent-button");
            if(categoryId == null){
                parentButton.classList.add("disabled");
                parentButton.onclick = null;
            }else{
                parentButton.classList.remove("disabled");
                parentButton.onclick = function(){
                    loadContent(categoryDetails.parent);
                }
            }

        document.getElementById("category-details-title").innerHTML = categoryDetails.title == null ? Localization.getAdditionalContent("root-title") : categoryDetails.title;

        const container = document.getElementById("category-content-list");
            container.innerHTML = "";

        new Stream(categoryDetails.children)
            .sorted(function(a, b){
                if(a.type == "CATEGORY" || b.type == "CATEGORY"){
                    if(a.type == b.type){
                        return a.title.localeCompare(b.title);
                    }

                    return a.type == "CATEGORY" ? -1 : 1;
                }

                return a.title.localeCompare(b.title);
            })
            .map(createNode)
            .forEach(function(node){container.appendChild(node)});

        function createNode(itemDetails){
            const factory = nodeFactories[itemDetails.type];
            if(!factory){
                throwException("IllegalArgument", "NodeFactory not present for type " + itemDetails.type);
            }

            const node = factory(itemDetails);
                node.id = createListItemId(itemDetails.id);
            return node;
        }
    }

    function categoryNodeFactory(itemDetails){
        const node = document.createElement("DIV");
            node.classList.add("list-item-details-item");
            node.classList.add("button");
            node.classList.add("category");

            node.onclick = function(){
                loadContent(itemDetails.id);
            }

            const title = document.createElement("SPAN");
                title.innerHTML = itemDetails.title;
        node.appendChild(title);

            const optionsContainer = document.createElement("DIV");
                optionsContainer.classList.add("list-item-options-container");

                const optionsButton = document.createElement("BUTTON");
                    optionsButton.classList.add("list-item-option-button");
                    optionsButton.classList.add("list-item-options-button");
                    optionsButton.innerHTML = Localization.getAdditionalContent("list-item-options-button");
                    optionsButton.onclick = function(e){
                        e.stopPropagation();
                    }
            optionsContainer.appendChild(optionsButton)

                const buttonListWrapper = document.createElement("DIV");
                    buttonListWrapper.classList.add("list-item-options-button-list-wrapper");

                    const deleteButton = document.createElement("BUTTON");
                        deleteButton.classList.add("list-item-option-button");
                        deleteButton.innerHTML = Localization.getAdditionalContent("delete-button");
                        deleteButton.onclick = function(e){
                            e.stopPropagation();
                            deleteCategory(itemDetails.id);
                        }
                buttonListWrapper.appendChild(deleteButton);
            optionsContainer.appendChild(buttonListWrapper);
        node.appendChild(optionsContainer);
        return node;

        function deleteCategory(categoryId){
            const confirmationDialogLocalization = new ConfirmationDialogLocalization()
                .withTitle(Localization.getAdditionalContent("category-deletion-confirmation-dialog-title"))
                .withDetail(Localization.getAdditionalContent("category-deletion-confirmation-dialog-detail"))
                .withConfirmButton(Localization.getAdditionalContent("category-deletion-confirmation-dialog-confirm-button"))
                .withDeclineButton(Localization.getAdditionalContent("category-deletion-confirmation-dialog-decline-button"));

            confirmationService.openDialog(
                DELETION_CONFIRMATION_DIALOG_ID,
                confirmationDialogLocalization,
                function(){
                    const request = new Request(Mapping.getEndpoint("DELETE_NOTEBOOK_LIST_ITEM", {listItemId: categoryId}))
                        request.processValidResponse = function(){
                            notificationService.showSuccess(Localization.getAdditionalContent("category-deleted"));
                            eventProcessor.processEvent(new Event(events.CATEGORY_DELETED, categoryId));
                        }
                    dao.sendRequestAsync(request);
                }
            )
        }
    }

    function createListItemId(listItemId){
        return "list-item-" + listItemId;
    }
})();