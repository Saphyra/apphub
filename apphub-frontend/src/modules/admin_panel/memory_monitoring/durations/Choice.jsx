import React from "react";
import Button from "../../../../common/component/input/Button";

const Choice = ({ data, currentChoice, setChoice, localizationHandler }) => {
    return (
        <Button
            id={"duration-choice-" + data.duration}
            className={"duration-choice " + (data.duration === currentChoice ? "active" : "")}
            label={localizationHandler.get(data.localizationKey)}
            onclick={() => setChoice(data.duration)}
        />
    )
}

export default Choice;