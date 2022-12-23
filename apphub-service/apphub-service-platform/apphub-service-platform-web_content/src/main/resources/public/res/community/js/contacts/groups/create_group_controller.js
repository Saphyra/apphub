scriptLoader.loadScript("/res/common/js/validation_util.js");

(function CreateGroupController(){
    pageLoader.addLoader(addEventListener, "Add eventListener to Create Group search input");

    let creationAllowed = false;
    let validationTimeout = null;

    window.createGroupController = new function(){
        this.openTab = openTab;
        this.createGroup = createGroup;
    }

    function openTab(){
        document.getElementById(ids.createGroupNameInput).value = "";
        validate();
        switchTab("main-page", ids.createGroup);
    }

    function validate(){
        const value = document.getElementById(ids.createGroupNameInput).value;

        let process = {
            isValid: true,
            process: createSuccessProcess(ids.createGroupInvalidName)
        }
        if(value.length < 3){
            process = {
                isValid: false,
                process: createErrorProcess(ids.createGroupInvalidName, "group-name-too-short")
            }
        } else if(value.length > 30){
           process = {
               isValid: false,
               process: createErrorProcess(ids.createGroupInvalidName, "group-name-too-long")
           }
        }

        creationAllowed = process.isValid;
        setCreateButtonStatus();
        process.process();

        return value;
    }

    function createGroup(){
        const name = validate();

        if(!creationAllowed){
            return;
        }

        const request = new Request(Mapping.getEndpoint("COMMUNITY_GROUP_CREATE"), {value: name});
            request.convertResponse = jsonConverter;
            request.processValidResponse = function(group){
                notificationService.showSuccess(localization.getAdditionalContent("group-created"));
                groupsController.addGroup(group);
                groupDetailsController.openGroup(group);
            }
        dao.sendRequestAsync(request);
    }

    function setCreateButtonStatus(){
        document.getElementById(ids.createGroupButton).disabled = !creationAllowed;
    }

    function addEventListener(){
        document.getElementById(ids.createGroupNameInput).onkeyup = function(){
            creationAllowed = false;
            setCreateButtonStatus();

            if(validationTimeout){
                clearTimeout(validationTimeout);
            }

            validationTimeout = setTimeout(validate, 1000);
        }
    }
})();