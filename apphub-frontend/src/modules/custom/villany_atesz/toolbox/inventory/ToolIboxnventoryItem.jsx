import React, { useEffect, useState } from "react";
import InputField from "../../../../../common/component/input/InputField";
import ScheduledInputField from "../../../../../common/component/input/ScheduledInputField";
import Endpoints from "../../../../../common/js/dao/dao";
import SelectInput, { SelectOption } from "../../../../../common/component/input/SelectInput";
import ToolStatus from "../ToolStatus";
import Button from "../../../../../common/component/input/Button";
import DataListInputField, { DataListInputEntry } from "../../../../../common/component/input/DataListInputField";
import Optional from "../../../../../common/js/collection/Optional";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import { copyAndSet, isBlank } from "../../../../../common/js/Utils";

const ToolboxInventoryItem = ({
    tool,
    tools,
    setTools,
    setConfirmationDialogData,
    localizationHandler,
    toolTypes,
    loadToolTypes,
    storageBoxes,
    loadStorageBoxes
}) => {
    const [toolType, setToolType] = useState(new DataListInputEntry());
    const [storageBox, setStorageBox] = useState(new DataListInputEntry());

    useEffect(() => new Optional(tool.toolType).ifPresent((tt) => setToolType(new DataListInputEntry(tool.toolType.toolTypeId, tool.toolType.name))), [tool.toolType]);
    useEffect(() => new Optional(tool.storageBox).ifPresent((sb) => setStorageBox(new DataListInputEntry(tool.storageBox.storageBoxId, tool.storageBox.name))), [tool.storageBox]);

    const updateProperty = (property, value) => {
        tool[property] = value;

        copyAndSet(tools, setTools);
    }

    const editInventoried = async (inventoried) => {
        await Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_INVENTORIED.createRequest({ value: inventoried }, { toolId: tool.toolId })
            .send();

        updateProperty("inventoried", inventoried);
    }

    const editAcquiredAt = async (acquiredAt) => {
        if (!isBlank(acquiredAt)) {
            await Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_ACQUIRED_AT.createRequest({ value: acquiredAt }, { toolId: tool.toolId })
                .send();
        }

        updateProperty("acquiredAt", acquiredAt);
    }

    const editWarrantyExpiresAt = async (warrantyExpiresAt) => {
        await Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_WARRANTY_EXPIRES_AT.createRequest({ value: warrantyExpiresAt }, { toolId: tool.toolId })
            .send();

        updateProperty("warrantyExpiresAt", warrantyExpiresAt);
    }

    const editScrappedAt = async (scrappedAt) => {
        await Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_SCRAPPED_AT.createRequest({ value: scrappedAt }, { toolId: tool.toolId })
            .send();

        updateProperty("scrappedAt", scrappedAt);
    }

    const editStatus = async (status) => {
        await Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_STATUS.createRequest({ value: status }, { toolId: tool.toolId })
            .send();

        updateProperty("status", status);
    }

    const editToolType = async () => {
        const payload = {
            toolTypeId: toolType.key,
            name: toolType.value
        };

        const response = await Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_TOOL_TYPE.createRequest(payload, { toolId: tool.toolId })
            .send();

        updateProperty("toolType", response);
        loadToolTypes();
    }

    const editStorageBox = async () => {
        const payload = {
            storageBoxId: storageBox.key,
            name: storageBox.value
        };

        const response = await Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_STORAGE_BOX.createRequest(payload, { toolId: tool.toolId })
            .send();

        updateProperty("storageBox", response);
        loadStorageBoxes();
    }

    const sendRequest = (endpoint, newValue, payloadMapper = (value) => { return { value: value } }, validation = () => true) => {
        if (!validation(newValue)) {
            return false;
        }

        const send = async () => {
            await endpoint.createRequest(payloadMapper(newValue), { toolId: tool.toolId })
                .send();
        }

        send();

        return true;
    }

    const confirmDeletion = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "villany-atesz-toolbox-inventory-item-deletion-confirmation",
            localizationHandler.get("deletion-confirmation-title"),
            localizationHandler.get("deletion-confirmation-content", { name: tool.name }),
            [
                <Button
                    key="delete"
                    id="villany-atesz-toolbox-inventory-item-deletion-confirm-button"
                    label={localizationHandler.get("delete")}
                    onclick={deleteTool}
                />,
                <Button
                    key="cancel"
                    id="villany-atesz-toolbox-inventory-item-deletion-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const deleteTool = async () => {
        const response = await Endpoints.VILLANY_ATESZ_DELETE_TOOL.createRequest(null, { toolId: tool.toolId })
            .send();

        setTools(response);

        setConfirmationDialogData(null);
    }

    if (tool.inventoried) {
        return (
            <tr className="villany-atesz-toolbox-inventory-item">
                <td>
                    <InputField
                        className="villany-atesz-toolbox-inventory-item-inventoried"
                        type="checkbox"
                        checked={tool.inventoried}
                        onchangeCallback={editInventoried}
                    />
                </td>
                <td className="villany-atesz-toolbox-inventory-item-data-list-cell">
                    <span className="villany-atesz-toolbox-inventory-item-tool-type selectable">{toolType.value}</span>
                </td>
                <td>
                    <span className="villany-atesz-toolbox-inventory-item-brand selectable">{tool.brand}</span>
                </td>
                <td>
                    <span className="villany-atesz-toolbox-inventory-item-name selectable">{tool.name}</span>
                </td>
                <td className="villany-atesz-toolbox-inventory-item-data-list-cell">
                    <span className="villany-atesz-toolbox-inventory-item-storage-box selectable">{storageBox.value}</span>
                </td>
                <td>
                    <span className="villany-atesz-toolbox-inventory-item-cost selectable">{tool.cost}</span>
                </td>
                <td>
                    <span className="villany-atesz-toolbox-inventory-item-acquired-at selectable">{tool.acquiredAt}</span>
                </td>
                <td>
                    <span className="villany-atesz-toolbox-inventory-item-warranty-expires-at selectable">{tool.warrantyExpiresAt}</span>
                </td>
                <td>
                    <span className="villany-atesz-toolbox-inventory-item-scrapped-at selectable">{tool.scrappedAt}</span>
                </td>
                <td>
                    <span className="villany-atesz-toolbox-inventory-item-status selectable">{localizationHandler.get(tool.status.toLowerCase())}</span>
                </td>
                <td>
                    <Button
                        className="villany-atesz-toolbox-inventory-item-delete"
                        label={localizationHandler.get("delete")}
                        onclick={confirmDeletion}
                    />
                </td>
            </tr>
        );
    } else {
        return (
            <tr className="villany-atesz-toolbox-inventory-item">
                <td>
                    <InputField
                        className="villany-atesz-toolbox-inventory-item-inventoried"
                        type="checkbox"
                        checked={tool.inventoried}
                        onchangeCallback={editInventoried}
                    />
                </td>
                <td className="villany-atesz-toolbox-inventory-item-data-list-cell">
                    <DataListInputField
                        className="villany-atesz-toolbox-inventory-item-tool-type"
                        value={toolType}
                        setValue={setToolType}
                        options={toolTypes}
                        placeholder={localizationHandler.get("tool-type")}

                    />
                    <Button
                        className="villany-atesz-toolbox-inventory-item-tool-type-save-button"
                        onclick={editToolType}
                    />
                </td>
                <td>
                    <ScheduledInputField
                        type="text"
                        className="villany-atesz-toolbox-inventory-item-brand"
                        placeholder={localizationHandler.get("brand")}
                        value={tool.brand}
                        onchangeCallback={(newValue) => updateProperty("brand", newValue)}
                        scheduledCallback={(newValue) => sendRequest(
                            Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_BRAND,
                            newValue
                        )}
                        style={{ width: 8 * tool.name.length + "px" }}
                    />
                </td>
                <td>
                    <ScheduledInputField
                        type="text"
                        className="villany-atesz-toolbox-inventory-item-name"
                        placeholder={localizationHandler.get("name")}
                        value={tool.name}
                        onchangeCallback={(newValue) => updateProperty("name", newValue)}
                        scheduledCallback={(newValue) => sendRequest(
                            Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_NAME,
                            newValue,
                            nw => { return { value: nw } },
                            nw => !isBlank(nw)
                        )}
                        style={{ width: 8 * tool.name.length + "px" }}
                    />
                </td>
                <td className="villany-atesz-toolbox-inventory-item-data-list-cell">
                    <DataListInputField
                        className="villany-atesz-toolbox-inventory-item-storage-box"
                        value={storageBox}
                        setValue={setStorageBox}
                        options={storageBoxes}
                        placeholder={localizationHandler.get("storage-box")}

                    />
                    <Button
                        className="villany-atesz-toolbox-inventory-item-storage-box-save-button"
                        onclick={editStorageBox}
                    />
                </td>
                <td>
                    <ScheduledInputField
                        type="number"
                        className="villany-atesz-toolbox-inventory-item-cost"
                        placeholder={localizationHandler.get("cost")}
                        value={tool.cost}
                        onchangeCallback={(newValue) => updateProperty("cost", newValue)}
                        scheduledCallback={(newValue) => sendRequest(
                            Endpoints.VILLANY_ATESZ_TOOLBOX_INVENTORY_EDIT_COST,
                            newValue,
                            nw => { return { value: nw } },
                            nw => !isBlank(nw)
                        )}
                    />
                </td>
                <td>
                    <InputField
                        type="date"
                        className={"villany-atesz-toolbox-inventory-item-acquired-at" + (isBlank(tool.acquiredAt) ? " scheduled" : "")}
                        placeholder={localizationHandler.get("acquired-at")}
                        value={tool.acquiredAt}
                        onchangeCallback={editAcquiredAt}
                    />
                </td>
                <td>
                    <InputField
                        type="date"
                        className="villany-atesz-toolbox-inventory-item-warranty-expires-at"
                        placeholder={localizationHandler.get("warranty-expires-at")}
                        value={tool.warrantyExpiresAt}
                        onchangeCallback={editWarrantyExpiresAt}
                    />
                </td>
                <td>
                    <InputField
                        type="date"
                        className="villany-atesz-toolbox-inventory-item-scrapped-at"
                        placeholder={localizationHandler.get("scrapped-at")}
                        value={tool.scrappedAt}
                        onchangeCallback={editScrappedAt}
                    />
                </td>
                <td>
                    <SelectInput
                        className="villany-atesz-toolbox-inventory-item-status"
                        value={tool.status}
                        options={[
                            new SelectOption(localizationHandler.get("default"), ToolStatus.DEFAULT),
                            new SelectOption(localizationHandler.get("lost"), ToolStatus.LOST),
                            new SelectOption(localizationHandler.get("scrapped"), ToolStatus.SCRAPPED),
                            new SelectOption(localizationHandler.get("damaged"), ToolStatus.DAMAGED),
                        ]}
                        onchangeCallback={editStatus}
                    />
                </td>
                <td>
                    <Button
                        className="villany-atesz-toolbox-inventory-item-delete"
                        label={localizationHandler.get("delete")}
                        onclick={confirmDeletion}
                    />
                </td>
            </tr>
        );
    }
}

export default ToolboxInventoryItem;