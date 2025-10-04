import InputField from "../../../../common/component/input/InputField";
import Textarea from "../../../../common/component/input/Textarea";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import localizationData from "./event_localization.json";
import "./event.css";

const EventContent = ({ title, setTitle, content, setContent }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <span>
            <InputField
                id="calendar-event-title"
                value={title}
                onchangeCallback={setTitle}
                placeholder={localizationHandler.get("event-title")}
            />

            <Textarea
                id="calendar-event-content"
                placeholder={localizationHandler.get("content")}
                value={content}
                onchangeCallback={setContent}
                onKeyUpCallback={e => {
                    e.target.style.height = "auto";
                    e.target.style.height = e.target.scrollHeight + 6 + "px";
                }}
            />
        </span>
    );
}

export default EventContent;