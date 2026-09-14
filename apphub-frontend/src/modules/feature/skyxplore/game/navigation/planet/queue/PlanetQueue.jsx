import LocalizationHandler from "common/js/LocalizationHandler";
import localizationData from "./planet_queue_localization.json";
import "./planet_queue.css";
import Stream from "common/js/collection/Stream";
import QueueItem from "./item/QueueItem";

const PlanetQueue = ({ queue, planetId, setConfirmationDialogData, setDisplaySpinner }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const getContent = () => {
        return new Stream(queue)
            .sorted((a, b) => a.totalPriority - b.totalPriority)
            .map(queueItem => <QueueItem
                key={queueItem.itemId}
                queueItem={queueItem}
                planetId={planetId}
                setConfirmationDialogData={setConfirmationDialogData}
                setDisplaySpinner={setDisplaySpinner}
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