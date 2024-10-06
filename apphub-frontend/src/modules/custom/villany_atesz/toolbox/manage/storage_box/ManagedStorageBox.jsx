import React, { useEffect, useState } from "react";
import InputField from "../../../../../../common/component/input/InputField";
import Button from "../../../../../../common/component/input/Button";
import ConfirmationDialogData from "../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import { copyAndSet, isBlank, removeAndSet } from "../../../../../../common/js/Utils";
import { VILLANY_ATESZ_DELETE_STORAGE_BOX, VILLANY_ATESZ_EDIT_STORAGE_BOX } from "../../../../../../common/js/dao/endpoints/VillanyAteszEndpoints";

const ManagedStorageBox = ({ setConfirmationDialogData, localizationHandler, storageBox, storageBoxes, setStorageBoxes }) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [newName, setNewName] = useState("");

    useEffect(() => setNewName(storageBox.name), [storageBox.name]);

    const save = async () => {
        await VILLANY_ATESZ_EDIT_STORAGE_BOX.createRequest({ value: newName }, { storageBoxId: storageBox.storageBoxId })
            .send();

        storageBox.name = newName;

        copyAndSet(storageBoxes, setStorageBoxes);
        setEditingEnabled(false);
    }

    const discard = () => {
        setEditingEnabled(false);
        setNewName(storageBox.name);
    }

    const opendConfirmDeletionDialog = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "villany-atesz-toolbox-manage-storage-box-deletion-confirmation",
            localizationHandler.get("delete-storage-box-confirmation-title"),
            localizationHandler.get("delete-storage-box-confirmation-detail", { name: storageBox.name }),
            [
                <Button
                    key="delete"
                    id="villany-atesz-toolbox-manage-storage-box-deletion-confirm-button"
                    label={localizationHandler.get("delete")}
                    onclick={confirmDeletion}
                />,
                <Button
                    key="cancel"
                    id="villany-atesz-toolbox-manage-storage-box-deletion-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const confirmDeletion = async () => {
        await VILLANY_ATESZ_DELETE_STORAGE_BOX.createRequest(null, { storageBoxId: storageBox.storageBoxId })
            .send();

        removeAndSet(storageBoxes, sb => sb.storageBoxId === storageBox.storageBoxId, setStorageBoxes);

        setConfirmationDialogData(null);
    }


    return (
        <div className="villany-atesz-toolbox-manage-tab-content-item">
            {editingEnabled &&
                <div>
                    <InputField
                        className="villany-atesz-toolbox-manage-storage-box-name"
                        value={newName}
                        onchangeCallback={setNewName}
                        placeholder={localizationHandler.get("name")}
                        style={{
                            width: 8 * newName.length + "px",
                            minWidth: "200px"
                        }}
                    />
                    <Button
                        className={"villany-atesz-toolbox-manage-storage-box-save-button"}
                        onclick={save}
                        disabled={isBlank(newName)}
                        label="_"
                        title={localizationHandler.get("save")}
                    />
                    <Button
                        className={"villany-atesz-toolbox-manage-storage-box-discard-button"}
                        onclick={discard}
                        label="X"
                        title={localizationHandler.get("discard")}
                    />
                </div>
            }
            {!editingEnabled &&
                <div>
                    <span className="villany-atesz-toolbox-manage-storage-box-name">{storageBox.name}</span>
                    <Button
                        className="villany-atesz-toolbox-manage-storage-box-enable-editing-button"
                        onclick={() => setEditingEnabled(true)}
                        title={localizationHandler.get("edit")}
                    />
                    <Button
                        className="villany-atesz-toolbox-manage-storage-box-delete-button"
                        onclick={opendConfirmDeletionDialog}
                        title={localizationHandler.get("delete")}
                    />
                </div>
            }
        </div>
    );
}

export default ManagedStorageBox;