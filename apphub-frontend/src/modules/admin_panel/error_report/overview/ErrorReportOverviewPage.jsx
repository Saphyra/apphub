import React, { useEffect, useState } from "react";
import "./error_report.css";
import localizationData from "./error_report_localization.json";
import ErrorReportFilter from "./filter/ErrorReportFilter";
import ErrorReportPageSelector from "./page/ErrorReportPageSelector";
import ErrorReportStatus from "../ErrorReportStatus";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import ConfirmationDialogData from "../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Button from "../../../../common/component/input/Button";
import Header from "../../../../common/component/Header";
import ErrorReportList from "./list/ErrorReportList";
import Footer from "../../../../common/component/Footer";
import Constants from "../../../../common/js/Constants";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";
import { ToastContainer } from "react-toastify";
import useHasFocus from "../../../../common/hook/UseHasFocus";
import { useUpdateEffect } from "react-use";
import { hasValue, nullIfEmpty } from "../../../../common/js/Utils";
import { ADMIN_PANEL_DELETE_ERROR_REPORTS, ADMIN_PANEL_ERROR_REPORT_DELETE_ALL, ADMIN_PANEL_ERROR_REPORT_DELETE_READ, ADMIN_PANEL_GET_ERROR_REPORTS, ADMIN_PANEL_MARK_ERROR_REPORTS } from "../../../../common/js/dao/endpoints/AdminPanelEndpoints";

const ErrorReportOverviewPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [selectedErrorReports, setSelectedErrorReports] = useState([]);
    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [errorReports, setErrorReports] = useState([]);
    const [totalCount, setTotalCount] = useState(0);
    const [filterData, setFilterData] = useState({
        service: "",
        message: "",
        statusCode: "",
        startTime: "",
        endTime: "",
        pageSize: 25,
        status: "",
        page: 1
    });

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => load(), [filterData.page]);

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus) {
            load();
        }
    }, [isInFocus]);

    const load = () => {
        const fetch = async () => {
            const payload = {
                message: nullIfEmpty(filterData.message),
                statusCode: nullIfEmpty(filterData.statusCode),
                startTime: nullIfEmpty(filterData.startTime),
                endTime: nullIfEmpty(filterData.endTime),
                pageSize: filterData.pageSize,
                page: filterData.page,
                status: nullIfEmpty(filterData.status),
                service: nullIfEmpty(filterData.service),
            }

            const response = await ADMIN_PANEL_GET_ERROR_REPORTS.createRequest(payload)
                .send();
            setErrorReports(response.reports);
            setTotalCount(response.totalCount);
        }
        fetch();
    }

    const openDeleteAllConfirmation = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "error-report-delete-all-confirmation",
            localizationHandler.get("delete-all"),
            localizationHandler.get("delete-all-confirmation-content"),
            [
                <Button
                    key="delete"
                    id="error-report-confirm-delete-all"
                    label={localizationHandler.get("delete-all")}
                    onclick={deleteAll}
                />,
                <Button
                    key="cancel"
                    id="error-report-cancel-delete-all"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const deleteAll = async () => {
        await ADMIN_PANEL_ERROR_REPORT_DELETE_ALL.createRequest()
            .send();

        load();
        setConfirmationDialogData(null);
    }

    const openDeleteReadConfirmation = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "error-report-delete-read-confirmation",
            localizationHandler.get("delete-read"),
            localizationHandler.get("delete-read-confirmation-content"),
            [
                <Button
                    key="delete"
                    id="error-report-confirm-delete-read"
                    label={localizationHandler.get("delete-read")}
                    onclick={deleteRead}
                />,
                <Button
                    key="cancel"
                    id="error-report-cancel-delete-read"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const deleteRead = async () => {
        await ADMIN_PANEL_ERROR_REPORT_DELETE_READ.createRequest()
            .send();

        load();
        setConfirmationDialogData(null);
    }

    const openDeleteCheckedConfirmation = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "error-report-delete-checked-confirmation",
            localizationHandler.get("delete-checked"),
            localizationHandler.get("delete-checked-confirmation-content"),
            [
                <Button
                    key="delete"
                    id="error-report-confirm-delete-checked"
                    label={localizationHandler.get("delete-checked")}
                    onclick={deleteChecked}
                />,
                <Button
                    key="cancel"
                    id="error-report-cancel-delete-checked"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const deleteChecked = async () => {
        await ADMIN_PANEL_DELETE_ERROR_REPORTS.createRequest(selectedErrorReports)
            .send();

        load();
        setConfirmationDialogData(null);
    }

    const markCheckedAs = async (status) => {
        await ADMIN_PANEL_MARK_ERROR_REPORTS.createRequest(selectedErrorReports, { status: status })
            .send();

        load();
    }

    const getCenterButtons = () => {
        if (selectedErrorReports.length === 0) {
            return [];
        }

        return [
            <Button
                key="delete-checked"
                label={localizationHandler.get("delete-checked")}
                onclick={openDeleteCheckedConfirmation}
            />,
            <Button
                key="mark-checked-as-read"
                label={localizationHandler.get("mark-checked-as-read")}
                onclick={() => markCheckedAs(ErrorReportStatus.READ)}
            />,
            <Button
                key="mark-checked-as-unread"
                label={localizationHandler.get("mark-checked-as-unread")}
                onclick={() => markCheckedAs(ErrorReportStatus.UNREAD)}
            />,
            <Button
                key="mark-checked"
                label={localizationHandler.get("mark-checked")}
                onclick={() => markCheckedAs(ErrorReportStatus.MARKED)}
            />
        ]
    }

    return (
        <div id="error-report-overview" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <ErrorReportFilter
                    filterData={filterData}
                    setFilterData={setFilterData}
                    searchCallback={load}
                />

                <ErrorReportPageSelector
                    filterData={filterData}
                    setFilterData={setFilterData}
                    refreshCallback={load}
                />

                <ErrorReportList
                    totalCount={totalCount}
                    errorReports={errorReports}
                    refreshCallback={load}
                    selectedErrorReports={selectedErrorReports}
                    setSelectedErrorReports={setSelectedErrorReports}
                    setConfirmationDialogData={setConfirmationDialogData}
                />

            </main>

            <Footer
                leftButtons={[
                    <Button
                        key="home"
                        id="error-report-home"
                        onclick={() => window.location.href = Constants.MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                ]}

                centerButtons={getCenterButtons()}

                rightButtons={[
                    <Button
                        key="delete-all"
                        id="error-report-delete-all"
                        onclick={openDeleteAllConfirmation}
                        label={localizationHandler.get("delete-all")}
                    />,
                    <Button
                        key="delete-read"
                        id="error-report-delete-read"
                        onclick={openDeleteReadConfirmation}
                        label={localizationHandler.get("delete-read")}
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

export default ErrorReportOverviewPage;