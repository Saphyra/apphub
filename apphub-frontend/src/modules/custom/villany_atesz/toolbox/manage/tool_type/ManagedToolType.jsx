import React, { useEffect, useState } from "react";
import InputField from "../../../../../../common/component/input/InputField";
import Button from "../../../../../../common/component/input/Button";
import ConfirmationDialogData from "../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import { copyAndSet, isBlank, removeAndSet } from "../../../../../../common/js/Utils";
import { VILLANY_ATESZ_DELETE_TOOL_TYPE, VILLANY_ATESZ_EDIT_TOOL_TYPE } from "../../../../../../common/js/dao/endpoints/VillanyAteszEndpoints";

const ManagedToolType = ({ setConfirmationDialogData, localizationHandler, toolType, toolTypes, setToolTypes }) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [newName, setNewName] = useState("");

    useEffect(() => setNewName(toolType.name), [toolType.name]);

    const save = async () => {
        await VILLANY_ATESZ_EDIT_TOOL_TYPE.createRequest({ value: newName }, { toolTypeId: toolType.toolTypeId })
            .send();

        toolType.name = newName;

        copyAndSet(toolTypes, setToolTypes);
        setEditingEnabled(false);
    }

    const discard = () => {
        setEditingEnabled(false);
        setNewName(toolType.name);
    }

    const opendConfirmDeletionDialog = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "villany-atesz-toolbox-manage-tool-type-deletion-confirmation",
            localizationHandler.get("delete-tool-type-confirmation-title"),
            localizationHandler.get("delete-tool-type-confirmation-detail", { name: toolType.name }),
            [
                <Button
                    key="delete"
                    id="villany-atesz-toolbox-manage-tool-type-deletion-confirm-button"
                    label={localizationHandler.get("delete")}
                    onclick={confirmDeletion}
                />,
                <Button
                    key="cancel"
                    id="villany-atesz-toolbox-manage-tool-type-deletion-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const confirmDeletion = async () => {
        await VILLANY_ATESZ_DELETE_TOOL_TYPE.createRequest(null, { toolTypeId: toolType.toolTypeId })
            .send();

        removeAndSet(toolTypes, tt => tt.toolTypeId === toolType.toolTypeId, setToolTypes);

        setConfirmationDialogData(null);
    }

    return (
        <div className="villany-atesz-toolbox-manage-tab-content-item">
            {editingEnabled &&
                <div>
                    <InputField
                        className="villany-atesz-toolbox-manage-tool-type-name"
                        value={newName}
                        onchangeCallback={setNewName}
                        placeholder={localizationHandler.get("name")}
                        style={{
                            width: 8 * newName.length + "px",
                            minWidth: "200px"
                        }}
                    />
                    <Button
                        className={"villany-atesz-toolbox-manage-tool-type-save-button"}
                        onclick={save}
                        disabled={isBlank(newName)}
                        label="_"
                        title={localizationHandler.get("save")}
                    />
                    <Button
                        className={"villany-atesz-toolbox-manage-tool-type-discard-button"}
                        onclick={discard}
                        label="X"
                        title={localizationHandler.get("discard")}
                    />
                </div>
            }
            {!editingEnabled &&
                <div>
                    <span className="villany-atesz-toolbox-manage-tool-type-name">{toolType.name}</span>
                    <Button
                        className="villany-atesz-toolbox-manage-tool-type-enable-editing-button"
                        onclick={() => setEditingEnabled(true)}
                        title={localizationHandler.get("edit")}
                    />
                    <Button
                        className="villany-atesz-toolbox-manage-tool-type-delete-button"
                        onclick={opendConfirmDeletionDialog}
                        title={localizationHandler.get("delete")}
                    />
                </div>
            }
        </div>
    );
}

export default ManagedToolType;