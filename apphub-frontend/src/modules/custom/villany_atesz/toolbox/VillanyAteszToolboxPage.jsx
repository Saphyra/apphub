import React, { useEffect, useState } from "react";
import localizationData from "./villany_atesz_toolbox_page_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import ToolboxTab from "./ToolboxTab";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Button from "../../../../common/component/input/Button";
import Header from "../../../../common/component/Header";
import VillanyAteszPage from "../navigation/VillanyAteszPage";
import Constants from "../../../../common/js/Constants";
import VillanyAteszNavigation from "../navigation/VillanyAteszNavigation";
import Footer from "../../../../common/component/Footer";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";
import { ToastContainer } from "react-toastify";
import "./villany_atesz_toolbox_page.css";
import VillanyAteszToolboxNew from "./new/VillanyAteszToolboxNew";
import VillanyAteszToolboxOverview from "./overview/VillanyAteszToolboxOverview";
import VillanyAteszToolboxScrapped from "./scrapped/VillanyAteszToolboxScrapped";

const VillanyAteszToolboxPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [openedTab, setOpenedTab] = useState(ToolboxTab.OVERVIEW);
    const [confirmationDialogData, setConfirmationDialogData] = useState(null);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const getNavButtons = () => {
        return [
            <Button
                key="overview"
                id="villany-atesz-toolbox-navigation-overview-button"
                className={openedTab === ToolboxTab.OVERVIEW ? "opened" : ""}
                onclick={() => setOpenedTab(ToolboxTab.OVERVIEW)}
                label={localizationHandler.get("overview")}
            />,
            <Button
                key="new-tool"
                id="villany-atesz-toolbox-navigation-new-tool-button"
                className={openedTab === ToolboxTab.NEW ? "opened" : ""}
                onclick={() => setOpenedTab(ToolboxTab.NEW)}
                label={localizationHandler.get("new-tool")}
            />,
            <Button
                key="scrapped"
                id="villany-atesz-toolbox-navigation-scrapped-button"
                className={openedTab === ToolboxTab.SCRAPPED ? "opened" : ""}
                onclick={() => setOpenedTab(ToolboxTab.SCRAPPED)}
                label={localizationHandler.get("scrapped")}
            />,
        ];
    }

    const getContent = () => {
        switch (openedTab) {
            case ToolboxTab.NEW:
                return <VillanyAteszToolboxNew />
            case ToolboxTab.OVERVIEW:
                return <VillanyAteszToolboxOverview
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            case ToolboxTab.SCRAPPED:
                return <VillanyAteszToolboxScrapped
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            default:
                return "Unhandled StockTab: " + openedTab;
        }
    }

    return (
        <div id="villany-atesz-toolbox" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <VillanyAteszNavigation
                    page={VillanyAteszPage.TOOLBOX}
                    customs={getNavButtons()}
                />

                <div id="villany-atesz-toolbox-content">
                    {getContent()}
                </div>
            </main>

            <Footer
                rightButtons={
                    <Button
                        id="villany-atesz-home-button"
                        onclick={() => window.location.href = Constants.MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                }
            />

            {confirmationDialogData &&
                <ConfirmationDialog
                    id={confirmationDialogData.id}
                    title={confirmationDialogData.title}
                    content={confirmationDialogData.content}
                    choices={confirmationDialogData.choices}
                />
            }

            <ToastContainer />
        </div>
    );
}

export default VillanyAteszToolboxPage;