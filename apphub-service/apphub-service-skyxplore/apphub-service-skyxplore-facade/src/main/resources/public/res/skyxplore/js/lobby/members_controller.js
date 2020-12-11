(function MembersController(){
    window.membersController = new function(){

    }

    $(document).ready(init);

    function loadMembers(){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_LOBBY_GET_MEMBERS"));
            request.convertResponse = function(response){
                return JSON.parse(response.body);
            }
            request.processValidResponse = function(members){
                displayMembers(members);
            }
        dao.sendRequestAsync(request);
    }

    function displayMembers(members){
        console.log(members); //TODO
    }

    function init(){
        loadMembers();
    }
})();