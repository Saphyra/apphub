import { useState } from "react";
import ConfirmationDialog from "../../../../common/component/confirmation_dialog/ConfirmationDialog";
import useLoader from "../../../../common/hook/Loader";
import ErrorHandler from "../../../../common/js/dao/ErrorHandler";
import { ResponseStatus } from "../../../../common/js/dao/dao";
import getChoices from "./SelectedOccurrenceChoices";
import getContent from "./SelectedOccurrenceContent";
import { CALENDAR_GET_OCCURRENCE } from "../../../../common/js/dao/endpoints/CalendarEndpoints";
import { hasValue } from "../../../../common/js/Utils";

const SelectedOccurrence = ({
    occurrenceId,
    setDisplaySpinner,
    localizationHandler,
    setSelectedOccurrence,
    refresh,
    setConfirmationDialogData
}) => {
    const [occurrence, setOccurrence] = useState(null);

    useLoader({
        request: CALENDAR_GET_OCCURRENCE.createRequest(null, { occurrenceId: occurrenceId }),
        mapper: setOccurrence,
        listener: [occurrenceId],
        setDisplaySpinner: setDisplaySpinner,
        errorHandler: new ErrorHandler(
            (response) => response.status == ResponseStatus.NOT_FOUND,
            () => setSelectedOccurrence(null))
    });

    if (hasValue(occurrence)) {
        return <ConfirmationDialog
            id="calendar-selected-occurrence"
            title={occurrence.title}
            content={getContent(occurrence)}
            choices={
                getChoices({
                    occurrence: occurrence,
                    localizationHandler: localizationHandler,
                    setOccurrence: setOccurrence,
                    setDisplaySpinner: setDisplaySpinner,
                    setSelectedOccurrence: setSelectedOccurrence,
                    refresh: refresh,
                    setConfirmationDialogData: setConfirmationDialogData
                })
            }
        />
    }
}

export default SelectedOccurrence;