import React from "react";
import "./ban_user_info.css";
import localizationData from "./ban_user_info_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";

const BanUserInfo = ({ userData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <div id="ban-user-info">
            <fieldset>
                <legend>{localizationHandler.get("user")}</legend>

                <div className="ban-user-info-row">
                    <span>{localizationHandler.get("user-id")}</span>
                    <span>: </span>
                    <span id="ban-user-info-user-id">{userData.userId}</span>
                </div>

                <div className="ban-user-info-row">
                    <span>{localizationHandler.get("username")}</span>
                    <span>: </span>
                    <span id="ban-user-info-username">{userData.username}</span>
                </div>

                <div className="ban-user-info-row">
                    <span>{localizationHandler.get("email")}</span>
                    <span>: </span>
                    <span id="ban-user-info-email">{userData.email}</span>
                </div>
            </fieldset>
        </div>
    );
}

export default BanUserInfo;