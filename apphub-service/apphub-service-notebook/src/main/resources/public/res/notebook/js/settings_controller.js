(function SettingsController(){
    pageLoader.addLoader(addEventListeners, "Add setting event listeners");

    eventProcessor.registerProcessor(new EventProcessor(
        (eventType) => {return eventType == events.SETTINGS_LOADED},
        synchronizeSettings,
        true,
        "Synchronize settings UI based on config"
    ));

    function synchronizeSettings(){
        document.getElementById("settings-show-archived-input").checked = settings.get("show-archived") === "true";
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
                    .then(() => eventProcessor.processEvent(new Event(events.SETTINGS_MODIFIED)));
            }
    }
})();