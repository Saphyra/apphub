(function SettingsController(){
    pageLoader.addLoader(addEventListeners, "Add setting event listeners");

    window.settingsController = new function(){
        this.synchronizeSettings = function(){
            document.getElementById("settings-show-archived-input").checked = settings.get("show-archived") === "true";
        }
    }

    function addEventListeners(){
        const settingsToggleButton = document.getElementById("settings-container-toggle-button");

        const switchInstance = new Switch(
            ()=>{
                settingsToggleButton.style.borderColor = "red";
                $("#settings-container").fadeToggle();
            },
            ()=>{
                settingsToggleButton.style.borderColor = "initial";
                $("#settings-container").fadeToggle();
            }
        );

        settingsToggleButton.onclick = function(){
            switchInstance.apply();
        }

        const showArchivedCheckbox = document.getElementById("settings-show-archived-input")
            showArchivedCheckbox.onchange = function(){
                settings.set("show-archived", showArchivedCheckbox.checked)
                    .then(() => {
                        categoryContentController.reloadCategoryContent();
                        pinController.loadPinnedItems();
                        categoryTreeController.reloadCategories();
                    });
            }
    }
})();