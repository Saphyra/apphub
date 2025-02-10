import React, { useState } from "react";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import localizationData from "./elite_base_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import Constants from "../../../common/js/Constants";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "../../../common/component/Spinner";
import { ToastContainer } from "react-toastify";
import EliteBasePage from "./EliteBasePage";
import EliteBaseNavigation from "./navigation/EliteBaseNavigation";
import "./elite_base.css";
import EliteBasePages from "./pages/EliteBasePages";

const EliteBase = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(false);
    const [openedPage, setOpenedPage] = useState(sessionStorage.eliteBaseOpenedPage ? sessionStorage.eliteBaseOpenedPage : EliteBasePage.INDEX);

    const updateOpenedPage = (newValue) => {
        sessionStorage.eliteBaseOpenedPage = newValue;
        setOpenedPage(newValue);
    }

    return (
        <div id="elite-base" className="main-page">
            <main id="elite-base-main" className="headless">
                <EliteBaseNavigation
                    openedPage={openedPage}
                    setOpenedPage={updateOpenedPage}
                />

                <EliteBasePages
                    openedPage={openedPage}
                />
            </main>

            <Footer
                rightButtons={
                    <Button
                        id="notebook-home-button"
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

            {displaySpinner && <Spinner />}

            <ToastContainer />
        </div>
    );
}

export default EliteBase;