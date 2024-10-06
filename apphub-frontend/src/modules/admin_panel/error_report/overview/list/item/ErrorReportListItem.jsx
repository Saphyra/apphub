import React from "react";
import "./error_report_list_item.css";
import localizationData from "./error_report_list_item_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import ConfirmationDialogData from "../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../../../common/component/input/Button";
import InputField from "../../../../../../common/component/input/InputField";
import ErrorReportStatus from "../../../ErrorReportStatus";
import { addAndSet, removeAndSet } from "../../../../../../common/js/Utils";
import { ADMIN_PANEL_DELETE_ERROR_REPORTS, ADMIN_PANEL_ERROR_REPORT_DETAILS_PAGE, ADMIN_PANEL_MARK_ERROR_REPORTS } from "../../../../../../common/js/dao/endpoints/AdminPanelEndpoints";

const ErrorReportListItem = ({ errorReport, selectedErrorReports, setSelectedErrorReports, refreshCallback, setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const setCheckedStatus = (checked) => {
        if (checked) {
            addAndSet(selectedErrorReports, errorReport.id, setSelectedErrorReports);
        } else {
            removeAndSet(selectedErrorReports, id => id === errorReport.id, setSelectedErrorReports);
        }
    }

    const openDeleteConfirmation = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "error-report-delete-confirmation",
            localizationHandler.get("delete"),
            localizationHandler.get("delete-confirmation-content"),
            [
                <Button
                    key="delete"
                    id="error-report-confirm-delete"
                    label={localizationHandler.get("delete")}
                    onclick={deleteErrorReport}
                />,
                <Button
                    key="cancel"
                    id="error-report-cancel-delete"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const deleteErrorReport = async () => {
        await ADMIN_PANEL_DELETE_ERROR_REPORTS.createRequest([errorReport.id])
            .send();

        refreshCallback();
        setConfirmationDialogData(null);
    }

    const mark = async (status) => {
        await ADMIN_PANEL_MARK_ERROR_REPORTS.createRequest([errorReport.id], { status: status })
            .send();

        refreshCallback();
    }

    return (
        <tr className={"error-report-list-item " + errorReport.status.toLowerCase()}>
            <td className="error-report-list-item-checked-cell">
                <InputField
                    className={"error-report-list-item-checked"}
                    type="checkbox"
                    checked={selectedErrorReports.indexOf(errorReport.id) > -1}
                    onchangeCallback={setCheckedStatus}
                />
            </td>
            <td className="error-report-list-item-created-at">{errorReport.createdAt}</td>
            <td className="error-report-list-item-status-code">{errorReport.statusCode}</td>
            <td className="error-report-list-item-service">{errorReport.service}</td>
            <td className="error-report-list-item-message">{errorReport.message}</td>
            <td className="error-report-list-item-operations">
                <Button
                    className="error-report-list-item-delete"
                    label={localizationHandler.get("delete")}
                    onclick={openDeleteConfirmation}
                />

                {errorReport.status !== ErrorReportStatus.UNREAD &&
                    <Button
                        className="error-report-list-item-mark-as-unread"
                        label={localizationHandler.get("mark-as-unread")}
                        onclick={() => mark(ErrorReportStatus.UNREAD)}
                    />
                }

                {errorReport.status !== ErrorReportStatus.READ &&
                    <Button
                        className="error-report-list-item-mark-as-read"
                        label={localizationHandler.get("mark-as-read")}
                        onclick={() => mark(ErrorReportStatus.READ)}
                    />
                }

                {errorReport.status !== ErrorReportStatus.MARKED &&
                    <Button
                        className="error-report-list-item-mark-as-marked"
                        label={localizationHandler.get("mark")}
                        onclick={() => mark(ErrorReportStatus.MARKED)}
                    />
                }

                <Button
                    className="error-report-list-item-open"
                    label={localizationHandler.get("open")}
                    onclick={() => {
                        window.open(ADMIN_PANEL_ERROR_REPORT_DETAILS_PAGE.assembleUrl({id: errorReport.id}));
                        setTimeout(() => refreshCallback(), 1000);
                    }}
                />
            </td>
        </tr>
    );
}

export default ErrorReportListItem;