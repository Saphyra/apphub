import { hasValue } from "common/js/Utils";
import SelectedDateContent from "./SelectedDateContent";
import SelectedOccurrenceContent from "./SelectedOccurrenceContent";
import { CALENDAR_CREATE_EVENT_PAGE } from "modules/feature/calendar/CalendarEndpoints";

const RightPanel = ({
    selectedDate,
    activeLabel,
    showArchived,
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
                showArchived={showArchived}
                refreshCounter={refreshCounter}
                setDisplaySpinner={setDisplaySpinner}
                setSelectedOccurrence={setSelectedOccurrence}
            />

            {hasValue(selectedOccurrence) &&
                <SelectedOccurrenceContent
                    eventId={selectedOccurrence.eventId}
                    occurrenceId={selectedOccurrence.occurrenceId}
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