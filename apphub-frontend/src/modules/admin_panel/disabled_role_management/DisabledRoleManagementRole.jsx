import React, { useState } from "react";
import roleLocalizationData from "../role_localization.json"
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import InputField from "../../../common/component/input/InputField";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import Button from "../../../common/component/input/Button";
import NotificationService from "../../../common/js/notification/NotificationService";
import { USER_DATA_DISABLE_ROLE, USER_DATA_ENABLE_ROLE } from "../../../common/js/dao/endpoints/UserEndpoints";

const DisabledRoleManagementRole = ({ role, enabled, localizationHandler, setRoles }) => {
    const roleLocalizationHandler = new LocalizationHandler(roleLocalizationData);

    const [password, setPassword] = useState("");
    const [displayEnableConfirmation, setDisplayEnableConfirmation] = useState(false);
    const [displayDisableConfirmation, setDisplayDisableConfirmation] = useState(false);

    const modifyStatus = (enabled) => {
        if (enabled) {
            setDisplayEnableConfirmation(true);
        } else {
            setDisplayDisableConfirmation(true);
        }
    }

    const enableRole = async () => {
        if (password.length === 0) {
            NotificationService.showError(localizationHandler.get("empty-password"));
            return;
        }

        setPassword("");

        const response = await USER_DATA_ENABLE_ROLE.createRequest({ value: password }, { role: role })
            .send();

        setRoles(response);
        setDisplayEnableConfirmation(false);
        NotificationService.showSuccess(localizationHandler.get("role-enabled"));
    }

    const disableRole = async () => {
        if (password.length === 0) {
            NotificationService.showError(localizationHandler.get("empty-password"));
            return;
        }

        setPassword("");

        const response = await USER_DATA_DISABLE_ROLE.createRequest({ value: password }, { role: role })
            .send();

        setRoles(response);
        setDisplayDisableConfirmation(false);
        NotificationService.showSuccess(localizationHandler.get("role-disabled"));
    }

    const getConfirmationDialogContent = (label) => {
        return (
            <div>
                <div>{label}</div>
                <div className="centered">
                    <InputField
                        id="disabled-role-management-password"
                        type="password"
                        value={password}
                        onchangeCallback={setPassword}
                        placeholder={localizationHandler.get("password")}
                    />
                </div>
            </div>
        )
    }

    return (
        <tr className={"disabled-role-management-role role-" + role}>
            <td className="disabled-role-management-role-name">{roleLocalizationHandler.get(role)}</td>
            <td className="disabled-role-management-role-enabled-cell">
                <InputField
                    className="disabled-role-management-role-enabled"
                    type="checkbox"
                    checked={enabled}
                    onchangeCallback={modifyStatus}
                />

                {displayEnableConfirmation &&
                    <ConfirmationDialog
                        id="disabled-role-management-enable-role-confirmation"
                        title={localizationHandler.get("enable-role")}
                        content={getConfirmationDialogContent(localizationHandler.get("enable-role-content", { role: roleLocalizationHandler.get(role) }))}
                        choices={[
                            <Button
                                key="enable"
                                id="disabled-role-management-enable-button"
                                onclick={enableRole}
                                label={localizationHandler.get("enable-role")}
                            />,
                            <Button
                                key="cancel"
                                id="disabled-role-management-enable-cancel-button"
                                onclick={() => setDisplayEnableConfirmation(false)}
                                label={localizationHandler.get("cancel")}
                            />
                        ]}
                    />
                }

                {displayDisableConfirmation &&
                    <ConfirmationDialog
                        id="disabled-role-management-disable-role-confirmation"
                        title={localizationHandler.get("disable-role")}
                        content={getConfirmationDialogContent(localizationHandler.get("disable-role-content", { role: roleLocalizationHandler.get(role) }))}
                        choices={[
                            <Button
                                key="disable"
                                id="disabled-role-management-disable-button"
                                onclick={disableRole}
                                label={localizationHandler.get("disable-role")}
                            />,
                            <Button
                                key="cancel"
                                id="disabled-role-management-disable-cancel-button"
                                onclick={() => setDisplayDisableConfirmation(false)}
                                label={localizationHandler.get("cancel")}
                            />
                        ]}
                    />
                }
            </td>
        </tr>
    );
}

export default DisabledRoleManagementRole;