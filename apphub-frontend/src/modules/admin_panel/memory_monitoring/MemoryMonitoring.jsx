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
import useWebSocket from "react-use-websocket";
import Durations from "./durations/Durations.jsx";
import "./memory_monitoring.css";
import Diagrams from "./diagrams/Diagrams";
import LocalDateTime from "../../../common/js/date/LocalDateTime";
import serviceList from  "./services.json";
import Stream from "../../../common/js/collection/Stream";
import Switches from "./switches/Switches";
import WebSocketEndpoint from "../../../common/hook/ws/WebSocketEndpoint.js";
import WebSocketEventName from "../../../common/hook/ws/WebSocketEventName.js";

const MemoryMonitoring = () => {
    const wsUrl = "ws://" + window.location.host + WebSocketEndpoint.ADMIN_PANEL_MEMORY_MONITORING;
    const localizationHandler = new LocalizationHandler(localizationData);
    const { sendMessage, lastMessage } = useWebSocket(wsUrl, { share: true, shouldReconnect: () => true });
    const [reports, setReports] = useState([]);
    const [duration, setDuration] = useState(30);
    const [switches, setSwitches] = useState(new Stream(serviceList).toMapStream(key => key, key => true).toObject());

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);
    useEffect(() => handleMessage(), [lastMessage]);

    const handleMessage = () => {
        if (lastMessage === null) {
            return;
        }

        const message = JSON.parse(lastMessage.data);

        if (message.eventName === WebSocketEventName.PING) {
            sendMessage(lastMessage.data);
            return;
        }

        reports.push(message.payload);

        const currentTime = LocalDateTime.now()
            .getEpoch() / 1000;

        setReports(reports.filter(item => item.epochSeconds > currentTime - 1800));
    }

    document.title = localizationHandler.get("title");

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