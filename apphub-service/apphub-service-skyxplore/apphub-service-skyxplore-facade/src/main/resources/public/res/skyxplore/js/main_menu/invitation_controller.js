(function InvitationController(){
    const senders = [];

    window.invitationController = new function(){
        this.createHandler = function(){
            return new WebSocketEventHandler(
                function(eventName){return eventName == "invitation"},
                displayInvitation
            )
        }
    }

    function displayInvitation(invitation){
        if(senders.indexOf(invitation.senderId) > -1){
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
                buttons.appendChild(acceptButton);

                    const rejectButton = document.createElement("BUTTON");
                        rejectButton.innerHTML = Localization.getAdditionalContent("reject-invitation");
                        rejectButton.onclick = function(){
                            container.removeChild(node);
                            senders.splice(senders.indexOf(invitation.senderId), 1);
                        }
                buttons.appendChild(rejectButton);
            node.appendChild(buttons);

        senders.push(invitation.senderId);

        animationFacade.rollInVertical(node, container, 500);
    }
})();