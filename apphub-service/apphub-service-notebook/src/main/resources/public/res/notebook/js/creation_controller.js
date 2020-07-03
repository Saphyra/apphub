(function CreationController(){
    events.SAVE_CATEGORY = "save-category";
    events.CATEGORY_SAVED = "CATEGORY_SAVED";

    let currentCategoryId = null;

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.OPEN_CREATE_CATEGORY_DIALOG},
        function(){
            loadChildrenOfCategory(null);
        }
    ));

    eventProcessor.registerProcessor(new EventProcessor(
            function(eventType){return eventType == events.SAVE_CATEGORY},
            saveCategory
        ));

    function loadChildrenOfCategory(categoryId){
        currentCategoryId = categoryId;
        const request = new Request(Mapping.getEndpoint("GET_CHILDREN_OF_NOTEBOOK_CATEGORY", null, {categoryId: categoryId, type: "CATEGORY"}));
            request.convertResponse = function(response){
                return JSON.parse(response.body)
            }
            request.processValidResponse = function(categoryResponse){
                displayChildrenOfCategory(categoryId, categoryResponse.parent, categoryResponse.children);
            }
        dao.sendRequestAsync(request);
    }

    function displayChildrenOfCategory(categoryId, parent, categories){
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

        const container = document.getElementById("create-category-parent-selection-category-list");
            container.innerHTML = "";

            new Stream(categories)
                .sorted(function(a, b){return a.title.localeCompare(b.title)})
                .map(function(category){return createCategoryNode(category)})
                .forEach(function(node){container.appendChild(node)});

        function createCategoryNode(category){
            const node = document.createElement("DIV");
                node.classList.add("category");
                node.classList.add("button");
                node.classList.add("create-category-category");

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
            notificationService.showError(Localization.getAdditionalContent("new-category-title-empty"));
            return;
        }

        const request = new Request(Mapping.getEndpoint("CREATE_NOTEBOOK_CATEGORY"), {parent: currentCategoryId, title: value});
            request.processValidResponse = function(){
                notificationService.showSuccess(Localization.getAdditionalContent("category-created"));
                eventProcessor.processEvent(new Event(events.CATEGORY_SAVED));
            }
        dao.sendRequestAsync(request);
    }
})();