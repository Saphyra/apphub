import React from "react";
import "./ban_users.css";
import localizationData from "./ban_users_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import Stream from "../../../../../common/js/collection/Stream";
import BanUser from "./user/BanUser";

const BanUsers = ({ users }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const getUsers = () => {
        return new Stream(users)
            .map(user => <BanUser
                key={user.userId}
                user={user}
            />)
            .toList();
    }

    return (
        <div id="ban-users-wrapper">
            <table id="ban-users" className="formatted-table">
                <thead>
                    <tr>
                        <th>{localizationHandler.get("user-id")}</th>
                        <th>{localizationHandler.get("username")}</th>
                        <th>{localizationHandler.get("email")}</th>
                        <th>{localizationHandler.get("banned-roles")}</th>
                        <th>{localizationHandler.get("marked-for-deletion")}</th>
                    </tr>
                </thead>

                <tbody>
                    {getUsers()}
                </tbody>
            </table>
        </div>
    );
}

export default BanUsers;