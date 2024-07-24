import React from "react";
import ToolStatus from "../ToolStatus";
import Button from "../../../../../common/component/input/Button";
import Endpoints from "../../../../../common/js/dao/dao";
import Utils from "../../../../../common/js/Utils";

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
            <td className="villany-atesz-toolbox-overview-item-brand">{tool.brand}</td>
            <td className="villany-atesz-toolbox-overview-item-name">{tool.name}</td>
            <td >
                <span className="villany-atesz-toolbox-overview-item-cost">{tool.cost}</span>
                <span> Ft</span>
            </td>
            <td className="villany-atesz-toolbox-overview-item-acquired-at">{tool.acquiredAt}</td>
            <td className="villany-atesz-toolbox-overview-item-warranty-expires-at">{tool.warrantyExpiresAt}</td>
            <td>
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
                    />
                }

                {tool.status !== ToolStatus.LOST &&
                    <Button
                        className="villany-atesz-toolbox-overview-item-set-to-lost-button"
                        label={localizationHandler.get("set-status-to-lost")}
                        onclick={() => setStatus(ToolStatus.LOST)}
                    />
                }

                <Button
                    className="villany-atesz-toolbox-overview-item-set-to-scrapped-button"
                    label={localizationHandler.get("set-status-to-scrapped")}
                    onclick={() => setStatus(ToolStatus.SCRAPPED)}
                />
            </td>
        </tr>
    );
}

export default ToolboxOverviewItem;