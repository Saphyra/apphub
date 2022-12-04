(function ChecklistCreationController(){
    let currentCategoryId = null;

    window.checklistCreationController = new function(){
        this.save = save;
        this.newItem = newItem;
        this.openCreateChecklistDialog = function(){
            document.getElementById("create-checklist-selected-category-title").innerText = localization.getAdditionalContent("root-title");
            document.getElementById("new-checklist-title").value = "";
            document.getElementById("new-checklist-content-wrapper").innerHTML = "";
            loadChildrenOfCategory(categoryContentController.getCurrentCategoryId());
            switchTab("main-page", "create-checklist");
            switchTab("button-wrapper", "create-checklist-buttons");
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
        document.getElementById("create-checklist-selected-category-title").innerText = title;

        const parentButton = document.getElementById("create-checklist-parent-selection-parent-button");
            if(categoryId == null){
                parentButton.classList.add("disabled");
                parentButton.onclick = null;
            }else{
                parentButton.classList.remove("disabled");
                parentButton.onclick = function(){
                    loadChildrenOfCategory(parent);
                }
            }

        const container = document.getElementById("create-checklist-parent-selection-category-list");
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

    function newItem(){
        const listItem = document.createElement("DIV");
            listItem.classList.add("checklist-item");
            listItem.classList.add("create-checklist-item");

            const reorderButtonWrapper = document.createElement("DIV");
                reorderButtonWrapper.classList.add("checklist-item-reorder-button-wrapper");

                const upButton = document.createElement("BUTTON");
                    upButton.innerText = "^";
                    upButton.onclick = function(){
                        const sibling = listItem.previousSibling;
                        if(sibling){
                            document.getElementById("new-checklist-content-wrapper").insertBefore(listItem, sibling);
                        }
                    }
            reorderButtonWrapper.appendChild(upButton);
                const downButton = document.createElement("BUTTON");
                    downButton.innerText = "v";
                    downButton.onclick = function(){
                        const sibling = listItem.nextSibling;
                        if(sibling){
                            document.getElementById("new-checklist-content-wrapper").insertBefore(listItem, sibling.nextSibling);
                        }
                    }
            reorderButtonWrapper.appendChild(downButton);
        listItem.appendChild(reorderButtonWrapper);

            const content = document.createElement("DIV");
                content.classList.add("checklist-item-content");
                content.contentEditable = true;
        listItem.appendChild(content);

            const optionsButtonWrapper = document.createElement("DIV");
                optionsButtonWrapper.classList.add("checklist-item-options-button-wrapper");

                const checked = document.createElement("INPUT");
                    checked.classList.add("checklist-item-options-checked")
                    checked.type = "checkbox";
            optionsButtonWrapper.appendChild(checked);

                const removeButton = document.createElement("BUTTON");
                    removeButton.innerText = "X";
                    removeButton.onclick = function(){
                        document.getElementById("new-checklist-content-wrapper").removeChild(listItem);
                    }
            optionsButtonWrapper.appendChild(removeButton);
        listItem.appendChild(optionsButtonWrapper);

        document.getElementById("new-checklist-content-wrapper").appendChild(listItem);
    }

    function save(){
        const title = document.getElementById("new-checklist-title").value;

        if(!title.length){
            notificationService.showError(localization.getAdditionalContent("new-item-title-empty"));
            return;
        }

        const items = document.getElementsByClassName("create-checklist-item");
        const nodes = [];

        for(let i = 0; i < items.length; i++){
            const item = items[i];
            nodes.push(fetchChecklistItemNode(item, i));
        }

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_CREATE_CHECKLIST_ITEM"), {parent: currentCategoryId, title: title, nodes: nodes});
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("checklist-saved"));
                eventProcessor.processEvent(new Event(events.LIST_ITEM_SAVED));
                pageController.openMainPage();
            }
        dao.sendRequestAsync(request);

        function fetchChecklistItemNode(listItem, order){
            const checked = listItem.querySelector(".checklist-item-options-checked").checked;
            const content = listItem.querySelector(".checklist-item-content").innerText;
            return {
                order: order,
                checked: checked,
                content: content
            }
        }
    }
})();