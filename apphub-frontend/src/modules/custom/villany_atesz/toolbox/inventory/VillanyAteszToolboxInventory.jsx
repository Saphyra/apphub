import React, { useEffect, useState } from "react";
import "./villany_atesz_toolbox_inventory.css";
import localizationData from "./villany_atesz_toolbox_inventory_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import useLoader from "../../../../../common/hook/Loader";
import Endpoints from "../../../../../common/js/dao/dao";
import Stream from "../../../../../common/js/collection/Stream";
import filterTool from "../ToolFilter";
import sortTools from "../ToolSorter";
import ToolboxInventoryItem from "./ToolIboxnventoryItem";
import InputField from "../../../../../common/component/input/InputField";
import { DataListInputEntry } from "../../../../../common/component/input/DataListInputField";
import Button from "../../../../../common/component/input/Button";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";

const VillanyAteszToolboxInventory = ({ setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [search, setSearch] = useState("");
    const [tools, setTools] = useState([]);
    const [toolTypes, setToolTypes] = useState([]);
    const [storageBoxes, setStorageBoxes] = useState([]);

    useLoader(Endpoints.VILLANY_ATESZ_GET_TOOLS.createRequest(), setTools);
    useEffect(() => loadToolTypes(), []);
    useEffect(() => loadStorageBoxes(), []);

    const loadToolTypes = () => {
        const fetch = async () => {
            const response = await Endpoints.VILLANY_ATESZ_GET_TOOL_TYPES.createRequest()
                .send();

            const entries = new Stream(response)
                .map(item => new DataListInputEntry(item.toolTypeId, item.name))
                .toList();

            setToolTypes(entries);
        }
        fetch();
    }

    const loadStorageBoxes = () => {
        const fetch = async () => {
            const response = await Endpoints.VILLANY_ATESZ_GET_STORAGE_BOXES.createRequest()
                .send();

            const entries = new Stream(response)
                .map(item => new DataListInputEntry(item.storageBoxId, item.name))
                .toList();

            setStorageBoxes(entries);
        }
        fetch();
    }

    const confirmResetInventoried = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "villany-atesz-toolbox-inventory-reset-inventoried-confirmation",
            localizationHandler.get("confirm-reset-inventoried-title"),
            localizationHandler.get("confirm-reset-inventoried-detail"),
            [
                <Button
                    key="reset"
                    id="villany-atesz-toolbox-inventory-reset-inventoried-confirm-button"
                    label={localizationHandler.get("reset")}
                    onclick={resetInventoried}
                />,
                <Button
                    key="cancel"
                    id="villany-atesz-toolbox-inventory-reset-inventoried-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ))
    }

    const resetInventoried = async () => {
        const response = await Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_RESET_INVENTORIED.createRequest()
            .send();

        setTools(response);

        setConfirmationDialogData(null);
    }

    const getItems = () => {
        return new Stream(tools)
            .filter(tool => filterTool(tool, search))
            .sorted(sortTools)
            .map(tool => <ToolboxInventoryItem
                key={tool.toolId}
                tool={tool}
                tools={tools}
                setTools={setTools}
                setConfirmationDialogData={setConfirmationDialogData}
                localizationHandler={localizationHandler}
                toolTypes={toolTypes}
                loadToolTypes={loadToolTypes}
                storageBoxes={storageBoxes}
                loadStorageBoxes={loadStorageBoxes}
            />
            )
            .toList()
    }

    return (
        <div id="villany-atesz-toolbox-inventory">
            <InputField
                id="villany-atesz-stock-toolbox-items-search"
                placeholder={localizationHandler.get("search")}
                value={search}
                onchangeCallback={setSearch}
            />

            <table className="formatted-table">
                <thead>
                    <tr>
                        <td>
                            {localizationHandler.get("inventoried")}
                            <Button
                                id="villany-atesz-stock-toolbox-reset-inventoried-button"
                                label="X"
                                onclick={confirmResetInventoried}
                            />
                        </td>
                        <td>{localizationHandler.get("tool-type")}</td>
                        <td>{localizationHandler.get("brand")}</td>
                        <td>{localizationHandler.get("name")}</td>
                        <td>{localizationHandler.get("storage-box")}</td>
                        <td>{localizationHandler.get("cost")}</td>
                        <td>{localizationHandler.get("acquired-at")}</td>
                        <td>{localizationHandler.get("warranty-expires-at")}</td>
                        <td>{localizationHandler.get("scrapped-at")}</td>
                        <td>{localizationHandler.get("status")}</td>
                        <td></td>
                    </tr>
                </thead>
                <tbody>
                    {getItems()}
                </tbody>
            </table>
        </div>
    );
}

export default VillanyAteszToolboxInventory;