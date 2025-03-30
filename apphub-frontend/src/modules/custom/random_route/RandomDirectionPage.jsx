import React, { useState } from "react";
import Footer from "../../../common/component/Footer";
import Button from "../../../common/component/input/Button";
import localizationData from "./random_route_localization.json";
import LocalizationHandler from "../../../common/js/LocalizationHandler";
import Constants from "../../../common/js/Constants";
import DirectionSelector from "./DirectionSelector";
import { LEFT_OR_RIGHT, STRAIGHT_LEFT_OR_RIGHT, STRAIGHT_OR_LEFT, STRAIGHT_OR_RIGHT } from "./Intersection";
import "./random_route.css";
import { hasValue } from "../../../common/js/Utils";
import Spinner from "../../../common/component/Spinner";

const RandomDirectionPage = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const [displaySpinner, setDisplaySpinner] = useState(false);
    const [direction, setDirection] = useState(null);

    return (
        <div id="random-direction" className="main-page" >
            <main className="headless">
                <DirectionSelector
                    intersection={STRAIGHT_OR_LEFT}
                    setDirection={setDirection}
                    setDisplaySpinner={setDisplaySpinner}
                />

                <DirectionSelector
                    intersection={STRAIGHT_OR_RIGHT}
                    setDirection={setDirection}
                    setDisplaySpinner={setDisplaySpinner}
                />

                <DirectionSelector
                    intersection={STRAIGHT_LEFT_OR_RIGHT}
                    setDirection={setDirection}
                    setDisplaySpinner={setDisplaySpinner}
                />

                <DirectionSelector
                    intersection={LEFT_OR_RIGHT}
                    setDirection={setDirection}
                    setDisplaySpinner={setDisplaySpinner}
                />

                {hasValue(direction) &&
                    <div id="random-direction-result" className={direction}>}</div>
                }
            </main>

            <Footer rightButtons={
                <Button
                    id="home-button"
                    onclick={() => window.location.href = Constants.MODULES_PAGE}
                    label={localizationHandler.get("home")}
                />
            } />

            {displaySpinner && <Spinner />}
        </div>
    );
}

export default RandomDirectionPage;