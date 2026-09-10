import { hasValue } from "common/js/Utils";
import ConstructionAreaConstructionFooter from "./ConstructionAreaConstructionFooter";
import ConstructionAreaDeconstructionFooter from "./ConstructionAreaDeconstructionFooter";
import ConstructionAreeaIdleFooter from "./ConstructionAreeaIdleFooter";

const ConstructionAreaFooter = ({ surface, localizationHandler, setConfirmationDialogData, openPage, setDisplaySpinner }) => {
    const constructionArea = surface.constructionArea;

    if (hasValue(constructionArea.construction)) {
        return <ConstructionAreaConstructionFooter
            surface={surface}
            localizationHandler={localizationHandler}
            setConfirmationDialogData={setConfirmationDialogData}
            setDisplaySpinner={setDisplaySpinner}
        />
    } else if (hasValue(constructionArea.deconstruction)) {
        return <ConstructionAreaDeconstructionFooter
            surface={surface}
            localizationHandler={localizationHandler}
            setConfirmationDialogData={setConfirmationDialogData}
            setDisplaySpinner={setDisplaySpinner}
        />
    } else {
        return <ConstructionAreeaIdleFooter
            localizationHandler={localizationHandler}
            constructionArea={surface.constructionArea}
            setConfirmationDialogData={setConfirmationDialogData}
            openPage={openPage}
            setDisplaySpinner={setDisplaySpinner}
        />
    }
}

export default ConstructionAreaFooter;