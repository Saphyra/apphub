import React from "react";
import ConstructionHeader from "./ConstructionHeader";
import DeconstructionHeader from "./DeconstructionHeader";
import TerraformationHeader from "./TerraformationHeader";
import QueueItemType from "../../../../../common/constants/QueueItemType";
import { throwException } from "../../../../../../../../common/js/Utils";

const QueueItemHeader = ({ queueItem, localizationHandler }) => {
    switch (queueItem.type) {
        case QueueItemType.CONSTRUCTION:
            return <ConstructionHeader
                queueItem={queueItem}
                localizationHandler={localizationHandler}
            />
        case QueueItemType.DECONSTRUCTION:
            return <DeconstructionHeader
                queueItem={queueItem}
                localizationHandler={localizationHandler}
            />
        case QueueItemType.TERRAFORMATION:
            return <TerraformationHeader
                queueItem={queueItem}
                localizationHandler={localizationHandler}
            />
        default:
            throwException("IllegalArgument", "Unhandled QueueItemType " + queueItem.type);
    }
}

export default QueueItemHeader;