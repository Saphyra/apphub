(function CategoryContentController(){
    let currentCategoryId = null;

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

                parentButton.ondragover = function(e){}
            }else{
                parentButton.classList.remove("disabled");
                parentButton.onclick = function(){
                    loadCategoryContent(categoryDetails.parent, false);
                }
                parentButton.ondragover = function(e){
                    e.preventDefault();
                }
                parentButton.ondrop = function(e){
                    e.preventDefault();
                    const listItemId = e.dataTransfer.getData("listItemId");
                    const parent = categoryDetails.parent;

                    listItemEditionService.moveListItem(listItemId, parent);
                }
            }

        document.getElementById("category-details-title").innerText = categoryDetails.title == null ? localization.getAdditionalContent("root-title") : categoryDetails.title;

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
            .filter((item)=>{return isTrue(settings.get("show-archived")) || !item.archived})
            .map(function(itemDetails){return createNode(categoryId, itemDetails, displayOpenParentCategoryButton)})
            .forEach(function(node){container.appendChild(node)});

        function createNode(categoryId, itemDetails){
            const factory = contentController.nodeFactories[itemDetails.type];
            if(!factory){
                throwException("IllegalArgument", "NodeFactory not present for type " + itemDetails.type);
            }

            const node = factory(categoryId, itemDetails, displayOpenParentCategoryButton);
                node.id = contentController.createListItemId(itemDetails.id);
                if(!itemDetails.enabled){
                    node.onclick = null;
                    node.classList.add("disabled");
                }
            return node;
        }
    }
})();