(function LinkCreationController(){
    let currentCategoryId = null;

    window.linkCreationController = new function(){
        this.saveLink = saveLink;
        this.openCreateLinkDialog = function(){
            document.getElementById("new-link-title").value = "";
            document.getElementById("new-link-url").value = "";
            loadChildrenOfCategory(categoryContentController.getCurrentCategoryId());
            switchTab("main-page", "create-link");
            switchTab("button-wrapper", "create-link-buttons");
        }
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
        const parentButton = document.getElementById("create-link-parent-selection-parent-button");
            if(categoryId == null){
                parentButton.classList.add("disabled");
                parentButton.onclick = null;
            }else{
                parentButton.classList.remove("disabled");
                parentButton.onclick = function(){
                    loadChildrenOfCategory(parent);
                }
            }

        document.getElementById("create-link-current-category-title").innerText = title || localization.getAdditionalContent("root-title");

        const container = document.getElementById("create-link-parent-selection-category-list");
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

    function saveLink(){
        const title = document.getElementById("new-link-title").value;
        const url = document.getElementById("new-link-url").value;
        if(!title.length){
            notificationService.showError(localization.getAdditionalContent("new-item-title-empty"));
            return;
        }
        if(!url.length){
            notificationService.showError(localization.getAdditionalContent("link-url-empty"));
            return;
        }

        const request = new Request(Mapping.getEndpoint("NOTEBOOK_CREATE_LINK"), {parent: currentCategoryId, title: title, url: url});
            request.processValidResponse = function(){
                notificationService.showSuccess(localization.getAdditionalContent("link-created"));
                categoryContentController.reloadCategoryContent();
                pageController.openMainPage();
            }
        dao.sendRequestAsync(request);
    }
})();