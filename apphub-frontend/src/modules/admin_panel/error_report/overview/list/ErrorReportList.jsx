import "./error_report_list.css";
import ErrorReportListItem from "./item/ErrorReportListItem";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import localizationData from "./error_report_list_localization.json";
import InputField from "../../../../../common/component/input/InputField";
import Stream from "../../../../../common/js/collection/Stream";

const ErrorReportList = ({ totalCount, errorReports, refreshCallback, selectedErrorReports, setSelectedErrorReports, setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const selectAll = () => {
        const copy = new Stream(errorReports)
            .map(errorReport => errorReport.id)
            .toList();
        setSelectedErrorReports(copy);
    }

    const getItems = () => {
        return new Stream(errorReports)
            .sorted((a, b) => b.createdAt.localeCompare(a.createdAt))
            .map(errorReport => <ErrorReportListItem
                key={errorReport.id}
                errorReport={errorReport}
                selectedErrorReports={selectedErrorReports}
                setSelectedErrorReports={setSelectedErrorReports}
                refreshCallback={refreshCallback}
                setConfirmationDialogData={setConfirmationDialogData}
            />)
            .toList();
    }

    return (
        <div id="error-report-list-wrapper">
            <table id="error-report-list" className="formatted-table">
                <thead>
                    <tr>
                        <th className="error-report-list-checkbox-head">
                            <InputField
                                id="error-report-list-select-all"
                                type="checkbox"
                                checked={true}
                                onchangeCallback={(o, e) => e.preventDefault()}
                                onclickCallback={selectAll}
                            />
                            <span>/</span>
                            <InputField
                                id="error-report-list-deselect-all"
                                type="checkbox"
                                checked={false}
                                onchangeCallback={(o, e) => e.preventDefault()}
                                onclickCallback={() => setSelectedErrorReports([])}
                            />
                        </th>
                        <th>{localizationHandler.get("time-of-occurrence")}</th>
                        <th>{localizationHandler.get("status-code")}</th>
                        <th>{localizationHandler.get("service")}</th>
                        <th>{localizationHandler.get("message")}</th>
                        <th>{localizationHandler.get("total-count", {value: totalCount})}</th>
                    </tr>
                </thead>

                <tbody>
                    {getItems()}
                </tbody>
            </table>
        </div>
    );
}

export default ErrorReportList;