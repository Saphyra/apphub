import React from "react";
import Button from "../../../../../common/component/input/Button";
import Constants from "../../../../../common/js/Constants";
import Endpoints from "../../../../../common/js/dao/dao";

const Invitaion = ({ senderId, senderName, localizationHandler, declineInvitation }) => {
    const acceptInvitation = async () => {
        await Endpoints.SKYXPLORE_LOBBY_ACCEPT_INVITATION.createRequest(null, { invitorId: senderId })
            .send();

        window.location.href = Constants.SKYXPLORE_LOBBY_PAGE;
    }

    return (
        <div className="skyxplore-main-menu-invitation">
            <h4 className="skyxplore-main-menu-invitation-name">
                <span>{senderName}</span>
                <span> </span>
                <span>{localizationHandler.get("invited-you")}</span>
            </h4>
            <div className="skyxplore-main-menu-invitation-buttons">
                <Button
                    className="skyxplore-main-menu-invitation-accept-button"
                    label={localizationHandler.get("accept")}
                    onclick={acceptInvitation}
                />

                <Button
                    className="skyxplore-main-menu-invitation-decline-button"
                    label={localizationHandler.get("decline")}
                    onclick={() => declineInvitation(senderId)}
                />
            </div>
        </div>
    );
}

export default Invitaion;