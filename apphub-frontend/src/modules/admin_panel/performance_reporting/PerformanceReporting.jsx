import { useEffect, useRef, useState } from "react";
import localizationData from "./performance_reporting_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import ConfirmationDialog from "../../../common/component/confirmation_dialog/ConfirmationDialog";
import Constants from "../../../common/js/Constants";
import { ADMIN_PANEL_PERFORMANCE_REPORTING_GET_REPORTS, ADMIN_PANEL_PERFORMANCE_REPORTING_GET_TOPICS } from "../../../common/js/dao/endpoints/AdminPanelEndpoints";
import "./performance_reporting.css";
import PerformanceReportingTopics from "./PerformanceReportingTopics";
import Stream from "../../../common/js/collection/Stream";
import RefreshIntervalSelector from "./RefreshIntervalSelector";
import PerformanceReportingReports from "./PerformanceReportingReports";
import Spinner from "../../../common/component/Spinner";

const PerformanceReporting = ({ }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [confirmationDialogData, setConfirmationDialogData] = useState(null);
    const [topics, setTopics] = useState([]);
    const topicsRef = useRef(topics);
    const [refreshInterval, setRefreshInterval] = useState(5000);
    const [interval, sInterval] = useState(null);
    const [reports, setReports] = useState({});
    const [displayMiniSpinner, setDisplayMiniSpinner] = useState(false);

    useEffect(() => setRefresh(), [refreshInterval]);
    useEffect(() => { topicsRef.current = topics; }, [topics]);
    useEffect(() => loadTopics(), []);

    const setRefresh = () => {
        if (refreshInterval == 0) {
            clearInterval(interval);
        }

        const i = setInterval(
            () => {
                loadTopics();
                loadReports();
            },
            refreshInterval
        );
        sInterval(i);

        return () => clearInterval(i);
    }

    const loadTopics = () => {
        const fetch = async () => {
            const response = await ADMIN_PANEL_PERFORMANCE_REPORTING_GET_TOPICS.createRequest()
                .send();
            setTopics(response);
        }
        fetch();
    }

    const loadReports = () => {
        const fetch = async () => {
            const enabledTopics = new Stream(topicsRef.current)
                .filter(topic => topic.enabled)
                .toList();

            const newReports = {};
            for (let i in enabledTopics) {
                const topic = enabledTopics[i];

                const response = await ADMIN_PANEL_PERFORMANCE_REPORTING_GET_REPORTS.createRequest(null, { topic: topic.topic })
                    .send(setDisplayMiniSpinner);

                newReports[topic.topic] = response;
            }

            setReports(newReports);
        }
        fetch();
    }

    return (
        <div id="performance-reporting" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <div id="performance-reporting-operations">
                    <RefreshIntervalSelector
                        refreshInterval={refreshInterval}
                        setRefreshInterval={setRefreshInterval}
                        localizationHandler={localizationHandler}
                        loadCallback={loadReports}
                    />

                    <PerformanceReportingTopics
                        topics={topics}
                        setTopics={setTopics}
                        localizationHandler={localizationHandler}
                        setConfirmationDialogData={setConfirmationDialogData}
                    />

                    {displayMiniSpinner && <Spinner id="performance-reporting-mini-spinner" />}
                </div>

                <PerformanceReportingReports
                    reports={reports}
                    localizationHandler={localizationHandler}
                    loadReports={loadReports}
                />
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
        </div>
    );
}

export default PerformanceReporting;