import React, { useEffect, useState } from "react";
import "./modules.css";
import LocalizationHandler from "../../common/js/LocalizationHandler";
import localizationData from "./modules_page_localization.json";
import Header from "../../common/component/Header";
import Favorites from "./modules_page/Favorites";
import Modules from "./modules_page/Modules";
import Footer from "../../common/component/Footer";
import Button from "../../common/component/input/Button";
import sessionChecker from "../../common/js/SessionChecker";
import NotificationService from "../../common/js/notification/NotificationService";
import { ToastContainer } from "react-toastify";
import logout from "../../common/js/LogoutController";
import { MODULES_GET } from "../../common/js/dao/endpoints/ModulesEndpoints";

const ModulesPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [modules, setModules] = useState([]);

    useEffect(() => fetchModules(), []);
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const fetchModules = () => {
        const fetch = async () => {
            const response = await MODULES_GET.createRequest()
                .send();

            setModules(response);
        };
        fetch();
    }

    document.title = localizationHandler.get("title");

    const logoutButton = <Button
        key={"logout-button"}
        id="logout-button"
        label={localizationHandler.get("logout")}
        onclick={() => logout()}
    />

    return (
        <div id="modules" className="main-page">
            <Header label={localizationHandler.get("title")} />

            <main>
                <Favorites
                    pageLocalizationHandler={localizationHandler}
                    modules={modules}
                    updateModules={setModules}
                />
                <Modules
                    pageLocalizationHandler={localizationHandler}
                    modules={modules}
                    updateModules={setModules}
                />
            </main>

            <Footer rightButtons={[logoutButton]} />

            <ToastContainer />
        </div>
    );
}

export default ModulesPage;