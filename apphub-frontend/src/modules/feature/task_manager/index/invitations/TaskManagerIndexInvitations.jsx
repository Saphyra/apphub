import useLoader from "common/hook/Loader";
import { useState } from "react";
import { TASK_MANAGER_ACCEPT_INVITATION, TASK_MANAGER_GET_INVITATIONS, TASK_MANAGER_REJECT_INVITATION } from "../../TaskManagerEndpoints";
import Stream from "common/js/collection/Stream";
import Button from "common/component/input/Button";
import ConfirmationDialogData from "common/component/confirmation_dialog/ConfirmationDialogData";

const TaskManagerIndexInvitations = ({ setDisplaySpinner, localizationHandler, setConfirmationDialogData, refreshCounter, refresh }) => {
    const [invitations, setInvitations] = useState([]);

    useLoader({
        request: TASK_MANAGER_GET_INVITATIONS.createRequest(),
        mapper: setInvitations,
        setDisplaySpinner: setDisplaySpinner,
        listener: [refreshCounter]
    })

    return (
        <div id="task-manager-index-invitations">
            <div className="task-manager-index-section-title">{localizationHandler.get("invitations")}</div>

            {invitations.length === 0 &&
                <div className="task-manager-index-section-empty">{localizationHandler.get("no-invitations")}</div>
            }

            {invitations.length > 0 &&
                <div id="task-manager-index-invitation-list">{getInvitations()}</div>
            }
        </div>
    );

    function getInvitations() {
        return new Stream(invitations)
            .map(invitation =>
                <div
                    key={invitation.invitedByUserId + invitation.organizationId}
                    className="task-manager-index-invitation"
                    title={invitation.organizationDescription}
                >
                    {localizationHandler.get(
                        "invitation",
                        {
                            invitedByUsername: invitation.invitedByUsername,
                            invitedByUserEmail: invitation.invitedByUserEmail,
                            organizationName: invitation.organizationName
                        }
                    )}
                    <div>

                        <Button
                            className="task-manager-index-invitation-accept-button"
                            label={localizationHandler.get("accept")}
                            onclick={() => confirmAcceptInvitation(invitation)}
                        />

                        <Button
                            className="task-manager-index-invitation-reject-button"
                            label={localizationHandler.get("reject")}
                            onclick={() => confirmRejectInvitation(invitation)}
                        />
                    </div>
                </div>
            )
            .toList();

        function confirmAcceptInvitation(invitation) {
            setConfirmationDialogData(new ConfirmationDialogData(
                "task-manager-index-invitation-accept-confirmation-dialog",
                localizationHandler.get("accept-invitation-confirmation-dialog-title"),
                localizationHandler.get("accept-invitation-confirmation-dialog-message", { username: invitation.invitedByUsername, email: invitation.invitedByUserEmail, organizationName: invitation.organizationName }),
                [
                    <Button
                        key="accept"
                        id="task-manager-index-invitation-accept-confirmation-dialog-accept-button"
                        label={localizationHandler.get("accept")}
                        onclick={() => acceptInvitation(invitation)}
                    />,
                    <Button
                        key="accept"
                        id="task-manager-index-invitation-accept-confirmation-dialog-cancel-button"
                        label={localizationHandler.get("cancel")}
                        onclick={() => setConfirmationDialogData(null)}
                    />
                ]
            ));

            async function acceptInvitation(invitation) {
                await TASK_MANAGER_ACCEPT_INVITATION.createRequest(null, { organizationId: invitation.organizationId })
                    .send(setDisplaySpinner);
                refresh();
                setConfirmationDialogData(null);
            }
        }

        function confirmRejectInvitation(invitation) {
            setConfirmationDialogData(new ConfirmationDialogData(
                "task-manager-index-invitation-reject-confirmation-dialog",
                localizationHandler.get("reject-invitation-confirmation-dialog-title"),
                localizationHandler.get("reject-invitation-confirmation-dialog-message", { username: invitation.invitedByUsername, email: invitation.invitedByUserEmail, organizationName: invitation.organizationName }),
                [
                    <Button
                        key="reject"
                        id="task-manager-index-invitation-reject-confirmation-dialog-reject-button"
                        label={localizationHandler.get("reject")}
                        onclick={() => rejectInvitation(invitation)}
                    />,
                    <Button
                        key="cancel"
                        id="task-manager-index-invitation-reject-confirmation-dialog-cancel-button"
                        label={localizationHandler.get("cancel")}
                        onclick={() => setConfirmationDialogData(null)}
                    />
                ]
            ));

            async function rejectInvitation(invitation) {
                await TASK_MANAGER_REJECT_INVITATION.createRequest(null, { organizationId: invitation.organizationId })
                    .send(setDisplaySpinner);
                refresh();
                setConfirmationDialogData(null);
            }
        }
    }
}

export default TaskManagerIndexInvitations;