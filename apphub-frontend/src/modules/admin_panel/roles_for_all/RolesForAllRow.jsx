import React, { useState } from "react";
import roleLocalizationData from "../role_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import Button from "../../../common/component/input/Button";
import InputField from "../../../common/component/input/InputField";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import NotificationService from "../../../common/js/notification/NotificationService";
import { USER_DATA_ADD_ROLE_TO_ALL, USER_DATA_REMOVE_ROLE_FROM_ALL } from "../../../common/js/dao/endpoints/UserEndpoints";

const RolesForAllRow = ({ localizationHandler, role }) => {
    const roleLocalizationHandler = new LocalizationHandler(roleLocalizationData);

    const [displayAddToAllConfirmation, setDisplayAddToAllConfirmation] = useState(false);
    const [displayRemoveFromAllConfirmation, setDisplayRemoveFromAllConfirmation] = useState(false);
    const [password, setPassword] = useState("");

    const addToAll = async () => {
        if (password.length === 0) {
            NotificationService.showError(localizationHandler.get("empty-password"));
            return;
        }

        setPassword("");

        await USER_DATA_ADD_ROLE_TO_ALL.createRequest({ value: password }, { role: role })
            .send();

        NotificationService.showSuccess(localizationHandler.get("role-added-to-all"));
        setDisplayAddToAllConfirmation(false);
    }

    const removeFromAll = async () => {
        if (password.length === 0) {
            NotificationService.showError(localizationHandler.get("empty-password"));
            return;
        }

        setPassword("");

        await USER_DATA_REMOVE_ROLE_FROM_ALL.createRequest({ value: password }, { role: role })
            .send();

        NotificationService.showSuccess(localizationHandler.get("role-revoked-from-all"));
        setDisplayRemoveFromAllConfirmation(false);
    }

    const getConfirmationDialogContent = (label) => {
        return (
            <div>
                <div>{label}</div>
                <div className="centered">
                    <InputField
                        id="roles-for-all-password"
                        type="password"
                        value={password}
                        onchangeCallback={setPassword}
                        placeholder={localizationHandler.get("password")}
                    />
                </div>
            </div>
        )
    }

    const addToAllButton = () => {
        return <Button
            className="roles-for-all-add-to-all-button"
            label={localizationHandler.get("add-to-all")}
            onclick={() => setDisplayAddToAllConfirmation(true)}
        />
    }

    const removeFromAllButton = () => {
        return <Button
            className="roles-for-all-remove-from-all-button"
            label={localizationHandler.get("remove-from-all")}
            onclick={() => setDisplayRemoveFromAllConfirmation(true)}
        />
    }

    return (
        <tr
            id={"roles-for-all-role-" + role.toLowerCase()}
            className="roles-for-all-role"
        >
            <td>{roleLocalizationHandler.get(role)}</td>
            <td>
                {addToAllButton()}

                {displayAddToAllConfirmation &&
                    <ConfirmationDialog
                        id={"roles-for-all-add-to-all-confirmation-dialog"}
                        title={localizationHandler.get("add-to-all")}
                        content={getConfirmationDialogContent(localizationHandler.get("add-to-all-content", { role: roleLocalizationHandler.get(role) }))}
                        choices={[
                            <Button
                                key="add-to-all"
                                id="roles-for-all-add-to-all-button"
                                label={localizationHandler.get("add-to-all")}
                                onclick={addToAll}
                            />,
                            <Button
                                key="cancel"
                                id="roles-for-all-add-to-all-cancel-button"
                                label={localizationHandler.get("cancel")}
                                onclick={() => setDisplayAddToAllConfirmation(false)}
                            />
                        ]}
                    />
                }
            </td>
            <td>
                {removeFromAllButton()}

                {displayRemoveFromAllConfirmation &&
                    <ConfirmationDialog
                        id={"roles-for-all-remove-from-all-confirmation-dialog"}
                        title={localizationHandler.get("remove-from-all")}
                        content={getConfirmationDialogContent(localizationHandler.get("remove-from-all-content", { role: roleLocalizationHandler.get(role) }))}
                        choices={[
                            <Button
                                key="remove-from"
                                id="roles-for-all-remove-from-all-button"
                                label={localizationHandler.get("remove-from-all")}
                                onclick={removeFromAll}
                            />,
                            <Button
                                key="cancel"
                                id="roles-for-all-remove-from-all-cancel-button"
                                label={localizationHandler.get("cancel")}
                                onclick={() => setDisplayRemoveFromAllConfirmation(false)}
                            />
                        ]}
                    />
                }
            </td>
        </tr>
    );
}

export default RolesForAllRow;