import React from "react";

const DirectionSelector = ({ intersection, setDirection, setDisplaySpinner }) => {
    return (
        <div
            className={"random-direction-intersection-selector button " + intersection.classes}
            onClick={generateRandomDirection}
        >
        </div>
    );

    function generateRandomDirection() {
        setDisplaySpinner(true);

        setTimeout(
            () => {
                setDirection(intersection.getRandomDirection())
                setDisplaySpinner(false);
            },
            500
        );
    }
}

export default DirectionSelector;