import React from "react";
import CitizenAssignmentType from "./CitizenAssignmentType";
import localizationData from "./citizen_assignment_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import buildingLocalizationData from "../../../../common/localization/building_localization.json";
import surfaceTypeLocalizationData from "../../../../common/localization/surface_localization.json";
import resourceLocalizationData from "../../../../common/localization/resource_localization.json";

const CitizenAssignment = ({ assignment }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const buildingLocalizationHandler = new LocalizationHandler(buildingLocalizationData);
    const surfaceTypeLocalizationHandler = new LocalizationHandler(surfaceTypeLocalizationData);
    const resourceLocalizationHandler = new LocalizationHandler(resourceLocalizationData);

    const getContent = () => {
        const data = assignment.data;

        switch (assignment.type) {
            case CitizenAssignmentType.IDLE:
                return localizationHandler.get("idle");
            case CitizenAssignmentType.CONSTRUCTION:
                return localizationHandler.get("construction", { building: buildingLocalizationHandler.get(assignment.data) })
                case CitizenAssignmentType.DECONSTRUCTION:
                    return localizationHandler.get("deconstruction", { building: buildingLocalizationHandler.get(assignment.data) })
            case CitizenAssignmentType.REST:
                return localizationHandler.get("rest", { status: Math.round(data.restedForTicks / data.restForTicks * 100) })
            case CitizenAssignmentType.TERRAFORMATION:
                return localizationHandler.get(
                    "terraformation",
                    {
                        originalSurfaceType: surfaceTypeLocalizationHandler.get(data.originalSurfaceType),
                        targetSurfaceType: surfaceTypeLocalizationHandler.get(data.targetSurfaceType)
                    }
                )
            case CitizenAssignmentType.PRODUCTION_ORDER:
                return localizationHandler.get("production-order", { resource: resourceLocalizationHandler.get(data) });
            case CitizenAssignmentType.STORAGE_SETTING:
                return localizationHandler.get("storage-setting", { resource: resourceLocalizationHandler.get(data) });
            default:
                return JSON.stringify(assignment);
        }
    }

    return (
        <div>
            <span>{localizationHandler.get("current-activity")}</span>
            <span>: </span>
            <span>
                {getContent()}
            </span>
        </div>
    );
}

export default CitizenAssignment;