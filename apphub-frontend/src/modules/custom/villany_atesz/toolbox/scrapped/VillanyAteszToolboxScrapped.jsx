import React, { useState } from "react";
import localizationData from "./villany_atesz_toolbox_scrapped_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import Endpoints from "../../../../../common/js/dao/dao";
import useLoader from "../../../../../common/hook/Loader";
import ToolStatus from "../ToolStatus";
import ToolboxScrappedItem from "./ToolboxScrappedItem";
import InputField from "../../../../../common/component/input/InputField";
import Stream from "../../../../../common/js/collection/Stream";
import "./villany_atesz_toolbox_scrapped.css";

const VillanyAteszToolboxScrapped = ({setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [search, setSearch] = useState("");
    const [tools, setTools] = useState([]);

    useLoader(Endpoints.VILLANY_ATESZ_GET_TOOLS.createRequest(), setTools);

    const getTools = () => {
        return new Stream(tools)
            .filter(tool => tool.status === ToolStatus.SCRAPPED)
            .filter(tool => search.length === 0
                || tool.brand.indexOf(search) > 0
                || tool.name.indexOf(search) > 0
                || tool.cost.indexOf(search) > 0
                || tool.acquiredAt.indexOf(search) > 0
                || tool.warrantyExpiresAt.indexOf(search) > 0
            )
            .sorted((a, b) => a.name.localeCompare(b.name))
            .map(tool => <ToolboxScrappedItem
                key={tool.toolId}
                localizationHandler={localizationHandler}
                tool={tool}
                setTools={setTools}
                setConfirmationDialogData={setConfirmationDialogData}
            />)
            .toList();
    }
    return (
        <div id="villany-atesz-toolbox-scrapped">
            <InputField
                id="villany-atesz-toolbox-scrapped-search"
                placeholder={localizationHandler.get("search")}
                value={search}
                onchangeCallback={setSearch}
            />

            <table id="villany-atesz-toolbox-scrapped-table" className="formatted-table">
                <thead>
                    <tr>
                        <th>{localizationHandler.get("brand")}</th>
                        <th>{localizationHandler.get("name")}</th>
                        <th>{localizationHandler.get("cost")}</th>
                        <th>{localizationHandler.get("acquired-at")}</th>
                        <th>{localizationHandler.get("warranty-expires-at")}</th>
                        <th>{localizationHandler.get("scrapped-at")}</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody id="villany-atesz-toolbox-scrapped-tools">
                    {getTools()}
                </tbody>
            </table>
        </div>
    );
}

export default VillanyAteszToolboxScrapped;