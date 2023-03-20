import React, { useEffect, useState } from "react";
import "./modules.css";
import LocalizationHandler from "../../common/js/LocalizationHandler";
import localizationData from "./modules_page_localization.json";
import Header from "../../common/component/Header";
import Endpoints from "../../common/js/dao/dao";
import Favorites from "./modules_page/Favorites";
import Modules from "./modules_page/Modules";
import Footer from "../../common/component/Footer";
import Button from "../../common/component/input/Button";
import logout from "./controller/LogoutController";
import modulesList from "./modules.json"; //TODO delete file

const ModulesPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [modules, setModules] = useState([]);

    useEffect(() => fetchModules(), []);

    const fetchModules = () => {
        const fetch = async () => {
            const response = await Endpoints.MODULES_GET.createRequest()
                .send();

            setModules(response);
        };
        //setModules(modulesList); //TODO use fetch
        fetch();
    }

    document.title = localizationHandler.get("title");

    const logoutButton = <Button
        key={"logout-button"}
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
        </div>
    );
}

export default ModulesPage;