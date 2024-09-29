import React from "react";
import Endpoints from "../../../../../common/js/dao/dao";
import ToolStatus from "../ToolStatus";
import Button from "../../../../../common/component/input/Button";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Optional from "../../../../../common/js/collection/Optional";
import { copyAndSet } from "../../../../../common/js/Utils";

const ToolboxScrappedItem = ({ localizationHandler, tool, setTools, setConfirmationDialogData }) => {
    const descrap = async () => {
        const response = await Endpoints.VILLANY_ATESZ_SET_TOOL_STATUS.createRequest({ value: ToolStatus.DEFAULT }, { toolId: tool.toolId })
            .send();

        copyAndSet(response, setTools);
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

        copyAndSet(response, setTools);

        setConfirmationDialogData(null);
    }

    return (
        <tr className="villany-atesz-toolbox-scrapped-item">
            <td className="villany-atesz-toolbox-overview-tool-type selectable">{new Optional(tool.toolType).map(toolType => toolType.name).orElse("")}</td>
            <td className="villany-atesz-toolbox-scrapped-item-brand selectable">{tool.brand}</td>
            <td className="villany-atesz-toolbox-scrapped-item-name selectable">{tool.name}</td>
            <td className="villany-atesz-toolbox-overview-storage-box selectable">{new Optional(tool.storageBox).map(storageBox => storageBox.name).orElse("")}</td>
            <td className="selectable">
                <span className="villany-atesz-toolbox-scrapped-item-cost">{tool.cost}</span>
                <span> Ft</span>
            </td>
            <td className="villany-atesz-toolbox-scrapped-item-acquired-at selectable">{tool.acquiredAt}</td>
            <td className="villany-atesz-toolbox-scrapped-item-warranty-expires-at selectable">{tool.warrantyExpiresAt}</td>
            <td className="villany-atesz-toolbox-scrapped-item-scrapped-at selectable">{tool.scrappedAt}</td>
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