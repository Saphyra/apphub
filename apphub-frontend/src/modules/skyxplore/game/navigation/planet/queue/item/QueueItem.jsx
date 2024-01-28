import React, { useEffect, useState } from "react";
import "./queue_item.css";
import buildingLocalizationData from "../../../../common/localization/building_localization.json";
import surfaceLocalizationData from "../../../../common/localization/surface_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import localizationData from "./queue_item_localization.json";
import LabelWrappedInputField from "../../../../../../../common/component/input/LabelWrappedInputField";
import NumberInput from "../../../../../../../common/component/input/NumberInput";
import Endpoints from "../../../../../../../common/js/dao/dao";
import Button from "../../../../../../../common/component/input/Button";
import ConfirmationDialogData from "../../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import QueueItemHeader from "./header/QueueItemHeader";

const QueueItem = ({ queueItem, planetId, setConfirmationDialogData }) => {
    const buildingLocalizationHandler = new LocalizationHandler(buildingLocalizationData);
    const localizationHandler = new LocalizationHandler(localizationData);
    const surfaceLocalizationHandler = new LocalizationHandler(surfaceLocalizationData);
    const [priority, setPriority] = useState(5);

    useEffect(() => setPriority(queueItem.ownPriority), [queueItem]);

    const changePriority = (newPriority) => {
        Endpoints.SKYXPLORE_PLANET_SET_QUEUE_ITEM_PRIORITY.createRequest({ value: newPriority }, { planetId: planetId, type: queueItem.type, itemId: queueItem.itemId })
            .send();
    }

    const confirmCancelQueueItem = () => {
        const confirmationDialogData = new ConfirmationDialogData(
            "skyxplore-planet-queue-item-cancel-confirmation",
            localizationHandler.get("confirm-cancel-queue-item-title"),
            localizationHandler.get("confirm-cancel-queue-item-content"),
            [
                <Button
                    key="cancel"
                    id="skyxplore-game-planet-queue-item-cancel-button"
                    label={localizationHandler.get("cancel-queue-item")}
                    onclick={cancelQueueItem}
                />,
                <Button
                    key="continue"
                    id="skyxplore-game-planet-queue-item-continue-button"
                    label={localizationHandler.get("continue-queue-item")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        );

        setConfirmationDialogData(confirmationDialogData);
    }

    const cancelQueueItem = async () => {
        await Endpoints.SKYXPLORE_PLANET_CANCEL_QUEUE_ITEM.createRequest(null, { planetId: planetId, type: queueItem.type, itemId: queueItem.itemId })
            .send();

        setConfirmationDialogData(null);
    }

    return (
        <div className="skyxplore-game-planet-queue-item">
            <QueueItemHeader
                queueItem={queueItem}
                localizationHandler={localizationHandler}
            />

            <ProgressBar
                currentWorkPoints={queueItem.currentWorkPoints}
                requiredWorkPoints={queueItem.requiredWorkPoints}
            />

            <div className="skyxplore-game-planet-queue-item-priority-wrapper">
                <LabelWrappedInputField
                    className="skyxplore-game-planet-queue-item-priority"
                    preLabel={localizationHandler.get("priority") + ": "}
                    inputField={
                        <NumberInput
                            type="range"
                            min="1"
                            max="10"
                            value={priority}
                            onchangeCallback={(newPriority) => changePriority(newPriority)}
                        />
                    }
                    postLabel={priority}
                />
            </div>

            <Button
                className="skyxplore-game-planet-queue-item-cancel-button"
                label={localizationHandler.get("cancel")}
                onclick={confirmCancelQueueItem}
            />
        </div>
    );
}

const ProgressBar = ({ currentWorkPoints, requiredWorkPoints }) => {
    const progress = currentWorkPoints / requiredWorkPoints * 100;

    return (
        <div className="skyxplore-game-planet-queue-item-progress-bar">
            <div
                className="skyxplore-game-planet-queue-item-progress-bar-status"
                style={{
                    width: progress + "%"
                }}
            />

            <div className="skyxplore-game-planet-queue-item-progress-bar-label">
                <span className="skyxplore-game-planet-queue-item-progress-bar-label-value">{Math.floor(progress)}</span>
                <span>%</span>
            </div>
        </div>
    );
}

export default QueueItem;