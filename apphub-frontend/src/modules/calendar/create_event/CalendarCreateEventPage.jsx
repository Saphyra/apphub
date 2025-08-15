import { useEffect, useState } from "react";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import localizationData from "./localization/create_event_page_localization.json";
import sessionChecker from "../../../common/js/SessionChecker";
import NotificationService from "../../../common/js/notification/NotificationService";
import Header from "../../../common/component/Header";
import Footer from "../../../common/component/Footer";
import { ToastContainer } from "react-toastify";
import Spinner from "../../../common/component/Spinner";
import Button from "../../../common/component/input/Button";
import { CALENDAR_CREATE_EVENT, CALENDAR_CREATE_LABEL, CALENDAR_GET_LABELS, CALENDAR_PAGE } from "../../../common/js/dao/endpoints/CalendarEndpoints";
import useQueryParams from "../../../common/hook/UseQueryParams";
import { addAndSet, hasValue, isBlank, nullIfEmpty, removeAndSet, throwException } from "../../../common/js/Utils";
import LocalDate from "../../../common/js/date/LocalDate";
import { REPETITION_TYPE_DAYS_OF_MONTH, REPETITION_TYPE_DAYS_OF_WEEK, REPETITION_TYPE_EVERY_X_DAYS, REPETITION_TYPE_ONE_TIME, RepetitionType } from "..//common/js/RepetitionType";
import PreLabeledInputField from "../../../common/component/input/PreLabeledInputField";
import InputField from "../../../common/component/input/InputField";
import Textarea from "../../../common/component/input/Textarea";
import SelectInput, { MultiSelect, SelectOption } from "../../../common/component/input/SelectInput";
import MapStream from "../../../common/js/collection/MapStream";
import repetitionTypeLocalizationData from "../common/localization/repetition_type_localization.json";
import LabelWrappedInputField from "../../../common/component/input/LabelWrappedInputField";
import NumberInput from "../../../common/component/input/NumberInput";
import daysOfWeekLocalizationData from "../../../common/js/date/day_of_week_localization.json";
import Stream from "../../../common/js/collection/Stream";
import { DAYS_OF_WEEK } from "../../../common/js/date/DayOfWeek";
import useLoader from "../../../common/hook/Loader";
import Label from "./component/Label";
import "./create_event.css";
import { MAX_LABEL_LENGTH } from "../CalendarConstants";

