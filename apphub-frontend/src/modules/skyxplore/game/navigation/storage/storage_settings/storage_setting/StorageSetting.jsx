import React, { useEffect, useState } from "react";
import "./storage_setting.css";
import resourceLocalizationData from "../../../../common/localization/resource_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import PreLabeledInputField from "../../../../../../../common/component/input/PreLabeledInputField";
import NumberInput from "../../../../../../../common/component/input/NumberInput";
import localizationData from "./storage_setting_localization.json";
import LabelWrappedInputField from "../../../../../../../common/component/input/LabelWrappedInputField";
import RangeInput from "../../../../../../../common/component/input/RangeInput";
import Button from "../../../../../../../common/component/input/Button";
import Endpoints from "../../../../../../../common/js/dao/dao";
import ConfirmationDialogData from "../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import NotificationService from "../../../../../../../common/js/notification/NotificationService";

const StorageSetting = ({ storageSetting, setStorageSettings, setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const resourceLocalizationHandler = new LocalizationHandler(resourceLocalizationData);

    const [amount, setAmount] = useState(1);
    const [priority, setPriority] = useState(5);

    useEffect(() => setAmount(storageSetting.targetAmount), [storageSetting.targetAmount]);
    useEffect(() => setPriority(storageSetting.priority), [storageSetting.priority]);

    const reset = () => {
        setAmount(storageSetting.targetAmount);
        setPriority(storageSetting.priority);
    }

    const save = async () => {
        const payload = {
            storageSettingId: storageSetting.storageSettingId,
            dataId: storageSetting.dataId,
            targetAmount: amount,
            priority: priority
        }

        const response = await Endpoints.SKYXPLORE_PLANET_EDIT_STORAGE_SETTING.createRequest(payload)
            .send();

        setStorageSettings(response);

        NotificationService.showSuccess(localizationHandler.get("storage-setting-saved"));
    }

    const confirmDelete = () => {
        const confirmationDialogData = new ConfirmationDialogData(
            "skyxplore-game-storage-setting-deletion-confirmation-dialog",
            localizationHandler.get("delete-storage-setting-confirmation-dialog-title"),
            localizationHandler.get("delete-storage-setting-confirmation-dialog-detail", { commodity: resourceLocalizationHandler.get(storageSetting.dataId) }),
            [
                <Button
                    key="delete"
                    id="skyxplore-game-storage-setting-delete-button"
                    label={localizationHandler.get("delete-storage-setting")}
                    onclick={deleteStorageSetting}
                />,
                <Button
                    key="cancel"
                    id="skyxplore-game-storage-setting-cancel-deletion-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        )

        setConfirmationDialogData(confirmationDialogData);
    }

    const deleteStorageSetting = async () => {
        const response = await Endpoints.SKYXPLORE_PLANET_DELETE_STORAGE_SETTING.createRequest(null, { storageSettingId: storageSetting.storageSettingId })
            .send();
        setStorageSettings(response);
        setConfirmationDialogData(null);
    }

    return (
        <div
            id={"skyxplore-game-storage-settings-" + storageSetting.dataId}
            className="skyxplore-game-storage-setting"
        >
            <div className="skyxplore-game-storage-setting-title">{resourceLocalizationHandler.get(storageSetting.dataId)}</div>

            <div className="skyxplore-game-storage-setting-properties">
                <PreLabeledInputField
                    label={localizationHandler.get("amount") + ":"}
                    input={<NumberInput
                        className="skyxplore-game-storage-setting-amount"
                        onchangeCallback={setAmount}
                        value={amount}
                        min={1}
                    />}
                />

                <LabelWrappedInputField
                    preLabel={localizationHandler.get("priority") + ":"}
                    postLabel={priority}
                    inputField={<RangeInput
                        className="skyxplore-game-storage-setting-priority"
                        min={1}
                        max={10}
                        onchangeCallback={setPriority}
                        value={priority}
                    />}
                />
            </div>

            <div className="skyxplore-game-storage-setting-buttons">
                <Button
                    className="skyxplore-game-storage-setting-save-button"
                    label={localizationHandler.get("save")}
                    onclick={save}
                />

                <Button
                    className="skyxplore-game-storage-setting-delete-button"
                    label={localizationHandler.get("delete")}
                    onclick={confirmDelete}
                />

                <Button
                    className="skyxplore-game-storage-setting-reset-button"
                    label={localizationHandler.get("reset")}
                    onclick={reset}
                />
            </div>

        </div>
    );
}

export default StorageSetting;