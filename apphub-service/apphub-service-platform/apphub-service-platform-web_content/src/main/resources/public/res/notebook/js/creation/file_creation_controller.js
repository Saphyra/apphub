(function FileCreationController(){
    let currentCategoryId = null;

    window.fileCreationController = new function(){
        this.openCreateFileDialog = openCreateFileDialog;
        this.save = save;
    }
    
    function openCreateFileDialog(){
        document.getElementById("create-file-selected-category-title").innerText = localization.getAdditionalContent("root-title");
        document.getElementById("new-file-title").value = "";
        document.getElementById(ids.newFileInput).value = "";
        loadChildrenOfCategory(categoryContentController.getCurrentCategoryId());
        switchTab("main-page", "create-file");
        switchTab("button-wrapper", "create-file-buttons");
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
        
        function displayChildrenOfCategory(categoryId, parent, categories, title){
            document.getElementById("create-file-selected-category-title").innerText = title;
    
            const parentButton = document.getElementById("create-file-parent-selection-parent-button");
                if(categoryId == null){
                    parentButton.classList.add("disabled");
                    parentButton.onclick = null;
                }else{
                    parentButton.classList.remove("disabled");
                    parentButton.onclick = function(){
                        loadChildrenOfCategory(parent);
                    }
                }
    
            const container = document.getElementById("create-file-parent-selection-category-list");
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
    }

    function save(){
        const title = document.getElementById(ids.newFileTitle).value;
        const input = document.getElementById(ids.newFileInput);

        if(!title.length){
            notificationService.showError(localization.getAdditionalContent("new-item-title-empty"));
            return;
        }

        if(!input.files || !input.files[0]){
            notificationService.showError(localization.getAdditionalContent("no-file-selected"));
            return;
        }

        if(input.files[0].size > FILE_SIZE_LIMIT){
            notificationService.showError(localization.getAdditionalContent("file-too-big"));
            return;
        }

        const body = {
            title: title,
            parent: currentCategoryId,
            fileName: input.files[0].name,
            size: input.files[0].size
        }

        spinner.open();

        const createRequest = new Request(Mapping.getEndpoint("NOTEBOOK_CREATE_FILE"), body);
            createRequest.convertResponse = jsonConverter;
            createRequest.processValidResponse = function(response){
                uploadFile(input.files[0], response.value);
            }
        dao.sendRequestAsync(createRequest);

        function uploadFile(file, storedFileId){
            const formData = new FormData();
                formData.append("file", file);

            const uploadRequest = new Request(Mapping.getEndpoint("STORAGE_UPLOAD_FILE", {storedFileId: storedFileId}), formData);
                uploadRequest.processValidResponse = function(response){
                    notificationService.showSuccess(localization.getAdditionalContent("file-saved"));
                    categoryContentController.reloadCategoryContent();
                    pageController.openMainPage();
                    spinner.close();
                }
            dao.sendRequestAsync(uploadRequest);
        }
    }
})();