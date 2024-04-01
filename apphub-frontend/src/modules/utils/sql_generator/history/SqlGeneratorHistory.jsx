import React, { useState } from "react";
import "./sql_generator_history.css";
import localizationData from "./sql_generator_history_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";

const SqlGeneratorHistory = ({ }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [items, setItems] = useState([]);

    return (
        <div id="sql-generator-history">
            <div id="sql-generator-history-title">{localizationHandler.get("title")}</div>
        </div>
    )
}

export default SqlGeneratorHistory;