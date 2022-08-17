(function CategoryListController(){
    let openedCategories = [];

    eventProcessor.registerProcessor(new EventProcessor(
        (eventType) => {return eventType == events.SETTINGS_LOADED},
        loadCategories,
        true,
        "Load category tree"
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        (eventType) => {return eventType == events.SETTINGS_MODIFIED || eventType == events.ITEM_ARCHIVED},
        reloadCategories,
        false,
        "Reload category tree"
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.CATEGORY_DELETED},
        function(event){
            document.getElementById(createWrapperId(event.getPayload())).remove();
        },
        false,
        "Delete category from category tree"
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.CATEGORY_SAVED},
        reloadCategories,
        false,
        "Reload category tree"
    ));

    function reloadCategories(){
        openedCategories = new Stream(document.getElementsByClassName("category-children-container"))
            .filter(function(node){
                return node.style.display != "none"
            })
            .map(function(node){return node.parentElement})
            .map(function(node){return node.id})
            .toList();
        loadCategories();
    }

    function loadCategories(){
        const request = new Request(Mapping.getEndpoint("NOTEBOOK_GET_CATEGORY_TREE"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(categories){
                displayCategories(categories);
            }
        dao.sendRequestAsync(request);
    }

    function displayCategories(categories){
        const container = document.getElementById("category-list");
            container.innerHTML = "";

            const root = {
                categoryId: null,
                title: Localization.getAdditionalContent("root-title"),
                children: categories
            };

        container.appendChild(createTree(root));

        function createTree(category){
            const wrapper = document.createElement("DIV");
                wrapper.classList.add("category-wrapper");
                if(category.categoryId == null){
                    wrapper.classList.add("root-wrapper");
                }

                wrapper.id = createWrapperId(category.categoryId);

                const button = document.createElement("DIV");
                    button.classList.add("button");
                    button.classList.add("category");
                    button.classList.add("category-view-button");
                    button.onclick = function(){
                        categoryContentController.loadCategoryContent(category.categoryId, true);
                    }

                    const titleLabel = document.createElement("SPAN");
                        titleLabel.classList.add("category-view-button-title")
                        titleLabel.innerText = category.title;
                wrapper.appendChild(button);

                    const toggleButton = document.createElement("SPAN");
                        toggleButton.classList.add("button");
                        toggleButton.classList.add("category-view-toggle-button");

                        const isOpened = openedCategories.indexOf(createWrapperId(category.categoryId)) > -1;

                        if(isOpened || category.categoryId == null){
                            toggleButton.innerText = "^";
                        }else{
                            toggleButton.innerText = "v";
                        }

                button.appendChild(toggleButton);
                button.appendChild(titleLabel);

                    const displayedChildren = new Stream(category.children)
                          .sorted(function(c1, c2){return c1.title.localeCompare(c2.title)})
                          .filter((item)=>{return isTrue(settings.get("show-archived")) || !item.archived})
                          .toList();

                    if(displayedChildren.length == 0){
                        toggleButton.classList.add("invisible");
                    }else{
                        const childrenContainer = document.createElement("DIV");
                            if(category.categoryId == null || isOpened){
                                childrenContainer.style.display = "block";
                            }else{
                                childrenContainer.style.display = "none";
                            }
                            childrenContainer.classList.add("category-children-container");
                    wrapper.appendChild(childrenContainer);

                            new Stream(displayedChildren)
                                .map(createTree)
                                .forEach(function(w){childrenContainer.appendChild(w)});

                        toggleButton.onclick = function(e){
                            e.stopPropagation();
                            if(childrenContainer.style.display != "none"){
                                toggleButton.innerText = "v";
                                $(childrenContainer).fadeOut();
                            }else{
                                toggleButton.innerText = "^";
                                $(childrenContainer).fadeIn();
                            }
                        }
                    }

            return wrapper;
        }
    }

    function createWrapperId(categoryId){
        return "category-wrapper-" + categoryId;
    }
})();