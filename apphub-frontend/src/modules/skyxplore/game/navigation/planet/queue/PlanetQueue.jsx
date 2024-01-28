import React from "react";
import "./planet_queue.css"
import localizationData from "./planet_queue_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import Stream from "../../../../../../common/js/collection/Stream";
import QueueItem from "./item/QueueItem";

const PlanetQueue = ({ queue, planetId, setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const getContent = () => {
        return new Stream(queue)
            .sorted((a, b) => a.totalPriority - b.totalPriority)
            .map(queueItem => <QueueItem
                key={queueItem.itemId}
                queueItem={queueItem}
                planetId={planetId}
                setConfirmationDialogData={setConfirmationDialogData}
            />)
            .toList();
    }

    return (
        <div id="skyxplore-game-planet-queue">
            <div id="skyxplore-game-planet-queue-title">{localizationHandler.get("title")}</div>

            {getContent()}
        </div>
    );
}

export default PlanetQueue;