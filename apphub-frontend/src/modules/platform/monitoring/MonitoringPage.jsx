import { useEffect, useState } from "react";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import sessionChecker from "../../../common/js/SessionChecker";
import localizationData from "./monitoring_page_localization.json";
import NotificationService from "../../../common/js/notification/NotificationService";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import { ToastContainer } from "react-toastify";
import Spinner from "../../../common/component/Spinner";
import MonitoringInputs from "./input/MonitoringInputs";
import { hasValue } from "../../../common/js/Utils";
import MonitoringBoard from "./board/MonitoringBoard";
import "./monitoring.css";
import { MODULES_PAGE } from "modules/etc/modules/ModulesEndpoints";

const MonitoringPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [displaySpinner, setDisplaySpinner] = useState(false);
    const [queryData, setQueryData] = useState(null);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    return (
        <div id="monitoring" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <MonitoringInputs
                    localizationHandler={localizationHandler}
                    setDisplaySpinner={setDisplaySpinner}
                    setQueryData={setQueryData}
                />

                {hasValue(queryData) &&
                    <MonitoringBoard
                        setDisplaySpinner={setDisplaySpinner}
                        localizationHandler={localizationHandler}
                        queryData={queryData}
                    />
                }
            </main>

            <Footer
                rightButtons={
                    <Button
                        id="home-button"
                        onclick={() => window.location.href = MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                }
            />

            <ToastContainer />

            {displaySpinner && <Spinner />}
        </div>
    );
}

export default MonitoringPage;