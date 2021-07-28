(function ActionButtonFactory(){
    window.actionButtonFactory = new function(){
        this.create = function(parent, itemDetails, deleteCallBack){
            const optionsContainer = document.createElement("DIV");
                optionsContainer.classList.add("list-item-options-container");

                const optionsButton = document.createElement("BUTTON");
                    optionsButton.classList.add("list-item-option-button");
                    optionsButton.classList.add("list-item-options-button");
                    optionsButton.innerHTML = Localization.getAdditionalContent("list-item-options-button");
                    optionsButton.onclick = function(e){
                        e.stopPropagation();
                    }
            optionsContainer.appendChild(optionsButton)

                const buttonListWrapper = document.createElement("DIV");
                    buttonListWrapper.classList.add("list-item-options-button-list-wrapper");

                    const deleteButton = document.createElement("BUTTON");
                        deleteButton.classList.add("list-item-option-button");
                        deleteButton.classList.add("delete-button");
                        deleteButton.innerHTML = Localization.getAdditionalContent("delete-button");
                        deleteButton.onclick = function(e){
                            e.stopPropagation();
                            deleteCallBack();
                        }
                buttonListWrapper.appendChild(deleteButton);

                    const cloneButton = document.createElement("BUTTON");
                        cloneButton.classList.add("list-item-option-button");
                        cloneButton.classList.add("clone-button");
                        cloneButton.innerHTML = Localization.getAdditionalContent("clone-button");
                        cloneButton.onclick = function(e){
                            e.stopPropagation();
                            listItemCloneService.clone(itemDetails.id, true);
                        }
                buttonListWrapper.appendChild(cloneButton);

                    const editButton = document.createElement("BUTTON");
                        editButton.classList.add("list-item-option-button");
                        editButton.classList.add("edit-button");
                        editButton.innerHTML = Localization.getAdditionalContent("edit-button");
                        editButton.onclick = function(e){
                            e.stopPropagation();
                            listItemEditionService.openEditListItemWindow(parent, itemDetails);
                        }
                buttonListWrapper.appendChild(editButton);

                    const pinButton = document.createElement("BUTTON");
                        pinButton.classList.add("pin-button");
                        if(itemDetails.pinned){
                            pinButton.classList.add("pinned");
                        }
                        pinButton.onclick = function(e){
                            e.stopPropagation();
                            itemDetails.pinned ? pinController.unpin(itemDetails.id) : pinController.pin(itemDetails.id);
                        }
                buttonListWrapper.appendChild(pinButton);

            optionsContainer.appendChild(buttonListWrapper);

            return optionsContainer;
        }
    }
})();