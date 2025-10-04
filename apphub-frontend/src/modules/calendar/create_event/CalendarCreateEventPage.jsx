import { useEffect, useState } from "react";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import localizationData from "./create_event_page_localization.json";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import { ToastContainer } from "react-toastify";
import Spinner from "../../../common/component/Spinner";
import Button from "../../../common/component/input/Button";
import { CALENDAR_PAGE } from "../../../common/js/dao/endpoints/CalendarEndpoints";
import useQueryParams from "../../../common/hook/UseQueryParams";
import { hasValue, nullIfEmpty, throwException } from "../../../common/js/Utils";
import LocalDate from "../../../common/js/date/LocalDate";
import { REPETITION_TYPE_ONE_TIME } from "../common/repetition_type/RepetitionType";
import EventDate from "../common/event/EventDate";
import EventTime from "../common/event/EventTime";
import EventContent from "../common/event/EventContent";
import EventRepetition from "../common/event/EventRepetition";
import EventReminder from "../common/event/EventReminder";
import EventLabels from "../common/event/EventLabels";
import createEvent from "./createEvent";
import Optional from "../../../common/js/collection/Optional";

const CalendarCreateEventPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);

    document.title = localizationHandler.get("title");
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const queryParams = useQueryParams();

    const [displaySpinner, setDisplaySpinner] = useState(0);

    const [startDate, setStartDate] = useState(hasValue(queryParams.startDate) ? LocalDate.parse(queryParams.startDate) : LocalDate.now());
    const [endDate, setEndDate] = useState(null);
    const [time, setTime] = useState(null);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [repetitionType, setRepetitionType] = useState(REPETITION_TYPE_ONE_TIME);
    const [repetitionData, setRepetitionData] = useState("");
    const [repeatForDays, setRepeatForDays] = useState(1);
    const [remindMeBeforeDays, setRemindMeBeforeDays] = useState(0);
    const [existingLabels, setExistingLabels] = useState([]);
    const [newLabels, setNewLabels] = useState([]);

    return (
        <div id="calendar-create-event" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main className="calendar-event">
                <fieldset>
                    <legend>{localizationHandler.get("when")}</legend>

                    <EventDate
                        repetitionType={repetitionType}
                        startDate={startDate}
                        setStartDate={setStartDate}
                        endDate={endDate}
                        setEndDate={setEndDate}
                    />

                    <EventTime
                        time={time}
                        setTime={setTime}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("what")}</legend>

                    <EventContent
                        title={title}
                        setTitle={setTitle}
                        content={content}
                        setContent={setContent}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("repetition")}</legend>

                    <EventRepetition
                        repetitionType={repetitionType}
                        setRepetitionType={setRepetitionType}
                        repetitionData={repetitionData}
                        setRepetitionData={setRepetitionData}
                        repeatForDays={repeatForDays}
                        setRepeatForDays={setRepeatForDays}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("reminder")}</legend>

                    <EventReminder
                        remindMeBeforeDays={remindMeBeforeDays}
                        setRemindMeBeforeDays={setRemindMeBeforeDays}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("labels")}</legend>

                    <EventLabels
                        existingLabels={existingLabels}
                        setExistingLabels={setExistingLabels}
                        setDisplaySpinner={setDisplaySpinner}
                        newLabels={newLabels}
                        setNewLabels={setNewLabels}
                    />
                </fieldset>
            </main>

            <Footer
                centerButtons={[
                    <Button
                        id="calendar-create-event-button"
                        key="create"
                        onclick={() => createEvent(
                            {
                                repetitionType: repetitionType,
                                repetitionData: repetitionData,
                                repeatForDays: repeatForDays,
                                startDate: new Optional(startDate).map(d => d.toString()).orElse(null),
                                endDate: new Optional(nullIfEmpty(endDate)).map(d => d.toString()).orElse(null),
                                time: new Optional(time).map(d => d.formatWithoutSeconds()).orElse(null),
                                title: title,
                                content: content,
                                remindMeBeforeDays: remindMeBeforeDays
                            },
                            existingLabels,
                            setDisplaySpinner,
                            newLabels
                        )}
                        label={localizationHandler.get("create")}
                    />
                ]}
                rightButtons={[
                    <Button
                        id="calendar-create-event-back-button"
                        key="back"
                        onclick={() => window.location.href = CALENDAR_PAGE}
                        label={localizationHandler.get("back")}
                    />
                ]}
            />

            {displaySpinner > 0 && <Spinner />}

            <ToastContainer />
        </div>
    );
}

export default CalendarCreateEventPage;