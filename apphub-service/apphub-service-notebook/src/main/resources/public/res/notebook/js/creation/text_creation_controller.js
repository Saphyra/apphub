(function TextCreationController(){
    let currentCategoryId = null;

    window.textCreationController = new function(){
        this.openCreateTextDialog = function(){
            document.getElementById("new-text-title").value = "";
            document.getElementById("new-text-content").value = "";
            loadChildrenOfCategory(categoryContentController.getCurrentCategoryId());
            switchTab("main-page", "create-text");
            switchTab("button-wrapper", "create-text-buttons");
        }
        this.save = save;
    }

    function loadChildrenOfCategory(categoryId){
        currentCategoryId = categoryId;

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_GET_CHILDREN_OF_CATEGORY", null, {categoryId: categoryId, type: "CATEGORY"}));
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
        document.getElementById("create-text-selected-category-title").innerHTML = title;

        const parentButton = document.getElementById("create-text-parent-selection-parent-button");
            if(categoryId == null){
                parentButton.classList.add("disabled");
                parentButton.onclick = null;
            }else{
                parentButton.classList.remove("disabled");
                parentButton.onclick = function(){
                    loadChildrenOfCategory(parent);
                }
            }

        const container = document.getElementById("create-text-parent-selection-category-list");
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
        const title = document.getElementById("new-text-title").value;
        const content = document.getElementById("new-text-content").value;

        if(!title.length){
            notificationService.showError(Localization.getAdditionalContent("new-item-title-empty"));
            return;
        }

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_CREATE_TEXT"), {parent: currentCategoryId, title: title, content: content});
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("text-saved"));
                eventProcessor.processEvent(new Event(events.LIST_ITEM_SAVED));
                pageController.openMainPage();
            }
        dao.sendRequestAsync(request);
    }
})();