import React, { useEffect, useState } from "react";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import { ToastContainer } from "react-toastify";
import Constants from "../../../common/js/Constants";
import localizationData from "./disabled_role_management_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import roleLocalizationData from "../role_localization.json";
import "./disabled_role_management.css";
import useLoader from "../../../common/hook/Loader";
import Stream from "../../../common/js/collection/Stream";
import DisabledRoleManagementRole from "./DisabledRoleManagementRole";
import { USER_DATA_GET_DISABLED_ROLES } from "../../../common/js/dao/endpoints/UserEndpoints";

const DisabledRoleManagement = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const roleLocalizationHandler = new LocalizationHandler(roleLocalizationData);
    document.title = localizationHandler.get("title");

    const [roles, setRoles] = useState([]);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    useLoader({ request: USER_DATA_GET_DISABLED_ROLES.createRequest(), mapper: setRoles })

    const getContent = () => {
        return new Stream(roles)
            .sorted((a, b) => roleLocalizationHandler.get(a.role).localeCompare(roleLocalizationHandler.get(b.role)))
            .map(role => <DisabledRoleManagementRole
                key={role.role}
                localizationHandler={localizationHandler}
                role={role.role}
                enabled={!role.disabled}
                setRoles={setRoles}
            />)
            .toList();
    }

    return (
        <div id="disabled-role-management" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <table id="disabled-role-management-table" className="formatted-table">
                    <thead>
                        <tr>
                            <th>{localizationHandler.get("role")}</th>
                            <th>{localizationHandler.get("enabled")}</th>
                        </tr>
                    </thead>

                    <tbody>
                        {getContent()}
                    </tbody>
                </table>
            </main>

            <Footer
                leftButtons={
                    <Button
                        id="disabled-role-management-home-button"
                        onclick={() => window.location.href = Constants.MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                }
            />

            <ToastContainer />
        </div>
    );
}

export default DisabledRoleManagement;