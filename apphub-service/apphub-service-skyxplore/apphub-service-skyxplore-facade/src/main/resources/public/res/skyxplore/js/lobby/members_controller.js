(function MembersController(){
    const members = {};

    window.membersController = new function(){
        this.createCharacterJoinedHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return eventName == "join-to-lobby"},
                processCharacterJoinedEvent
            );
        };

        this.createReadinessHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return eventName == webSocketEvents.SET_READINESS},
                setReadiness
            )
        }

        this.createAllianceChangedHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return eventName == webSocketEvents.CHANGE_ALLIANCE},
                processAllianceChangedEvent
            )
        }

        this.setReady = setReady;
        this.setUnready = setUnready;
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
        document.getElementById(ids.host).appendChild(createMemberPanel(members.host, true, members.alliances));

        const membersContainer = document.getElementById(ids.membersList);

            new Stream(members.members)
                .map(function(member){return createMemberPanel(member, false, members.alliances)})
                .forEach(function(node){membersContainer.appendChild(node)});
    }

    function processCharacterJoinedEvent(event){
        const existingMember = members[event.userId];

        if(existingMember){
            existingMember.setReadiness(event.ready);
        }else{
            const member = {
                userId: event.userId,
                characterName: event.characterName,
                ready: event.ready,
                alliance: event.alliance
            }

            const memberPanel = createMemberPanel(member, event.host, event.alliances);
            if(event.host){
                const container = document.getElementById(ids.host);
                    container.innerHTML = "";
                    container.appendChild(memberPanel);
            }else{
                const container = document.getElementById(ids.membersList);
                    container.appendChild(memberPanel);
            }
        }
    }

    function createMemberPanel(member, isHost, alliances){
        if(members[member.userId]){
            return members[member.userId].container;
        }

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
                    if(member.userId != window.userId && window.host != window.userId){
                        allianceSelectMenu.disabled = "disabled";
                    }else{
                        allianceSelectMenu.onchange = function(){
                            handleAllianceChange(allianceSelectMenu.value, member.userId);
                        }
                    }

                    const noAllianceOption = document.createElement("OPTION");
                        noAllianceOption.innerHTML = Localization.getAdditionalContent("no-alliance");
                        noAllianceOption.value = "no-alliance";
                allianceSelectMenu.appendChild(noAllianceOption);

                    const newAllianceOption = document.createElement("OPTION");
                        newAllianceOption.innerHTML = Localization.getAdditionalContent("new-alliance");
                        newAllianceOption.value = "new-alliance";
                allianceSelectMenu.appendChild(newAllianceOption);

                new Stream(alliances)
                    .map(function(alliance){
                        const option = document.createElement("OPTION");
                            option.innerHTML = alliance
                            option.value = alliance;
                        return option
                    })
                    .forEach(function(node){allianceSelectMenu.appendChild(node)});

                allianceSelectMenu.value = member.alliance || "no-alliance";
            allianceNode.appendChild(allianceSelectMenu);
        container.appendChild(allianceNode);

        const memberData = {
            container: container,
            ready: member.ready,
            userId: member.userId,
            allianceSelectMenu: allianceSelectMenu,
            isHost: isHost,

            setReadiness: function(r){
                if(r){
                    container.classList.add("ready");
                }else{
                    container.classList.remove("ready");
                }
            }
        }

        members[member.userId] = memberData;
        return container;
    }

    function setReadiness(event){
        members[event.userId].setReadiness(event.ready);
    }

    function setReady(){
        const event = new WebSocketEvent(webSocketEvents.SET_READINESS, true);
        pageController.webSocketConnection.sendEvent(event);

        switchTab("ready-button", "set-unready");
    }

    function setUnready(){
        const event = new WebSocketEvent(webSocketEvents.SET_READINESS, false);
        pageController.webSocketConnection.sendEvent(event);

        switchTab("ready-button", "set-ready");
    }

    function handleAllianceChange(allianceValue, userId){
        const payload = {
            userId: userId,
            alliance: allianceValue
        };

        const event = new WebSocketEvent(webSocketEvents.CHANGE_ALLIANCE, payload);

        pageController.webSocketConnection.sendEvent(event);
    }

    function processAllianceChangedEvent(event){
        if(event.newAlliance){
            new MapStream(members)
                .toListStream()
                .forEach(function(member){
                    const option = document.createElement("OPTION");
                            option.innerHTML = event.alliance;
                            option.value = event.alliance;
                    member.allianceSelectMenu.appendChild(option);
                });
        }

        members[event.userId].allianceSelectMenu.value = event.alliance;
    }

    function init(){
        loadMembers();
    }
})();