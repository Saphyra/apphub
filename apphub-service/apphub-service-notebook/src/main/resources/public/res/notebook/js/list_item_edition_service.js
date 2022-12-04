(function ListItemEditionService(){
    let editedItemDetails = null;
    let selectedCategoryId = null;

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
        switchTab("button-wrapper", "edit-list-item-buttons")
    }

    function loadChildrenOfCategory(originalListItemId, categoryId){
        selectedCategoryId = categoryId;
        const request = new Request(Mapping.getEndpoint("NOTEBOOK_GET_CHILDREN_OF_CATEGORY", null, {categoryId: categoryId, type: "CATEGORY", exclude: originalListItemId}));
            request.convertResponse = function(response){
                return JSON.parse(response.body)
            }
            request.processValidResponse = function(categoryResponse){
                displayChildrenOfCategory(originalListItemId, categoryId, categoryResponse.parent, categoryResponse.children, categoryResponse.title);
            }
        dao.sendRequestAsync(request);
    }

    function displayChildrenOfCategory(originalListItemId, categoryId, parent, categories, title){
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

        document.getElementById("edit-list-item-current-category-title").innerText = title || localization.getAdditionalContent("root-title");

        const container = document.getElementById("edit-list-item-parent-selection-category-list");
            container.innerHTML = "";

            if(!categories.length){
                const noContentText = document.createElement("DIV");
                    noContentText.classList.add("no-content");
                    noContentText.innerText = localization.getAdditionalContent("category-empty");
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

                node.innerText = category.title;

                node.onclick = function(){
                    loadChildrenOfCategory(originalListItemId, category.id);
                }

            return node;
        }
    }

    function editListItem(){
        const title = document.getElementById("edit-list-item-title-input").value;
        const value = document.getElementById("edit-list-item-value-input").value;
        if(!title.length){
            notificationService.showError(localization.getAdditionalContent("new-item-title-empty"));
            return;
        }
        
        const isLink = editedItemDetails.type == "LINK";
        if(isLink && !value.length){
            notificationService.showError(localization.getAdditionalContent("link-url-empty"));
            return;
        }
        
        const body = {
            title: title,
            value: isLink ? value : null,
            parent: selectedCategoryId
        }

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_EDIT_LIST_ITEM", {listItemId: editedItemDetails.id}), body);
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("item-saved"));
                eventProcessor.processEvent(new Event(events.CATEGORY_SAVED));
            }
        dao.sendRequestAsync(request);
    }
})();