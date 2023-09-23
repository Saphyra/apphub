import React from "react";
import durationChoices from "./duration_choices.json";
import Stream from "../../../../common/js/collection/Stream";
import Choice from "./Choice";
import localization from "./duration_choice_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";

const Durations = ({ duration, setDuration }) => {
    const localizationHandler = new LocalizationHandler(localization);

    const getChoices = () => {
        return new Stream(durationChoices)
            .sorted((a, b) => a.duration - b.duration)
            .map(choice =>
                <Choice
                    key={choice.duration}
                    data={choice}
                    currentChoice={duration}
                    setChoice={setDuration}
                    localizationHandler={localizationHandler}
                />
            )
            .toList();
    }

    return (
        <div id="duration-choices">
            {getChoices()}
        </div>
    );
}

export default Durations;