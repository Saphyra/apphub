import React, { useEffect, useState } from "react";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import localizationData from "./main_menu_resources/page_localization.json";
import Redirection from "../Redirection";
import "../skyxplore.css";
import Header from "../../../common/component/Header";
import "./main_menu_resources/main_menu_page.css";
import { ToastContainer } from "react-toastify";
import Contacts from "./main_menu_page/Contacts";
import NewGameConfirmationDialog from "./main_menu_page/NewGameConfirmationDialog";
import MainMenuButtons from "./main_menu_page/MainMenuButtons";
import Invitations from "./main_menu_page/Invitations";

const SkyXploreMainMenuPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [displaynNewGameConfirmationDialog, setDisplaynNewGameConfirmationDialog] = useState(false);

    useEffect(() => Redirection.forMainMenu(), []);
    useEffect(() => sessionChecker(), []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => { delete sessionStorage.skyXplorePageHistory }, [])

    return (
        <div>
            <div className="skyxplore-background" />

            <div id="skyxplore-main-menu" className="main-page skyxplore-main">
                <Header label={localizationHandler.get("page-title")} />

                <main className="footerless">
                    <MainMenuButtons
                        localizationHandler={localizationHandler}
                        setDisplaynNewGameConfirmationDialog={setDisplaynNewGameConfirmationDialog}
                    />

                    <Contacts
                        localizationHandler={localizationHandler}
                    />

                    <Invitations
                        localizationHandler={localizationHandler}
                    />
                </main>
            </div>

            {displaynNewGameConfirmationDialog && <NewGameConfirmationDialog
                localizationHandler={localizationHandler}
                setDisplaynNewGameConfirmationDialog={setDisplaynNewGameConfirmationDialog}
            />}

            <ToastContainer />
        </div>
    );
}

export default SkyXploreMainMenuPage;