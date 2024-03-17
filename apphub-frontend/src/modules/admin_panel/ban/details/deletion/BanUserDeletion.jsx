import React, { useEffect, useState } from "react";
import localizationData from "./ban_user_deletion_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import "./ban_user_deletion.css";
import InputField from "../../../../../common/component/input/InputField";
import Button from "../../../../../common/component/input/Button";
import NotificationService from "../../../../../common/js/notification/NotificationService";
import ConfirmationDialog from "../../../../../common/component/confirmation_dialog/ConfirmationDialog";
import Endpoints from "../../../../../common/js/dao/dao";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";

const BanUserDeletion = ({ userData, setUserData, setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [markForDeletionAt, setMarkForDeletionAt] = useState("");
    const [password, setPassword] = useState("");
    const [displayConfirmDeletionDialog, setDisplayConfirmDeletionDialog] = useState(false);

    const openScheduleDeletionConfirmation = () => {
        if (markForDeletionAt.length == 0) {
            NotificationService.showError(localizationHandler.get("time-not-selected"));
            return;
        }

        setDisplayConfirmDeletionDialog(true);
    }

    const scheduleDeletion = async () => {
        if (password.length == 0) {
            NotificationService.showError(localizationHandler.get("empty-password"));
            return;
        }

        setPassword("");

        const payload = {
            password: password,
            markForDeletionAt: markForDeletionAt
        }

        const response = await Endpoints.ACCOUNT_MARK_FOR_DELETION.createRequest(payload, { userId: userData.userId })
            .send();

        setUserData(response);
        setDisplayConfirmDeletionDialog(false);
        setMarkForDeletionAt("");
    }

    const getConfirmationDialogContent = () => {
        return (
            <div>
                <UserInfoContent
                    labelKey={"confirm-schedule-deletion-content"}
                    localizationHandler={localizationHandler}
                    userData={userData}
                />

                <div className="centered">
                    <InputField
                        id="ban-user-schedule-deletion-password"
                        type="password"
                        value={password}
                        onchangeCallback={setPassword}
                        placeholder={localizationHandler.get("password")}
                    />
                </div>
            </div>
        )
    }

    const openCancelConfirmation = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "ban-user-cancel-deletion-confirmation",
            localizationHandler.get("cancel-deletion-confirmation-title"),
            <UserInfoContent
                labelKey={"cancel-deletion-confirmation-content"}
                localizationHandler={localizationHandler}
                userData={userData}
            />,
            [
                <Button
                    key="cancel-delete"
                    id="ban-user-cancel-deletion-confirm-button"
                    label={localizationHandler.get("cancel-deletion")}
                    onclick={cancelDeletion}
                />,
                <Button
                    key="cancel"
                    id="ban-user-cancel-deletion-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const cancelDeletion = async () => {
        const response = await Endpoints.ACCOUNT_UNMARK_FOR_DELETION.createRequest(null, { userId: userData.userId })
            .send();

        setUserData(response);
        setConfirmationDialogData(null);
    }

    return (
        <div id="ban-user-deletion">
            <span id="ban-user-deletion-is-marked-for-deletion-wrapper">
                <span>{localizationHandler.get("marked-for-deletion")}</span>
                <span>: </span>
                <span id="ban-user-deletion-is-marked-for-deletion">{localizationHandler.get(userData.markedForDeletion)}</span>
            </span>

            {!userData.markedForDeletion &&
                <span id="ban-user-schedule-deletion-wrapper">
                    <InputField
                        id="ban-user-schedule-deletion-at"
                        type="datetime-local"
                        value={markForDeletionAt}
                        onchangeCallback={setMarkForDeletionAt}
                    />

                    <Button
                        id="ban-user-schedule-deletion"
                        label={localizationHandler.get("schedule-deletion")}
                        onclick={openScheduleDeletionConfirmation}
                    />
                </span>
            }

            {userData.markedForDeletion &&
                <span>
                    <span>{localizationHandler.get("marked-for-deletion-at")}</span>
                    <span>: </span>
                    <span id="ban-user-scheduled-for-deletion-at">{userData.markedForDeletionAt}</span>
                </span>
            }

            {userData.markedForDeletion &&
                <Button
                    id="ban-user-cancel-deletion"
                    label={localizationHandler.get("cancel")}
                    onclick={openCancelConfirmation}
                />
            }

            {displayConfirmDeletionDialog &&
                <ConfirmationDialog
                    id="ban-user-schedule-deletion-confirmation"
                    title={localizationHandler.get("schedule-deletion")}
                    content={getConfirmationDialogContent()}
                    choices={[
                        <Button
                            key="delete"
                            id="ban-user-schedule-deletion-confirm-button"
                            label={localizationHandler.get("schedule-deletion")}
                            onclick={scheduleDeletion}
                        />,
                        <Button
                            key="cancel"
                            id="ban-user-schedule-deletion-cancel-button"
                            label={localizationHandler.get("cancel")}
                            onclick={() => setDisplayConfirmDeletionDialog(false)}
                        />
                    ]}
                />
            }
        </div>
    );
}

const UserInfoContent = ({ labelKey, userData, localizationHandler }) => {
    return (
        <div>
            <div>{localizationHandler.get(labelKey)}</div>

            <div className="ban-user-row">
                <span>{localizationHandler.get("user-id")}</span>
                <span>: </span>
                <span id="ban-user-user-id">{userData.userId}</span>
            </div>

            <div className="ban-user-row">
                <span>{localizationHandler.get("username")}</span>
                <span>: </span>
                <span id="ban-user-username">{userData.username}</span>
            </div>

            <div className="ban-user-row">
                <span>{localizationHandler.get("email")}</span>
                <span>: </span>
                <span id="ban-user-email">{userData.email}</span>
            </div>
        </div>
    )
}

export default BanUserDeletion;