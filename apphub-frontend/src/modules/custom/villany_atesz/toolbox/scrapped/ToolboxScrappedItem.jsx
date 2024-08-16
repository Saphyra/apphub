import React from "react";
import Endpoints from "../../../../../common/js/dao/dao";
import Utils from "../../../../../common/js/Utils";
import ToolStatus from "../ToolStatus";
import Button from "../../../../../common/component/input/Button";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Optional from "../../../../../common/js/collection/Optional";

const ToolboxScrappedItem = ({ localizationHandler, tool, setTools, setConfirmationDialogData }) => {
    const descrap = async () => {
        const response = await Endpoints.VILLANY_ATESZ_SET_TOOL_STATUS.createRequest({ value: ToolStatus.DEFAULT }, { toolId: tool.toolId })
            .send();

        Utils.copyAndSet(response, setTools);
    }

    const confirmDeletion = () => {
        const confirmationDialogData = new ConfirmationDialogData(
            "villany-atesz-toolbox-scrapped-deletion-confirmation",
            localizationHandler.get("delete-tool-confirmation-dialog-title"),
            localizationHandler.get("delete-tool-confirmation-dialog-content", { name: tool.name }),
            [
                <Button
                    key="delete"
                    id="villany-atesz-toolboc-scrapped-deletion-confirm-button"
                    label={localizationHandler.get("delete")}
                    onclick={deleteTool}
                />,
                <Button
                    key="cancel"
                    id="villany-atesz-toolboc-scrapped-deletion-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        );
        setConfirmationDialogData(confirmationDialogData);
    }

    const deleteTool = async () => {
        const response = await Endpoints.VILLANY_ATESZ_DELETE_TOOL.createRequest(null, { toolId: tool.toolId })
            .send();

        Utils.copyAndSet(response, setTools);

        setConfirmationDialogData(null);
    }

    return (
        <tr className="villany-atesz-toolbox-scrapped-item">
            <td className="villany-atesz-toolbox-overview-tool-type">{new Optional(tool.toolType).map(toolType => toolType.name).orElse("")}</td>
            <td className="villany-atesz-toolbox-scrapped-item-brand">{tool.brand}</td>
            <td className="villany-atesz-toolbox-scrapped-item-name">{tool.name}</td>
            <td className="villany-atesz-toolbox-overview-storage-box">{new Optional(tool.storageBox).map(storageBox => storageBox.name).orElse("")}</td>
            <td >
                <span className="villany-atesz-toolbox-scrapped-item-cost">{tool.cost}</span>
                <span> Ft</span>
            </td>
            <td className="villany-atesz-toolbox-scrapped-item-acquired-at">{tool.acquiredAt}</td>
            <td className="villany-atesz-toolbox-scrapped-item-warranty-expires-at">{tool.warrantyExpiresAt}</td>
            <td className="villany-atesz-toolbox-scrapped-item-scrapped-at">{tool.scrappedAt}</td>
            <td>
                <Button
                    className="villany-atesz-toolbox-scrapped-item-set-to-default-button"
                    label={localizationHandler.get("set-status-to-default")}
                    onclick={() => descrap()}
                />
                <Button
                    className="villany-atesz-toolbox-scrapped-item-delete-button"
                    label={localizationHandler.get("delete")}
                    onclick={() => confirmDeletion()}
                />
            </td>
        </tr>
    );
}

export default ToolboxScrappedItem;