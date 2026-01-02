import { CALENDAR_CREATE_EVENT_PAGE } from "../../../../../common/js/dao/endpoints/CalendarEndpoints";
import { hasValue } from "../../../../../common/js/Utils";
import SelectedDateContent from "./SelectedDateContent";
import CelectedOccurrenceContent from "./SelectedOccurrenceContent";

const RightPanel = ({
    selectedDate,
    activeLabel,
    setDisplaySpinner,
    selectedOccurrence,
    setSelectedOccurrence,
    refreshCounter,
    refresh,
    setConfirmationDialogData,
    localizationHandler
}) => {
    return (
        <div id="calendar-right-panel">
            <div id="calendar-selected-date-title" className="nowrap">{selectedDate.format()}</div>

            <SelectedDateContent
                selectedDate={selectedDate}
                activeLabel={activeLabel}
                refreshCounter={refreshCounter}
                setDisplaySpinner={setDisplaySpinner}
                setSelectedOccurrence={setSelectedOccurrence}
            />

            {hasValue(selectedOccurrence) &&
                <CelectedOccurrenceContent
                    occurrenceId={selectedOccurrence}
                    setDisplaySpinner={setDisplaySpinner}
                    setSelectedOccurrence={setSelectedOccurrence}
                    localizationHandler={localizationHandler}
                    refresh={refresh}
                    setConfirmationDialogData={setConfirmationDialogData}
                />
            }

            <div
                id="calendar-selected-date-create-new"
                className="button"
                onClick={() => window.location.href = CALENDAR_CREATE_EVENT_PAGE.assembleUrl(null, { startDate: selectedDate.format() })}
            >
                {localizationHandler.get("create-new")}
            </div>
        </div>
    );
}

export default RightPanel;