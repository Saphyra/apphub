import React, { useEffect, useState } from "react";
import MapStream from "../../../../common/js/collection/MapStream";
import WebSocketEventName from "../../../../common/js/ws/WebSocketEventName";
import Invitaion from "./invitations/Invitation";
import "./invitations/invitations.css"

const Invitations = ({ localizationHandler, lastEvent }) => {
    const [invitations, setInvitations] = useState({});

    useEffect(() => handleEvent(), [lastEvent]);

    const handleEvent = () => {
        if (lastEvent === null) {
            return;
        }

        if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION) {
            const invitation = lastEvent.payload;

            const copy = new MapStream(invitations)
                .clone()
                .add(invitation.senderId, invitation.senderName)
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