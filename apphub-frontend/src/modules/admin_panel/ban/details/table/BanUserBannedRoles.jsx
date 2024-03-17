import React from "react";
import "./ban_user_banned_roles.css";
import localizationData from "./ban_user_banned_roles_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import Stream from "../../../../../common/js/collection/Stream";
import BanUserBannedRole from "./ban/BanUserBannedRole";

const BanUserBannedRoles = ({ userData, setUserData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const getTableBody = () => {
        if (userData.bans.length === 0) {
            return (
                <tr>
                    <td id="ban-user-no-banned-roles" colSpan={5}>{localizationHandler.get("no-bans")}</td>
                </tr>
            )
        }

        return new Stream(userData.bans)
            .map(ban => <BanUserBannedRole
                key={ban.id}
                ban={ban}
                userData={userData}
                setUserData={setUserData}
            />)
            .toList();
    }

    return (
        <table id="ban-user-banned-roles" className="formatted-table">
            <thead>
                <tr>
                    <th>{localizationHandler.get("role")}</th>
                    <th>{localizationHandler.get("expiration")}</th>
                    <th>{localizationHandler.get("reason")}</th>
                    <th>{localizationHandler.get("banned-by")}</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                {getTableBody()}
            </tbody>
        </table>
    )
}

export default BanUserBannedRoles;