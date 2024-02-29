import React, { useEffect, useState } from "react";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import localizationData from "./role_management_localization.json";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import Constants from "../../../common/js/Constants";
import { ToastContainer } from "react-toastify";
import "./role_management.css";
import RoleManagementSearch from "./RoleManagementSearch";
import RoleManagementSearchResult from "./search_result/RoleManagementSearchResult";

const RoleManagementPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [query, setQuery] = useState("");
    const [users, setUsers] = useState([]);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    return (
        <div id="role-management" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <RoleManagementSearch
                    localizationHandler={localizationHandler}
                    query={query}
                    setQuery={setQuery}
                    setUsers={setUsers}
                />

                <RoleManagementSearchResult
                    localizationHandler={localizationHandler}
                    users={users}
                    setUsers={setUsers}
                    query={query}
                />
            </main>

            <Footer
                leftButtons={
                    <Button
                        id="role-management-home-button"
                        onclick={() => window.location.href = Constants.MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                }
            />

            <ToastContainer />
        </div>
    );
}

export default RoleManagementPage;