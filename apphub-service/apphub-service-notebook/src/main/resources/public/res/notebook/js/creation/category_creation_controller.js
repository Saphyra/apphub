(function CategoryCreationController(){
    let currentCategoryId = null;

    window.categoryCreationController = new function(){
        this.openCreateCategoryDialog = function(){
            loadChildrenOfCategory(categoryContentController.getCurrentCategoryId());
            document.getElementById("new-category-title").value = "";
            switchTab("main-page", "create-category");
            switchTab("button-wrapper", "create-category-buttons");
        }
        this.save = saveCategory;
    }

    function loadChildrenOfCategory(categoryId){
        currentCategoryId = categoryId;
        const request = new Request(Mapping.getEndpoint("NOTEBOOK_GET_CHILDREN_OF_CATEGORY", null, {categoryId: categoryId, type: "CATEGORY"}));
            request.convertResponse = function(response){
                return JSON.parse(response.body)
            }
            request.processValidResponse = function(categoryResponse){
                displayChildrenOfCategory(categoryId, categoryResponse.parent, categoryResponse.children, categoryResponse.title);
            }
        dao.sendRequestAsync(request);
    }

    function displayChildrenOfCategory(categoryId, parent, categories, title){
        const parentButton = document.getElementById("create-category-parent-selection-parent-button");
            if(categoryId == null){
                parentButton.classList.add("disabled");
                parentButton.onclick = null;
            }else{
                parentButton.classList.remove("disabled");
                parentButton.onclick = function(){
                    loadChildrenOfCategory(parent);
                }
            }

        document.getElementById("create-category-current-category-title").innerText = title || Localization.getAdditionalContent("root-title");

        const container = document.getElementById("create-category-parent-selection-category-list");
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

    function saveCategory(){
        const value = document.getElementById("new-category-title").value;
        if(!value.length){
            notificationService.showError(Localization.getAdditionalContent("new-item-title-empty"));
            return;
        }

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_CREATE_CATEGORY"), {parent: currentCategoryId, title: value});
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("category-created"));
                eventProcessor.processEvent(new Event(events.CATEGORY_SAVED));
            }
        dao.sendRequestAsync(request);
    }
})();