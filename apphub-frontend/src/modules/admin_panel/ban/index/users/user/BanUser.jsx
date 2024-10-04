import React from "react";
import roleLocalizationData from "../../../../role_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import Stream from "../../../../../../common/js/collection/Stream";
import "./ban_user.css";
import localizationData from "./ban_user_localization.json";
import { ADMIN_PANEL_BAN_DETAILS_PAGE } from "../../../../../../common/js/dao/endpoints/UserEndpoints";

const BanUser = ({ user }) => {
    const roleLocalizationHandler = new LocalizationHandler(roleLocalizationData);
    const localizationHandler = new LocalizationHandler(localizationData);

    const getBannedRoles = () => {
        return new Stream(user.bannedRoles)
            .map(role => roleLocalizationHandler.get(role))
            .sorted((a, b) => a.localeCompare(b))
            .join(", ");
    }

    return (
        <tr className="ban-user" onClick={() => window.open(ADMIN_PANEL_BAN_DETAILS_PAGE.assembleUrl({ userId: user.userId }))}>
            <td className="ban-user-user-id">{user.userId}</td>
            <td className="ban-user-username">{user.username}</td>
            <td className="ban-user-email"> {user.email}</td>
            <td className="ban-user-roles">{getBannedRoles()}</td>
            <td className="ban-user-marked-for-deletion-cell">{localizationHandler.get(user.markedForDeletion)}</td>
        </tr>
    );
}

export default BanUser;