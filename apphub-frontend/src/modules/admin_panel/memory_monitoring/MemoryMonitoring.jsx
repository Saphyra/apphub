import React, { useEffect, useState } from "react";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import localizationData from "./memory_monitoring_page_localization.json";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import Constants from "../../../common/js/Constants";
import { ToastContainer } from "react-toastify";
import Durations from "./durations/Durations.jsx";
import "./memory_monitoring.css";
import Diagrams from "./diagrams/Diagrams";
import LocalDateTime from "../../../common/js/date/LocalDateTime";
import serviceList from "./services.json";
import Stream from "../../../common/js/collection/Stream";
import Switches from "./switches/Switches";
import WebSocketEndpoint from "../../../common/hook/ws/WebSocketEndpoint.js";
import WebSocketEventName from "../../../common/hook/ws/WebSocketEventName.js";
import useConnectToWebSocket from "../../../common/hook/ws/WebSocketFacade.js";
import { hasValue } from "../../../common/js/Utils.js";

const MemoryMonitoring = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [reports, setReports] = useState([]);
    const [duration, setDuration] = useState(30);
    const [switches, setSwitches] = useState(new Stream(serviceList).toMapStream(key => key, key => true).toObject());

    useConnectToWebSocket(
        WebSocketEndpoint.ADMIN_PANEL_MEMORY_MONITORING,
        (lastEvent) => processEvent(lastEvent)
    );

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const processEvent = (lastEvent) => {
        if (!hasValue(lastEvent) || lastEvent.eventName === WebSocketEventName.PING) {
            return;
        }

        const currentTime = LocalDateTime.now()
            .getEpoch() / 1000;

        reports.push(lastEvent.payload);

        setReports(reports.filter(item => item.epochSeconds > currentTime - 1800));
    }

    return (
        <div id="memory-monitoring" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <Durations
                    duration={duration}
                    setDuration={setDuration}
                />

                <Switches
                    services={switches}
                    setServices={setSwitches}
                />

                <Diagrams
                    services={switches}
                    reports={reports}
                    duration={duration}
                    localizationHandler={localizationHandler}
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

            <ToastContainer />
        </div>
    );
}

export default MemoryMonitoring;