import React, { useState } from "react";
import roleLocalizationData from "../../../../role_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import localizationData from "./ban_user_banned_role_localization.json";
import Textarea from "../../../../../../common/component/input/Textarea";
import "./ban_user_banned_role.css";
import Button from "../../../../../../common/component/input/Button";
import ConfirmationDialog from "../../../../../../common/component/confirmation_dialog/ConfirmationDialog";
import InputField from "../../../../../../common/component/input/InputField";
import NotificationService from "../../../../../../common/js/notification/NotificationService";
import { ACCOUNT_REVOKE_BAN } from "../../../../../../common/js/dao/endpoints/UserEndpoints";

const BanUserBannedRole = ({ userData, ban, setUserData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const roleLocalizationHandler = new LocalizationHandler(roleLocalizationData);

    const [password, setPassword] = useState("");
    const [displayCancelConfirmationDialog, setDisplayCancelConfirmationDialog] = useState(false);

    const getExpiration = () => {
        if (ban.permanent) {
            return localizationHandler.get("permanent");
        }

        return ban.expiration;
    }

    const getConfirmationDialogContent = () => {
        return (
            <div>
                <div>
                    <div>{localizationHandler.get("confirm-cancel-ban-content", { role: roleLocalizationHandler.get(ban.bannedRole) })}</div>

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

                <div className="centered">
                    <InputField
                        id="ban-user-banned-role-password"
                        type="password"
                        value={password}
                        onchangeCallback={setPassword}
                        placeholder={localizationHandler.get("password")}
                    />
                </div>
            </div>
        )
    }

    const revokeBan = async () => {
        if (password.length === 0) {
            NotificationService.showError(localizationHandler.get("empty-password"));
            return;
        }

        setPassword("");

        const response = await ACCOUNT_REVOKE_BAN.createRequest({ value: password }, { banId: ban.id })
            .send();

        setUserData(response);
    }

    return (
        <tr className="ban-user-banned-role">
            <td className="ban-user-banned-role-role">{roleLocalizationHandler.get(ban.bannedRole)}</td>
            <td className="ban-user-banned-role-exoiration nowrap">{getExpiration()}</td>
            <td className="ban-user-banned-role-reason-cell">
                <Textarea
                    className="ban-user-banned-role-reason"
                    disabled={true}
                    value={ban.reason}
                />
            </td>
            <td>
                <div className="nowrap">
                    <span className="ban-user-banned-by-label">{localizationHandler.get("username")}</span>
                    <span>: </span>
                    <span className="ban-user-banned-by-username">{ban.bannedByUsername}</span>
                </div>

                <div className="nowrap">
                    <span className="ban-user-banned-by-label">{localizationHandler.get("email")}</span>
                    <span>: </span>
                    <span className="ban-user-banned-by-email">{ban.bannedByEmail}</span>
                </div>
            </td>
            <td>
                <Button
                    className="ban-user-banned-role-revoke-button"
                    label={localizationHandler.get("revoke-ban")}
                    onclick={() => setDisplayCancelConfirmationDialog(true)}
                />

                {displayCancelConfirmationDialog &&
                    <ConfirmationDialog
                        id="ban-user-banned-role-cancel-confirmation"
                        title={localizationHandler.get("revoke-ban")}
                        content={getConfirmationDialogContent()}
                        choices={[
                            <Button
                                key="revoke"
                                id="ban-user-banned-role-confirm-revoke-button"
                                label={localizationHandler.get("revoke-ban")}
                                onclick={revokeBan}
                            />,
                            <Button
                                key="cancel"
                                id="ban-user-banned-role-cancel-revoke-button"
                                label={localizationHandler.get("cancel")}
                                onclick={() => setDisplayCancelConfirmationDialog(false)}
                            />
                        ]}
                    />
                }
            </td>
        </tr>
    );
}

export default BanUserBannedRole;