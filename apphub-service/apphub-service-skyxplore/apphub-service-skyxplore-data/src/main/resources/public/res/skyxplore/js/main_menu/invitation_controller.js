(function InvitationController(){
    const invitations = {};

    window.invitationController = new function(){
        this.createHandlers = function(){
            return [
                new WebSocketEventHandler(
                    function(eventName){return eventName == "invitation"},
                    displayInvitation
                ),
                new WebSocketEventHandler(
                    function(eventName){return eventName == "skyxplore-main-menu-cancel-invitation"},
                    rejectInvitation
                )
            ]
        }
    }

    function displayInvitation(invitation){
        if(invitations[invitation.senderId]){
            return;
        }

        const container = document.getElementById(ids.invitationContainer);

            const node = document.createElement("DIV");
                node.classList.add("invitation");

                const title = document.createElement("DIV");
                    title.classList.add("invitation-title");
                    title.innerHTML = Localization.getAdditionalContent("invitation-title");
            node.appendChild(title);

                const name = document.createElement("DIV");
                    name.classList.add("invitation-sender-name");
                    name.innerHTML = invitation.senderName;
            node.appendChild(name);

                const message = document.createElement("DIV");
                    message.classList.add("invitation-message");
                    message.innerHTML = Localization.getAdditionalContent("invitation-message");
            node.appendChild(message);

                const buttons = document.createElement("DIV");
                    buttons.classList.add("invitation-buttons");

                    const acceptButton = document.createElement("BUTTON");
                        acceptButton.innerHTML = Localization.getAdditionalContent("accept-invitation");
                        acceptButton.onclick = function(){
                            acceptInvitation(invitation.senderId);
                        }
                buttons.appendChild(acceptButton);

                    const rejectButton = document.createElement("BUTTON");
                        rejectButton.innerHTML = Localization.getAdditionalContent("reject-invitation");
                        rejectButton.onclick = function(){
                            rejectInvitation(invitation.senderId);
                        }
                buttons.appendChild(rejectButton);
            node.appendChild(buttons);

        invitations[invitation.senderId] = node;
        roll.rollInVertical(node, container, 500);
    }

    function rejectInvitation(senderId){
        const node = invitations[senderId];
        document.getElementById(ids.invitationContainer).removeChild(node);
        delete invitations[senderId];
    }

    function acceptInvitation(senderId){
        const request = new Request(Mapping.getEndpoint("SKYXPLORE_LOBBY_ACCEPT_INVITATION", {invitorId: senderId}));
            request.processValidResponse = function(){
                window.location.href = Mapping.SKYXPLORE_LOBBY_PAGE;
            }
        dao.sendRequestAsync(request);
    }
})();