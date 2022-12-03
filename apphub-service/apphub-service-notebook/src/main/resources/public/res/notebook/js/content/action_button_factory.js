(function ActionButtonFactory(){
    window.actionButtonFactory = new function(){
        this.create = function(parent, itemDetails, node, deleteCallBack, displayOpenParentCategoryButton){
            const optionsContainer = document.createElement("DIV");
                optionsContainer.classList.add("list-item-options-container");

                const copyTitleButton = domBuilder.create("BUTTON")
                    .addClass("list-item-copy-title-button")
                    .innerText("C")
                    .onclick((e) => {
                        e.stopPropagation();
                        navigator.clipboard.writeText(itemDetails.title);
                        notificationService.showSuccess(Localization.getAdditionalContent("list-item-title-copied"));
                    })
                    .appendTo(optionsContainer);

                if(displayOpenParentCategoryButton){
                    const openParentCategoryButton = document.createElement("BUTTON");
                        openParentCategoryButton.classList.add("list-item-option-button");
                        openParentCategoryButton.classList.add("list-item-open-parent-button");

                        openParentCategoryButton.title = itemDetails.parentTitle || Localization.getAdditionalContent("root-title");
                        openParentCategoryButton.innerText = Localization.getAdditionalContent("open-parent");

                        openParentCategoryButton.onclick = function(e){
                            e.stopPropagation();
                            categoryContentController.loadCategoryContent(itemDetails.parentId);
                        }
                    optionsContainer.appendChild(openParentCategoryButton);
                }

                const optionsButtonWrapper = document.createElement("SPAN");
                    optionsButtonWrapper.classList.add("list-item-options-wrapper");

                    const optionsButton = document.createElement("BUTTON");
                        optionsButton.classList.add("list-item-option-button");
                        optionsButton.classList.add("list-item-options-button");
                        optionsButton.innerText = Localization.getAdditionalContent("list-item-options-button");
                        optionsButton.onclick = function(e){
                            e.stopPropagation();
                        }
                optionsButtonWrapper.appendChild(optionsButton)

                    const buttonListWrapper = document.createElement("DIV");
                        buttonListWrapper.classList.add("list-item-options-button-list-wrapper");

                        const deleteButton = document.createElement("BUTTON");
                            deleteButton.classList.add("list-item-option-button");
                            deleteButton.classList.add("delete-button");
                            deleteButton.innerText = Localization.getAdditionalContent("delete-button");
                            deleteButton.onclick = function(e){
                                e.stopPropagation();
                                deleteCallBack();
                            }
                    buttonListWrapper.appendChild(deleteButton);

                        const cloneButton = document.createElement("BUTTON");
                            cloneButton.classList.add("list-item-option-button");
                            cloneButton.classList.add("clone-button");
                            cloneButton.innerText = Localization.getAdditionalContent("clone-button");
                            cloneButton.onclick = function(e){
                                e.stopPropagation();
                                listItemCloneService.clone(itemDetails.id, true);
                            }
                    buttonListWrapper.appendChild(cloneButton);

                        const editButton = document.createElement("BUTTON");
                            editButton.classList.add("list-item-option-button");
                            editButton.classList.add("edit-button");
                            editButton.innerText = Localization.getAdditionalContent("edit-button");
                            editButton.onclick = function(e){
                                e.stopPropagation();
                                listItemEditionService.openEditListItemWindow(parent, itemDetails);
                            }
                    buttonListWrapper.appendChild(editButton);

                        const pinButton = document.createElement("BUTTON");
                            pinButton.title = Localization.getAdditionalContent("pin-button-title");
                            pinButton.classList.add("pin-button");
                            if(itemDetails.pinned){
                                pinButton.classList.add("pinned");
                            }
                            pinButton.onclick = function(e){
                                e.stopPropagation();
                                itemDetails.pinned ? pinController.unpin(itemDetails.id) : pinController.pin(itemDetails.id);
                            }
                    buttonListWrapper.appendChild(pinButton);

                        const archiveButton = document.createElement("BUTTON");
                            archiveButton.title = Localization.getAdditionalContent("archive-button-title");
                            archiveButton.classList.add("archive-button");
                            if(itemDetails.archived){
                                node.classList.add("archived");
                            }
                            archiveButton.onclick = function(e){
                                e.stopPropagation();
                                itemDetails.archived = !itemDetails.archived;

                                const request = new Request(Mapping.getEndpoint("NOTEBOOK_ARCHIVE_ITEM", {listItemId: itemDetails.id}), {value: itemDetails.archived});
                                    request.processValidResponse = function(){
                                        itemDetails.archived ? node.classList.add("archived") : node.classList.remove("archived");
                                        if(!itemDetails.archived && settings.get("show-archived" !== "true")){
                                            eventProcessor.processEvent(new Event(events.ITEM_ARCHIVED));
                                        }
                                    }
                                dao.sendRequestAsync(request);
                            }
                    buttonListWrapper.appendChild(archiveButton);

                optionsButtonWrapper.appendChild(buttonListWrapper);

            optionsContainer.appendChild(optionsButtonWrapper);

            return optionsContainer;
        }
    }
})();