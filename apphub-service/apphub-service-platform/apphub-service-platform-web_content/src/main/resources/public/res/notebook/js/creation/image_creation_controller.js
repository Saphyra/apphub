(function ImageCreationController(){
    pageLoader.addLoader(addEventListeners, "Add create image event listeners");

    let currentCategoryId = null;

    window.imageCreationController = new function(){
        this.openCreateImageDialog = openCreateImageDialog;
        this.save = save;
    }
    
    function openCreateImageDialog(){
        document.getElementById("create-image-selected-category-title").innerText = localization.getAdditionalContent("root-title");
        document.getElementById("new-image-title").value = "";
        document.getElementById(ids.newImageInput).value = "";
        document.getElementById(ids.newImagePreview).src = "";
        loadChildrenOfCategory(categoryContentController.getCurrentCategoryId());
        switchTab("main-page", "create-image");
        switchTab("button-wrapper", "create-image-buttons");
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
            document.getElementById("create-image-selected-category-title").innerText = title;
    
            const parentButton = document.getElementById("create-image-parent-selection-parent-button");
                if(categoryId == null){
                    parentButton.classList.add("disabled");
                    parentButton.onclick = null;
                }else{
                    parentButton.classList.remove("disabled");
                    parentButton.onclick = function(){
                        loadChildrenOfCategory(parent);
                    }
                }
    
            const container = document.getElementById("create-image-parent-selection-category-list");
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
        const title = document.getElementById(ids.newImageTitle).value;
        const input = document.getElementById(ids.newImageInput);

        if(!title.length){
            notificationService.showError(localization.getAdditionalContent("new-item-title-empty"));
            return;
        }

        if(!input.files || !input.files[0]){
            notificationService.showError(localization.getAdditionalContent("no-file-selected"));
            return;
        }

        const body = {
            title: title,
            parent: currentCategoryId,
            fileName: input.files[0].name,
            size: input.files[0].size
        }

        //TODO add spinner

        const createRequest = new Request(Mapping.getEndpoint("NOTEBOOK_CREATE_IMAGE"), body);
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
                    notificationService.showSuccess(localization.getAdditionalContent("image-saved"));
                    categoryContentController.reloadCategoryContent();
                    pageController.openMainPage();
                }
            dao.sendRequestAsync(uploadRequest);
        }
    }

    function addEventListeners(){
        const input = document.getElementById(ids.newImageInput);
        input.onchange = function(){
            if(input.files && input.files[0]){
                const reader = new FileReader();

                reader.onload = (e) => {document.getElementById(ids.newImagePreview).src = e.target.result};

                reader.readAsDataURL(input.files[0]);
            }
        }
    }
})();