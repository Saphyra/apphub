import React, { useEffect, useState } from "react";
import "./sql_generator.css";
import localizationData from "./sql_generator_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import Constants from "../../../common/js/Constants";
import { ToastContainer } from "react-toastify";
import SqlGeneratorHistory from "./history/SqlGeneratorHistory";
import SqlGeneratorContent from "./content/SqlGeneratorContent";
import QueryType from "./QueryType";
import QueryData from "./QueryData";

const SqlGeneratorPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("page-title");

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const [queryData, setQueryData] = useState(new QueryData(QueryType.INSERT));

    return (
        <div className="main-page">
            <Header label={localizationHandler.get("title")} />

            <main id="sql-generator">
                <SqlGeneratorHistory

                />

                <SqlGeneratorContent
                    queryData={queryData}
                    setQueryData={setQueryData}
                />
            </main>

            <Footer
                rightButtons={
                    <Button
                        id="sql-generator-home-button"
                        onclick={() => window.location.href = Constants.MODULES_PAGE}
                        label={localizationHandler.get("home")}
                    />
                }
            />

            <ToastContainer />
        </div>
    );
}

export default SqlGeneratorPage;