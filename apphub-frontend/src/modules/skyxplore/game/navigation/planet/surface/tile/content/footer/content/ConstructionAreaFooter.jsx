import React from "react";
import ConstructionAreaConstructionFooter from "./ConstructionAreaConstructionFooter";
import ConstructionAreaDeconstructionFooter from "./ConstructionAreaDeconstructionFooter";
import ConstructionAreeaIdleFooter from "./ConstructionAreeaIdleFooter";
import { hasValue } from "../../../../../../../../../../common/js/Utils";

const ConstructionAreaFooter = ({ surface, localizationHandler, setConfirmationDialogData, openPage }) => {
    const constructionArea = surface.constructionArea;

    if (hasValue(constructionArea.construction)) {
        return <ConstructionAreaConstructionFooter
            surface={surface}
            localizationHandler={localizationHandler}
            setConfirmationDialogData={setConfirmationDialogData}
        />
    } else if (hasValue(constructionArea.deconstruction)) {
        return <ConstructionAreaDeconstructionFooter
            surface={surface}
            localizationHandler={localizationHandler}
            setConfirmationDialogData={setConfirmationDialogData}
        />
    } else {
        return <ConstructionAreeaIdleFooter
            localizationHandler={localizationHandler}
            constructionArea={surface.constructionArea}
            setConfirmationDialogData={setConfirmationDialogData}
            openPage={openPage}
        />
    }
}

export default ConstructionAreaFooter;