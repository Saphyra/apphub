import React, { useState } from "react";
import roles from "../../roles.json";
import roleLocalizationData from "../../role_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import Stream from "../../../../common/js/collection/Stream";
import Button from "../../../../common/component/input/Button";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";
import InputField from "../../../../common/component/input/InputField";
import Utils from "../../../../common/js/Utils";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Endpoints from "../../../../common/js/dao/dao";

const RoleManagementSearchResultUser = ({ localizationHandler, user, users, setUsers, query }) => {
    const roleLocalizationHandler = new LocalizationHandler(roleLocalizationData);

    const [password, setPassword] = useState("");
    const [roleToGrant, setRoleToGrant] = useState(null);
    const [roleToRevoke, setRoleToRevoke] = useState(null);

    const getGrantedRoles = () => {
        return new Stream(user.roles)
            .filter(role => roles.indexOf(role) >= 0)
            .sorted(roleComparator)
            .map(role => <Button
                key={role}
                className={"role-management-role-button role-" + role}
                label={roleLocalizationHandler.get(role)}
                onclick={() => setRoleToRevoke(role)}
            />)
            .toList();
    }

    const getAvailableRoles = () => {
        return new Stream(roles)
            .filter(role => user.roles.indexOf(role) < 0)
            .sorted(roleComparator)
            .map(role => <Button
                key={role}
                className={"role-management-role-button role-" + role}
                label={roleLocalizationHandler.get(role)}
                onclick={() => setRoleToGrant(role)}
            />)
            .toList();
    }

    const roleComparator = (a, b) => {
        return roleLocalizationHandler.get(a).localeCompare(roleLocalizationHandler.get(b));
    }

    const grantRole = async () => {
        if (password.length === 0) {
            NotificationService.showError(localizationHandler.get("empty-password"));
            return;
        }

        setPassword("");

        const payload = {
            userId: user.userId,
            role: roleToGrant,
            password: password,
            query: query
        }

        const response = await Endpoints.USER_DATA_ADD_ROLE.createRequest(payload)
            .send();

        updateUser(response);
        setRoleToGrant(null);
        NotificationService.showSuccess(localizationHandler.get("role-granted"));
    }

    const revokeRole = async () => {
        if (password.length === 0) {
            NotificationService.showError(localizationHandler.get("empty-password"));
            return;
        }

        setPassword("");

        const payload = {
            userId: user.userId,
            role: roleToRevoke,
            password: password,
            query: query
        }

        const response = await Endpoints.USER_DATA_REMOVE_ROLE.createRequest(payload)
            .send();

        updateUser(response);
        setRoleToRevoke(null);
        NotificationService.showSuccess(localizationHandler.get("role-revoked"));
    }

    const updateUser = (user) => {
        const copy = new Stream(users)
            .remove(u => u.userId === user.userId)
            .add(user)
            .toList();

        setUsers(copy);
    }

    const getConfirmationDialogContent = (label) => {
        return (
            <div>
                <div>{label}</div>
                <div className="centered">
                    <InputField
                        id="role-management-password"
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
        <tr className="role-management-search-result-user">
            <td className="role-management-search-result-user-name">{user.username}</td>
            <td className="role-management-search-result-user-email">{user.email}</td>
            <td className="role-management-search-result-user-granted-roles">
                {getGrantedRoles()}

                {Utils.hasValue(roleToGrant) &&
                    <ConfirmationDialog
                        id={"role-management-grant-role-confirmation"}
                        title={localizationHandler.get("grant-role")}
                        content={getConfirmationDialogContent(localizationHandler.get(
                            "grant-role-content",
                            {
                                role: roleLocalizationHandler.get(roleToGrant),
                                username: user.username,
                                email: user.email
                            }
                        ))}
                        choices={[
                            <Button
                                key="grant-role"
                                id="role-management-grant-role-button"
                                label={localizationHandler.get("grant-role")}
                                onclick={grantRole}
                            />,
                            <Button
                                key="cancel"
                                id="role-management-grant-role-cancel-button"
                                label={localizationHandler.get("cancel")}
                                onclick={() => setRoleToGrant(null)}
                            />
                        ]}
                    />
                }
            </td>
            <td className="role-management-search-result-user-available-roles">
                {getAvailableRoles()}

                {Utils.hasValue(roleToRevoke) &&
                    <ConfirmationDialog
                        id={"role-management-revoke-role-confirmation"}
                        title={localizationHandler.get("revoke-role")}
                        content={getConfirmationDialogContent(localizationHandler.get(
                            "revoke-role-content",
                            {
                                role: roleLocalizationHandler.get(roleToRevoke),
                                username: user.username,
                                email: user.email
                            }
                        ))}
                        choices={[
                            <Button
                                key="revoke-role"
                                id="role-management-revoke-role-button"
                                label={localizationHandler.get("revoke-role")}
                                onclick={revokeRole}
                            />,
                            <Button
                                key="cancel"
                                id="role-management-revoke-role-cancel-button"
                                label={localizationHandler.get("cancel")}
                                onclick={() => setRoleToRevoke(null)}
                            />
                        ]}
                    />
                }
            </td>
        </tr>
    );
}

export default RoleManagementSearchResultUser;