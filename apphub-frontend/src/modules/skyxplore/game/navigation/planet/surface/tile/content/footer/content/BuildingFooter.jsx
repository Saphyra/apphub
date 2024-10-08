import React from "react";
import BuildingConstructionFooter from "./BuildingConstructionFooter";
import BuildingDeconstructionFooter from "./BuildingDeconstructionFooter";
import BuildingIdleFooter from "./BuildingIdleFooter";
import { hasValue } from "../../../../../../../../../../common/js/Utils";

const BuildingFooter = ({ surface, localizationHandler, setConfirmationDialogData, planetId, openPage }) => {
    const building = surface.building;

    if (hasValue(building.construction)) {
        return <BuildingConstructionFooter
            surface={surface}
            localizationHandler={localizationHandler}
            setConfirmationDialogData={setConfirmationDialogData}
            planetId={planetId}
        />
    } else if (hasValue(building.deconstruction)) {
        return <BuildingDeconstructionFooter
            surface={surface}
            localizationHandler={localizationHandler}
            setConfirmationDialogData={setConfirmationDialogData}
            planetId={planetId}
        />
    } else {
        return <BuildingIdleFooter
            localizationHandler={localizationHandler}
            surface={surface}
            setConfirmationDialogData={setConfirmationDialogData}
            planetId={planetId}
            openPage={openPage}
        />
    }
}

export default BuildingFooter;