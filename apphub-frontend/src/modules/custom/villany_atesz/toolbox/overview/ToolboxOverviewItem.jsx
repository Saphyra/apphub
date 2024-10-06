import React from "react";
import ToolStatus from "../ToolStatus";
import Button from "../../../../../common/component/input/Button";
import Endpoints from "../../../../../common/js/dao/dao";
import Utils from "../../../../../common/js/Utils";
import Optional from "../../../../../common/js/collection/Optional";

const StatusMapping = {};
StatusMapping[ToolStatus.DEFAULT] = "";
StatusMapping[ToolStatus.LOST] = "background-red";
StatusMapping[ToolStatus.DAMAGED] = "background-orange";

const ToolboxOverviewItem = ({ localizationHandler, tool, setTools }) => {
    const setStatus = async (status) => {
        const response = await Endpoints.VILLANY_ATESZ_SET_TOOL_STATUS.createRequest({ value: status }, { toolId: tool.toolId })
            .send();

        Utils.copyAndSet(response, setTools);
    }

    return (
        <tr className={"villany-atesz-toolbox-overview-item " + StatusMapping[tool.status]}>
            <td className="villany-atesz-toolbox-overview-tool-type selectable">{new Optional(tool.toolType).map(toolType => toolType.name).orElse("")}</td>
            <td className="villany-atesz-toolbox-overview-item-brand selectable">{tool.brand}</td>
            <td className="villany-atesz-toolbox-overview-item-name selectable">{tool.name}</td>
            <td className="villany-atesz-toolbox-overview-storage-box selectable">{new Optional(tool.storageBox).map(storageBox => storageBox.name).orElse("")}</td>
            <td className="selectable">
                <span className="villany-atesz-toolbox-overview-item-cost">{tool.cost}</span>
                <span> Ft</span>
            </td>
            <td className="villany-atesz-toolbox-overview-item-acquired-at selectable">{tool.acquiredAt}</td>
            <td className="villany-atesz-toolbox-overview-item-warranty-expires-at selectable">{tool.warrantyExpiresAt}</td>
            <td className="villany-atesz-toolbox-overview-item-operations">
                {tool.status !== ToolStatus.DEFAULT &&
                    <Button
                        className="villany-atesz-toolbox-overview-item-set-to-default-button"
                        label={localizationHandler.get("set-status-to-default")}
                        onclick={() => setStatus(ToolStatus.DEFAULT)}
                    />
                }

                {tool.status !== ToolStatus.DAMAGED &&
                    <Button
                        className="villany-atesz-toolbox-overview-item-set-to-damaged-button"
                        label={localizationHandler.get("set-status-to-damaged")}
                        onclick={() => setStatus(ToolStatus.DAMAGED)}
                        title={localizationHandler.get("set-status-to-damaged-title")}
                    />
                }

                {tool.status !== ToolStatus.LOST &&
                    <Button
                        className="villany-atesz-toolbox-overview-item-set-to-lost-button"
                        label={localizationHandler.get("set-status-to-lost")}
                        onclick={() => setStatus(ToolStatus.LOST)}
                        title={localizationHandler.get("set-status-to-lost-title")}
                    />
                }

                <Button
                    className="villany-atesz-toolbox-overview-item-set-to-scrapped-button"
                    label={localizationHandler.get("set-status-to-scrapped")}
                    onclick={() => setStatus(ToolStatus.SCRAPPED)}
                    title={localizationHandler.get("set-status-to-scrapped-title")}
                />
            </td>
        </tr>
    );
}

export default ToolboxOverviewItem;