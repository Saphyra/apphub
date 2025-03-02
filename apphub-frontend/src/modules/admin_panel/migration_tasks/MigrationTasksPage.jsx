import React, { useEffect, useState } from "react";
import localizationData from "./migration_tasks_page_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import Constants from "../../../common/js/Constants";
import Stream from "../../../common/js/collection/Stream";
import MigrationTask from "./MigrationTask";
import "./migration_tasks.css";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import { ADMIN_PANEL_MIGRATION_DELETE_TASK, ADMIN_PANEL_MIGRATION_GET_TASKS, ADMIN_PANEL_MIGRATION_TRIGGER_TASK } from "../../../common/js/dao/endpoints/AdminPanelEndpoints";
import { ToastContainer } from "react-toastify";

const MigrationTasksPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [migrationTasks, setMigrationTasks] = useState([]);
    const [confirmationDialogData, setConfirmationDialogData] = useState(null);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => loadMigrationTasks(), []);

    const loadMigrationTasks = () => {
        const fetch = async () => {
            const response = await ADMIN_PANEL_MIGRATION_GET_TASKS.createRequest()
                .send();

            setMigrationTasks(response);
        }
        fetch();
    }

    const getContent = () => {
        return new Stream(migrationTasks)
            .map(migrationTask => <MigrationTask
                key={migrationTask.event}
                localizationHandler={localizationHandler}
                data={migrationTask}
                deleteCallback={() => confirmDeleteTask(migrationTask)}
                triggerCallback={() => confirmTriggerTask(migrationTask)}
            />)
            .toList();
    }

    const confirmDeleteTask = (migrationTask) => {
        const data = {
            id: "migration-tasks-confirm-deleteion",
            title: localizationHandler.get("confirm-deletion-title"),
            content: localizationHandler.get("confirm-deletion-detail", { event: migrationTask.event, name: migrationTask.name }),
            choices: [
                <Button
                    key="delete"
                    id="migration-tasks-confirm-deletion-button"
                    label={localizationHandler.get("delete")}
                    onclick={() => deleteTask(migrationTask.event)}
                />,
                <Button
                    key="cancel"
                    id="migration-tasks-cancel-deletion-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        }

        setConfirmationDialogData(data);
    }

    const deleteTask = async (event) => {
        const response = await ADMIN_PANEL_MIGRATION_DELETE_TASK.createRequest(null, { event: event })
            .send();

        setMigrationTasks(response);
        setConfirmationDialogData(null)
    }

    const confirmTriggerTask = (migrationTask) => {
        const data = {
            id: "migration-tasks-confirm-trigger",
            title: localizationHandler.get("confirm-trigger-title"),
            content: localizationHandler.get("confirm-trigger-detail", { event: migrationTask.event, name: migrationTask.name }),
            choices: [
                <Button
                    key="trigger"
                    id="migration-tasks-confirm-trigger-button"
                    label={localizationHandler.get("trigger")}
                    onclick={() => triggerTask(migrationTask.event)}
                />,
                <Button
                    key="cancel"
                    id="migration-tasks-cancel-trigger-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        }

        setConfirmationDialogData(data);
    }

    const triggerTask = async (event) => {
        const response = await ADMIN_PANEL_MIGRATION_TRIGGER_TASK.createRequest(null, { event: event })
            .send();

        setMigrationTasks(response);
        setConfirmationDialogData(null)
        NotificationService.showSuccess(localizationHandler.get("migration-triggered"))
    }

    return (
        <div id="migration-tasks" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <table id="migration-tasks-table" className="formatted-table">
                    <thead>
                        <tr>
                            <th colSpan={4}>{localizationHandler.get("migration-tasks")}</th>
                        </tr>
                        <tr>
                            <th>{localizationHandler.get("event")}</th>
                            <th>{localizationHandler.get("name")}</th>
                            <th>{localizationHandler.get("completed")}</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        {getContent()}
                    </tbody>
                </table>

            </main>

            <Footer
                leftButtons={
                    <Button
                        id="memory-monitoring-home-button"
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

export default MigrationTasksPage;