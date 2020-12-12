(function MembersController(){
    const members = {};

    window.membersController = new function(){
        this.createCharacterJoinedHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return eventName == "join-to-lobby"},
                processCharacterJoinedEvent
            );
        };
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
        document.getElementById(ids.host).appendChild(createMemberPanel(members.host, true));

        const membersContainer = document.getElementById(ids.membersList, false);

            new Stream(members.members)
                .map(createMemberPanel)
                .forEach(function(node){membersContainer.appendChild(node)});
    }

    function processCharacterJoinedEvent(event){
        //TODO
    }

    function createMemberPanel(member, isHost){
        const container = document.createElement("DIV");
            container.classList.add("lobby-member");

            const memberName = document.createElement("DIV");
                memberName.classList.add("lobby-member-name");
                memberName.innerHTML = member.characterName;
        container.appendChild(memberName);

            const allianceNode = document.createElement("DIV");
                allianceNode.classList.add("lobby-member-alliance-container");
                const allianceNodeTitle = document.createElement("SPAN");
                    allianceNodeTitle.innerHTML = Localization.getAdditionalContent("alliance-title") + ": ";
            allianceNode.appendChild(allianceNodeTitle);

                const allianceSelectMenu = document.createElement("SELECT");
                    const noAllianceOption = document.createElement("OPTION");
                        noAllianceOption.innerHTML = Localization.getAdditionalContent("no-alliance");
                        noAllianceOption.value = "";
                allianceSelectMenu.appendChild(noAllianceOption);

                    const newAllianceOption = document.createElement("OPTION");
                        newAllianceOption.innerHTML = Localization.getAdditionalContent("new-alliance");
                allianceSelectMenu.appendChild(newAllianceOption);
            allianceNode.appendChild(allianceSelectMenu);
        container.appendChild(allianceNode);

        const memberData = {
            container: container,
            ready: member.ready,
            userId: member.userId,
            allianceSelectMenu: allianceSelectMenu,
            isHost: isHost
        }

        members[member.userId] = memberData;
        return container;
    }

    function init(){
        loadMembers();
    }
})();