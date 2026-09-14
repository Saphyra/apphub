import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./modules_page_localization.json";
import "./modules.css";
import { useEffect, useState } from "react";
import NotificationService from "common/js/notification/NotificationService";
import Button from "common/component/input/Button";
import Header from "common/component/Header";
import logout from "common/js/LogoutController";
import Favorites from "./modules_page/Favorites";
import Modules from "./modules_page/Modules";
import Footer from "common/component/Footer";
import { ToastContainer } from "react-toastify";
import { MODULES_GET } from "./ModulesEndpoints";
import Spinner from "common/component/Spinner";

const ModulesPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [displaySpinner, setDisplaySpinner] = useState(false);

    const [modules, setModules] = useState([]);

    useEffect(() => fetchModules(), []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const fetchModules = () => {
        const fetch = async () => {
            const response = await MODULES_GET.createRequest()
                .send(setDisplaySpinner);

            setModules(response);
        };
        fetch();
    }

    document.title = localizationHandler.get("title");

    const logoutButton = <Button
        key={"logout-button"}
        id="logout-button"
        label={localizationHandler.get("logout")}
        onclick={() => logout(setDisplaySpinner)}
    />

    return (
        <div id="modules" className="main-page">
            <Header label={localizationHandler.get("title")} />

            <main>
                <Favorites
                    pageLocalizationHandler={localizationHandler}
                    modules={modules}
                    updateModules={setModules}
                    setDisplaySpinner={setDisplaySpinner}
                />
                <Modules
                    pageLocalizationHandler={localizationHandler}
                    modules={modules}
                    updateModules={setModules}
                    setDisplaySpinner={setDisplaySpinner}
                />
            </main>

            <Footer rightButtons={[logoutButton]} />

            <ToastContainer />

            {displaySpinner && <Spinner />}
        </div>
    );
}

export default ModulesPage;