//TODO make it smaller
const CalendarCreateEventPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const repetitionTypeLocalizationHandler = new LocalizationHandler(repetitionTypeLocalizationData);
    const daysOfWeekLocalizationHandler = new LocalizationHandler(daysOfWeekLocalizationData);

    document.title = localizationHandler.get("title");
    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const queryParams = useQueryParams();

    const [displaySpinner, setDisplaySpinner] = useState(0);

    const [startDate, setStartDate] = useState(hasValue(queryParams.startDate) ? queryParams.startDate : LocalDate.now().toString());
    const [endDate, setEndDate] = useState(null);
    const [time, setTime] = useState("");
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [repetitionType, setReqpetitionType] = useState(REPETITION_TYPE_ONE_TIME);
    const [repetitionData, setRepetitionData] = useState("");
    const [repeatForDays, setRepeatForDays] = useState(1);
    const [remindMeBeforeDays, setRemindMeBeforeDays] = useState(0);
    const [existingLabels, setExistingLabels] = useState([]);
    const [newLabels, setNewLabels] = useState([]);

    const [availableLabels, setAvailableLabels] = useState({});
    const [newLabel, setNewLabel] = useState("");

    useEffect(setDefaultRepetitionData, [repetitionType]);

    useLoader({
        request: CALENDAR_GET_LABELS.createRequest(),
        mapper: v => setAvailableLabels(new Stream(v).toMap(i => i.labelId)),
        setDisplaySpinner: setDisplaySpinner
    });

    console.log(displaySpinner)

    return (
        <div id="calendar-create-event" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main>
                <fieldset>
                    <legend>{localizationHandler.get("when")}</legend>

                    <PreLabeledInputField
                        label={repetitionType === REPETITION_TYPE_ONE_TIME ? localizationHandler.get("date") : localizationHandler.get("start-date")}
                        input={<InputField
                            id="calendar-create-event-start-date"
                            type={"date"}
                            value={startDate}
                            onchangeCallback={setStartDate}
                        />}
                    />

                    {repetitionType !== REPETITION_TYPE_ONE_TIME &&
                        <PreLabeledInputField
                            label={localizationHandler.get("end-date")}
                            input={<InputField
                                id="calendar-create-event-end-date"
                                type={"date"}
                                value={endDate}
                                onchangeCallback={setEndDate}
                            />}
                        />
                    }

                    <span className="nowrap">
                        <PreLabeledInputField
                            label={localizationHandler.get("time")}
                            input={<InputField
                                id="calendar-create-event-time"
                                type={"time"}
                                value={time}
                                onchangeCallback={setTime}
                            />}
                        />

                        <Button
                            id="calendar-create-event-reset-time"
                            label={localizationHandler.get("reset-time")}
                            onclick={() => setTime(null)}
                        />
                    </span>
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("what")}</legend>

                    <InputField
                        id="calendar-create-event-title"
                        value={title}
                        onchangeCallback={setTitle}
                        placeholder={localizationHandler.get("event-title")}
                    />

                    <Textarea
                        id="calendar-create-event-content"
                        placeholder={localizationHandler.get("content")}
                        value={content}
                        onchangeCallback={setContent}
                        onKeyUpCallback={e => {
                            e.target.style.height = "auto";
                            e.target.style.height = e.target.scrollHeight + 6 + "px";
                        }}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("repetition")}</legend>

                    <PreLabeledInputField
                        label={localizationHandler.get("repetition-type")}
                        input={<SelectInput
                            id="calendar-create-event-repetition-type"
                            value={repetitionType}
                            onchangeCallback={setReqpetitionType}
                            options={getRepetitionTypes()}
                        />}
                    />

                    {repetitionType === REPETITION_TYPE_EVERY_X_DAYS &&
                        <LabelWrappedInputField
                            preLabel={localizationHandler.get("every-x-days-pre-label")}
                            postLabel={localizationHandler.get("every-x-days-post-label")}
                            inputField={<NumberInput
                                id="calendar-create-event-repetiton-data"
                                value={repetitionData}
                                onchangeCallback={setRepetitionData}
                                min={1}
                            />}
                        />
                    }

                    {repetitionType === REPETITION_TYPE_DAYS_OF_WEEK && typeof repetitionData === "object" &&
                        <MultiSelect
                            id="calendar-create-event-repetition-data"
                            value={repetitionData}
                            onchangeCallback={setRepetitionData}
                            options={getDaysOfWeekOptions()}
                        />
                    }

                    {repetitionType === REPETITION_TYPE_DAYS_OF_MONTH && typeof repetitionData === "object" &&
                        <MultiSelect
                            id="calendar-create-event-repetition-data"
                            value={repetitionData}
                            onchangeCallback={setRepetitionData}
                            options={getDaysOfMonthOptions()}
                        />
                    }

                    <LabelWrappedInputField
                        preLabel={localizationHandler.get("repeat-for-pre-label")}
                        postLabel={localizationHandler.get("repeat-for-post-label")}
                        inputField={<NumberInput
                            id="calendar-create-event-repeat-for"
                            value={repeatForDays}
                            onchangeCallback={setRepeatForDays}
                            min={1}
                        />}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("reminder")}</legend>

                    <LabelWrappedInputField
                        preLabel={localizationHandler.get("remind-me-before-days-pre-label")}
                        postLabel={localizationHandler.get("remind-me-before-days-post-label")}
                        inputField={<NumberInput
                            id="calendar-create-event-remind-me-before-days"
                            value={remindMeBeforeDays}
                            onchangeCallback={setRemindMeBeforeDays}
                            min={0}
                        />}
                    />
                </fieldset>

                <fieldset>
                    <legend>{localizationHandler.get("labels")}</legend>

                    <div id="calendar-create-event-labels">{localizationHandler.get("labels-of-event")}: {getLabels()}</div>
                    <div id="calendar-create-event-available-labels">{localizationHandler.get("available-labels")}: {getAvailableLabels()}</div>
                    <div>
                        <InputField
                            id="calendar-create-event-new-label"
                            value={newLabel}
                            onchangeCallback={setNewLabel}
                            placeholder={localizationHandler.get("new-label")}
                        />

                        <Button
                            id="calendar-create-event-new-label-button"
                            label={localizationHandler.get("add")}
                            onclick={addNewLabel}
                        />
                    </div>
                </fieldset>
            </main>

            <Footer
                centerButtons={[
                    <Button
                        id="calendar-create-event-button"
                        key="create"
                        onclick={create}
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

    async function create() {
        if (repetitionType !== REPETITION_TYPE_ONE_TIME) {
            if (isBlank(endDate)) {
                NotificationService.showError(localizationHandler.get("empty-end-date"));
                return;
            }

            if (LocalDate.parse(endDate).isBefore(LocalDate.parse(startDate))) {
                NotificationService.showError(localizationHandler.get("end-date-before-start-date"));
                return;
            }
        }

        if (repeatForDays < 1) {
            NotificationService.showError(localizationHandler.get("repeat-for-days-too-low"));
            return;
        }

        if (repetitionType === REPETITION_TYPE_EVERY_X_DAYS && repetitionData < 1) {
            NotificationService.showError("repeat-every-x-days-too-low");
            return;
        }

        if (repetitionType === REPETITION_TYPE_DAYS_OF_WEEK || repetitionType === REPETITION_TYPE_DAYS_OF_MONTH) {
            if (repeatForDays.length === 0) {
                NotificationService.showError(localizationHandler.get("no-days-defined"));
                return;
            }
        }

        if (isBlank(title)) {
            NotificationService.showError(localizationHandler.get("blank-title"));
            return;
        }

        if (remindMeBeforeDays < 0) {
            NotificationService.showError(localizationHandler.get("remind-me-before-days-too-low"));
            return;
        }

        const newLabelIds = await createLabels();

        const payload = {
            repetitionType: repetitionType,
            repetitionData: nullIfEmpty(repetitionData),
            repeatForDays: repeatForDays,
            startDate: startDate,
            endDate: nullIfEmpty(endDate),
            time: nullIfEmpty(time),
            title: title,
            content: content,
            remindMeBeforeDays: remindMeBeforeDays,
            labels: new Stream(existingLabels)
                .addAll(newLabelIds)
                .toList()
        }

        await CALENDAR_CREATE_EVENT.createRequest(payload)
            .send(setDisplaySpinner);

        NotificationService.storeSuccessText(localizationHandler.get("event-created"));
        window.location.href = CALENDAR_PAGE;

        async function createLabels() {
            return await Promise.all(newLabels.map(label => createLabel(label)));

            async function createLabel(label) {
                return CALENDAR_CREATE_LABEL.createRequest({ value: label })
                    .send(setDisplaySpinner)
                    .then(response => response.value);
            }
        }
    }


    function addNewLabel() {
        if (isBlank(newLabel)) {
            NotificationService.showError(localizationHandler.get("new-label-too-short"));
            return;
        }

        if (newLabel.length > MAX_LABEL_LENGTH) {
            NotificationService.showError(localizationHandler.get("new-label-too-long"));
            return;
        }

        if (new MapStream(availableLabels).toListStream().anyMatch(label => label.label === newLabel)) {
            NotificationService.showError(localizationHandler.get("label-already-exists"));
            return;
        }

        if (new Stream(newLabels).anyMatch(label => label === newLabel)) {
            NotificationService.showError(localizationHandler.get("label-already-exists"));
            return;
        }

        addAndSet(newLabels, newLabel, setNewLabels);
        setNewLabel("");
    }

    function getAvailableLabels() {
        return new MapStream(availableLabels)
            .toListStream()
            .filter(label => existingLabels.indexOf(label.labelId) < 0)
            .sorted((a, b) => a.label.localeCompare(b.label))
            .map(label => <Label
                key={label.labelId}
                text={label.label}
                callback={() => addAndSet(existingLabels, label.labelId, setExistingLabels)}
            />)
            .toList();
    }

    function getLabels() {
        const el = new Stream(existingLabels)
            .toMap(
                labelId => availableLabels[labelId].label,
                labelId => <Label
                    key={labelId}
                    text={availableLabels[labelId].label}
                    callback={() => removeAndSet(existingLabels, l => l === labelId, setExistingLabels)}
                />);
        const nl = new Stream(newLabels)
            .toMap(
                label => label,
                label => <Label
                    key={label}
                    text={label}
                    callback={() => removeAndSet(newLabels, l => l === label, setNewLabels)}
                />);

        const merged = { ...el, ...nl };

        return new MapStream(merged)
            .sorted((a, b) => a.key.localeCompare(b.key))
            .toList();
    }

    function getDaysOfMonthOptions() {
        return new Stream(Array.from({ length: 31 }, (_, i) => i + 1))
            .map(day => new SelectOption(day, day))
            .toList();
    }

    function getDaysOfWeekOptions() {
        return new Stream(DAYS_OF_WEEK)
            .map(dayOfWeek => new SelectOption(daysOfWeekLocalizationHandler.get(dayOfWeek), dayOfWeek))
            .toList();
    }

    function setDefaultRepetitionData() {
        switch (repetitionType) {
            case REPETITION_TYPE_ONE_TIME:
                setRepetitionData("");
                break;
            case REPETITION_TYPE_EVERY_X_DAYS:
                setRepetitionData(1);
                break;
            case REPETITION_TYPE_DAYS_OF_WEEK:
            case REPETITION_TYPE_DAYS_OF_MONTH:
                setRepetitionData([]);
                break;
            default:
                throwException("IllegalArgument", "Unhandled repetitionType: " + repetitionType);
        }
    }

    function getRepetitionTypes() {
        return new MapStream(RepetitionType)
            .toListStream((k, v) => k)
            .map(r => new SelectOption(repetitionTypeLocalizationHandler.get(r), r))
            .toList();
    }
}

export default CalendarCreateEventPage;