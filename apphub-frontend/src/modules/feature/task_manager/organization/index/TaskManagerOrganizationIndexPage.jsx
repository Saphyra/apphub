import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./task_manager_organization_index_page_localization.json";
import { useEffect, useState } from "react";
import NotificationService from "common/js/notification/NotificationService";
import Header from "common/component/Header";
import Footer from "common/component/Footer";
import Button from "common/component/input/Button";
import { TASK_MANAGER_GET_ORGANIZATION, TASK_MANAGER_PAGE } from "../../TaskManagerEndpoints";
import ConfirmationDialog from "common/component/confirmation_dialog/ConfirmationDialog";
import Spinner from "common/component/Spinner";
import { ToastContainer } from "react-toastify";
import useLoader from "common/hook/Loader";
import { useParams } from "react-router";
import ResponseStatus from "common/js/dao/ResponseStatus";
import { hasValue } from "common/js/Utils";
import ErrorHandler from "common/js/dao/ErrorHandler";
import TaskManagerOrganizationNotifications from "./notifications/TaskManagerOrganizationNotifications";
import "./task_manager_organization_index.css";

const TaskManagerOrganizationIndexPage = () => {
    const { organizationId } = useParams();

    const localizationHandler = new LocalizationHandler(localizationData);

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [displaySpinner, setDisplaySpinner] = useState(0);

    useEffect(() => NotificationService.displayStoredMessages(), []);

    const updateDisplaySpinner = (display) => {
        setDisplaySpinner(prev => prev + (display ? 1 : -1));
    }

    const [organization] = useLoader({
        request: TASK_MANAGER_GET_ORGANIZATION.createRequest(null, { organizationId: organizationId }),
        listener: [organizationId],
        setDisplaySpinner: updateDisplaySpinner,
        errorHandler: new ErrorHandler(
            (error) => error.statusKey == ResponseStatus.NOT_FOUND || error.statusKey == ResponseStatus.FORBIDDEN,
            () => window.location.href = TASK_MANAGER_PAGE
        )
    });

    if (hasValue(organization)) {
        document.title = localizationHandler.get("title", { name: organization.organizationName });
    }

    return (
        <div id="task-manager-organization-index" className="main-page">
            {hasValue(organization) &&
                <Header label={organization.organizationName} />
            }


            <main id="task-manager-organization-index-main">
                <TaskManagerOrganizationNotifications
                    setDisplaySpinner={updateDisplaySpinner}
                    organizationId={organizationId}
                />
            </main>

            <Footer
                rightButtons={
                    <Button
                        id="task-manager-organization-index-back-button"
                        onclick={() => window.location.href = TASK_MANAGER_PAGE}
                        label={localizationHandler.get("back")}
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

export default TaskManagerOrganizationIndexPage;