(function RoleController(){
    scriptLoader.loadScript("/res/common/js/localization/custom_localization.js");

    events.SEARCH_USER_ATTEMPT = "search_user_attempt"

    const roleLocalization = new CustomLocalization("admin_panel", "roles");
    const availableRoles = [];
    let searchUserTimeout = null;

    $(document).ready(function(){
        initAvailableRoles();
        addEventListeners();
    });

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.LOCALIZATION_LOADED},
        displayUsers,
        true
    ));

    eventProcessor.registerProcessor(new EventProcessor(
        function(eventType){return eventType == events.SEARCH_USER_ATTEMPT},
        function(){
            if(searchUserTimeout){
                clearTimeout(searchUserTimeout);
            }

            searchUserTimeout = setTimeout(displayUsers, getValidationTimeout());
        }
    ));

    function displayUsers(){
        const searchText = document.getElementById("search-input").value;
        const container = document.getElementById("users");
            container.innerHTML = "";

        if(searchText.length < 3){
            displayTooShortSearchText(container);
            return;
        }

        function displayTooShortSearchText(container){
            const node = document.createElement("div");
                node.id = "too-short-search-text";
                node.innerHTML = Localization.getAdditionalContent("too-short-search-text");
            container.appendChild(node);
        }
    }

    function initAvailableRoles(){
        const request = new Request(Mapping.getEndpoint("AVAILABLE_ROLES"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(roles){
                new Stream(roles)
                    .forEach(function(role){availableRoles.push(role)});
            }
        dao.sendRequestAsync(request);
    }

    function addEventListeners(){
        $("#search-input").on("keyup", function(){
            eventProcessor.processEvent(new Event(events.SEARCH_USER_ATTEMPT));
        });
    }
})();