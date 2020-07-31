(function ListItemEditionService(){
    let editedItemDetails = null;
    let currentCategoryId = null;

    window.listItemEditionService = new function(){
        this.openEditListItemWindow = openEditListItemWindow;
        this.editListItem = editListItem;
    }

    function openEditListItemWindow(parent, itemDetails){
        editedItemDetails = itemDetails;

        document.getElementById("edit-list-item-title-input").value = itemDetails.title;
        const valueInput = document.getElementById("edit-list-item-value-input");
        if(itemDetails.type == "LINK"){
            valueInput.style.visibility = "visible";
            valueInput.value = itemDetails.value;
        }else{
            valueInput.style.visibility = "hidden";
        }

        loadChildrenOfCategory(itemDetails.id, parent);
        switchTab("main-page", "edit-list-item")
    }

    function loadChildrenOfCategory(originalListItemId, categoryId){
        currentCategoryId = categoryId;
        const request = new Request(Mapping.getEndpoint("GET_CHILDREN_OF_NOTEBOOK_CATEGORY", null, {categoryId: categoryId, type: "CATEGORY", exclude: originalListItemId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body)
            }
            request.processValidResponse = function(categoryResponse){
                displayChildrenOfCategory(originalListItemId, categoryId, categoryResponse.parent, categoryResponse.children);
            }
        dao.sendRequestAsync(request);
    }

    function displayChildrenOfCategory(originalListItemId, categoryId, parent, categories){
        const parentButton = document.getElementById("edit-list-item-parent-selection-parent-button");
            if(categoryId == null){
                parentButton.classList.add("disabled");
                parentButton.onclick = null;
            }else{
                parentButton.classList.remove("disabled");
                parentButton.onclick = function(){
                    loadChildrenOfCategory(originalListItemId, parent);
                }
            }

        const container = document.getElementById("edit-list-item-parent-selection-category-list");
            container.innerHTML = "";

            if(!categories.length){
                const noContentText = document.createElement("DIV");
                    noContentText.classList.add("no-content");
                    noContentText.innerHTML = Localization.getAdditionalContent("category-empty");
                container.appendChild(noContentText);
            }

            new Stream(categories)
                .sorted(function(a, b){return a.title.localeCompare(b.title)})
                .map(function(category){return createCategoryNode(originalListItemId, category)})
                .forEach(function(node){container.appendChild(node)});

        function createCategoryNode(originalListItemId, category){
            const node = document.createElement("DIV");
                node.classList.add("category");
                node.classList.add("button");
                node.classList.add("edit-list-item-category");

                node.innerHTML = category.title;

                node.onclick = function(){
                    loadChildrenOfCategory(originalListItemId, category.id);
                }

            return node;
        }
    }

    function editListItem(){

    }
})();