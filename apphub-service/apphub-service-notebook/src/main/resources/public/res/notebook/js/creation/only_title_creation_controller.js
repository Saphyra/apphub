(function OnlyTitleCreationController(){
    let currentCategoryId = null;

    window.onlyTitleCreationController = new function(){
        this.save = save;
        this.openCreateOnlyTitleDialog = function(){
            loadChildrenOfCategory(categoryContentController.getCurrentCategoryId());
            document.getElementById("new-only-title-title").value = "";
            switchTab("main-page", "create-only-title");
            switchTab("button-wrapper", "create-only-title-buttons");
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
       const parentButton = document.getElementById("create-only-title-parent-selection-parent-button");
               if(categoryId == null){
                   parentButton.classList.add("disabled");
                   parentButton.onclick = null;
               }else{
                   parentButton.classList.remove("disabled");
                   parentButton.onclick = function(){
                       loadChildrenOfCategory(parent);
                   }
               }

           document.getElementById("create-only-title-current-category-title").innerText = title || localization.getAdditionalContent("root-title");

           const container = document.getElementById("create-only-title-parent-selection-category-list");
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

    function save(){
        const value = document.getElementById("new-only-title-title").value;

        if(!value.length){
            notificationService.showError(localization.getAdditionalContent("new-item-title-empty"));
            return;
        }

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_CREATE_ONLY_TITLE"), {parent: currentCategoryId, title: value});
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("only-title-created"));
                eventProcessor.processEvent(new Event(events.LIST_ITEM_SAVED));
                pageController.openMainPage();
            }
        dao.sendRequestAsync(request);
    }
})();