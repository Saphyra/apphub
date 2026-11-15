import Button from "common/component/input/Button";
import grantLocalizationData from "../common/share_with/grant_localization.json";
import LocalizationHandler from "common/js/LocalizationHandler";
import { removeAndSet } from "common/js/Utils";

const CreateEventShareWith = ({ localizationHandler, shareWith, setShareWith }) => {
    const grantLocalizationHandler = new LocalizationHandler(grantLocalizationData);

    return (
        <div id="calendar-create-event-shared-with" style={{ display: "flex", flexDirection: "row", flexWrap: "wrap" }}>
            {shareWith.map((item, idx) => (
                <fieldset key={idx} className="shared-user-item">
                    <legend>
                        {item.user.username} ({item.user.email})
                        <Button
                            label="X"
                            onclick={() => removeAndSet(shareWith, i => i === item, setShareWith)}
                        />
                    </legend>

                    <ul>
                        {item.grants.map((g, gi) => (<li key={gi}>{grantLocalizationHandler.get(g, { type: localizationHandler.get("event") })}</li>))}
                        </ul>
                </fieldset>
            ))}
        </div>
    )
}

export default CreateEventShareWith;