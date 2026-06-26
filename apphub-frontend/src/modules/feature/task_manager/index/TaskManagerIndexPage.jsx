import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./task_manager_page_localization.json";
import { useEffect, useState } from "react";
import NotificationService from "common/js/notification/NotificationService";
import Header from "common/component/Header";
import Footer from "common/component/Footer";
import Button from "common/component/input/Button";
import { MODULES_PAGE } from "modules/etc/modules/ModulesEndpoints";
import ConfirmationDialog from "common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "common/component/Spinner";
import { ToastContainer } from "react-toastify";
import "./task_manager_index_page.css";
import TaskManagerIndexOrganizations from "./organizations/TaskManagerIndexOrganizations";
import TaskManagerIndexInvitations from "./invitations/TaskManagerIndexInvitations";
import useRefresh from "common/hook/Refresh";

const TaskManagerIndexPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(0);
    const [refreshCounter, refresh] = useRefresh();

    useEffect(() => NotificationService.displayStoredMessages(), []);

    const updateDisplaySpinner = (display) => {
        setDisplaySpinner(prev => prev + (display ? 1 : -1));
    }

    return (
        <div id="task-manager-index" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main id="task-manager-index-main">
                <TaskManagerIndexOrganizations
                    setDisplaySpinner={updateDisplaySpinner}
                    localizationHandler={localizationHandler}
                    refreshCounter={refreshCounter}
                />


                <TaskManagerIndexInvitations
                    setDisplaySpinner={updateDisplaySpinner}
                    localizationHandler={localizationHandler}
                    setConfirmationDialogData={setConfirmationDialogData}
                    refreshCounter={refreshCounter}
                    refresh={refresh}
                />
            </main>

            <Footer
                rightButtons={
                    <Button
                        id="task-manager-home-button"
                        onclick={() => window.location.href = MODULES_PAGE}
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

            {displaySpinner > 0 && <Spinner />}

            <ToastContainer />
        </div>
    );
}

export default TaskManagerIndexPage;