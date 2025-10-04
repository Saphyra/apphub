import React, { useState } from "react";
import useLoader from "../../../../../common/hook/Loader";
import InputField from "../../../../../common/component/input/InputField";
import localizationData from "./villany_atesz_toolbox_overview_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import "./villany_atesz_toolbox_overview.css";
import Stream from "../../../../../common/js/collection/Stream";
import ToolStatus from "../ToolStatus";
import ToolboxOverviewItem from "./ToolboxOverviewItem";
import filterTool from "../ToolFilter";
import sortTools from "../ToolSorter";
import { VILLANY_ATESZ_GET_TOOLS } from "../../../../../common/js/dao/endpoints/VillanyAteszEndpoints";

const VillanyAteszToolboxOverview = ({ setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [search, setSearch] = useState("");
    const [tools, setTools] = useState([]);

    useLoader({ request: VILLANY_ATESZ_GET_TOOLS.createRequest(), mapper: setTools });

    const getTools = () => {
        return new Stream(tools)
            .filter(tool => tool.status !== ToolStatus.SCRAPPED)
            .filter(tool => filterTool(tool, search))
            .sorted(sortTools)
            .map(tool => <ToolboxOverviewItem
                key={tool.toolId}
                localizationHandler={localizationHandler}
                tool={tool}
                setTools={setTools}
            />)
            .toList();
    }

    return (
        <div id="villany-atesz-toolbox-overview">
            <InputField
                id="villany-atesz-toolbox-overview-search"
                placeholder={localizationHandler.get("search")}
                value={search}
                onchangeCallback={setSearch}
            />

            <table id="villany-atesz-toolbox-overview-table" className="formatted-table">
                <thead>
                    <tr>
                        <th>{localizationHandler.get("tool-type")}</th>
                        <th>{localizationHandler.get("brand")}</th>
                        <th>{localizationHandler.get("name")}</th>
                        <th>{localizationHandler.get("storage-box")}</th>
                        <th>{localizationHandler.get("cost")}</th>
                        <th>{localizationHandler.get("acquired-at")}</th>
                        <th>{localizationHandler.get("warranty-expires-at")}</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody id="villany-atesz-toolbox-overview-tools">
                    {getTools()}
                </tbody>
            </table>
        </div>
    );
}

export default VillanyAteszToolboxOverview;