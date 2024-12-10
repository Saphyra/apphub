import React from "react";
import CitizenAssignmentType from "./CitizenAssignmentType";
import localizationData from "./citizen_assignment_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import surfaceTypeLocalizationData from "../../../../common/localization/surface_localization.json";
import resourceLocalizationData from "../../../../common/localization/resource_localization.json";
import constructionAreaLocalizationData from "../../../../common/localization/construction_area_localization.json";
import buildingModuleLocalizationData from "../../../../common/localization/building_module_localization.json";

const CitizenAssignment = ({ assignment }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const surfaceTypeLocalizationHandler = new LocalizationHandler(surfaceTypeLocalizationData);
    const resourceLocalizationHandler = new LocalizationHandler(resourceLocalizationData);
    const constructionAreaLocalizationHandler = new LocalizationHandler(constructionAreaLocalizationData);
    const buildingModuleLocalizationHandler = new LocalizationHandler(buildingModuleLocalizationData);

    const getContent = () => {
        const data = assignment.data;

        switch (assignment.type) {
            case CitizenAssignmentType.IDLE:
                return localizationHandler.get("idle");
            case CitizenAssignmentType.CONSTRUCT_CONSTRUCTION_AREA:
                return localizationHandler.get("construction", { item: constructionAreaLocalizationHandler.get(assignment.data) })
            case CitizenAssignmentType.DECONSTRUCT_CONSTRUCTION_AREA:
                return localizationHandler.get("deconstruction", { item: constructionAreaLocalizationHandler.get(assignment.data) })
            case CitizenAssignmentType.CONSTRUCT_BUILDING_MODULE:
                return localizationHandler.get("construction", { item: buildingModuleLocalizationHandler.get(assignment.data) })
            case CitizenAssignmentType.DECONSTRUCT_BUILDING_MODULE:
                return localizationHandler.get("deconstruction", { item: buildingModuleLocalizationHandler.get(assignment.data) })
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