(function CategoryContentController(){
    let currentCategoryId = null;

    pageLoader.addLoader(function(){loadCategoryContent(null, false)}, "Load root category content");

    window.categoryContentController = new function(){
        this.loadCategoryContent = loadCategoryContent;
        this.reloadCategoryContent = function(){
            loadCategoryContent(currentCategoryId, false);
        }
        this.getCurrentCategoryId = function(){
            return currentCategoryId;
        }
        this.displayCategoryDetails = displayCategoryDetails;
    }

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){
            return eventType == events.CATEGORY_SAVED
                || eventType == events.LIST_ITEM_SAVED
        },
        function(){
            loadCategoryContent(currentCategoryId, false);
        },
        false,
        "Reload category content"
    ));

    function loadCategoryContent(categoryId, shouldSwitchTab){
        currentCategoryId = categoryId;
        const request = new Request(Mapping.getEndpoint("NOTEBOOK_GET_CHILDREN_OF_CATEGORY", null, {categoryId: categoryId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body)
            }
            request.processValidResponse = function(categoryResponse){
                displayCategoryDetails(categoryId, categoryResponse);
                if(shouldSwitchTab){
                    pageController.openMainPage();
                }
            }
        dao.sendRequestAsync(request);
    }

    function displayCategoryDetails(categoryId, categoryDetails, displayOpenParentCategoryButton){
        const parentButton = document.getElementById("category-content-parent-selection-parent-button");
            if(categoryId == null){
                parentButton.classList.add("disabled");
                parentButton.onclick = null;
            }else{
                parentButton.classList.remove("disabled");
                parentButton.onclick = function(){
                    loadCategoryContent(categoryDetails.parent, false);
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
            .map(function(itemDetails){return createNode(categoryId, itemDetails, displayOpenParentCategoryButton)})
            .forEach(function(node){container.appendChild(node)});

        function createNode(categoryId, itemDetails){
            const factory = contentController.nodeFactories[itemDetails.type];
            if(!factory){
                throwException("IllegalArgument", "NodeFactory not present for type " + itemDetails.type);
            }

            const node = factory(categoryId, itemDetails, displayOpenParentCategoryButton);
                node.id = contentController.createListItemId(itemDetails.id);
            return node;
        }
    }
})();