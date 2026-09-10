import Button from "common/component/input/Button";
import "./invitations.css";
import { SKYXPLORE_LOBBY_ACCEPT_INVITATION, SKYXPLORE_LOBBY_PAGE } from "modules/feature/skyxplore/lobby/SkyXploreLobbyEndpoints";

const Invitation = ({ senderId, senderName, localizationHandler, declineInvitation, setDisplaySpinner }) => {
    const acceptInvitation = async () => {
        await SKYXPLORE_LOBBY_ACCEPT_INVITATION.createRequest(null, { invitorId: senderId })
            .send(setDisplaySpinner);

        window.location.href = SKYXPLORE_LOBBY_PAGE;
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

export default Invitation;