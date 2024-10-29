import React from "react";
import ConstructionAreaConstructionFooter from "./ConstructionAreaConstructionFooter";
import ConstructionAreaDeconstructionFooter from "./ConstructionAreaDeconstructionFooter";
import ConstructionAreeaIdleFooter from "./ConstructionAreeaIdleFooter";
import { hasValue } from "../../../../../../../../../../common/js/Utils";

const ConstructionAreaFooter = ({ surface, localizationHandler, setConfirmationDialogData, planetId }) => {
    const constructionArea = surface.constructionArea;

    if (hasValue(constructionArea.construction)) {
        return <ConstructionAreaConstructionFooter
            surface={surface}
            localizationHandler={localizationHandler}
            setConfirmationDialogData={setConfirmationDialogData}
            planetId={planetId}
        />
    } else if (hasValue(constructionArea.deconstruction)) {
        return <ConstructionAreaDeconstructionFooter
            surface={surface}
            localizationHandler={localizationHandler}
            setConfirmationDialogData={setConfirmationDialogData}
            planetId={planetId}
        />
    } else {
        return <ConstructionAreeaIdleFooter
            localizationHandler={localizationHandler}
            surface={surface}
            setConfirmationDialogData={setConfirmationDialogData}
            planetId={planetId}
        />
    }
}

export default ConstructionAreaFooter;