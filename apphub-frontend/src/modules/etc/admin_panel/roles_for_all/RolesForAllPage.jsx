import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./roles_for_all_localization.json";
import "./roles_for_all.css";
import roleLocalizationData from "modules/etc/admin_panel/role_localization.json";
import { useEffect, useState } from "react";
import sessionChecker from "common/js/SessionChecker";
import NotificationService from "common/js/notification/NotificationService";
import useLoader from "common/hook/Loader";
import Stream from "common/js/collection/Stream";
import roles from "modules/etc/admin_panel/roles.json";
import RolesForAllRow from "./RolesForAllRow";
import Header from "common/component/Header";
import Footer from "common/component/Footer";
import Button from "common/component/input/Button";
import { ToastContainer } from "react-toastify";
import Constants from "common/js/Constants";
import { USER_DATA_ROLES_FOR_ALL_RESTRICTED } from "../AdminPanelEndpoints";

const RolesForAllPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const roleLocalizationHandler = new LocalizationHandler(roleLocalizationData);
    document.title = localizationHandler.get("title");

    const [restrictedRoles, setRestrictedRoles] = useState([]);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    useLoader({ request: USER_DATA_ROLES_FOR_ALL_RESTRICTED.createRequest(), mapper: setRestrictedRoles });

    const getRoles = () => {
        return new Stream(roles)
            .filter(role => restrictedRoles.indexOf(role) < 0)
            .sorted((a, b) => roleLocalizationHandler.get(a).localeCompare(roleLocalizationHandler.get(b)))
            .map(role => <RolesForAllRow
                key={role}
                localizationHandler={localizationHandler}
                role={role}
            />)
            .toList();
    }

    return (
        <div id="roles-for-all" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <table id="roles-for-all-table" className="formatted-table">
                    <tbody>
                        {getRoles()}
                    </tbody>
                </table>
            </main>

            <Footer
                leftButtons={
                    <Button
                        id="roles-for-all-home-button"
                        onclick={() => window.location.href = Constants.MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                }
            />

            <ToastContainer />
        </div>
    );
}

export default RolesForAllPage;