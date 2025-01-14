import React, { useEffect, useState } from "react";
import localizationData from "./error_report_details_localization.json";
import { useParams } from "react-router";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Header from "../../../../common/component/Header";
import Footer from "../../../../common/component/Footer";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";
import { ToastContainer } from "react-toastify";
import useLoader from "../../../../common/hook/Loader";
import "./error_report_details.css";
import StackTraceException from "./stack_trace/StackTraceException";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import ConfirmationDialogData from "../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import ErrorReportStatus from "../ErrorReportStatus";
import { hasValue } from "../../../../common/js/Utils";
import { ADMIN_PANEL_DELETE_ERROR_REPORTS, ADMIN_PANEL_GET_ERROR_REPORT, ADMIN_PANEL_MARK_ERROR_REPORTS } from "../../../../common/js/dao/endpoints/AdminPanelEndpoints";

const ErrorReportDetailsPage = () => {
    const { errorReportId } = useParams();
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [errorReport, setErrorReport] = useState({});

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useLoader(ADMIN_PANEL_GET_ERROR_REPORT.createRequest(null, { id: errorReportId }), setErrorReport);

    const openDeleteConfirmation = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "error-report-details-delete-confirmation",
            localizationHandler.get("delete"),
            localizationHandler.get("delete-confirmation-content"),
            [
                <Button
                    key="close"
                    id="error-report-details-delete-confirmation-confirm"
                    label={localizationHandler.get("delete")}
                    onclick={() => deleteErrorReport()}
                />,
                <Button
                    key="cancel"
                    id="error-report-details-delete-confirmation-cancel"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const deleteErrorReport = async () => {
        await ADMIN_PANEL_DELETE_ERROR_REPORTS.createRequest([errorReportId])
            .send();

        window.close();
    }

    const mark = async (status) => {
        await ADMIN_PANEL_MARK_ERROR_REPORTS.createRequest([errorReportId], { status: status })
            .send();

        NotificationService.showSuccess(localizationHandler.get("error-report-updated"))
    }

    return (
        <div id="error-report-details" className="main-page">
            <Header label={errorReportId} />

            <main>
                <fieldset className="selectable">
                    <legend>{localizationHandler.get("created-at")}</legend>
                    <div className="error-report-details-value">{errorReport.createdAt}</div>
                </fieldset>

                <fieldset className="selectable">
                    <legend>{localizationHandler.get("service")}</legend>
                    <div className="error-report-details-value">{errorReport.service}</div>
                </fieldset>

                <fieldset className="selectable">
                    <legend>{localizationHandler.get("message")}</legend>
                    <div className="error-report-details-value">{errorReport.message}</div>
                </fieldset>

                <fieldset className="selectable">
                    <legend>{localizationHandler.get("response")}</legend>
                    <div className="error-report-details-value">{errorReport.responseBody}</div>
                </fieldset>

                {hasValue(errorReport.exception) &&
                    <div>
                        <fieldset className="selectable">
                            <legend>{localizationHandler.get("thread")}</legend>
                            <div className="error-report-details-value">{errorReport.exception.thread}</div>
                        </fieldset>

                        <fieldset className="selectable">
                            <legend>{localizationHandler.get("stack-trace")}</legend>
                            <StackTraceException
                                exception={errorReport.exception}
                            />
                        </fieldset>
                    </div>
                }
            </main>

            <Footer
                leftButtons={[
                    <Button
                        key="close"
                        id="error-report-details-close"
                        label={localizationHandler.get("close")}
                        onclick={() => window.close()}
                    />
                ]}
                centerButtons={[
                    <Button
                        key="delete"
                        id="error-report-details-confirm-delete"
                        label={localizationHandler.get("delete")}
                        onclick={openDeleteConfirmation}
                    />,
                    <Button
                        key="mark-as-unread"
                        id="error-report-details-mark-as-unread"
                        label={localizationHandler.get("mark-as-unread")}
                        onclick={() => mark(ErrorReportStatus.UNREAD)}
                    />,
                    <Button
                        key="mark"
                        id="error-report-details-mark"
                        label={localizationHandler.get("mark")}
                        onclick={() => mark(ErrorReportStatus.MARKED)}
                    />,
                    <Button
                        key="unmark"
                        id="error-report-details-unmark"
                        label={localizationHandler.get("unmark")}
                        onclick={() => mark(ErrorReportStatus.READ)}
                    />
                ]}
                rightButtons={[
                    <Button
                        key="home"
                        id="error-report-details-home"
                        label={localizationHandler.get("home")}
                        onclick={() => window.location.href = Constants.MODULES_PAGE}
                    />,
                    <Button
                        key="back"
                        id="error-report-details-back"
                        label={localizationHandler.get("back")}
                        onclick={() => window.location.href = Constants.ERROR_REPORT_PAGE}
                    />

                ]}
            />

            {hasValue(confirmationDialogData) &&
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

export default ErrorReportDetailsPage;