import React, { useState } from "react";
import MapStream from "../../../../common/js/collection/MapStream";
import Invitaion from "./invitations/Invitation";
import "./invitations/invitations.css"
import WebSocketEventName from "../../../../common/hook/ws/WebSocketEventName";
import WebSocketEndpoint from "../../../../common/hook/ws/WebSocketEndpoint";
import useConnectToWebSocket from "../../../../common/hook/ws/WebSocketFacade";

const Invitations = ({ localizationHandler }) => {
    const [invitations, setInvitations] = useState({});

    useConnectToWebSocket(
        WebSocketEndpoint.SKYXPLORE_INVITATION,
        event => processEvent(event)
    )

    const processEvent = (event) => {
        if (event.eventName === WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION) {
            const invitation = event.payload;

            const copy = new MapStream(invitations)
                .clone()
                .add(invitation.senderId, invitation.senderName)
                .toObject();
            setInvitations(copy);
        } else if (event.eventName === WebSocketEventName.SKYXPLORE_MAIN_MENU_CANCEL_INVITATION) {
            const copy = new MapStream(invitations)
                .clone()
                .filter((senderId, senderName) => senderId !== event.payload)
                .toObject();

            setInvitations(copy);
        }
    }

    const declineInvitation = (senderId) => {
        const copy = new MapStream(invitations)
            .filter((key, value) => key !== senderId)
            .toObject();
        setInvitations(copy);
    }

    const getInvitations = () => {
        return new MapStream(invitations)
            .map((senderId, senderName) =>
                <Invitaion
                    key={senderId}
                    localizationHandler={localizationHandler}
                    senderId={senderId}
                    senderName={senderName}
                    declineInvitation={declineInvitation}
                />)
            .toList();
    }

    return (
        <div id="skyxplore-main-menu-invitations">
            {getInvitations()}
        </div>
    );
}

export default Invitations;