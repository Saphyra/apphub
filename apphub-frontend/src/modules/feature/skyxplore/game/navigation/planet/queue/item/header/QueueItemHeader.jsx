import QueueItemType from "modules/feature/skyxplore/game/common/constants/QueueItemType"
import TerraformationHeader from "./TerraformationHeader"
import ConstructConstructionAreaHeader from "./ConstructConstructionAreaHeader"
import DeconstructConstructionAreaHeader from "./DeconstructConstructionAreaHeader"
import DeconstructBuildingModuleHeader from "./DeconstructBuildingModuleHeader"
import ConstructBuildingModuleHeader from "./ConstructBuildingModuleHeader"
import { throwException } from "common/js/Utils"

const QueueItemHeader = ({ queueItem, localizationHandler }) => {
    switch (queueItem.type) {
        case QueueItemType.TERRAFORMATION:
            return <TerraformationHeader
                queueItem={queueItem}
                localizationHandler={localizationHandler}
            />
        case QueueItemType.CONSTRUCT_CONSTRUCTION_AREA:
            return <ConstructConstructionAreaHeader
                queueItem={queueItem}
                localizationHandler={localizationHandler}
            />
        case QueueItemType.DECONSTRUCT_CONSTRUCTION_AREA:
            return <DeconstructConstructionAreaHeader
                queueItem={queueItem}
                localizationHandler={localizationHandler}
            />
        case QueueItemType.DECONSTRUCT_BUILDING_MODULE:
            return <DeconstructBuildingModuleHeader
                queueItem={queueItem}
                localizationHandler={localizationHandler}
            />
        case QueueItemType.CONSTRUCT_BUILDING_MODULE:
            return <ConstructBuildingModuleHeader
                queueItem={queueItem}
                localizationHandler={localizationHandler}
            />
        default:
            throwException("IllegalArgument", "Unhandled QueueItemType " + queueItem.type);
    }
}

export default QueueItemHeader;